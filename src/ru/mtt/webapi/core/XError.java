package ru.mtt.webapi.core;

import java.util.Set;

/**
 *
 * Error composite descriptor
 *
 * @author rnasibullin@mtt.ru
 */

public class XError  extends XSmartObject {


    int code = 0;
    String description = "";


    public XError(int code, String description) {
        super();
        this.code = code;
        this.description = description;
    }

    public XError() {
        super();
    }

    @Override
    public Object getFieldByName(String name) throws WAPIException {
        
        if (IConstants._CODE.equals(name)) {
            return code;  
        } else if (IConstants._DESCRIPTION.equals(name)) {
            return description;
        }
        return null;
    }

    @Override
    public void setFieldByName(String name, Object value) throws WAPIException {
        
        if (IConstants._CODE.equals(name)) {
            code = (Integer) value;  
        } else if (IConstants._DESCRIPTION.equals(name)) {
            description = (String) value;
        }
           
    }

    @Override
    public String toJSONString() {
        
           String x = "{\"code:"+code+",\"description\":"+"\""+description+"\"}";
           return x;

    }

    
}
