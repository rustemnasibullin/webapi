package ru.mtt.webapi.camel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.Service;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.spi.LifecycleStrategy;
import org.apache.camel.spi.RouteContext;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.aspectj.ConfigurableObject;

import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.utils.XUtils;

public class CamelLifecycleStrategy extends XConfigurableObject  implements LifecycleStrategy {

    Set<String> camelAvailable =  new HashSet<String>();    
    Logger log = Logger.getLogger(CamelLifecycleStrategy.class);

    
    public CamelLifecycleStrategy() {
        super();
    }

    public void setCamelContext (CamelContext context) throws Exception { 
        
        
           XUtils.ilog("log/camel2.log",context.getName()+"  Camel key:  "+camelAvailable); 
           context.addLifecycleStrategy(this); 
                
    }

    @Override
    public void doConfig() {
            
           String cntxs = this.getConfigParameter("camel."+_POSTFIX);    // TODO Implement this method
           if (cntxs != null) { 
           String [] lst = cntxs.split(":");
           for (String x: lst) { 
                camelAvailable.add(x);
           }
           }
        
    }

    @Override
    public void onComponentAdd(String string, Component component) {
        // TODO Implement this method
    }

    @Override
    public void onContextStart(CamelContext camelContext) throws VetoCamelContextStartException {
        XUtils.ilog("log/camel2.log","Camel key:  "+camelContext.getName()+" / "+camelAvailable); 
             
        

        if (!camelAvailable.contains(camelContext.getName())) {
            
            XUtils.ilog("log/camel2.log", "ContextName:   "+camelContext.getName()+" - "+camelContext.isStartingRoutes());
            List<Route> routes = camelContext.getRoutes();
            XUtils.ilog("log/camel2.log", "routes --->>> "+routes.size()); 
            
           // throw new VetoCamelContextStartException("Not Allowed",camelContext); 
            
            
        }
    }

    @Override
    public void onComponentRemove(String string, Component component) {
        // TODO Implement this method
    }

    @Override
    public void onContextStop(CamelContext camelContext) {
        // TODO Implement this method
    }

    @Override
    public void onEndpointAdd(Endpoint endpoint) {
        // TODO Implement this method
    }

    @Override
    public void onEndpointRemove(Endpoint endpoint) {
        // TODO Implement this method
    }

    @Override
    public void onErrorHandlerAdd(RouteContext routeContext, Processor processor,
                                  ErrorHandlerFactory errorHandlerFactory) {
        // TODO Implement this method
    }

    @Override
    public void onErrorHandlerRemove(RouteContext routeContext, Processor processor,
                                     ErrorHandlerFactory errorHandlerFactory) {
        // TODO Implement this method
    }

    @Override
    public void onRouteContextCreate(RouteContext routeContext) {
        // TODO Implement this method
    }

    @Override
    public void onRoutesAdd(Collection<Route> collection) {
        // TODO Implement this method
    }

    @Override
    public void onRoutesRemove(Collection<Route> collection) {
        // TODO Implement this method
    }

    @Override
    public void onServiceAdd(CamelContext camelContext, Service service, Route route) {
        // TODO Implement this method
    }

    @Override
    public void onServiceRemove(CamelContext camelContext, Service service, Route route) {
        // TODO Implement this method
    }

    @Override
    public void onThreadPoolAdd(CamelContext camelContext, ThreadPoolExecutor threadPoolExecutor, String string,
                                String string2, String string3, String string4) {
        // TODO Implement this method
    }

    @Override
    public void onThreadPoolRemove(CamelContext camelContext, ThreadPoolExecutor threadPoolExecutor) {
        // TODO Implement this method
    }


}
