package ru.mtt.rservice.core;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import ru.mtt.webapi.core.IMemCache;

/**
 *  ServiceFarn handler - control distributed services for monitoring and acquisition statistics
 * 
 *  @author rnasibullin@mtt.ru  Chief 
 */

public class MAPIServiceFarmHandler {
    
    static MAPIServiceFarmHandler instance = null;
    ConcurrentHashMap <String, MAPIServiceHandler> mapis = new ConcurrentHashMap <String, MAPIServiceHandler>();
    ConcurrentHashMap <String, MAPIServiceHandler> handlers = new ConcurrentHashMap <String, MAPIServiceHandler>();
    IMemCache cache;
    
    public void stopScanService(String OID) {
        
           MAPIServiceHandler MS = mapis.remove(OID);
           MS.setEnabled (false);
           
    };

    public ConcurrentHashMap<String, MAPIServiceHandler> getMapis() {
        return mapis;
    }

    public void setCache(IMemCache cache) {
        this.cache = cache;
    }

    public IMemCache getCache() {
        return cache;
    }

    public void startScanService(String OID, String jmxHost, int jmxPort) {
        
           MAPIServiceHandler MS = handlers.get (jmxHost+":"+jmxPort);
           if (MS==null) {
               MS = new MAPIServiceHandler(); 
               MS.setHost(jmxHost);
               MS.setPort(jmxPort);
               handlers.put (jmxHost+":"+jmxPort, MS);
           }
           MS.setEnabled (true);
           mapis.put (OID, MS);
           
    };
    
    public void actualizeService(String OID, double avFactor) {
    };
    
    
    public synchronized static MAPIServiceFarmHandler getInstance() {
           if (instance == null) instance = new MAPIServiceFarmHandler();
           return instance; 
    }
    
    public MAPIServiceHandler getMAPIServiceHandler(String h) {
           return  mapis.get (h);  
    };

    
    private MAPIServiceFarmHandler() {
        
        super();
    
    }
    
    public void uploadActiveStatistics() {
        
           Enumeration <MAPIServiceHandler> enm = mapis.elements();
           while (enm.hasMoreElements()) {
               
                  MAPIServiceHandler srv = enm.nextElement();
                  if (srv.isEnabled()) {
                      srv.retreiveData(); 
                  }  
               
           }
    
    };
    
    
}
