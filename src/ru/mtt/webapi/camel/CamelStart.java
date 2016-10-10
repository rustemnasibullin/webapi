package ru.mtt.webapi.camel;

import java.io.InputStream;

import java.util.HashSet;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.Main;
import org.apache.log4j.Logger;

import java.util.List;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.Endpoint;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;

import org.junit.runner.Runner;

import ru.mtt.webapi.core.IConfigurableObject;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.utils.XUtils;

/**
 *
 *  Camel context starter-class
 *
 *  @author rnasibullin@mtt.ru
 */
public class CamelStart extends Main implements IConfigurableObject {

    Logger log = Logger.getLogger (CamelStart.class);
    Thread rr  = null;
    Thread rc  = null;
    ConcurrentHashMap <String, ProducerTemplate> tmpl = new ConcurrentHashMap <String, ProducerTemplate> ();
    protected String config = null;
    protected Properties ps = new Properties();
    Set<String> camelAvailable =  new HashSet<String>();    


    public CamelStart() {

           super();      
        
    }


    public ProducerTemplate discoverEndPoint (String epq) {
       
           ProducerTemplate p = null;
           p = tmpl.get (epq);
           
           if (p == null) {
           List<CamelContext> cc = this.getCamelContexts();

           for (CamelContext v: cc) {
                Endpoint ps = v.getEndpoint(epq);
                if (ps != null)  {
                p = v.createProducerTemplate();
                p.setDefaultEndpoint(ps);
                tmpl.put (epq, p);
                return p; 
                }
           }
           
           }


           return p;
    
    };


    public void setConfig(String config) {
        this.config = config;
        try {

            InputStream sx = this.getClass().getClassLoader().getResourceAsStream(config);
            ps.load (sx);
            doConfig();

        } catch (Throwable ee) {
            ee.printStackTrace();
        }
    }

    /**
    * Apply sonfiguration
    */
    public void doConfig() {
        
        String cntxs = this.getConfigParameter("camel."+XConfigurableObject._POSTFIX);    // TODO Implement this method
        XUtils.ilog("log/camel0.log", "camel."+XConfigurableObject._POSTFIX+"="+cntxs); 

        if (cntxs != null) { 
        String [] lst = cntxs.split(":");
        for (String x: lst) { 
             camelAvailable.add(x);
        }
        }
        
        XUtils.ilog("log/camel0.log", "camelAvailable: "+camelAvailable); 

        
    };

    public void reConfig () {        
           List<CamelContext> cc = this.getCamelContexts();
           XUtils.ilog ("log/camelstart.log", CamelStart.this.isStarting()+"/"+ CamelStart.this.isStarted());

           for (CamelContext camelContext: cc) {
               
               List<Route> routes = camelContext.getRoutes();
               if (!camelAvailable.contains(camelContext.getName())) {
                   
                   XUtils.ilog("log/camelstart.log", "ContextName:   "+camelContext.getName()+" - "+camelContext.isStartingRoutes());
                   XUtils.ilog("log/camelstart.log", "routes : " + routes.size() + " - " + camelAvailable); 
                   try {
                     
                     for (Route r: routes) {
                     
                         XUtils.ilog("log/camelstart.log", "Route.suspend(): "+r.getId()); 
                         camelContext.suspendRoute(r.getId());
                     
                     }
                     
                     camelContext.suspend();
                     XUtils.ilog("log/camelstart.log", "camelContext.suspend(): "+camelContext.getName()); 
                                         
                   } catch (Exception ee) {
                     XUtils.ilog("log/camelstart.log", "camelContext.suspend(): "+XUtils.info (ee)); 
                   }
                   
                   // throw new VetoCamelContextStartException("Not Allowed",camelContext); 
                   
               } else {
                   
                   try {

                   if (!camelContext.isAutoStartup()) camelContext.startAllRoutes();  
                   
                   } catch (Exception ee) {
                     XUtils.ilog("log/camelstart.log", "camelContext.suspend(): "+XUtils.info (ee)); 
                   }
                   
                   
               }

               
               
           }
    
    };


    public boolean isOnActive () {        
           ServiceStatus ts = this.getStatus();
           List<CamelContext> cc = this.getCamelContexts();
           boolean x = false;  

           XUtils.ilog("log/camelstart.log", "Contexts:   "+cc.size()+" - "+ts);

           for (CamelContext camelContext: cc) {
               
                   XUtils.ilog("log/camelstart.log", "ContextName:   "+camelContext.getName()+" - "+camelContext.isStartingRoutes());
                   List<Route> routes = camelContext.getRoutes();
                   XUtils.ilog("log/camelstart.log", "routes : " + routes.size() + " - " + camelAvailable); 
                   XUtils.ilog("log/camelstart.log", "Route.suspend(): "+
                                                     camelContext.isSetupRoutes()+"/"+camelContext.isStartingRoutes()+"/"+camelContext.getStatus());
                   
           }

           return x;                
               
    
    };

    

    @Override
    public String getConfigParameter(String paraName) {
        return ps.getProperty(paraName);
    }


    @Override
    public int getIntConfigParameter(String paraName) {
        
        int v = -1;
        try {
            
            v = Integer.parseInt(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }

    @Override
    public double getDoubleConfigParameter(String paraName) {
        
        double v = 0.0;
        try {
            
            v = Double.parseDouble(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }

    @Override
    public boolean getBoolConfigParameter(String paraName) {
        
        boolean v = false;
        try {
            
            v = Boolean.parseBoolean(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }

    @Override
    public long getLongConfigParameter(String paraName) {
        
        long v = -1;
        try {
            
            v = Long.parseLong(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }

    

    public void activate () {

           List<CamelContext> cc = this.getCamelContexts();

           for (CamelContext v: cc) {
                v.setApplicationContextClassLoader(this.getClass().getClassLoader());
           }

           Runner r = new Runner();
           RunController c = new RunController();
           rr = new Thread(r);
           rr.start();
           rc = new Thread(c);
           rc.start();
           log.info("Camel started1");

       }


       public void deactivate () {
              try {
                 if (rr != null) rr.interrupt();
                 this.stop();
                 rr = null;
              } catch (Throwable ee) {
                 ee.printStackTrace();
              }
       }


    class Runner implements Runnable {


        public void run () {
            
            try {
                
                log.info("Camel started2");
                CamelStart.this.start();
                log.info("Camel started3");
                
                
                
            } catch (Throwable ee) {
                ee.printStackTrace();
            }

        }


    }

    class RunController implements Runnable {


        public void run () {
            
            try {
                
                for (;;) {

                     XUtils.ilog ("log/camelstart.log", CamelStart.this.isStarting()+" | "+ CamelStart.this.isStarted());


                     if (CamelStart.this.isStarted()) {    
                         reConfig ();
                         break;    
                     } else {
                         XUtils.ilog ("log/camelstart.log"," --> "+ CamelStart.this.isStarted());
                         isOnActive ();
                     }

                     Thread.currentThread().sleep(200);


                }
                
            } catch (Throwable ee) {
                ee.printStackTrace();
            }

        }


    }


}
