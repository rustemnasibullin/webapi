package ru.mtt.webapi.core;

import java.text.SimpleDateFormat;


/**
 * Interface with basic constants for  WEBAPI Mail.ru 
 * 
 * @author rnasibullin@mtt.ru
 */

public interface IConstants {


    public final static int MIN_AMOUNT = 300;
    public final static int MAX_AMOUNT = 1500; 
    public final static int PERCENT    =  20; 

    public final static String _FREQ = "FREQ";
    public final static String _RESPT = "RESPT";
    public final static double _FREQ_MAX = 10.0;
    public final static double _RESPT_MAX = 400.0;
    public final static int _NMaXTrend = 60;
    public final static String _KEY = "KEY";
    public final static String _ID = "ID";
    public final static String _ERROR = "ERROR"; 
    public final static String _CODE = "CODE"; 
    public final static String _DESCRIPTION = "DESCRIPTION";    
    public final static String _BLNK = "";    
    public final static String _SIZE = "SIZE";
    public final static SimpleDateFormat frmt  = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat frmt2  = new SimpleDateFormat ("Y-m-d"); 
    public final static SimpleDateFormat frmt1  = new SimpleDateFormat ("dd.MM.yyyy");
    public final static SimpleDateFormat frmt3  = new SimpleDateFormat ("YYYY-MM-dd");
    public final static String[] IVRLang = new String[]{"ar","cm","de","el","en","es","fr","he","it","pt","ru","sv","yu"};
    public final static String CACHE_TYPE_BALANCES = "balances_";
    public final static String CACHE_TYPE_TARIFFS = "tariffs_";
    public final static String CACHE_TYPE_CALL_HISTORY = "callhistory_";
    public final static String CACHE_TYPE_PAYMENT_HISTORY = "paymenthistory_";
    public final static String CACHE_TYPE_USERCARD = "usercard_";
    public final static int CACHE_ROUTING_TABLE_CACHE_LIFETIME = 86400;
    public final static byte _CHAR  = 1;
    public final static byte _LONG  = 2;
    public final static byte _INT   = 3;
    public final static byte _DBL   = 4;
    public final static byte _BOOL  = 5;
    public final static long _TERM  = 3600;
    public final static int _TICKS_MAX = 18000;
    public final static int _ERR_CRITICAL = 5;
    public final static double _AVFACTOR_THRESHOLD = 0.6;
}
