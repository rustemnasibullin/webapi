package ru.mtt.webapi.core;

/**
 * Created by R.Nasibullin on 7/8/14.
 *
 * Interface for object may be configured from particular property or configuration file
 * @author rnasibullin@mtt.ru
 */
public interface IConfigurableObject extends IConstants {
    

    /**
     * SET UP CONFIGURATION FILE before apply  config parameters
     *
     * @param config  configuration file path
     */
       void setConfig(String config);

    /**
     * Apply sonfiguration
     */
       void doConfig();


    /**
     * Retreive parameter value
     *
     * @param paraName  parameter key 
     * @return value of
     */
     String getConfigParameter (String paraName);

    /**
     * Retreive parameter value
     *
     * @param paraName parameter key 
     * @return value of
     */
     int getIntConfigParameter (String paraName);

    /**
     * Retreive parameter value
     *
     * @param paraName  parameter key 
     * @return value of
     */
     boolean getBoolConfigParameter (String paraName);

    /**
     * Retreive parameter value 
     *
     * @param paraName  parameter key 
     * @return value of
     */
     long getLongConfigParameter (String paraName);

    /**
     * Retreive parameter value
     *
     * @param paraName  parameter key 
     * @return value of
     */
     double getDoubleConfigParameter (String paraName);

}
