package ru.mtt.webapi.core;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;


/**
 * Abstract ValueObject with self-composition
 *
 *
 * @author rnasibullin@mtt.ru
 */
public abstract class XSmartObject extends LinkedHashMap implements IConstants, Serializable {
    
    static final String _VERSION = System.getProperty("xversion", "1.0"); 
   
    public XSmartObject() {
        super();
    }
    
    
    public abstract Object getFieldByName(String name)  throws WAPIException;
    public abstract void setFieldByName(String name, Object value)  throws WAPIException;
    
    
    
    public Object getValueOf(String name) {
        
           Object val = null;
        
           try {
                val = getFieldByName(name);
           } catch (WAPIException ee) {
                ee.printStackTrace();
           }
           
           return val;

    };


    public Integer getValueOfAsInteger(String name) {
        
           Object val = getValueOf(name);
           Integer vo = null;
        
           if (val instanceof Integer) {
                vo = (Integer) val;
           }
           
           return vo;

    };
    
    public Boolean getValueOfAsBoolean(String name) {
        
           Object val = getValueOf(name);
           Boolean vo = null;
        
           if (val instanceof Boolean) {
                vo = (Boolean) val;
           }
           
           return vo;

    };
    public Long getValueOfAsLong(String name) {
        
           Object val = getValueOf(name);
           Long vo = null;
        
           if (val instanceof Integer) {
                vo = (Long) val;
           }
           
           return vo;

    };
    public Double getValueOfAsDouble(String name) {
        
           Object val = getValueOf(name);
           Double vo = null;
        
           if (val instanceof Double) {
                vo = (Double) val;
           }
           
           return vo;

    };
    
    public String getValueOfAsString(String name) {
        
           Object val = getValueOf(name);
           String vo = null;
        
           if (val instanceof String) {
                vo = (String) val;
           }
           
           return vo;

    };

    public String getValueOfAsQString(String name) {
        
           Object val = getValueOf(name);
           String vo = null;
        
           if (val instanceof String) {
                vo = (String) val;
           }
           
           return "'"+vo+"'";

    };
    
    
    public void  setValueOf(String name,  Object value) {
        
        try {
            setFieldByName(name, value);
        } catch (WAPIException ee) {
            ee.printStackTrace();
        }
    
    
    };   
    
    public static Long resolveAsLong(Object value) {
           Long v = null;
           if (value != null) {
               if (value instanceof BigDecimal) {
                   v = ((BigDecimal)value).longValue();
               }
           }
           return v;
    };
    
    
    
    public static String checkEmpty(Object v) {
        
           if (v ==null) return "";
           else return v.toString();
        
    }
    
    public static Double resolveAsDouble(Object value) {
           Double v = null;
           if (value != null) {
               if (value instanceof BigDecimal) {
                   v =  ((BigDecimal)value).doubleValue();
               }
           }
           return v;
    };

    
    
    public boolean hasNext () {
           return false;
    }

    public XSmartObject next () {
           return null;
    }
    
    public String toJSONString () {
           return "{}";
    }
    
    public String toString () {
           return toJSONString();
    }
    
    
    
    public static String getString(ResultSet resultSet, String fld, String defValue) throws WAPIException {
     
        String d = null;
        
        try {
         
                d = resultSet.getString(fld);
            
        } catch (SQLException ee) {
            
                d = defValue;
          
        }
        
        return d;

    }


    public static Double getDouble(ResultSet resultSet, String fld, Double defValue) throws WAPIException {
     
        Double d = null;
        
        try {
         
                d = resultSet.getDouble(fld);
            
        } catch (SQLException ee) {
            
                d = defValue;
                
        }
        
        return d;

    }

    public static Date getDate(ResultSet resultSet, String fld, Date defValue) throws WAPIException {
     
        Date d = null;
        
        try {
         
                d = resultSet.getDate(fld);
            
        } catch (SQLException ee) {
            
                d = defValue;
            
        }
        
        return d;

    }


    public static Long getLong(ResultSet resultSet, String fld, Long defValue) throws WAPIException {
     
        Long d = null;
        
        try {
         
                d = resultSet.getLong(fld);
            
        } catch (SQLException ee) {
            
                d = defValue;
                
        }
        
        return d;

    }

    public static Integer getInteger(ResultSet resultSet, String fld, Integer defValue) throws WAPIException {
     
        Integer d = null;
        
        try {
         
                d = resultSet.getInt(fld);
            
        } catch (SQLException ee) {
            
                d = defValue; 
            
        }
        
        return d;

    }
    
    
}
