package ru.mtt.webapi.memcache.test;

import java.util.List;

import org.apache.camel.spring.SpringCamelContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.mtt.webapi.dom.Route;
import ru.mtt.webapi.dom.SimpleXSmartObject;
import ru.mtt.webapi.memcache.RoutingTable;
import ru.mtt.webapi.memcache.SystemCache;
import ru.mtt.webapi.utils.XUtils;

public class Consumer {
    
    SystemCache s = null;
    
    public Consumer(SystemCache sc) {
        super();
        s = sc;
    }
    

    public void start() {
        
        int j = 0;
        
        for (;;) {
              
        j++; 
            
        if (j > 100) break;
            
        List<SimpleXSmartObject> x = s.readAllByClass(SimpleXSmartObject.class);  
        for (SimpleXSmartObject xi: x) {

             XUtils.ilog("prod.log", "xi: "+xi); 
                    
        }
                
        try {    
                         Thread.currentThread().sleep(1000);    
        } catch (InterruptedException ee) {
                    
        }
            
            
        }
        
        
        
    }


    public void start2() {
        
        int j = 0;
        
        for (;;) {
              
        j++; 
            
        if (j > 100) break;
            
        List<Route> x = s.readAllByClass(Route.class);  
        for (Route xi: x) {

             XUtils.ilog("prod.log", "xi: "+xi); 
                    
        }
                
        try {    
                 Thread.currentThread().sleep(2000);    
        } catch (InterruptedException ee) {
                    
        }
            
            
        }
        
        
        
    }

    
    
    public static void main (String[] a) {
           
           ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/webapibeans.xml");
           context.start();
           SystemCache s = (SystemCache)context.getBean("SystemCache");

           Consumer x = new Consumer (s);
           
           x.start2 ();
        context.close();
    }
    
   
    
}
