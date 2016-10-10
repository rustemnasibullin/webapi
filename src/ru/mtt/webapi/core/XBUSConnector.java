package ru.mtt.webapi.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.camel.*;
import org.apache.log4j.Logger;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.camel.model.LoadBalanceDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.mtt.webapi.utils.XUtils;

/**
 * Created by R.Nasibullin .
 * Unified XBUS.Camel connector for basic and reserved channel.
 *
 * @author rnasibullin@mtt.ru
 */
public class XBUSConnector {

    static final int _DELAY = 5000;
    Logger log = Logger.getLogger(XBUSConnector.class);
    CamelContext x = null;
    SedaEndpoint ep = null;
    SedaEndpoint eq = null;
    Producer pq = null;
    Exchange qs = null;
    Producer ps = null;
    Exchange xs = null;
    
    Gson gson =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    AtomicLong cnt = new AtomicLong(0);
    AtomicLong suc = new AtomicLong(0);
    int p0 = 0;
    int p1 = 0;
    int p2 = 0;
    int p3 = 0;
    Map <Long, TransactProcessor> procs = new ConcurrentHashMap<>();
    Set <XNode> nodes = new ConcurrentSkipListSet<>(); 
    static XBUSConnector instance = null;
   
    public static synchronized XBUSConnector getInstance () {
        
        if (instance == null) {
            instance = new XBUSConnector ();
            
        }
        
        return instance; 
        
    }

    public TransactProcessor getTX (long iTX) {

           return procs.get (iTX);

    }

    public final ReentrantLock lock = new ReentrantLock ();

    public Logger getLog() {
           return log;
    }

    public void setLog(Logger log) {
           this.log = log;
    }


    private XBUSConnector () {
        
        
            XNetDiscoverer xnet = new XNetDiscoverer ();
            xnet.start();
        
        
    }


    public void lock() {

        lock.lock();

    };

    public void unlock() {

           lock.unlock();

    };


    public long beginTX() {

           Long iTX  =  0L;

           if (cnt.get()==Long.MAX_VALUE) {
               cnt.set(0L);
           }
           TransactProcessor p = new TransactProcessor ();
           iTX = cnt.incrementAndGet();
           procs.put (iTX,p);
           p.execute("@BEGIN");
           log.debug(iTX + "  -  " + procs.size());

        return iTX;

    }

    public void commitTX(Long iTX ) {

        TransactProcessor p = procs.remove(iTX);
        p.execute("@COMMIT");

    }

    public synchronized void reConfigRoutes (List <XNode> xnodes) {
        
           boolean conf = false; 
           if (xnodes.size () != nodes.size()) {

             conf = true;  

           } else {
               
               if (nodes.containsAll(xnodes)) {
                   conf = false;                    
               } else  {
                   conf = true;
               }
               
           }
     
           if (conf) {

               nodes.clear();   
               for (XNode x: xnodes) {
                    nodes.add (x);
               }
               configRoutes ();

           }
        
    }

    public void configRoutes () {

        try {

                ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/process.xml");
                if (x != null) {
                    x.stop (); 
                }  

                x = new SpringCamelContext(context);
                
                
                if (!this.lock.isLocked()) {

                lock();
                

                int n = nodes.size();
                final String[]  xnodes1 = new String[n];
                final String[]  xnodes2 = new String[n];
                Iterator<XNode> ITR = nodes.iterator();
                for (int i=0; i<n; i++) {
                         XNode c = ITR.next(); 
                         xnodes1[i] = "mina2:tcp://"+c.getHost()+":"+c.getPort()+"?sync=true&textline=true";
                         xnodes2[i] = "mina2:tcp://"+c.getHost()+":"+c.getPort()+"?sync=true&textline=true";
                };
                    
                x.addRoutes(new RouteBuilder(){

                    public void configure () {

                        errorHandler(deadLetterChannel("seda:recoerr").maximumRedeliveries(20).redeliveryDelay(15000));
                        LoadBalanceDefinition ps = from("seda:jobs").loadBalance();
                        ps.setRef("roundRobinRef");
                        ps.to(xnodes1);

                    }

                });

 
                x.addRoutes(new RouteBuilder(){

                    public void configure () {

                        errorHandler(deadLetterChannel("seda:recoerr").maximumRedeliveries(20).redeliveryDelay(15000));
                        LoadBalanceDefinition ps = from("seda:quest").loadBalance();
                        ps.setRef("roundRobinRefSynch");
                        ps.to(xnodes2);

                    }

                });


                x.addRoutes(new RouteBuilder(){

                    public void configure () {

                        from("seda:recoerr")
                        .wireTap("seda:tapederr")
                        .to("bean:logger?method=info");

                    }

                });

                unlock(); 
                x.start();

                ep = (SedaEndpoint) x.getEndpoint("seda:jobs");
                ps = ep.createProducer();
                xs = ps.createExchange(ExchangePattern.InOut);

                eq = (SedaEndpoint) x.getEndpoint("seda:quest");
                pq = eq.createProducer();
                qs = pq.createExchange(ExchangePattern.InOut);

            }


        } catch (Throwable ee) {

            ee.printStackTrace();
            log.info("Error Initialization: "+ee.getClass().getName());

        }

    }


 
    public synchronized String singleCommand(String cmd) {

           return new TransactProcessor().execute(cmd);

    };

    public void stop () {

        try {

            if (x != null) {
                x.stop();
            }

        } catch (Throwable ee) {

          ee.printStackTrace();

        }

    }

    public void populate (IConfigurableObject o) {

        p0 = o.getIntConfigParameter ("port");
        p1 = o.getIntConfigParameter ("port1");
        p2 = o.getIntConfigParameter ("port2");
        p3 = o.getIntConfigParameter ("port3");
        
        XNode x1 = new XNode ("127.0.0.1", p1);
        XNode x2 = new XNode ("127.0.0.1", p2);
        XNode x3 = new XNode ("127.0.0.1", p3);
        nodes.add (x1);
        nodes.add (x2);
        nodes.add (x3);
        
        
        log.info("Ports:  "+p0+"/"+p1+"/"+p2+"/"+p3);
        configRoutes();

    }

    private void ilog(String fn, String d) {

        XUtils.ilog(fn, d);

    }

    private void plog(String fn, String d) {

        XUtils.plog(fn, d);

    }

    public Object getStatistics () {

        return "XBUS Trafik:"+cnt+"/"+suc;

    }




    class TransactProcessor   {

        String exeJobFin = null;
        int actLines = 0;

        protected String camelSenderSynch(String exeRequest) throws Exception  {

            String res = "{'status':'empty request'}";

            if (exeRequest==null || "null".equals(exeRequest) ) {
                try {

                    throw new Throwable();

                } catch (Throwable ee) {

                    log.error(null, ee);
                    ee.printStackTrace();
 
                }

            }

            if (x==null) throw new Exception("Camel Context has not initiated.");

        
            if (exeRequest.trim().startsWith("{") && exeRequest.trim().endsWith("}")) {

        

                res = executeExchange (exeRequest, true);

            }

            return res;

        }




        private String  executeExchange (String exeJob, boolean synch) throws Exception {

            String res = "";
            Producer pss  =  null;
            Exchange xss  =  null;
            
            if (synch) {
                xss = qs;
                pss = pq;
            } else {
                xss = xs;
                pss = ps;
            }

            if (xss == null) XUtils.ilog ("err.log", "XSS: " + xss + "  -  synch: "+ synch);

            System.out.println (pss + " ---------------------- ///////////////// -----------------------------    " + xss);
            System.out.println ("Test info Data_IN:------------ ///////////////// ------------------ \n" + exeJob);
            
            xss.getIn().setBody(exeJob);
            log.debug("Data_IN: \n" + exeJob);
 
            pss.process(xss);
            
            System.out.println ("Test:-------------------------------------<<<<<<<<<<<<<<<<<<<<<< "+exeJob);

            if (xss.hasOut()) {
                res = xss.getOut().getBody().toString();
                log.debug("Data_OUT: \n" + res);
                suc.incrementAndGet();
            }


            return res;

        }


        public String executeCommand(String exeJob) throws Exception  {

            System.out.println ("tEST:  "+exeJob);


            String res = "";

            if (x==null) throw new Exception("Camel Context has not initiated.");

            try {

                ProducerTemplate producer = x.createProducerTemplate();
                producer.sendBody("seda:jobs", exeJob);

            } catch (Throwable ee) {

                ee.printStackTrace();
                throw new Exception("Camel can't transfer data.", ee);

            }

            return res;

        }


        protected String camelSender(String exeJob) throws Exception  {

            String res = "";

            if (exeJob==null || "null".equals(exeJob) ) {
                
                try {

                    throw new Throwable();

                } catch (Throwable ee) {

                    log.error(null, ee);
                    ee.printStackTrace();
                }

            }

            if (x==null) throw new Exception("Camel Context has not initiated.");

            boolean ex = false;
            boolean tx = false;

            if ("@BEGIN".equals(exeJob)) {
                exeJobFin = exeJob;
                return res;
            } else if ("@COMMIT".equals(exeJob)) {

                exeJobFin+="\n"+exeJob;
                ex = true;

            } else if (exeJob.startsWith("{") && exeJob.endsWith("}")) {

                res = executeExchange (exeJob, false);

            } else if (exeJobFin != null) {

                if (exeJob.startsWith("@")) {
                    exeJobFin += "\n" + exeJob.substring(1);
                    actLines++;
                }

            } else {

                ex = true;
                exeJobFin = exeJob;
                actLines = 1;

            }

            if (ex) {

                if (actLines>0) {
                    res = executeExchange(exeJobFin, false);
                }
                actLines=0;
                exeJobFin = null;

            }

            return res;

        }





        public String execute(String cmd) {

            String res  =  null;
            log.debug("Command:  " + cmd);
            try {

                res  = camelSenderSynch(cmd);
                System.out.println ("camelSenderSynch: " + res); 
                log.debug("Camel invocation: " + cmd + "=" + res);

            } catch (Throwable ee) {

                ee.printStackTrace();
                ilog("err.txt", cmd + "  Real execute exeJobFin:" + exeJobFin);
                res = "Command Excition failed:"+ee.getMessage();

            }

            return res;

        }

        public String executeSynch(String cmd) {

            String res  =  null;

            log.debug("Command:  " + cmd);


            try {

                res  = camelSenderSynch(cmd);
                log.debug("Camel invocation: " + cmd + "=" + res);

            } catch (Throwable ee) {

                ee.printStackTrace();
                ilog("err.txt", cmd + "  Real execute exeJobFin:" + exeJobFin);
                res = "Command Excution failed:"+ee.getMessage();

            }

            return res;

        }

    }
    
    class XNode implements Comparable {
        
        String host;
        int port;

        @Override
        public int compareTo(Object object) {
            XNode n = (XNode) object;
            String h  = n.getHost();
            int p = n.getPort();

            if (p == port && host.equals(h))  return 0;          

            return 1;
        }

        public XNode(String host, int port) {
               super();
               this.host = host;
               this.port = port;
        }


        public String getHost() {
               return host;   
        }

        public int getPort() {
               return port;   
        }

        @Override
        public int hashCode() {
            String xs = host+":"+port;
            return xs.hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object != null && object instanceof XNode) {  
            XNode x = (XNode) object;
            if (x.getHost().equals(host) && x.getPort() == port) return true; 
            }
            
            return false;
        }

    }
    
    class XNetDiscoverer {


        List <XNode> nodes = new ArrayList <XNode> ();

        public List <XNode> discoverXNodes () {       
            
            
         
            
            XNode x1 = new XNode ("127.0.0.1", p1);
            XNode x2 = new XNode ("127.0.0.1", p2);
            XNode x3 = new XNode ("127.0.0.1", p3);
            nodes.add (x1);
            nodes.add (x2);
            nodes.add (x3);
            return nodes;



        }
    
    
        public void start () {
            
               ExecutorService exec = Executors.newSingleThreadExecutor();
               Future s = exec.submit(new Runnable(){
               
               public void run () {
                   
                   
                   while (true) {
                    
                       try {
                           
                         Thread.currentThread().sleep(_DELAY);
                         List <XNode> nodes = discoverXNodes ();                          
                         reConfigRoutes (nodes);                 
                       
                       } catch (InterruptedException ee) {
                           
                         ee.printStackTrace();  
                           
                       }
                       
                       
                   }
                   
                   
                                                             
               }
                   
               });
            
            
            
            
            
            
        }
        
        
        
    }
    
    
    
    

}
