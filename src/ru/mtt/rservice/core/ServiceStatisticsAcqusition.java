 package ru.mtt.rservice.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;

import ru.mtt.webapi.utils.XUtils;


/**
 *  Statistic data (Quantil collections)) collecting object
 *
 *  @author rnasibullin@mtt.ru  Chief
 */

public class ServiceStatisticsAcqusition {
    
    long startTIME   =   0l;
    long kvant = 60*1000;
    ConcurrentHashMap <String, List<Quantil>> trends = new ConcurrentHashMap();
    ConcurrentHashMap <String, Double> services = new ConcurrentHashMap();
    ConcurrentSkipListSet<String> methods = new ConcurrentSkipListSet<> ();  
    Logger log = Logger.getLogger(ServiceStatisticsAcqusition.class);
   
    public List<Quantil> getTrend (String meth) {
        
           return trends.get(meth);
        
    }
    
    public ServiceStatisticsAcqusition() {
        
           super();
           
    }
    
    public void start () {
           startTIME   =   System.currentTimeMillis();   
    }

    public void  registerService(String servName, double avFactor) {
           services.put (servName, avFactor);
    }

    public Double getAvailability (String servName) {
           return services.get(servName);
    }


    public void setServices(ConcurrentHashMap<String, Double> services) {
           this.services = services;
    }

    public ConcurrentHashMap<String, Double> getServices() {
           return services;
    }

    public void  registerEvent(String methNameAlias, long dur, long timeev, double avFactor) {
        
           XUtils.ilog("log/test_0001.log", methNameAlias);
           String  methName = methNameAlias; 
           int nx = methName.indexOf ("."); 
           String servName  =  null;
           if (nx > 0) {
               methName = methNameAlias.substring(nx + 1); 
               servName = methNameAlias.substring(nx); 
           }

           if (servName != null) {
           services.put (servName, avFactor);
           }

           XUtils.ilog("log/test_0001.log", methNameAlias);
        
           if (!methods.contains(methName)) methods.add(methName); 
           List<Quantil> v = trends.get ("*");
           List<Quantil> x = trends.get (methName);
           if (x == null) {
               x =  new ArrayList<Quantil>(); 
               trends.put (methName, x);
           } 

           if (v == null) {
               v =  new ArrayList<Quantil>(); 
               trends.put ("*", v);
           } 
           
           int nKvant = (int)((timeev - startTIME)/kvant);
           log.debug ("Register event:  "+methName+" : "+dur+" : "+timeev+" : "+nKvant);
           Quantil q = null; 
           Quantil h = null; 
 
           if (nKvant >= v.size()) {
               
               int xs = nKvant - v.size();
               long ts  = v.size()*kvant;
               for (int i=0; i<xs; i++){
                    h = new Quantil(kvant);
                    h.setTs(startTIME+i*kvant+ts);
                    v.add (h); 
               }
               h = new Quantil(kvant);
               h.setTs(startTIME+nKvant*kvant);
               v.add (h); 
               
           } else {
           
               if (nKvant >=0 && nKvant<x.size()) h = v.get (nKvant);
               
           }

           if (nKvant >= x.size()) {
            
               int xs = nKvant - x.size();
               long ts  = x.size()*kvant;
               for (int i=0; i<xs; i++){
                    q = new Quantil(kvant);
                    q.setTs(startTIME+i*kvant+ts);
                    x.add (q); 
               }
              
               q = new Quantil(kvant);
               q.setTs(startTIME+nKvant*kvant);
               x.add (q); 
            
           } else {
        
             if (nKvant<x.size()) q = x.get (nKvant);
            
           }

           
           if (q != null) {
               q.increment(1, dur);
               q.setAvailability(avFactor);    
           }
           
           if (h != null) {
               h.increment(1, dur);
               h.setAvailability(avFactor);    
           }

        
    }


    public Set<String> getMethods() {
        return methods;
    }


    public static void main (String [] as) {
       
        
    }
    
    
    
}
