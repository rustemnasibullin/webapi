package ru.mtt.webapi.core;

import java.io.InputStream;

import java.util.List;

import org.apache.log4j.Logger;

import java.util.Properties;

import org.apache.commons.io.IOUtils;

import ru.mtt.webapi.utils.XUtils;


/**
 *
 * Abstract Configurable object for build real objects
 *
 *
 * @author rnasibullin@mtt.ru
 */
public abstract class XConfigurableObject implements IConfigurableObject {

    public static String _POSTFIX = "";
    protected String config = null;
    protected Properties ps = new Properties();
    Logger logger = Logger.getLogger(XConfigurableObject.class);
    public static boolean API_OFF = false;


    public void setConfig(String config) {
        this.config = config;
        try {

            logger.info("ConfigProperties: \n"+config);
            InputStream sx = this.getClass().getClassLoader().getResourceAsStream(config);
            ps.load (sx);
            logger.info("Properties: \n"+ps);
            doConfig();

        } catch (Throwable ee) {
            ee.printStackTrace();
        }
    }

    protected void dp (Object v) {

    //    if (true) return;
        logger.debug(v);
    }

    @Override
    public String getConfigParameter(String paraName) {
        String xp = ps.getProperty(paraName);
        String xpp = null;
        
        if (xp != null) {
            
            xpp = XUtils.compileParamString (xp);
            
        }
        
        
        return xpp;
    }


    @Override
    public int getIntConfigParameter(String paraName) {
        
        int v = -1;
        try {
            
            v = Integer.parseInt(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }

    @Override
    public double getDoubleConfigParameter(String paraName) {
        
        double v = 0.0;
        try {
            
            v = Double.parseDouble(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }

    @Override
    public boolean getBoolConfigParameter(String paraName) {
        
        boolean v = false;
        try {
            
            v = Boolean.parseBoolean(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }

    @Override
    public long getLongConfigParameter(String paraName) {
        
        long v = -1;
        try {
            
            v = Long.parseLong(ps.getProperty(paraName));
            
        } catch (Throwable ee) {
            
        }
        
        return v;
    }



}
