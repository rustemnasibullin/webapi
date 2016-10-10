package ru.mtt.webapi.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import ru.mtt.rservice.core.MAPIServiceFarmHandler;
import ru.mtt.rservice.core.MIBControlObject;

/**
 * SNMP TRAP priocessing workflow Route builder
 * 
 * @author rnasibullin@mtt.ru
 */

public class XRouteBuilder  extends RouteBuilder {
       
       static MIBControlObject  co = new MIBControlObject ();
    
    
           @Override
           public void configure() throws Exception {
                from("snmp:0.0.0.0:162?protocol=udp&type=TRAP").process(
                new Processor() {
                        public void process(Exchange exchange) throws Exception {
                        Object in = exchange.getIn();
                        log.info ("Information:   "+in);
                        co.parse(in);
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
                    }
                });

           }
           
           
           public XRouteBuilder() {
                  super();
           }
}
