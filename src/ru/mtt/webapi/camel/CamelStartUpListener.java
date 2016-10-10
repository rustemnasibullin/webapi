package ru.mtt.webapi.camel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.StartupListener;

import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.utils.XUtils;

/**
 *
 *  Partricular Cantext start class-listener
 *
 *  @author rnasibullin@mtt.ru
 */

public class CamelStartUpListener extends XConfigurableObject implements StartupListener  {
    
Set<String> camelAvailable =  new HashSet<String>();    
Logger log = Logger.getLogger(CamelStartUpListener.class);
    
public CamelStartUpListener() {
       super();
}
    
public void onCamelContextStarted (CamelContext context, boolean alreadyStarted) throws Exception { 
      
       log.info ("ContextName:   "+context.getName());

       XUtils.ilog("log/camel.log","Camel key:  camel."+_POSTFIX); 
       XUtils.ilog("log/camel.log","Camel key:  "+camelAvailable); 
 

       if (!camelAvailable.contains(context.getName())) {
           
           XUtils.ilog("log/camel.log", "ContextName:   "+context.getName()+" - "+alreadyStarted+" - "+context.isStartingRoutes()+ " NotActivate !");
           List<Route> rs = context.getRoutes();
           for (Route r: rs) {
               
               XUtils.ilog("log/camel.log", "ContextName:   "+context.getName()+" - "+r.getId());
          
           }
            
         
           //context.stop();
           
       }
       
       
} 

public void setCamelContext (CamelContext context) throws Exception { 
    
       context.addStartupListener (this); 
            
}

@Override
public void doConfig() {
        
       log.info("Camel key:  camel."+_POSTFIX); 
        
       String cntxs = this.getConfigParameter("camel."+_POSTFIX);    // TODO Implement this method
       XUtils.ilog ("log/camel.log", "cntxs:  "+cntxs);
       if (cntxs != null) {
       String [] lst = cntxs.split(":");
       for (String x: lst) { 
            camelAvailable.add(x);
            log.info("Camel key:  camel."+x); 
       }
       }
    
}

}

