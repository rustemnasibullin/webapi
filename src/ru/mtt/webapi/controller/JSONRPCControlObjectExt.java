package ru.mtt.webapi.controller;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Set;

import ru.mtt.webapi.core.XAction;


/**
 *  JSON-RPC Control interface Implementaion for Map of Parameters  
 * 
 *  @author @author RNasibullin@mtt.ru
 */


public class JSONRPCControlObjectExt  implements Serializable, IJSONRPCControlObject {

        @Expose Object error = null;
        @Expose Object result = null;
        @Expose String version = null;
        @Expose String method = null;
        @Expose String id = null;
        @Expose String jsonrpc = "2.0";
        @Expose LinkedHashMap<String,Object> params = new LinkedHashMap<String,Object>();


        public void setResult(Object result) {
            this.result = result;
        }

        public Object getResult() {
            return result;
        }
        
        
        public Object getParamsList() {
            
               return params;
            
        }

        public void setError(Object error) {
            this.error = error;
        }

        public Object getError() {
            return error;
        }


        public void setVersion(String version) {
            this.version = version;
        }

        public String getVersion() {
            return version;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setJsonrpc(String jsonrpc) {
            this.jsonrpc = jsonrpc;
        }

        public String getJsonrpc() {
            return jsonrpc;
        }

        public void setParams(LinkedHashMap<String,Object> params) {
            this.params = params;
        }

        public LinkedHashMap<String,Object> getParams() {
            return params;
        }

        public void complete () {


      

        }
        
        
        public String toString () {
               
                   String errors = null;
                   if (error != null) errors="\""+error+"\"";
                   if (method !=null) { 
            
                     String s = "{'id':'"+id+"','method':'"+method+"','jsonrpc':'"+jsonrpc+"'";
                     if (params != null) {
                           
                           String x = null;
                           Set<Map.Entry <String,Object>> paramset = params.entrySet();
                           for (Map.Entry <String,Object> p:paramset) {
                                if (x != null) x += ",";
                                x = "";
                                x+="'"+p.getKey()+"'="+"'"+p.getValue()+"'";
                           }
                           
                           s+= ",'params':{"+x+"}}";
                     }

                     return s;  
                           
                   } else if (result != null) {
              
                     return "{\"result\":"+result+",\"id\":\""+id+"\",\"jsonrpc\":\""+jsonrpc+"\",\"error\":"+errors+"}";

                   } else {

                     return "{}";

                   }
                   
            
               
        }
        
        
    
}
