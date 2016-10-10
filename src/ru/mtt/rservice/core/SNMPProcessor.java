package ru.mtt.rservice.core;


/**
 *  SNMP Message processing bean
 * 
 *  @author rnasibullin@mtt.ru  Chief 
 */


public class SNMPProcessor {
    
    static final MIBControlObject  co = new MIBControlObject ();

    
    public SNMPProcessor() {
        super();
    }
    
    public Object process(Object o) {
        
           co.parse(o);
           int evId = co.getEventId();
           switch (evId) {
           case MIBControlObject._SERVICESTARTED:
               MAPIServiceFarmHandler.getInstance().startScanService(co.getOID(), co.getJmxHost(), co.getJmxPort());
           break;    
           case MIBControlObject._SERVICESTOPED:
               MAPIServiceFarmHandler.getInstance().stopScanService(co.getOID());
           break;    
           case MIBControlObject._SERVICEINTROUBLE:
               MAPIServiceFarmHandler.getInstance().stopScanService(co.getOID());
           break;    
           case MIBControlObject._SERVICERELEIVED:
               MAPIServiceFarmHandler.getInstance().startScanService(co.getOID(), co.getJmxHost(), co.getJmxPort());
           break;    
           }

           return null;
        
    }
    
    
}
