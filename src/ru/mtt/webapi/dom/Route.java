package ru.mtt.webapi.dom;

import java.io.Serializable;

import java.sql.ResultSet;
import java.sql.SQLException;

import ru.mtt.webapi.core.IJDBCPopulable;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.utils.XUtils;

/**
 * Route description from shards map
 *
 * @author rnasibullin@mtt.ru
 */

public class Route extends XSmartObject  {

    Long id;
    String type;
    String to;
    String server_id;
    String cache_instance_id;
    Short spot_id;      
    String from;

    @Override
    public Object getFieldByName(String name) throws WAPIException {
     

        if (name.equals("ID")) {
           return id;
        } else if (name.equals("id")) {
           return id;
        } else if (name.equals("type")) {
           return type;
        } else if (name.equals("to")) {
            return to;
        } else if (name.equals("server_id")) {
            return server_id;
        } else if (name.equals("cache_instance_id")) {
            return cache_instance_id;
        } else if (name.equals("spot_id")) {  
            return spot_id;
        } else if (name.equals("from")) {
            return from;
        } else {
          throw new WAPIException("Field with name not defined: "+name);  
        }
        
    }

    @Override
    public void setFieldByName(String name, Object value) throws WAPIException {
        
        if (name.equals("ID")) {
           id = (Long) value;
        } else if (name.equals("id")) {
           id = (Long) value;
        } else if (name.equals("type")) {
           type = (String) value;
        } else if (name.equals("to")) {
           to = (String) value;
        } else if (name.equals("server_id")) {
           server_id = (String) value;
        } else if (name.equals("cache_instance_id")) {
           cache_instance_id = (String) value;
        } else if (name.equals("spot_id")) {  
           spot_id = (Short) value;
        } else if (name.equals("from")) {
           from = (String) value;
        } else {
          throw new WAPIException("Field with name not defined: "+name);  
        }
        

    }

    public Route() {
               super();
        }
    
        
        public Route(Long id, String type, String from, String to, String server_id, String cache_instance_id,
                     Short spot_id) {
            super();
            this.id = id;
            this.type = type;
            this.from = from;
            this.to = to;
            this.server_id = server_id;
            this.cache_instance_id = cache_instance_id;
            this.spot_id = spot_id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getFrom() {
            return from;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getTo() {
            return to;
        }

        public void setServer_id(String server_id) {
            this.server_id = server_id;
        }

        public String getServer_id() {
            return server_id;
        }

        public void setCache_instance_id(String cache_instance_id) {
            this.cache_instance_id = cache_instance_id;
        }

        public String getCache_instance_id() {
            return cache_instance_id;
        }

        public void setSpot_id(Short spot_id) {
            this.spot_id = spot_id;
        }

        public Short getSpot_id() {
            return spot_id;
        }


    @Override
    public String toJSONString() {
        String xs =    "{"+XUtils.toJSONPair("id",id)+","
                             +XUtils.toJSONPair("type",type)+","
                             +XUtils.toJSONPair("to",to)+","
                             +XUtils.toJSONPair("server_id",server_id)+","
                             +XUtils.toJSONPair("cache_instance_id",cache_instance_id)+","
                             +XUtils.toJSONPair("spot_id",spot_id)+","
                             +XUtils.toJSONPair("from",from)+
                             "}";
       return xs;
    }


    @Override
    public boolean equals(Object obj) {
         
           boolean r = false; 
           if (obj != null && obj instanceof Route) {
               Route x = (Route) obj;
               Long ids = x.getId();
               if (ids.longValue()==id.longValue()) {
                   r = true;
               }
           }

           return r;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


}
