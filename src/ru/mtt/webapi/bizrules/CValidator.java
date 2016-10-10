package ru.mtt.webapi.bizrules;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import java.util.ResourceBundle;
import java.util.Set;
import org.apache.log4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.mtt.webapi.core.IChainProcedure;
import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XAction;
import ru.mtt.webapi.core.XCollection;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.utils.XUtils;



/**
 * Parameters validation controller. 
 * 
 * @author rnasibullin@mtt.ru  Chief 
 */

public class CValidator extends XConfigurableObject implements IChainProcedure  {
    
    
    Logger logger = Logger.getLogger(CValidator.class);
    private Pattern pattern;
    private Matcher matcher;
    private static final String pfx =  "88314";
    private static final String EMAIL_PATTERN = 
               "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
               + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Set <Integer> optionsIds = new HashSet <Integer>(); 
    private static final ResourceBundle countriesIds = ResourceBundle.getBundle("cfg.countries"); 
    private static final Set <String> currencyIds = new HashSet <String>(); 
    private static final Set <String> langIds = new HashSet <String>(); 
    
    static {
   
        optionsIds.add (2760);
        optionsIds.add (507);
        optionsIds.add (527);
        optionsIds.add (1569);
        optionsIds.add (1570);
        optionsIds.add (1571);
        optionsIds.add (2062);
        
        langIds.add ("ar");
        langIds.add ("cm");
        langIds.add ("de");
        langIds.add ("el");
        langIds.add ("en");
        langIds.add ("es");
        langIds.add ("fr");
        langIds.add ("he");
        langIds.add ("it");
        langIds.add ("pt");
        langIds.add ("ru");
        langIds.add ("sv");
        langIds.add ("yu");
        
        currencyIds.add ("RUB");
        currencyIds.add ("EUR");
        currencyIds.add ("USD");
        
    };
    
    public CValidator() {
           super();
           pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public void doConfig() {
        
    }


    public void init() {
        
    }

    public void stop() {
        
    }

    public boolean checkBizRuleEmail(String uid) {
            
           matcher = pattern.matcher(uid);
           return matcher.matches();
            
    }

    public boolean checkBizRuleSIPID(String sip) {
            
        if (!XUtils.isEmpty(sip)) return true;
        return false;
            
    }

    public <T> boolean validateConsistency(T obj) {
           if (obj==null) return false;
           return true;
    };

    public Object execute (String act, String[] params, Object evIN) {
    
    
               XCollection res = (XCollection) evIN;
           
               if (evIN==null) {
                   ArrayList<XSmartObject> lst  = new ArrayList<XSmartObject> ();
                   res = new XCollection(lst);
               }               
          
               try {               
               
               if ("checkBizRuleEmail".equals(act)) {
               
               
               String uid = null;
               
               
               if (params.length>0) uid = params[0]; 
               
               if (uid==null || uid.equals("null")) {
               
               } else if (!checkBizRuleEmail(uid)) {
                   
                   res.setFieldByName(_ERROR,1);
                   res.setFieldByName(_DESCRIPTION,"Invalid UID");

               }
               
               
               } else if ("checkPhonePrefix".equals(act)) {
               
               
                      String paramstr = act.substring("checkPhonePrefix".length()+1); 
                      String[] pars = paramstr.split("[(,)]");
                      String val = null;
                      if (pars[0].startsWith("#")) { 
   
                      int i_a = Integer.parseInt(pars[0].substring(1));
                      val = params[i_a]; 
                      
                      if (!val.startsWith(pfx)) {
                          val = pfx + val;
                          params[i_a] = val;
                      }
   
                      }  
                
                   
               
          } else if (act.startsWith("checkISO639_1")){
                       
                       String paramstr = act.substring("checkISO639_1".length()+1); 
                       String[] pars = paramstr.split("[(,)]");
                       String val = null;
                       if (pars[0].startsWith("#")) { 
                       int i_a = Integer.parseInt(pars[0].substring(1));
                       if (i_a < params.length) val = params[i_a]; 
                       }  
                       
                       
                       logger.debug("Language: "+val);
                       
                       if (XUtils.isEmpty(val)) {

                        
                           
                       } else {

                           if (!langIds.contains(val))  {
                           res.setFieldByName (_ERROR,1);
                           res.setFieldByName (_DESCRIPTION,"wrong value of parameter (ISO-639-1).");
                           }
                           
                       }
                   

               } else if (act.startsWith("checkISO4217")){
                   
                   String paramstr = act.substring("checkISO4217".length()+1); 
                   String[] pars = paramstr.split("[(,)]");
                   String val = null;
                   if (pars[0].startsWith("#")) { 
                   int i_a = Integer.parseInt(pars[0].substring(1));
                   val = params[i_a]; 
                   }  
                   logger.debug("checkISO4217: "+val);

                   if (val == null) {

                       res.setFieldByName (_ERROR,1);
                       res.setFieldByName (_DESCRIPTION,"Currency Type not defined.");
                       
                   } else {

                       if (!currencyIds.contains(val)) {
                       res.setFieldByName (_ERROR,1);
                       res.setFieldByName (_DESCRIPTION,"Currency Type not defined properly (ISO4217).");
                       }
                       
                   }
       
               } else if ("checkBizRuleEmailNotNull".equals(act)) {
                   
                   String uid = null;
                   if (params.length>0) uid = params[0]; 
                   
                   
                   if (uid==null || uid.equals("null")) {
                       
                       res.setFieldByName (_ERROR,1);
                       res.setFieldByName (_DESCRIPTION,"UID is null");
                   
                   } else if (!checkBizRuleEmail(uid)) {
                       
                       res.setFieldByName (_ERROR,1);
                       res.setFieldByName (_DESCRIPTION,"Invalid UID");
                       

                   }

               } else if (act.startsWith("checkBooleanFlags")) {
                   
                   String paramstr = act.substring("checkBooleanFlags".length()+1); 
                   String[] pars = paramstr.split("[(,)]");
                   
                   for (String b: pars) {
                       if (b.startsWith("#")) { 
                       int i_a = Integer.parseInt(pars[0].substring(1));
                       String val = null;
                       
                       if (i_a<params.length) val = params[i_a];
                       if (!XUtils.isEmpty(val)) {
                           if (!val.equals("true") && !val.equals("false")) {
                           res.setFieldByName(_ERROR,1);
                           res.setFieldByName(_DESCRIPTION,"Boolean Flag must be 'true' or 'false'");
                           }
                       }
                       }      
                   }
                   
                    
               }
                   
               } catch (WAPIException e) {
                 e.printStackTrace();  
                 try {
                 res.setFieldByName(_ERROR,1);
                 res.setFieldByName(_DESCRIPTION,"Validation unable");
                 } catch (WAPIException ee) {
                   ee.printStackTrace();  
                 }
               }
              
              
               
    
           return res;
           
    }
    

}
