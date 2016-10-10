package ru.mtt.webapi.core;

import ru.mtt.webapi.core.IConfigurableObject;
import ru.mtt.webapi.core.XAction;
import ru.mtt.webapi.core.XConfigurableObject;

/**
 *  Basic abstract command for cronicle commands
 *
 *  @author @author RNasibullin@mtt.ru
 */

public abstract class XCommandAction  extends XAction implements IConfigurableObject {
    
    
    XConfigurableObject delegator = null;
   
    protected  String[] args = null;
    
    public XCommandAction() {

        super();
        delegator = new ActionConfigurator();
        
    }

    abstract public void run(String[] args);

    @Override
    public String[] getActionList() {
           return new String[0];
    }

    @Override
    public String[] getOperationList(String actId) {
           return new String[0];
    }

    @Override
    public String[] getParametersList(String actId) {
           return new String[0];
    }

    @Override
    public boolean getBoolConfigParameter(String paraName) {
           return delegator.getBoolConfigParameter(paraName);
    }

    @Override
    public String getConfigParameter(String paraName) {
           return delegator.getConfigParameter(paraName);
    }

    @Override
    public double getDoubleConfigParameter(String paraName) {
           // TODO Implement this method
           return delegator.getDoubleConfigParameter(paraName);
    }

    @Override
    public int getIntConfigParameter(String paraName) {
           return delegator.getIntConfigParameter(paraName);
    }

    @Override
    public long getLongConfigParameter(String paraName) {
           // TODO Implement this method
           return delegator.getLongConfigParameter(paraName);
    }

    @Override
    public void doConfig() {
           // TODO Implement this method
    }

    @Override
    public void setConfig(String config) {
           delegator.setConfig(config) ;
    }
    
    
    class ActionConfigurator extends XConfigurableObject {

        @Override
        public void doConfig() {
            // TODO Implement this method
        }
    };

    
}
