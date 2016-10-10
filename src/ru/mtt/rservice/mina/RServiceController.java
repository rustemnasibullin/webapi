package ru.mtt.rservice.mina;

import java.io.IOException;
import java.io.Writer;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import org.apache.mina.http.api.HttpRequest;

import ru.mtt.rservice.core.MAPIServiceFarmHandler;
import ru.mtt.rservice.core.MAPIServiceHandler;
import ru.mtt.rservice.core.MIBControlObject;
import ru.mtt.rservice.widgets.StatusPageWidgetCommand;
import ru.mtt.webapi.controller.XWebApiController;
import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.IMemCache;
import ru.mtt.webapi.core.IWidgetCommand;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.mina.HTTPResponseWrapper;
import ru.mtt.webapi.mina.MinaRequestHandler;

/**
 *  Registry Service controlling object - Mina RequestHandler
 * 
 *  @author rnasibullin@mtt.ru  Chief 
 */

public class RServiceController  extends XConfigurableObject implements MinaRequestHandler, IConstants {
    
    IMemCache cache;
    Logger log = Logger.getLogger(RServiceController.class);
    IWidgetCommand widgetCommand  = new StatusPageWidgetCommand  ();
    
    
    public RServiceController() {
        super();
    }

    public void setCache(IMemCache cache) {
        this.cache = cache;
    }

    public IMemCache getCache() {
        return cache;
    }

    @Override
    public void doConfig() {
        // TODO Implement this method
    }


    @Override
    public HTTPResponseWrapper acceptRequest(IoSession sess, HttpRequest msg) {

        Writer pw = new java.io.StringWriter();
        String contentType = "application/json;charset=utf-8";
        String sessId = String.valueOf(sess.getId());
        
        try {
          
        String[] response = (String[] ) execute (msg); 
        pw.write(response[0]);
        pw.close();
        pw.flush();
            
        if (response.length>1) {
            contentType = response[1];
        }
            
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

    @Override
    public HTTPResponseWrapper acceptRequest(IoSession sess, String msgDecoded) {
        
        Writer pw = new java.io.StringWriter();
        String contentType = "application/json;charset=utf-8";
        return new HTTPResponseWrapper(pw.toString(),"utf-8", contentType);

    }


    private Object execute (HttpRequest cmd) {
    
            String ds = null;
            String ctype = "application/json;charset=utf-8";
            
            String rPath = cmd.getRequestPath();
            String[] rs = rPath.split("/"); 
            String  type =  cmd.getParameter ("TYPE");

            if (rs == null || rs.length < 3) {
                
            widgetCommand.execute(cmd.getParameters());
            ds = widgetCommand.print();
            ctype = "text/html;charset=utf-8";     
                
            }  else   {
            
            String  d1s  =  cmd.getParameter ("DATESTART");
            String  d2s  =  cmd.getParameter ("DATEEND");
            String  alias  =  cmd.getParameter ("ALIAS");
                
            if (type == null) type = "JSON";
            Date d1 = null;
            Date d2 = null;
            
            if (d1s != null) {
                try {
                  d1 = IConstants.frmt.parse(d1s);
                } catch (ParseException pe) {
                  pe.printStackTrace();    
                }
            }
            
            if (d2s != null) {
                try {
                    
                  d2 = IConstants.frmt.parse(d1s);
                    
                } catch (ParseException pe) {
                    
                  pe.printStackTrace();    
                  
                }
            }
            
            String[] hosts = rs[0].split(":");
            String method = rs[1];
            String metric = rs[2];
            
            if (d2==null) {
                
                d2 = new Date ();
                Calendar c = Calendar.getInstance();
                c.add(Calendar.SECOND,(int)(0-_TERM));
                d1 = c.getTime();
                
            }
            
            MAPIServiceFarmHandler instance = MAPIServiceFarmHandler.getInstance();

            if (type.equals("HTML")) {
                ds  =  "<html><body>";  
                ctype = "text/html;charset=utf-8";     
            } 
            
            if (type.equals("JSON")) {
                ds = "{[";
            } 

            
            boolean start = false;
                
            if (alias != null) {
                
                String[] xsrvInfo = getCloudServiceInfoDatagrammas(alias);
                boolean vstart = false;
                for (String xs: xsrvInfo) {
                     if (vstart) ds+=",";
                     ds += xs;
                     vstart = true; 
                }
                                                                   
            } else {
                
            for (String h: hosts) {

            if (type.equals("JSON")) {
                if (start) ds+=",";
                ds += "{\"host\":"+h+"\",\"metric\":\""+metric+"\",\"method\":\""+method+"\",\"data\":{";
            }

            start = true;

            MAPIServiceHandler mapi = instance.getMAPIServiceHandler(h);
            Map<Long, Double> data  = mapi.getData(metric, method, d1, d2);
                
            if (type.equals("JSON")) {

                 Set<Map.Entry<Long, Double>> xs = data.entrySet();
                 boolean vstart = false;
                 for (Map.Entry<Long, Double> x:  xs) {
                      Long   x0 = x.getKey();                       
                      Double s0 = x.getValue();       
                      if (vstart) ds+=",";
                      ds += x0+":"+s0;
                      vstart = true;
                 }
                 ds += "}}";

            } 
                
                
            }

            }
 
            if (type.equals("HTML")) {
            ds  +=  "</body></html>";  
            } 

            if (type.equals("JSON")) {
                ds += "]}";
            } 
                
            }
            
            return new String[]{ds, ctype};
    
    }; 

    
    public String[] getCloudServiceInfoDatagrammas(String serviceAlias) {
        
           Map<String, MAPIServiceHandler> m =  MAPIServiceFarmHandler.getInstance().getMapis();
           Set<Map.Entry <String, MAPIServiceHandler>> ms =  m.entrySet();
           List <String> x1 = new ArrayList<String> (); 
           for (Map.Entry <String, MAPIServiceHandler> x: ms) {
                String  hostId = x.getKey(); 
                MAPIServiceHandler h = x.getValue();
                double d1 = h.getAvailabilityFactor(serviceAlias);
                double d2 = h.getAvailabilityFactor();
                double tot = d1*d2;
                x1.add ("host="+hostId+";AF="+tot+";");
                
           };

           return x1.toArray(new String[x1.size()]);
        
    }

        
    public void start () {
        
    }

    public void stop () {
        
    }

}
