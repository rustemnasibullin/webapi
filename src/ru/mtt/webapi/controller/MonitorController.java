package ru.mtt.webapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;

import org.snmp4j.util.TreeListener;

import ru.mtt.rservice.core.IMonitorHolder;
import ru.mtt.rservice.core.MAPIServiceFarmHandler;
import ru.mtt.rservice.core.MAPIServiceHandler;
import ru.mtt.rservice.core.Quantil;
import ru.mtt.rservice.core.ServiceStatisticsAcqusition;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XAction;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dispatcher.WebApiDispatcher;
import ru.mtt.webapi.dispatcher.WebApiDispatcherMBean;
import ru.mtt.webapi.utils.XUtils;


/**
 *
 * Monitor for service node integrated with ZABBIX solution
 *
 * @author rnasibullin@mtt.ru
 */
public class MonitorController implements IMonitorHolder {
    
    TreeSet<Quantil> trends = new TreeSet<Quantil>();
    ConcurrentHashMap<String, Long> timestamps = new ConcurrentHashMap<String, Long>();
    WebApiDispatcherMBean mon = null;
    
    public MonitorController() {
        super();
    }


    @Override
    public void setMonitor(WebApiDispatcherMBean m) {
           mon = m;
    }


    public void start() {
        
    }
    
    public void stop() {
        
    }
    
    long tmax = 0L;
    public void process(Exchange exchange) throws Exception {
        
        XUtils.ilog("log/monitor.log", "Log with Monitor");  

        try {   
        
                String qs = exchange.getIn().getBody(String.class);
                String res =  "[]";
                org.apache.camel.component.http.HttpMessage  msg = (org.apache.camel.component.http.HttpMessage) exchange.getIn();
                HttpServletRequest req = msg.getRequest();
                String path = req.getRequestURI(); 
            
 //             http://<host>:8771/webapi/getRequestsPerSecond - выводить количество запросов в секунду
 //             http://<host>:8771/webapi/getAvgTimeRequests (с параметром в виде имени метода) выводить среднее время обработки 
         
                if (path.equals("/webapi")) {
                    
                    ServiceStatisticsAcqusition sc = WebApiDispatcher.sc;
                    List<Quantil> ls = sc.getTrend("*");
                    long ts = 0L;
                    
                    if (ls != null) {
                    res = "[";
                    boolean start = false;
                    
                    for (Quantil lsi: ls) {
                        
                        if (lsi.getTs()>=tmax) {
                          //   trends.add(lsi);
                             ts = lsi.getTs();
                             if (start) {
                                 res+=",";
                             }
                             res+=lsi;
                             start = true;
                        }
                        
                    }
                    
                    res+="]";
                    tmax = ts;
                    
                    }
                    
                } else if (path.equals("/webapi/getMethodsList")) {
                    Set<String> xm = mon.getMethods();
                    res = "[";
                    boolean start = false;
                    for (String x: xm) {
                        if (start) {
                            res+=",";
                        }
                        start = true;
                        res+="\""+x+"\"";
                    }
                    res+="]";
                } else if (path.equals("/webapi/getRequestsPerSecond")) {
                
                List<Quantil> ls = mon.getTrend("*");
                long ts = 0L;
            
                if (ls != null) {
                res = "[";
                boolean start = false;
                
                for (Quantil lsi: ls) {
                    
                    if (lsi.getTs()>=tmax) {
                         //   trends.add(lsi);
                         ts = lsi.getTs();
                         if (start) {
                             res+=",";
                         }
                         res+=(int)(Math.round(lsi.getFreq()));
                         start = true;
                    }
                    
                }
                
                res+="]";
                tmax = ts;
                
                }
            
            } else if (path.equals("/webapi/getAvgTimeRequests")) {
       
                String xs = req.getParameter("METHOD");
                res = getAvrTime(xs);

            } else if (path.equals("/webapi/getAvFactor")) {
                
                String xs = req.getParameter("METHOD");
                res = getAvFactor(xs);
                
            
            } else {
              
              String xs = req.getParameter("ALIAS");
              String tp = req.getParameter("TYPE");
              res = "";
              Map<String, MAPIServiceHandler> m =  MAPIServiceFarmHandler.getInstance().getMapis();
              Set<Map.Entry <String, MAPIServiceHandler>> ms =  m.entrySet();
              for (Map.Entry <String, MAPIServiceHandler> x: ms) {
                     String  hostId = x.getKey(); 
                     MAPIServiceHandler h = x.getValue();
                     double d1 = h.getAvailabilityFactor(xs);
                     double d2 = h.getAvailabilityFactor();
                     double tot = d1*d2;
                  
                     if ("JSON".equals(tp)) {
                         if (res.length()>0) res+=",";
                         res+="{host:\""+hostId+"\",AF="+tot+"}";
                     } else {
                         res+="host="+hostId+",AF="+tot+"\n";
                     }
              };

              if ("JSON".equals(tp)) {
                    if (res.length()>0) res+=",";
                    res="["+res+"]";
              }
                
                    
            }
       
            exchange.getOut().setBody(res);
                
        } catch (Throwable ee) {

                XUtils.ilog("log/monitor.log", XUtils.info(ee));  
                String err_mess = ee.getMessage();
                exchange.getOut().setBody(err_mess);

        }
        
    }

    public String getAvFactor(String xs) {
    
        List<Quantil> ls = mon.getTrend(xs);
        long ts = 0L;
        String res = "";
        Long tmax_x = timestamps.get (xs);
        if (tmax_x == null) tmax_x = new Long(0L); 
        XUtils.ilog("log/_xlog.log", xs+"="+ls.size());
        if (ls != null) {
        res = "[";
        boolean start = false;
        
        for (Quantil lsi: ls) {
            
            XUtils.ilog("log/_xlog.log", lsi.getTs()+" = "+tmax_x);
            if (lsi.getTs()>=tmax_x) {
              //   trends.add(lsi);
                 ts = lsi.getTs();
                 if (start) {
                     res+=",";
                 }
                 res+=lsi.getAvFactor();
                 start = true;
            }
            
        }
        }
        
        res+="]";
        tmax_x = ts;
        timestamps.put (xs, tmax_x);
        
        return res;
    
    
    
    };

    public String getAvrTime(String xs) { 
    List<Quantil> ls = mon.getTrend(xs);
    long ts = 0L;
    String res = "";
    Long tmax_x = timestamps.get (xs);
    if (tmax_x == null) tmax_x = new Long(0L); 
    XUtils.ilog("log/_xlog.log", xs+"="+ls.size());
    if (ls != null) {
    res = "[";
    boolean start = false;
    
    for (Quantil lsi: ls) {
        
        XUtils.ilog("log/_xlog.log", lsi.getTs()+" = "+tmax_x);
        if (lsi.getTs()>=tmax_x) {
          //   trends.add(lsi);
             ts = lsi.getTs();
             if (start) {
                 res+=",";
             }
             res+=(int)(Math.round(lsi.getMO()));
             start = true;
        }
        
    }
    }
    
    res+="]";
    tmax_x = ts;
    timestamps.put (xs, tmax_x);
    
    return res;
    
    }

    public static void main (String [] a) {

         
        MonitorController v = new MonitorController ();
        
        v.setMonitor(new WebApiDispatcher());
        long t = System.currentTimeMillis();
        while (true) {
            
            long ts = System.currentTimeMillis(); 
            if ((ts-t)> 2*60000) {
                break;
            }
            
            try {
                
              Thread.currentThread().sleep(300);
                
            } catch (InterruptedException ee) {
                
            }
            v.mon.registerEvent("MyMethod", 12, ts, 1.0);
            
        }
        
        String vs = v.getAvrTime("MyMethod");
        System.out.println ("MyMethod:  " + vs);

    }

    
}
