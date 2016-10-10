package ru.mtt.webapi.utils;


import java.io.IOException;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 *  SNMP utility 
 *
 *  @author rnasibullin@mtt.ru
 */

public class SNMPUtils {
    
    static Logger log = Logger.getRootLogger();
    
    
    public class snmpt extends Thread{
    /*Создаем поток, который будет слушать нужный порт.*/        
            private Snmp snmp = null;
            private Address targetAddress = GenericAddress.parse("udp:127.0.0.1/161");
            private Address targetAddress2 = GenericAddress.parse("udp:127.0.0.1/162");
            private TransportMapping transport = null;      
            
    /*В конструкторе устанавливаем слушателя SNMP протокола. При получении трапа просто сообщаем об этом в консоль.*/       
            public snmpt() throws Exception{
            transport = new DefaultUdpTransportMapping();
                snmp = new Snmp(transport);
            
                    CommandResponder trapPrinter = new CommandResponder() {
                            public synchronized void processPdu(CommandResponderEvent e){
                                    PDU command = e.getPDU();
                                    /*Здесь могли бы быть ваши действия (т.1234567890).
                                      Внутри класса PDU реализован полный функционал для анализа SNMP трапа.*/
                                    if (command != null) {
                                            System.out.println("Получен трап: " +command.toString());
                                    }
                            }
                    };
                    snmp.addNotificationListener(targetAddress, trapPrinter);
            }        
            
    /*Метод для посылки трапов. Посылаем пустой трап по адресу targetAddress.*/     
            public void send(){
                
                   send(null);
            
            }

            public void send(String value){
                    // setting up target
                    CommunityTarget c_target = new CommunityTarget();
                    c_target.setCommunity(new OctetString("public"));
                    c_target.setAddress(targetAddress2);
                    c_target.setRetries(2);
                    c_target.setTimeout(1500);
                    c_target.setVersion( SnmpConstants.version2c);  

                    PDU pdu = new PDU();
                    pdu.setType(PDU.INFORM);
                    pdu.add(new VariableBinding(SnmpConstants.snmpTrapEnterprise));
                    pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress));

                    try {
                            snmp.send(pdu, c_target, transport);
                    } catch (Throwable ex2) {
                            ex2.printStackTrace();
                    }
            }       
            
    /*Создаем поток, запускаем, посылаем тестовый трап.*/  
                                                                                    
                                                                                    
            
    /*Сам поток спит. От него лишь требуется держать в памяти наш обработчик trapPrinter*/  
    public void run() {
                    while(true)
                    try{
                            this.sleep(1000000);                    
                    }catch (Exception e) {
                            e.printStackTrace();
                    }                       
            }
    }

    public void start(String[] args) throws Exception{
        
       snmpt s = new snmpt();
  //     s.start();
       s.send();
       
    }


    public void start2(String[] args) throws Exception{
        
           Enumeration enm =  log.getAllAppenders(); 
           System.out.println (log.getAppender("TRAP_LOG"));
    
           while (enm.hasMoreElements()) {
                  Appender nel = (Appender) enm.nextElement();
                  System.out.println (nel.getName()+"  -  "+  nel.getClass().getName());
           }
           log.warn("start");
           
    }

  
    public SNMPUtils() {
        super();
    }
    
    
    public static void main (String [] x) {
        
        
           try {
               new SNMPUtils().start2(x);
           } catch (Exception ee) {
               ee.printStackTrace();
           }
        
    }
    
}
