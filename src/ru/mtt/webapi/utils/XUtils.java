package ru.mtt.webapi.utils;


import com.google.gson.Gson;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.io.RandomAccessFile;

import java.security.MessageDigest;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.util.Random;

import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import ru.mtt.webapi.controller.IJSONRPCControlObject;
import ru.mtt.webapi.controller.JSONRPCControlObject;
import ru.mtt.webapi.controller.JSONRPCControlObjectExt;
import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XAction;
import ru.mtt.webapi.core.XCollection;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dispatcher.WebApiDispatcher;

/**
 *
 * Some utility
 *
 * @author rnasibullin@mtt.ru
 */
public class XUtils {


    static Logger logger = Logger.getLogger(XUtils.class);
    static final ConcurrentHashMap <String, Pattern> patterns = new ConcurrentHashMap <>();
    static final ConcurrentHashMap <String, File> logs = new ConcurrentHashMap <>();
    static final Gson gson = new Gson (); 
    static Properties excludeList = null;
    static File ffs = new File ("debug.properties");  
    static long times = 0L;

    public static String md5(String key) {
        
           String md5s = null;
           
           try {
               
           byte[] bytesOfMessage = key.getBytes("UTF-8");
           MessageDigest md = MessageDigest.getInstance("MD5");
           byte[] thedigest = md.digest(bytesOfMessage);
           md5s = new String(thedigest);    
           
           } catch (Throwable ee) {
             ee.printStackTrace();  
           }
           
           return md5s;
           
    }
    
    public static void info(Throwable xn, Logger logger ) {
        
        
           StackTraceElement[] xv =  xn.getStackTrace();
           for (StackTraceElement xx: xv) {
                logger.info (xx);
           }
        
    }

    public static String info(Throwable xn ) {
        
           String x = xn.getMessage()+" - "+xn.getClass().getName();
           StackTraceElement[] xv =  xn.getStackTrace();
           for (StackTraceElement xx: xv) {
                x+=xx+"\n";
           }
        
           return x;
           
    }
    
    public static IJSONRPCControlObject toJSONRPCControl(String data) {
    
    String res =  null;
    IJSONRPCControlObject rpc = null;
    int xs =  data.indexOf ("\"params\"");
    String smpl = data.substring(xs+8);
    smpl = smpl.replaceAll(" ","");
    smpl = smpl.replaceAll("\n","");
    smpl = smpl.replaceAll("\t","");
    smpl = smpl.replaceAll("\r","");
    
    try {
    
    if (smpl.charAt(1)=='{') {
        rpc = gson.fromJson(data, JSONRPCControlObjectExt.class);
    } else {
        rpc = gson.fromJson(data, JSONRPCControlObject.class);
    }
        
    } catch (com.google.gson.JsonSyntaxException ee) {

            ee.printStackTrace();
    }
     
    return rpc;                

    }


    public static Date getDate (long t, String tz) {
           TimeZone tzone = TimeZone.getTimeZone(tz);
           Calendar cc = Calendar.getInstance (tzone); 
           cc.setTimeInMillis(t);
           return  cc.getTime(); 
    };

 
    public static String toJSONPair(String id, Object val) {
           
           String vals = String.valueOf (val);
           if (val instanceof String) {
               vals = "\""+val+"\"";
           }
           String x = "\""+id+"\":"+vals; 
           return x;
        
    }
        
        
    public static String empty(String v) {
        return (v==null)?"":v;           
    }

    public static boolean isEmpty(String v) {
        return (v!=null && v.length()>0)?false:true;           
    }

    
    public static String md5(double key) {
           return md5(String.valueOf(key));           
    }
    
    
    public static String getmypid() {
        
           return String.valueOf(Thread.currentThread().getId());
        
    }

    static Random randomGenerator = new Random();

    public static String generate_code(int length) {

        
        double num = randomGenerator.nextDouble();
        String code = XUtils.md5(num);
        String xcode = code.substring(2, 2+length).toUpperCase();
        return xcode;
        
        
    }

    public static boolean inArray(String[] arr, String el) {
    
           boolean res = false;
           for (String x: arr) {
               if (el.equals(x)){
                   res = true;
                   break;
               }
           }
    
           return res;
    
    }

    public static long microtime(boolean flag) {
    
           return System.nanoTime();
    
    };
    
    public static String q(Object o) {
        if (o != null && (!"null".equals(o))) {
            return "\""+o+"\"";
        } else {
            return "null";
        }
    }
        

    public static double round(double val, int  dp) {
        
           int x = (int) (val/dp);
           if (false) return val;
           double pws = Math.pow(10.0, dp);
           double vs  = Math.round(val*pws);
           return vs/pws;
        
    }

    public static long parseTime (String ms) {

        long t_ms = 0;

        if (ms.endsWith("d"))  {

            String xss = ms.substring(0, ms.length()-1);
            int mss = Integer.parseInt(xss);
            t_ms = mss*86400*1000L;

        }

        if (ms.endsWith("h"))  {

            String xss = ms.substring(0, ms.length()-1);
            int mss = Integer.parseInt(xss);
            t_ms = mss*3600*1000L;

        }

        if (ms.endsWith("m"))  {

            String xss = ms.substring(0, ms.length()-1);
            int mss = Integer.parseInt(xss);
            t_ms = mss*86400*1000*31L;

        }

        if (ms.endsWith("min"))  {

            String xss = ms.substring(0, ms.length()-3);
            int mss = Integer.parseInt(xss);
            t_ms = mss*60*1000L;

        }

        return t_ms;

    }



 
    public static String transformHexCookie(long cookie, String prefix) {
        try {
            return prefix == null ? XUtils.toHex(XUtils.longToBytes(cookie)) : prefix + XUtils.toHex(XUtils.longToBytes(cookie));
        } catch(Exception e) {
//            logger.error(e.toString());
            return "";
        }
    }

    public static final byte[] longToBytes(long v) // SMU 2012/09/17
    {
        byte[] longBuffer = new byte[ 8 ];

        longBuffer[0] = (byte)(v >>> 56);
        longBuffer[1] = (byte)(v >>> 48);
        longBuffer[2] = (byte)(v >>> 40);
        longBuffer[3] = (byte)(v >>> 32);
        longBuffer[4] = (byte)(v >>> 24);
        longBuffer[5] = (byte)(v >>> 16);
        longBuffer[6] = (byte)(v >>>  8);
        longBuffer[7] = (byte)(v >>>  0);

        return longBuffer;
    }
    public static String toHex(byte md5[])
    {
        StringBuilder result = new StringBuilder(md5.length);
        char hexChar[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'
        };
        for (int i = 0; i < md5.length; i++)
            result.append(hexChar[md5[i] >> 4 & 15]).append(hexChar[md5[i] & 15]);

        return result.toString();
    }


    public static long getCookieRetargetAsLong(String cookieValue) {
        if (cookieValue == null || cookieValue.length() < 16)
            return 0;

        return XUtils.hex2Long(cookieValue.length() <= 16 ? cookieValue : cookieValue.substring(cookieValue.length() - 16, cookieValue.length()));
    }



    public synchronized static boolean check_exclude_list (String fs) {
        
    boolean xf = true;    
    
        if (ffs.exists()) {
            if (times != ffs.lastModified()) {
            
                try {
                    
                  excludeList = new Properties();
                  excludeList.load(new FileInputStream (ffs));
                  times = ffs.lastModified();
                
                } catch (IOException ee) {
                  ee.printStackTrace();    
                }
                
            };
            if (excludeList.containsKey(fs)) {
            
                String s = (String) excludeList.get (fs);
                xf = false;
                int max = 0;
                
                try {
    
                  max = Integer.parseInt(s);
                  File ff = logs.get (fs);
                  if (ff == null) {
                      ff = new File(fs);     
                      logs.put (fs, ff);  
                  }
    
                  long ntot = ff.length();
                  if (ntot<max) xf = true;
    
                } catch (Throwable ee) {
                  Sys.out("");
                }
            
            }
            
        }
    
    
    return xf;
    
    }

    public static void ilog(String fn, String d) {

        boolean log_details = false;
        String xs = System.getProperty("MODE","DEBUG");
        if (xs != null && xs.contains("DEBUG")) log_details = true;

        if (check_exclude_list (fn)) {
        if (log_details) {
            try {

                org.apache.commons.io.FileUtils.writeStringToFile(new File(fn), d+"\n", true);

            } catch (Throwable ee) {
                ee.printStackTrace();
            }
        }
        }

    }

    public static boolean hasDebugMode() {

        boolean log_details = false;
        String xs = System.getProperty("MODE","DEBUG");
        if (xs != null && xs.contains("DEBUG"))  return true;
        return false; 
    }


    public static void plog(String fn, String d) {

        boolean log_details = false;
        String xs = System.getProperty("MODE","DEBUG");
        if (xs != null && xs.contains("PRODUCTION")) log_details = true;

        if (log_details) {
            try {

                org.apache.commons.io.FileUtils.writeStringToFile(new File(fn), d+"\n", true);

            } catch (Throwable ee) {
                ee.printStackTrace();
            }
        }

    }

    public static String wordwrap(String val, int length, String rplc, boolean flag) {
    
           String xs = val.substring(0,length);
           return xs+rplc;
    
    };
 
    public static Properties getProperties(String xpath) {

        Properties ps = new Properties();
        InputStream in = XUtils.class.getClassLoader().getResourceAsStream(xpath);

        try {

            List<String> ls = org.apache.commons.io.IOUtils.readLines(in);
            for (String o: ls) {
                if (o.startsWith("#")) continue;
                int xs = o.indexOf("=");
                if (xs<0) continue;
                String k = o.substring(0, xs);
                String v = o.substring(xs+1);
                Double dv = null;
                Integer iv = null;
                try {

                    dv = Double.parseDouble(v);

                } catch (NumberFormatException ex) {
                    //      ex.printStackTrace();
                }

                try {

                    iv = Integer.parseInt(v);

                } catch (NumberFormatException ex) {
                    //      ex.printStackTrace();
                }

                if (iv != null) {

                    ps.put (k, iv);

                } else if (dv != null) {

                    ps.put (k, dv);

                } else {

                    ps.put (k, v);

                }

            }

        } catch (IOException ee) {
            ee.printStackTrace();
        }

        return ps;

    }

    public static long hex2Long(String hexadecimal) {

        char[] chars;
        char c;
        long value = 0;
        int i;
        byte b;

        try {

            if (hexadecimal == null)
                throw new Exception("hexadecimal is null");

            chars = hexadecimal.toUpperCase().toCharArray();
            if (chars.length != 16)
                throw new Exception("Incomplete hex value");

            value = 0;
            b = 0;
            for (i = 0; i < 16; i++) {
                c = chars[i];

                if (c >= '0' && c <= '9')
                    value = ((value << 4) | (0xff & (c - '0')));
                else if (c >= 'A' && c <= 'F')
                    value = ((value << 4) | (0xff & (c - 'A' + 10)));
                else
                    throw new Exception("Invalid hex character: " + c);
            }

        } catch (Exception e) {
            return 0;
        }

        return value;

    }
    
    public static String xor_crc(String code) {
           String xc = code; 
           int buf = 0;
           int size = code.length();
           for (int i = 0; i < size; i++) {
                buf&= 0xffff;
                int val = code.charAt(i);
                val = ~val & 0xff;
                buf+=val;
                buf+=code.charAt(i)*256;
           }
           String bufs = String.valueOf(buf).trim();
           String xcc = new String (bufs.getBytes(),2,2);
           return xcc;
    };
    
    
    
    
    
    
    
    public static String utf8_decode(String str) {

           String xs = str;
           try {
           byte[] bs = str.getBytes("utf-8");
           xs = new String(bs, "utf-8");
           } catch (Throwable ee) {
           ee.printStackTrace();    
           }
           return xs; 

    }


    public static boolean isEmpty  (Object [] params) {
        
           boolean res = false; 
           if (params == null) {

               res = true; 

           } else {

               res = true; 
               for (Object o: params) {
                    if (!isEmpty((String)o)) {
                        res = false;
                    }
               }

           }

           return res; 
        
    }

    public static boolean isMatchedWith (String v, String ps) {
        
        Pattern p = patterns.get (ps);
        if (p == null) {
            p = Pattern.compile(ps);
            patterns.put (ps, p);  
        }
        
        Matcher m = p.matcher(v);
        boolean b = m.matches();
        return b;
        
    }


    public static void main5 (String[] a) {

      
   //        System.out.println (isMatchedWith ("test \"params\" :   {  test", "^/.(\"params\").[:](\\s*)./$")); 
        double s = 89.33333333333333333;
        System.out.println (s+" = "+XUtils.round(s,2));    

    }

    public static void main6 (String[] a) {

      
        try {
            
        Date ds = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse ("2015-06-09 11:00:43");    
        System.out.println (ds);
            
        System.out.println (new DecimalFormat("####.##").format(7676.0289002));    
        System.out.println (new DecimalFormat("######").format(76762));    
            

        } catch (Throwable ee) {
          ee.printStackTrace();  
        }
    
    
    
    }

    public static void main3 (String[] a) {

      
    //        System.out.println (isMatchedWith ("test \"params\" :   {  test", "^/.(\"params\").[:](\\s*)./$"));
        System.out.println (XUtils.isMatchedWith("07.04.2003", "[\\d]{2,2}[.][\\d]{2,2}[.][\\d]{4,4}"));    
        System.out.println (XUtils.isMatchedWith("cccc07.04.2003", "[\\d]{2,2}[.][\\d]{2,2}[.][\\d]{4,4}"));   
        System.out.println (XUtils.isMatchedWith("883140710176122", "\\d+"));   
        
         double amount = 12.4;
        float ff = (float)amount;
        System.out.println (ff);

    }


    public static void main4 (String[] a) {
        
        
        try {
            
        List <String>  lst = FileUtils.readLines(new File("src/countries.properties"));
            
        Properties pss = new Properties ();
        for (String x:  lst) {
             String[] xs = x.split("[,]");
             System.out.println (x);
             pss.put(xs[1].trim(), xs[0].trim());
        }

        pss.store(new FileWriter("countries.properties"), "ISO-3166-1");
            
        } catch (Throwable ee) {
          ee.printStackTrace();
        }
                                  
    }
 
    public static void main1 (String[] a) {
        
        String promo_code = "1AA7-A1DF-2316";
        promo_code = promo_code.toUpperCase();
        promo_code = promo_code.replaceAll("[-]", "");
        int nx = promo_code.length();
        String crc = promo_code.substring(nx - 2);
        String code = promo_code.substring(0, nx - 2);
        String crc2 =  xor_crc(code);
                                  
    }
    
    
    public static void main2 (String[] a) {
        
           int n_node = 3;
           Long ls1 = new Long(Integer.MAX_VALUE);
           Long ls2 = new Long(-Integer.MIN_VALUE);
           long dx = (ls1.longValue() - ls2.longValue())/n_node;
           
           System.out.println (dx);

           System.out.println (Integer.MIN_VALUE);
           System.out.println (Integer.MIN_VALUE+dx);
           System.out.println (Integer.MAX_VALUE-dx);
           System.out.println (Integer.MAX_VALUE);
           
           System.out.println (" --------------------------------------------- ");
       
           System.out.println (md5("test1@mail.ru").hashCode());
           System.out.println (md5("test2@mail.ru").hashCode());
           
           System.out.println (utf8_decode("\u0415\u041f\u0421_\u041f\u044d\u0439\u043e\u043d\u043b\u0430\u0439\u043d"));
        
    }
    
    
    public static String compileParamString(String x) {     
    
    String xp = x;
    if (xp != null) {
        
        while (true) {
        int xs = xp.indexOf("{SYSTEM."); 
        if (xs>=0) {
        int xst = xp.indexOf("}", xs); 
        String ids = xp.substring(xs+1, xst);  
        int ts = ids.indexOf(".");
        String idd = ids.substring(ts+1);
        String val = System.getProperty(idd);
        if (val == null) val="<"+idd+">";
        xp = xp.replaceAll ("\\{"+ids+"\\}", val);
        continue;    
        }
        break;    
        }
    }
    return xp;

    }
    
    
    public static int copy (String f, OutputStream out) {
           
        byte[] buffer = new byte[9096]; 
        int nt = 0;
        try {
            
            RandomAccessFile F = new RandomAccessFile (new File(f),"r"); 
            int n;
            while ((n=F.read(buffer))>0) {
                    out.write(buffer, 0, n);
                    nt+=n; 
            };
            out.close ();
            F.close();
            
        } catch (Throwable ee) {
            
          XUtils.ilog ("log/_xutils_copy.log", XUtils.info (ee));   
          
        }
        
        return nt;
        
    }
        
    public static void main001 (String[] a) {
      
        try {
            
          String test = "{SYSTEM.PAR}  FDSDCVVVV";    
          System.out.println (compileParamString(test));   
            
          FileOutputStream out1 = new FileOutputStream ("outtext1.wav");  
          new XUtils().copy ("x.wav",out1);  
            
          FileOutputStream out2 = new FileOutputStream ("outtext2.wav");  
          new XUtils().copy ("x.wav", out2);   
            
        } catch (Throwable ee) {
          ee.printStackTrace();  
        }
    
    
    
    }


    public static void main (String[] a) {

      
        XUtils.ilog("log/log.txt","xs");
        int i = 0;
        while ((i++)<1000)
        XUtils.ilog("log/log2.txt","xsffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffccccccccc");
        
    
    }
  

    

}
