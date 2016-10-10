package ru.mtt.webapi.core;

import java.util.Set;

import ru.mtt.webapi.utils.XUtils;

/**
 *
 * MAP object descriptor
 *
 * @author rnasibullin@mtt.ru
 */

public class XMap extends XSmartObject {
    public XMap() {
        super();
    }


    @Override
    public Object getFieldByName(String name) throws WAPIException {
        // TODO Implement this method
        return null;
    }

    @Override
    public void setFieldByName(String name, Object value) throws WAPIException {
       
       
           this.put (name,value);

    }

    @Override
    public String toJSONString() {
        
           String x = "{";

           Set xs = this.keySet();
           boolean start = true;
           for (Object xx: xs) {
               
                Object v = this.get (xx);
               
                String ts = v.toString();
                if (v instanceof XSmartObject) {
                   XSmartObject c = (XSmartObject) v;
                   ts = c.toJSONString();
                }   else if (v instanceof String) {
                   ts = XUtils.q (v.toString());
               }
                            
               if (!start) {
                   x+=",";    
               }
               x+="\""+xx+"\":"+ts;
               start = false;
               
           }
           

           x+="}";
        
           return x;
    }


}
