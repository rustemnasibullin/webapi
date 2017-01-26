package ru.mtt.rservice.core;

import java.io.InputStream;

import java.util.Properties;

import org.apache.log4j.Logger;

import ru.mtt.webapi.core.WebApiContextsLauncher;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.dispatcher.WebApiDispatcherMBean;
import ru.mtt.webapi.utils.Curl;
import ru.mtt.webapi.utils.XUtils;

public class SystemLogger extends Properties {
    
    public final static SystemLogger instance = new SystemLogger  ();
    org.apache.log4j.Logger rlogger = Logger.getRootLogger();
    Curl curl = null; 
    String host;
    String port;
    int tick;
    String local;
    Thread hearBeatingThread = null;
    volatile boolean stop_flag = false;


    private SystemLogger() {
        super();
    }
    
    public void start () {
        
           host = this.getProperty("host");
           port = this.getProperty("port");
           tick = Integer.parseInt (this.getProperty("tick"));
           local = this.getProperty("local");
           curl = new Curl();
           curl.setOpt(Curl.CURLOPT_METHOD, Curl.GET);
           
           XUtils.ilog("log/systemlogger.log", "DISPATCHER: "+host+":"+port+":"+tick+":"+local);
           
           hearBeatingThread = new Thread (new Runnable () {
               public void run () {
                   for (;;) {
                        try {  
                          Thread.currentThread().sleep (tick*1000);
                          WebApiDispatcherMBean dispatcher = (WebApiDispatcherMBean) WebApiContextsLauncher.findBean ("WebApiDispatcher");  
                          double avFactor = dispatcher.getAvailabilityFactor(); 
                          if (dispatcher != null) {   
                              if (avFactor<WebApiDispatcherMBean._AVFACTOR_MIN) warn("introuble");
                              else warn("releived");  
                          }
                        } catch (InterruptedException ee) {
                          XUtils.ilog ("log/dispatcher.log",XUtils.info(ee));  
                        }
                        if (stop_flag) break;  
                   }
               }
           });
           
           this.warn("start");
           hearBeatingThread.start();
        
    }
    
    
    public void stop () {
        stop_flag = true;
        this.warn("stop");

    }
    

    public static void configurate () {
        
        try {
        
           InputStream stream = ClassLoader.getSystemResource("dispatcher.properties").openStream();
           XUtils.ilog("log/syslogger.log","dispatcher.properties:"+stream); 

           instance.load(stream); 
           instance.start();
        
        } catch (Throwable x) {
           XUtils.ilog("log/syslogger.log",XUtils.info (x)); 
        }
    }
    
    
    public static SystemLogger getInstance () {
           return instance;        
    }
    
    public void warn (Object data) {
           rlogger.warn(data);
           if (curl != null) {
               curl.setOpt(Curl.CURLOPT_URL, "http://"+host+":"+port+"/warn/"+local+"/"+data);
               curl.execute();
           }
    }

    public void info (Object data) {
           rlogger.info(data);
           if (curl != null) {
               curl.setOpt(Curl.CURLOPT_URL, "http://"+host+":"+port+"/info/"+local+"/"+data);
               curl.execute();
           }
    }
    
    public void error (Object data) {
           rlogger.error(data);
           if (curl != null) {
               curl.setOpt(Curl.CURLOPT_URL, "http://"+host+":"+port+"/error/"+local+"/"+data);
               curl.execute();
           }
    }
    
}
