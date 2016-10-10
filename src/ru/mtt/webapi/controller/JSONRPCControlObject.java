package ru.mtt.webapi.controller;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 *  JSON-RPC Control interface Implementaion   
 * 
 *  @author @author RNasibullin@mtt.ru
 */

public class JSONRPCControlObject  implements Serializable, IJSONRPCControlObject {


        @Expose String error = null;
        @Expose Object result = null;
        @Expose String version = null;
        @Expose String method = null;
        @Expose String id = null;
        @Expose String jsonrpc = "2.0";
        @Expose String[] params = null;

        public JSONRPCControlObject () {
            
        }

        public Object getParamsList() {
        
               return params;
        
        }


        public void setError(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public Object getResult() {
            return result;
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

        public void setParams(String[] params) {
            this.params = params;
        }

        public String[] getParams() {
            return params;
        }

        public void complete () {
            
            
        }
        
        
        public String toString () {
               String errors = error;
               if (error != null) errors="\""+error+"\"";
               if (method !=null) { 
 
                   String s = "{'id':'"+id+"','method':'"+method+"','jsonrpc':'"+jsonrpc+"'";
                   if (params != null) {
                       
                       String x = null;
                       for (String p:params) {
                            if (x != null) x+=",";
                            else x = ""; 
                           
                            if ("true".equals (p) || "false".equals (p) || p==null) {
                                x+=p;   
                            } else {
                                x+="'"+p+"'"; 
                            }
                           
                       }
                       
                       s+= ",'params':["+x+"]}";
                   }

                 return s;  
                       
               } else if (result != null) {
          
                 return "{\"result\":"+result+",\"id\":\""+id+"\",\"jsonrpc\":\""+jsonrpc+"\",\"error\":"+errors+"}";

               } else {

                 return "{}";

               }
               
        }
        


  
}
