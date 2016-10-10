package ru.mtt.webapi.memcache.test;

import java.util.ArrayList;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dom.Route;
import ru.mtt.webapi.dom.SimpleXSmartObject;
import ru.mtt.webapi.memcache.RoutingTable;
import ru.mtt.webapi.memcache.SystemCache;

public class Producer {
   
   
    SystemCache s = null;
    
    
    public Producer(SystemCache sc) {
           super();
           s = sc;
    }
    
    
    public void start() {
        
        int j = 0;
        
        for (;;) {
              
        j++;
            
        if (j > 100) break;
            
            XSmartObject xs = new SimpleXSmartObject("J_"+j,"Test"); 
            s.insertOrReplace(xs);
            
            try {    
                     Thread.currentThread().sleep(1000);    
            } catch (InterruptedException ee) {
                
            }
        }
        
    }

    public void start2() {
        
        long j = 0L;
        ArrayList<XSmartObject> ls = new ArrayList<XSmartObject> ();
        RoutingTable r = new RoutingTable(ls);
        s.insertOrReplace(r);
        
        for (;;) {
              
        j++;
            
        if (j > 100) break;
            
            Route xs = new Route(); 
            xs.setId(j);
            r.add(xs);
            s.insertOrReplace(r);
            s.insertOrReplace(xs);
            
            try {    
                     Thread.currentThread().sleep(1000);    
            } catch (InterruptedException ee) {
                
            }
        }
        
    }

    
    public static void main (String[] a) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/webapibeans.xml");
        context.start();
        SystemCache s = (SystemCache)context.getBean("SystemCache");
        
        Producer x = new Producer (s);
        x.start2 ();
        context.close();

  
    }
    
    
    
    
}
