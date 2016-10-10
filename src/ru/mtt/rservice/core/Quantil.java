package ru.mtt.rservice.core;

import java.awt.Color;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.SimpleType;

import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.dispatcher.XDataGramma;

/**
 *  Quatil with predifined width implementation
 * 
 *  @author rnasibullin@mtt.ru  Chief 
 */

public class Quantil implements XDataGramma, Comparable {


    @Override
    public int compareTo(Object e) {

           if (e != null && e instanceof Quantil) {
               Quantil q = (Quantil) e;
               if (q.getTs() > ts) return 1;
               if (q.getTs() == ts) return 0;
               if (q.getTs() < ts) return -1;
           }
           return -1;

    }
    
    
    long w = 0L; 
    long ts = 0L;
          
    AtomicInteger count = new AtomicInteger();
    AtomicLong tot = new AtomicLong();
    volatile double af = 1.0; 

    
    public Quantil (){
    }

    public Quantil (long width){
           w = width;
    }

    public void setTs(long ts) {
           this.ts = ts;
    }

    public void setW(long w) {
           this.w = w;
    }

    public long getW() {
           return w;
    }

    public void setCount(AtomicInteger count) {
           this.count = count;
    }

    public AtomicInteger getCount() {
           return count;
    }

    public void setTot(AtomicLong tot) {
           this.tot = tot;
    }

    public void setAvailability(double avFactor) {
           af = avFactor;
    };    

    public AtomicLong getTot() {
           return tot;
    }

    public void increment(int c, long dur) {
           count.getAndAdd(c);
           tot.getAndAdd(dur);
    };

    public double getAvFactor () {
           return af;  
    }
          
    public double getMO () {
                 if (count.intValue()==0) return 0.0;
                 double v = tot.longValue()/(1.0*count.intValue());
                 return v;  
    }

    public double getFreq () {
              if (count.intValue()==0) return 0.0;
                 double v = count.intValue()/(w/1000.0);
                 return v;  
    }
          
    public Long getTs() {
                 return ts;
    };
          
    public Double getValue(String attr) {
          
              Double v = 0.0;
              switch (attr) {
              case IConstants._FREQ:
                   v = this.getFreq();
                   break; 
              case IConstants._RESPT:
                   v = this.getMO();  
                   break; 
              }
              
              
              return v;
          
          };
          
          @Override
          public int hashCode () {
              
                 return (int) ts;
              
          }
          
          @Override
          public boolean equals (Object e) {
        
                 if (e != null && e instanceof Quantil) {
                     Quantil q = (Quantil) e;
                     if (q.getTs() == ts) return true;
                 }
                 return false;

          }
         
          
          public String toString () {
                 return "[" + ts + "," + getFreq () +"," +getMO () + "]";
          }


}
