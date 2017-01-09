package ru.mtt.webapi.dispatcher;

import java.awt.Color;

import java.io.Writer;
import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.JMX;

import org.apache.log4j.Logger;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.api.HttpRequest;

import ru.mtt.rservice.core.Quantil;
import ru.mtt.rservice.core.ServiceStatisticsAcqusition;
import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.XBUSConnector;
import ru.mtt.webapi.mina.HTTPResponseWrapper;
import ru.mtt.webapi.mina.MinaRequestHandler;
import ru.mtt.webapi.utils.XUtils;

/**
 * Multythreaded WebApi calls dispatcher with buffered request processing services
 *
 * @author rnasibullin@mtt.ru
 */
public class WebApiDispatcher implements MinaRequestHandler, WebApiDispatcherMBean, IConstants
{
    
    public static boolean _StartControlling = false;
    AtomicBoolean started = new AtomicBoolean(false);
    AtomicBoolean locked = new AtomicBoolean(false);
    private volatile long lastRequestTime;
    private volatile long maxRequestTime;
    public static int instanceCount;
    Logger log = Logger.getLogger(WebApiDispatcher.class);
    public static final ServiceStatisticsAcqusition sc = new  ServiceStatisticsAcqusition ();
    
    
    static {
           sc.start(); 
    }

    @Override
    public List<Quantil> getTrend(String meth) {
           return sc.getTrend(meth);
    }

    public void startControlling() {
           _StartControlling = true;
    }


    public double getAvailabilityFactor(String serv) {
           ConcurrentHashMap<String, Double>  v = sc.getServices();
           return v.getOrDefault(serv, 0.0);
    };


    public void registerEvent(String methName, long dur, long timeev, double af)  {
           XUtils.ilog ("log/webapidispatcher.log", "info: " +methName + "/" + dur + "/" + timeev + "/" + af);
           sc.registerEvent(methName,dur, timeev, af);  
    }


    public Set<String> getServices() {
        
           HashSet <String> xs = new HashSet <String>();
           Map<String, Double> vs = sc.getServices();
           Set<Map.Entry<String, Double>> entries = vs.entrySet();
           for (Map.Entry<String, Double> x: entries) {
                double v = x.getValue();
                if (v*this.getAvailabilityFactor()> _AVFACTOR_THRESHOLD) {
                xs.add (x.getKey());   
                }
           }
           return xs;
    
    };  


    public String[] getMetricsValueTrendDataGrammas(String metricName, String mapiMethodName, Long key) {

        ArrayList<String> arr = new ArrayList<String>();

        try {   
            
           List<Quantil>  ts = sc.getTrend (mapiMethodName);
           log.debug("Trend  "+mapiMethodName +" - " + ts); 
           if (ts != null) {
           log.debug(metricName+"   Trend  "+mapiMethodName +" - " + ts.size()+" - "+key); 
           for (Quantil t:  ts) {
               
               log.debug(metricName+ " Trend  Key: " + mapiMethodName + " - " + t.getTs()+" : " + key); 
        
               if (t.getTs()>=key) {
                   
                   Long tts = t.getTs();         
                   Double val = t.getValue(metricName);
                   arr.add (tts + " : " + val);
                   log.debug(metricName+"  Trend  inn list  " + mapiMethodName + " - " + tts+" :  " + val); 
                   
               }
          
           }
           }
            
        } catch (Throwable ee) {
            
          ee.printStackTrace();  
            
        }
           
        return arr.toArray(new String[arr.size()]);
    
    }

    public WebApiDispatcher() {
           super();
        
           String portX = System.getProperty("com.sun.management.jmxremote.port");
        
           if (portX != null) {
           try {
           log.debug ("Create Dispatcher bean");    
           MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
           ObjectName name = new ObjectName("ru.mtt.webapi.dispatcher:type=WebApiDispatcher");
           mbs.registerMBean(this, name);
           } catch (Throwable e) {
           e.printStackTrace();
           }
           }

           started.getAndSet(true);
    }
    
    @Override
    public Set<String> getMethods() {
        
           return sc.getMethods();
        
    }
 
    
    @Override
    public void stop () {
           started.set(false);
    }


    @Override
    public HTTPResponseWrapper acceptRequest(IoSession sess, HttpRequest msg) {
        Writer pw = new java.io.StringWriter();
        String contentType = "application/json;charset=utf-8";
        return new HTTPResponseWrapper(pw.toString(),"utf-8", contentType);
    }

    @Override
    public HTTPResponseWrapper acceptRequest(IoSession sess, String msgDecoded) {

        Writer pw = new java.io.StringWriter();
        String contentType = "application/json;charset=utf-8";
        String sessId = String.valueOf(sess.getId());
 
        try {

        if (!started.get()) {

  
                pw.write("{'status':'ServiceNotAvailable'}");

  
        } else {

        XBUSConnector xConn =  XBUSConnector.getInstance();
  //      Long iTX = xConn.beginTX();
        String cmd = ""; 
            
        if (msgDecoded != null && msgDecoded.trim().length()>0) {
            cmd = msgDecoded;    
        }
            
        String response = xConn.singleCommand(cmd);
            
        pw.write(response);
 //       xConn.commitTX(iTX);
        }
            
        pw.close();
        pw.flush();

        } catch (Throwable ee) {
        
        ee.printStackTrace();
            
        }

        return new HTTPResponseWrapper(pw.toString(),"utf-8", contentType);


    };


    @Override
    public boolean isAvailable() {

           return started.get();

    };

    @Override
    public boolean isServiceLocked() {

           return locked.get();
    
    }


    @Override
    public void start() {

        String portX = System.getProperty("com.sun.management.jmxremote.port");
        
        if (portX == null) {


        try {
            
            MBeanServer myMBeanServer = MBeanServerFactory.createMBeanServer();
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:7703/server");
            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, myMBeanServer);
            cs.start();
            ObjectName name = new ObjectName("ru.mtt.webapi.dispatcher:type=WebApiDispatcher");
            myMBeanServer.registerMBean(this, name);
            log.debug ("Connect to server: " + name);

        } catch (Throwable ee) {

            log.info ("Can not connect to server: " + ee.getMessage());
            ee.printStackTrace();

        }
        }

        started.getAndSet(true);
        log.info ("Dispatcher started:  "+started);


    }


    @Override
    public long getLastRequestTime() {
        return lastRequestTime;
    }


    @Override
    public long getMaxRequestTime() {
        return maxRequestTime;
    }


    @Override
    public void resetMaxRequestTime() {
        maxRequestTime = 0;
    }


    @Override
    public double getMetricValue(String metricName, String mapiMethodName) {
        
        
        double V = 0.0;
        List<Quantil>  ts = sc.getTrend (mapiMethodName);

        if (ts != null) {        
        if (ts.size()>0) {
        Quantil q  = ts.get(ts.size()-1);
        switch (metricName) {   

        case _FREQ:    
            V = q.getFreq();
        break;

        case _RESPT:
            V = q.getMO();
        break;
        
        }
        }
        }

        return V;
        
    }

     
    @Override
    public void  registerService(String servName, double avFactor) {
           sc.registerService(servName, avFactor); 
    
    }; 
    

    @Override
    public double getAvailabilityFactor() {
        double V = 0.0;
        
        if (!isAvailable()) {
        return 0.01;
        } else {
        int nproc = Runtime.getRuntime().availableProcessors();
        long fmem = Runtime.getRuntime().freeMemory();
        long mmem = Runtime.getRuntime().maxMemory();
        V = nproc*(fmem/(mmem*1.0));
        } 
        
        log.debug("Test AF: " +V);
        
        return V;
    }

    public Double getAvailability(String service) {
    
           return  sc.getAvailability(service);
    
    }; 




    public static void main2 (String [] a) {


        try {

            ObjectName name = new ObjectName ("ru.mtt.webapi.dispatcher:type=WebApiDispatcher" );
            JMXServiceURL url = new JMXServiceURL ("service:jmx:rmi:///jndi/rmi://127.0.0.1:7703/jmxrmi");
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            WebApiDispatcherMBean mxbeanProxy = JMX.newMXBeanProxy(mbsc, name, WebApiDispatcherMBean.class);
            mxbeanProxy.resetMaxRequestTime();
            System.out.println (mxbeanProxy.getMetricValue("MRT", "getUidBySip"));

        } catch (Throwable ee) {
            ee.printStackTrace();
        }


    }



}
