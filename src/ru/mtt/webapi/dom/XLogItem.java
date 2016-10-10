package ru.mtt.webapi.dom;

import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XSmartObject;


/**
 * Structured Log Item object implementation
 *
 * @author rnasibullin@mtt.ru
 */

public class XLogItem extends XSmartObject {
    public XLogItem() {
        super();
    }


    @Override
    public void setFieldByName(String name, Object value) throws WAPIException {
        
        this.put( name, value);

    }

    @Override
    public Object getFieldByName(String name) throws WAPIException {

          return this.get(name);

    }

}
