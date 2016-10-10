package ru.mtt.webapi.core;

import com.google.gson.Gson;

import java.util.Collection;


import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import ru.mtt.webapi.controller.XWebApiController;


/**
 * Abstract action for WebApi call processing
 *
 *
 * @author rnasibullin@mtt.ru
 */

public abstract class XAction implements IOperationMap, IConstants {

    public static final ResourceBundle messages = ResourceBundle.getBundle("messages");
    public static final Gson gson = new Gson (); 

    Logger log = Logger.getLogger(XAction.class);
    
    protected Map <String,String[]> operations = new HashMap<String, String[]>();
    protected Map <String,String[]> parameters4operations = new HashMap<String, String[]>();
    
    public String[] getOperationList(String actId) {
    
           return operations.get(actId);
    
    };
    

    public String[] getParametersList(String actId) {
    
           return parameters4operations.get(actId);
    
    };

    


    
    
    /**
     * Object which contains whole configuration stuff
     */
    protected XWebApiController owner = null;

    public void setOwner(XWebApiController owner) {
        
           this.owner = owner;
           String[] acts = getActionList();
           
           for (String x: acts) {
               
                log.info ("Config activity method: "+x);
                String xoper   = this.owner.getConfigParameter("operations."+x);
                String xparams = this.owner.getConfigParameter("parameters4operations."+x);
                log.info (xoper);
                log.info (xparams);
                String[] v1 = gson.fromJson(xoper, String[].class);
                String[] v2 = gson.fromJson(xparams, String[].class);
                operations.put (x, v1);
                parameters4operations.put (x, v2);
               
           }
                  
    }

    public XWebApiController getOwner() {
        return owner;
    }

    public XAction() {
        super();
    }


    public Object execute (String act0, Map params)  {
        
           String[] lst = getParametersList(act0);
           int nss =  lst.length; 
           String[] pars = new String[nss]; 
           
           
           log.info ("Params: " + params);
           log.info ("ParamsDescr: " + lst);
           
           
           for (int i=0; i<nss; i++) {
                
                log.debug ("ParamsDescrI: " + lst[i]);
                String[] parAliases = lst[i].split("[|]");

                for (String aliasId: parAliases) {
                Object val =  params.get(aliasId);
                log.info ("ParamsALIAS: " + aliasId +" VALS: " +val);
                if (val != null) {
                    pars[i] = val.toString();
                    log.info ("Param: " + i+" : "+ pars[i]);
                    break;                              
                } 

                String alias = owner.getAlmaps().get(aliasId);
                val =  params.get(alias);
                if (val != null) {
                    pars[i] = val.toString();;
                    break; 
                } else {
                    pars[i] = alias;
                }
                    
                }                
           }
           
           return execute(act0, pars);
        
    }


    public Object execute (String act0, String[] params) {
    
           Object res = null;
           log.info (act0 + " operation");
           log.info ("Parameters:   " + params);
           log.info ("Operations1:  " + operations);
           log.info ("Operations2:  " + parameters4operations);
           IChainProcedure proc = null;
           Object  evIN = null;
           String[] operations = this.getOperationList(act0);
           log.info (act0 + " - " + operations);

           if (operations != null) {
           for (String operator : operations) {
           
               int nm =  operator.indexOf(".");
               
               if (nm>0) {
               
               String oper = operator.substring(0,nm).trim();
               String act  = operator.substring(nm+1).trim();
               log.debug (oper);
               
               if (oper.equals("ODE")) {
                   
                   proc = owner.getOde(); 
                   act = operator.substring(1);
                   
               } else if (oper.equals("Validator")) {
                   
                   proc = owner.getValidator();    

               } else if (oper.equals("MemCache2")) {

               }


               log.debug (proc);


               if (proc==null) {
                   
                   log.info ("Component not found: "+oper);
                   
               } else {
                   
                   

               Object evOUT = proc.execute(act, params, evIN);
                   
                   
               
               if (evOUT instanceof XSmartObject) {
                   
                   XSmartObject o = (XSmartObject) evOUT;
                   try {
                         Integer iErr = (Integer) o.getFieldByName(XSmartObject._ERROR);
                         if (iErr != null && iErr.intValue()>IConstants._ERR_CRITICAL) return evOUT;
                   } catch (WAPIException ee) {
                         ee.printStackTrace();    
                   }
               
               }
               
               res = evOUT;
               evIN = evOUT;
               
               }
               
               }
           }
               
           }
           
           return res;
    
    };

    
}
