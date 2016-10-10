package ru.mtt.webapi.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.Set;
import java.util.TreeMap;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.Future;

import java.util.concurrent.TimeUnit;

import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpMessage;
import org.apache.log4j.Logger;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import org.apache.mina.http.api.HttpRequest;

import org.apache.mina.http.api.HttpResponse;

import ru.mtt.rservice.core.IMonitorHolder;
import ru.mtt.webapi.bizrules.CValidator;
import ru.mtt.webapi.core.IChainProcedure;
import ru.mtt.webapi.core.IMemCache;
import ru.mtt.webapi.core.OrchestrationEngineProxy;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.mina.HTTPResponseWrapper;
import ru.mtt.webapi.core.XAction;
import ru.mtt.webapi.core.XBUSConnector;
import ru.mtt.webapi.core.XCollection;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dispatcher.WebApiDispatcher;
import ru.mtt.webapi.dispatcher.WebApiDispatcherMBean;
import ru.mtt.webapi.memcache.SystemCache;
import ru.mtt.webapi.mina.MinaRequestHandler;
import ru.mtt.webapi.utils.XUtils;


/**
 * WepApi request processing object
 *
 * @author rnasibullin@mtt.ru
 */
abstract public class XWebApiController extends XConfigurableObject implements Processor, MinaRequestHandler, IMonitorHolder {       

    public static long timeout = 40000;
    protected Gson gson = new Gson (); 
    protected Logger log = Logger.getLogger(XWebApiController.class);
    protected Map <String,XAction> actions = new HashMap <String, XAction>();
    protected WebApiDispatcherMBean mon = null;

    public void setOde1(OrchestrationEngineProxy ode) {
        this.ode = ode;
    }

    public OrchestrationEngineProxy getOde() {
        return ode;
    }

    public void setValidator(IChainProcedure validator) {
        this.validator = validator;
    }

    public IChainProcedure getValidator() {
        return validator;
    }
    protected SystemCache sysCache = null;
    protected OrchestrationEngineProxy ode = null;
    protected IChainProcedure validator = null;
    
    protected ConcurrentHashMap<String, String> almaps = new ConcurrentHashMap<String, String> ();
    
    protected final ExecutorService service = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(false).setPriority(Thread.MAX_PRIORITY)
                    .setNameFormat("HTTPD-%d")
                    .build());

   
    abstract public void loadAliases ();

    @Override
    public void setMonitor(WebApiDispatcherMBean m) {
           mon = m;
    }


    public XWebApiController () {
    
           loadAliases ();

    }

    @Override
    public HTTPResponseWrapper acceptRequest(IoSession sess, HttpRequest msg) {
        Writer pw = new java.io.StringWriter();
        String contentType = "application/json;charset=utf-8";
        try {
            
        if (msg instanceof IoBuffer) {
            
            IoBuffer simbuf = (IoBuffer) msg;
            byte[] buff = simbuf.array();
            String message_decoded = new String(buff, "UTF-8");  
            return acceptRequest(sess, message_decoded); 
          
        } else if (msg instanceof HttpRequest)  {
          
            HttpRequest req = (HttpRequest) msg;
            String path = req.getRequestPath();  
            final String opAlias = almaps.get (path);
            final XAction act  = actions.get(opAlias);
            
            log.info ("Path: "+path);
            log.info ("opAlias: "+opAlias);
            final Map m = req.getParameters();
            Future operation = service.submit(new Callable() {
            
            public Object call() {
                   return act.execute(opAlias, m);
            }
            
            });           
            
            Object obj = null;
            
            try { 
                
                obj = operation.get(timeout, TimeUnit.MILLISECONDS);
            
                log.info ("Operation obj: "+obj);

                if (obj == null) {
                    
                    pw.write("{'status':'empty result'}");

                } else {
                    
                    pw.write (obj.toString());
                    contentType="text/xml;charset=utf-8";
                        
                }

            
            
                } catch (TimeoutException ee) {
                
                    pw.write("{'status':'operation timeout exceeded'}");
              
                
                } catch (Throwable eee) {

                    pw.write("{'status':'"+eee.getMessage()+"'}");
                    
                }


        } else  {
            
            
          System.out.println ("MessClass: "+msg.getClass().getName());  
          pw.write("{'status':'not supported'}");
            
        }
        
        } catch (IOException ee) {
          ee.printStackTrace();    
        }
        
        HTTPResponseWrapper x = new HTTPResponseWrapper(pw.toString(),"utf-8", contentType);
        
        
        return x;
    }

    public void setAlmaps(ConcurrentHashMap<String, String> almaps) {
        this.almaps = almaps;
    }

    public ConcurrentHashMap<String, String> getAlmaps() {
        return almaps;
    }

    @Override
    public HTTPResponseWrapper acceptRequest(IoSession sess, String msgDecoded) {
        Writer pw = new java.io.StringWriter();
        String contentType = "application/json;charset=utf-8";
        String sessId = String.valueOf(sess.getId());
        log.debug("Msg: "+msgDecoded);
        
        try {

        String cmd = ""; 
            
        if (msgDecoded != null && msgDecoded.trim().length()>0) {
            cmd = msgDecoded;    
        }
        
        Object request = execute (cmd); 
        pw.write(request.toString());
        pw.close();
        pw.flush();
        
            
        } catch (Throwable ee) {
            
            ee.printStackTrace();
            
            try {
            
            pw.write("{'status':'ServiceError'}");             
            
            } catch (IOException exe) {
              exe.printStackTrace();  
            }
        }
            

    
        return new HTTPResponseWrapper(pw.toString(),"utf-8", contentType);

    }


  
    abstract public void process(Exchange exchange) throws Exception;
    

    public void setSysCache(SystemCache sysCache) {
        this.sysCache = sysCache;
    }

    public SystemCache getSysCache() {
        return sysCache;
    }

    public void doConfig() {
        
        Collection<XAction> x = actions.values(); 
        for (XAction a: x) {
             a.setOwner (this);
        }
        
    }



    public XAction getAction(String actId) {
           return actions.get (actId);
    }


    public void start () {
        
 
        
    }

    public void stop () {
        
    }
    
     
    
    

 
    

 
    public Object execute (Object in) {
                    
                  Object res = "{'status':'noresultdata'}";
                  String data  = in.toString();
                  String error = null;
                  IJSONRPCControlObject rpc = null;
                  
                  try {

                  rpc = XUtils.toJSONRPCControl(data);

                  } catch (Throwable ee) {

                          ee.printStackTrace();
                          res = "{'status':'error';'description':'JSON message is not valid'}";

                  }
                   
                  log.debug ("RPC Control: "+rpc);
                  String  act = null;
                  String  id  = null;
                  String  version  = null;
                  
                  
                  if (rpc != null) {
                  act = rpc.getMethod();
                  id  = rpc.getId();
                  version = rpc.getJsonrpc();
                  XAction ms = actions.get (act); 
                  log.debug ("XAction: "+act);
                    
                  if (ms != null)  {
                      
                      try {
                          
                          
                        long ts = System.currentTimeMillis();  
                        Object params =  rpc.getParamsList();
                          
                        String[] PARAMS = null;  
                          
                        if (params instanceof String[]) PARAMS  = (String[]) params;  
                        else if  (params instanceof LinkedHashMap) {
                            
                            LinkedHashMap<String,Object> map = (LinkedHashMap<String,Object>) params;    
                            String [] params_list = ms.getParametersList(act);
                            int nnx = params_list.length;
                            PARAMS = new String[nnx];
                            int j = 0;
                            
                            log.debug ("PARAMS_i : "+map);  
                            
                            for (String x: params_list) {
                                 log.debug ("PARAMS_i : "+x);  
                                 Object o = map.get (x);
                                 PARAMS  [j] = (o==null)? null:o.toString();
                                 j++;    
                            }
                            
                        }
                          
                        log.debug ("PARAMS : "+PARAMS);  
                        res =  ms.execute (act, PARAMS);
                        long dur = System.currentTimeMillis() - ts;
                       // mon.registerEvent(act, dur, ts);  

                        if (res instanceof XCollection) {
 
                            XCollection res_comp = (XCollection) res;
                            Object err = res_comp.getFieldByName(XSmartObject._ERROR);
                            
                            if (err != null) {
                                
                                Object errMsg = res_comp.getFieldByName(XSmartObject._DESCRIPTION);
                                if (errMsg != null) error = String.valueOf(errMsg);
                                error = String.valueOf(err);
                                
                            } else {
                                
                                res = res_comp;
                                
                            }
                            
                        }
          
                               
                      } catch (Throwable ee) {
                        
                        ee.printStackTrace();
                        StackTraceElement[]  els = ee.getStackTrace();
                        for (StackTraceElement x: els) {
                             log.info(x.toString()); 
                        }
                        res = "{'status':'error';'description':'"+ee.getClass().getName()+"("+ee.getMessage()+")'}";
                          
                      }
                          
                  }
                  }
                  
                  JSONRPCControlObject rpc_res = new JSONRPCControlObject();
                  rpc_res.setId(id);
                  rpc_res.setJsonrpc(version);
                  rpc_res.setError(error);
                  rpc_res.setResult(res);
                  return rpc_res;                

    }

}

