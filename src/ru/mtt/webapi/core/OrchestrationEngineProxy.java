package ru.mtt.webapi.core;

import java.util.logging.Logger;


/**
 * Unified Business process orchestration engine based upon Apache ServiceMix
 * and BPEL - process descriptors
 * 
 * @author rnasibullin@mtt.ru Ghief
 */

public class OrchestrationEngineProxy extends XConfigurableObject implements IChainProcedure {
    
    
    org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OrchestrationEngineProxy.class);
    
    
    public OrchestrationEngineProxy() {
           super();
    }


    public void init() {
           
    }

    public void stop() {
           
    }
   

    @Override
    public void doConfig() {
           
    }

    public long asynchProcess(String stereotype, String[] params) throws WAPIException  {
        
        
           long id  =  0L;
           return id;
           
        
    }
    
    public Object execute (String act, String[] params, Object evIN) {
    
           log.info ("Test: "+act);
           return evIN;
           
    }
 
    
    
    public XSmartObject synchProcess(String stereotype, String[] params) throws WAPIException  {
        
           XSmartObject res = null;
           return res;
           
        
    }

    public XSmartObject getProcessResult(long processId) throws WAPIException {
        
           XSmartObject res = null;
           return res;
           
        
    }
    
    
}
