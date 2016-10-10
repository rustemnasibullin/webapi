package ru.mtt.webapi.memcache;

import java.io.Serializable;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.mtt.webapi.core.IJDBCPopulable;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XCollection;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dom.Route;

/**
 *Shards RoutingTable description object implementation
 *
 * @author rnasibullin@mtt.ru
 */

    public class RoutingTable extends XCollection implements IJDBCPopulable {
   
    transient HashMap <String, List<Route>> routes = new HashMap <String, List<Route>> ();
   
    public RoutingTable() {
           super();
    }

    
    public RoutingTable(List<XSmartObject> c) {
         
    } 

    
    public int size() {
        
          return routes.size(); 
        
    }

    public void populate(ResultSet p1) throws WAPIException {
        
  
        try {
        
            Long id = p1.getLong("id");
            String type = p1.getString("type");
            String from = p1.getString("from");
            String to = p1.getString("to");
            String server_id = p1.getString("server_id");
            String cache_instance_id = p1.getString("cache_instance_id");
            Short  spot_id = p1.getShort("spot_id");
            
            Route r = new Route(id,type,from,to,server_id,cache_instance_id,spot_id);
            if (col == null) {
                col = new ArrayList<XSmartObject> ();
            }
            
            col.add (r);
            
            
            List<Route> c = routes.get(type);
            if (c==null) {
                c = new ArrayList<Route>();
                routes.put(type, c);
            }
            
            c.add (r);
            System.out.println (c.size()+" + "+col.size()+" - "+type+" : "+from);
            
            
        
        } catch (SQLException ee) {
          
          ee.printStackTrace();
          throw new WAPIException(ee);
            
        }
   
        
    }

    @Override
    public void add(XSmartObject x) {
           
           
           try {
               
           String type = (String) x.getFieldByName("type");
           
           List<Route> c = routes.get(type);
           if (c == null) {
               c = new ArrayList<Route>();
               routes.put(type, c);
           }
           
           this.col.remove(x);    
           c.remove(x);
           this.col.add(x);    
           c.add ((Route) x);  
               
           } catch (WAPIException ee) {
             ee.printStackTrace();  
           }
        
        
    }

    @Override
    public Object getFieldByName(String name) throws WAPIException {
        
           if ("ID".equals(name)) return "cache_routing_table";
           List<Route> rTbl = routes.get(name);
           return rTbl;
           
    }


    @Override
    public void setFieldByName(String name, Object value) throws WAPIException {
        // TODO Implement this method

    }

}
