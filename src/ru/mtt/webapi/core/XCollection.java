package ru.mtt.webapi.core;

import java.util.ArrayList;
import java.util.List;

/**
 * SmartObject for representation incapsulated collection. 
 * 
 * @author rnasibullin@mtt.ru
 */

public class XCollection extends XSmartObject {
    
    transient protected List <XSmartObject> col = null;
    int indx  = 0;
    String nameof;

    public void setIndx(int indx) {
        this.indx = indx;
    }

    public int getIndx() {
        return indx;
    }

    public void setNameof(String nameof) {
        this.nameof = nameof;
    }

    public String getNameof() {
        return nameof;
    }

    public XCollection() {

           super();
           col = new ArrayList <XSmartObject> ();

    } 
    
    public XCollection(List<XSmartObject> c) {

           super();
           col = c;

    } 

    public XCollection(String nm, List<XSmartObject> c) {

           super();
           col = c;
           nameof = nm;

    } 

    public boolean isEmpty () {
        
           return (!(col != null && col.size() > 0));
        
    }

    public void setFieldByName(String name, Object value)  throws WAPIException {
    
           System.out.println ("Test::::::  " + name +"   ----------    "+value); 
    
    
           if (name.startsWith("#")) {

               String indxs = name.substring(1);
               int index = Integer.parseInt(indxs);
               
               if (col.size()<=index) {
                   
                   int nn = index+1-col.size();
                   for (int i=0; i<nn; i++) {
                        col.add (null);
                   }
                   
                   col.add(index, (XSmartObject) value);
               }
               col.set(index, (XSmartObject) value);
               
           } else if (name.equals("NAME")) {
               
               nameof = (String) value;
               
           }
           
           
           if (_VERSION.equals("2.0")) {
           if (_ERROR.equals(name)) {
               XCollection xc = (XCollection) this.get (_ERROR);
               XError er = new XError ();
               if (xc == null) {
                   ArrayList<XSmartObject> ls = new ArrayList<XSmartObject> ();
                   xc = new XCollection(ls);
               }
               er.setFieldByName(IConstants._CODE, value);
               xc.add(er);
               value = xc;
           }
        
           if (_DESCRIPTION.equals(name)) {
               XCollection xc = (XCollection) this.get (_DESCRIPTION); 
               if (xc != null) {
                   Integer intx = (Integer) xc.getFieldByName("SIZE");
                   XError er = (XError) xc.getFieldByName("#"+(intx-1));
                   if (er != null) {
                       
                       try {
                       
                       er.setFieldByName(IConstants._DESCRIPTION, value);
                       throw new Throwable();   
                           
                       } catch(Throwable ee) {
                      
                         ee.printStackTrace();
                      
                       }     
                           
                   }
                   value = xc;
               }
           }
           }

           this.put (name, value); 
    
    };

    @Override
    public Object getFieldByName(String name) throws WAPIException {
        
           if (name.startsWith("#")) {
         
               int index  =  indx;
               if (name.length()>1) {
                   String indxs = name.substring(1);
                   index = Integer.parseInt(indxs);
               }
               
               XSmartObject value  =  null;
               System.out.println (index);
               if (index<col.size()) {
               value = col.get(index);
               } else {
               throw new WAPIException("Data not Found");    
               }
               return value;
               
           }
           
           if (name.equals("NAME")) {
               return nameof;
           }
           
           if (_SIZE.equals(name)) {
               return col.size();               
           }
            
           if (_ERROR.equals(name)) {
               return this.get (_ERROR); 
           }
        
           if (_DESCRIPTION.equals(name)) {
               return this.get (_DESCRIPTION); 
           }
        
           if (col != null) {
             
               if (indx<col.size()) {
                   XSmartObject obj = col.get(indx);
                   return obj.getFieldByName(name);
               }
           
           } else {
               
               throw new WAPIException("No data");
               
           }
        
           return null;
           
    }


    public boolean hasNext () {
        
           if (col != null) {
               if (indx<col.size()) {
                   return true;
               } else {
                   return false;
               }
           }
           return false;
           
    }

    public XSmartObject next () {
        
           if (col != null) {
               if (indx<col.size()) {
                   indx++;
               } else {
                   indx = 0;
                   return null;
               }
           }
           
           return col.get(indx-1);
           
    }

    public void add (XSmartObject x) {
        
           col.add (x);
           
    }
    
    public static void main (String[] arg) {
        
    }

    @Override
    public String toJSONString() {
        
        try { 
        
        Object err = this.getFieldByName(_ERROR);
        if (err != null) {
                
            return "{\"error\":"+err+",\"description\":\""+this.getFieldByName(_DESCRIPTION)+"\"}";    
                
        }
        
        } catch (WAPIException ee) {
          ee.printStackTrace();  
        }
        
        String c = "[";
        
        boolean st = true;
        if (col != null) {
            for (XSmartObject x: col){
            if (x==null) continue;    
            if (!st) c+=",";  
            c+=x.toJSONString();     
            st = false;
            }
        }
        
        c+="]";
        return c;
    }

}
