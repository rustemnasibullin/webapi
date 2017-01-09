package ru.mtt.webapi.utils;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import org.springframework.jms.core.MessageCreator;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import javax.jms.TextMessage;


import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;

import ru.mtt.webapi.core.XSmartObject;

public class MQClient {
    
    @Autowired
    org.springframework.jms.core.JmsTemplate template = null;
    String queue;
    int messageCount = 0;




    public void init () throws JMSException {
      ActiveMQConnectionFactory factory=new ActiveMQConnectionFactory("vm://testXA");
      factory.setWatchTopicAdvisories(false);
      Connection connection=factory.createConnection();
      connection.start();
      Session session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
      MessageProducer producer=session.createProducer(new ActiveMQQueue("scp_transacted"));
      TextMessage message=session.createTextMessage("Some Text, messageCount:" + messageCount++);
      message.setJMSCorrelationID("pleaseCorrelate");
      producer.send(message);
      connection.close();

    }
    
    
    public MQClient() {
           super();
    }


    public void setDestination(String queue) {
           this.queue = queue;
    }


    public String getDestination() {
           return queue;
    }


    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public JmsTemplate getTemplate() {
        return template;
    }

    public Object sendMessage (final Object objMessage, boolean withCorrId) {
           long cId = 0L;
           
           XUtils.ilog("log/mqclient.log","Test: "+objMessage);
           try {
           template.send(new MessageCreator() {
           public Message createMessage(Session session) throws JMSException {
                  return session.createTextMessage(objMessage.toString());
           }
        });
               
           } catch (Throwable ee) {
               XUtils.ilog("log/mqclient.log", XUtils.info (ee));
               return null;
           }
           
           
           
        return cId;
    }
    
    
    public static void main (String[] a) {
    
    
    
    } 
    
}
