package ru.mtt.webapi.storage;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import com.datastax.driver.core.SocketOptions;

import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;

import java.util.Date;

import java.util.List;

import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.utils.XUtils;

public class RTStorageDAO implements Runnable {

    public final static  String _INSERT_STMNT     = "INSERT INTO audiorefcluster.rtstorage (id,dateof,datax) VALUES (?,?,?);";
    public final static  String _UPDATE_STMNT = "UPDATE audiorefcluster.rtstorage SET datax=? WHERE id=? and dateof=?;";

    private String cassandraHosts = "172.16.104.31";    
    private Session session      = null;
    private Cluster cluster;
    
    
    public RTStorageDAO() {
        super();
    }
    
    protected Session getSession() { 
            
            if (session == null) session = cluster.connect();
            return session;
            
    }


    
    
    public void connect(String node) {
        
         
        String [] nodes = node.split(",");  
        SocketOptions qOpt = new SocketOptions (); 
        QueryOptions xOpt = new QueryOptions (); 
        xOpt.setFetchSize(100);
        qOpt.setReadTimeoutMillis(6000000);
        qOpt.setKeepAlive(false);
        qOpt.setConnectTimeoutMillis(600000);
        
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL,  5)
                      .setMaxConnectionsPerHost( HostDistance.LOCAL, 100)
                      .setCoreConnectionsPerHost(HostDistance.REMOTE, 2)
                      .setMaxConnectionsPerHost( HostDistance.REMOTE, 1500);
        
         
        if (nodes.length>1) { 
         
           cluster = Cluster.builder().addContactPoint(node).withPort(9042).withSocketOptions(qOpt).withLoadBalancingPolicy(new DCAwareRoundRobinPolicy("US_EAST")).build();

        } else { 
        
           cluster = Cluster.builder().addContactPoint(node).withPort(9042).withSocketOptions(qOpt).build();
        
        }
        

    };
    
    public void init() {

           boolean createKS = false;
           createKS = false;
           connect(cassandraHosts);

           if (createKS) {

           session = cluster.connect();
           session.execute("CREATE KEYSPACE IF NOT EXISTS  audiorefcluster WITH replication " + 
                           "= {'class':'SimpleStrategy', 'replication_factor':1};");
    
           session.execute("DROP TABLE IF EXISTS audiorefcluster.rtstorage;");
           
           session.execute("CREATE TABLE IF NOT EXISTS audiorefcluster.rtstorage (" +
                           "id text," + 
                           "dateof timestamp," + 
                           "datax text," + 
                           "PRIMARY KEY(id,dateof));");
    
                
           }
      
    }

    public void closeSession() {
    
    
        if (session != null) {
            session.close();
            session = null;
        }
    
    
    }; 



    public synchronized boolean registerData(String id, Date d, String data) {
    
    BatchStatement batch = new BatchStatement();
    boolean updateFlag = false;

    try { 
    
            if (updateFlag) {
                PreparedStatement updatePreparedStatement = getSession().prepare(_UPDATE_STMNT);
    //                    batch.add(updatePreparedStatement.bind(dateof,size,receller.intValue(),customer,mediatype,originalPath,newPath,h323_id,tags,status,todsec,uid));
                batch.add(updatePreparedStatement.bind(data,id,d));
            } else {
                PreparedStatement insertPreparedStatement = getSession().prepare(_INSERT_STMNT);
                batch.add(insertPreparedStatement.bind(id,d,data));
                
            }
        
            getSession().execute(batch);
            
        } catch (Throwable ee) {
            
          ee.printStackTrace();  
          XUtils.ilog ("log/dao.log", ee.getMessage()+"\n"+ee.getClass().getName() + "\n" + XUtils.info(ee));  
          return false;   
          
        } finally {
            
            closeSession();
            
        }
            
        return true;   
            
    }
        
  


    
    public void close() {};
    
    String nm;

    public void run () {
        
        int m = 0;    
        for (int i=0; i<600; i++) {
        int n = 0;    
   
            
        m++;    
        System.out.println ("Write: " + n);   
        registerData("X_"+i, new Date (),  "FFEC1D61 EE915233 D786B861 3752AF26");   
            
        System.out.println (nm+" Nx: "+m);
            
        }
        cluster.close();
        System.out.println ("Done. "+nm);
        
    }


    public static void main (String [] arg) {
        
           
  
        
           RTStorageDAO x1 = new RTStorageDAO ();
           x1.nm = "1: ";
           x1.init();
           
           RTStorageDAO x2 = new RTStorageDAO ();
           x2.nm = "2: ";
           x2.init();
    
           RTStorageDAO x3 = new RTStorageDAO ();
           x3.nm = "3: ";
           x3.init();

           Thread t1 = new Thread (x1); 
           Thread t2 = new Thread (x2); 
           Thread t3 = new Thread (x3);
           
           t1.start();
        //   t2.start();
        //   t3.start();
           
           try {
               
           t1.join();
       //    t2.join();
       //    t3.join();
               
           } catch (Throwable ee) {
              ee.printStackTrace(); 
           }
    
           
           


    }
        

    
    
    
}
