package ru.mtt.webapi.dom;

import java.util.Set;

import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XSmartObject;

/**
 * Simplified with pair NAME-VALUE parameters SmartObject with MAP field storage.
 *
 * @author rnasibullin@mtt.ru
 */

public class SimpleXSmartObject extends XSmartObject {
    
    Object val = null;
    String name = "Uknown";
    byte type = 0;

    public void setVal(Object val) {
        this.val = val;
    }

    public Object getVal() {
        return val;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getType() {
           return type;
    }
    
    
    public SimpleXSmartObject() {
           super();
    }

    public SimpleXSmartObject(String val) {
        super();
        this.val = val;
        type = _CHAR;
    }

    public SimpleXSmartObject(Long val) {
        super();
        this.val = val;
        type = _LONG;
    }

    public SimpleXSmartObject(Integer val) {
        super();
        this.val = val;
        type = _INT;
    }

    public SimpleXSmartObject(Double val) {
        super();
        this.val = val;
        type = _DBL;
    }


    public SimpleXSmartObject(String name, String val) {
        super();
        this.val = val;
        this.name=name;
        type = _CHAR;
    }

    public SimpleXSmartObject(String name, Long val) {
        super();
        this.val = val;
        this.name=name;
        type = _LONG;
    }

    public SimpleXSmartObject(String name, Integer val) {
        super();
        this.val = val;
        this.name=name;
        type = _INT;
    }

    public SimpleXSmartObject(String name, Double val) {
        super();
        this.val = val;
        this.name=name;
        type = _DBL;
        
    }

    public SimpleXSmartObject(String name, Object val) {
        super();
        this.val = val;
        this.name=name;
    }

    public SimpleXSmartObject(String name, Boolean val) {
        super();
        this.val = val;
        this.name = name;
        type = _BOOL;
    }

    @Override
    public Object getFieldByName(String name) throws WAPIException {
           if (name.equals("NAME")) {
               return this.name;
           } else if (name.equals("ID")) {
               return this.name;
           } else if (name.equals("VALUE")) {
               return val;
           }
           return null;
    }

    @Override
    public void setFieldByName(String name, Object value) throws WAPIException {
           if (name.equals("NAME")) {
               this.name = (String) value;
           } else if (name.equals("VALUE")) {
               val = value;
           } else {
               this.put (name, value);
           }
    }

    public String getStringValue() {
    
           if (type == _CHAR) return "\""+val+"\"";
           else return String.valueOf (val);
    
    };

    @Override
    public String toJSONString() {
           String x = "";
           String ext = "";
           
           if (this.size()>0) {
              
               Set<String> ks = this.keySet();
               for (String xk: ks ) {
                    Object v = this.get(xk);
                    ext+=","+"\""+xk+"\":"+"\"" + v + "\"";
               }
           }
           
           x+="{\""+name+"\":"+getStringValue()+ext+"}";
           return x;
    }


}
