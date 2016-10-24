package ru.mtt.webapi.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.io.PrintWriter;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import java.net.URLConnection;

import java.security.cert.X509Certificate;

import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;

import org.apache.mina.filter.logging.LoggingFilter;

import org.apache.mina.transport.socket.nio.NioSocketConnector;

import ru.mtt.webapi.core.IConstants;
import ru.mtt.webapi.core.WAPIException;
import ru.mtt.webapi.core.XCollection;
import ru.mtt.webapi.core.XSmartObject;
import ru.mtt.webapi.dom.SimpleXSmartObject;
import ru.mtt.webapi.io.IOClient;

/**
 *  CURL utility implementation
 *
 *  @author rnasibullin@mtt.ru
 */

public class Curl extends Properties  {
    
    private final String USER_AGENT = "Mozilla/5.0";
    static Logger log = Logger.getLogger(Curl.class);
    public static final String CURLOPT_URL = "CURLOPT_URL";
    public static final String CURLOPT_USERAGENT = "CURLOPT_USERAGENT";
    public static final String CURLOPT_COOKIE = "CURLOPT_COOKIE";
    public static final String CURLOPT_VERBOSE = "CURLOPT_VERBOSE";
    public static final String CURLOPT_SSL_VERIFYPEER = "CURLOPT_SSL_VERIFYPEER";
    public static final String CURLOPT_SSL_VERIFYHOST = "CURLOPT_SSL_VERIFYHOST";
    public static final String CURLOPT_REFERER = "CURLOPT_REFERER";
    public static final String CURLOPT_FAILONERROR = "CURLOPT_FAILONERROR";
    public static final String CURLOPT_FOLLOWLOCATION = "CURLOPT_FOLLOWLOCATION";
    public static final String CURLOPT_RETURNTRANSFER = "CURLOPT_RETURNTRANSFER";
    public static final String CURLOPT_TIMEOUT = "CURLOPT_TIMEOUT";
    public static final String CURLOPT_POST = "CURLOPT_POST";
    public static final String CURLOPT_METHOD = "CURLOPT_METHOD";
    public static final String CURLOPT_POSTFIELDS = "CURLOPT_POSTFIELDS";
    public static final String CURLOPT_AUTHORIZATION = "CURLOPT_AUTHORIZATION";
    

    Socket socket =  null; 
    


    public PostMethod factory4PostM(String serviceUrl, String data) {
    PostMethod post = new PostMethod(serviceUrl);
    String ref = this.getProperty(Curl.CURLOPT_REFERER);
    String userAg = this.getProperty(Curl.CURLOPT_USERAGENT);
    String cookie = this.getProperty(Curl.CURLOPT_COOKIE);
    post.setRequestBody(data);
    String sslVer = this.getProperty(Curl.CURLOPT_SSL_VERIFYHOST); 
    String fail = this.getProperty(Curl.CURLOPT_FAILONERROR);
    
    String floc = this.getProperty(Curl.CURLOPT_FOLLOWLOCATION);
    String trn = this.getProperty(Curl.CURLOPT_RETURNTRANSFER);
    if (floc.equals("1")) post.setFollowRedirects(true);
    post.setFollowRedirects(false);

        
    post.setRequestHeader("User-Agent", userAg);
    post.setRequestHeader("Referer", ref);
    post.setRequestHeader("Cookie", cookie);
    post.setRequestHeader("Accept", "*/*");
    post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    post.setRequestHeader("Content-Length", String.valueOf(data.getBytes().length));
    Header[]  h = post.getRequestHeaders();

    log.info ("Post Curl Headers2:");

    for (Header x:  h) {
         log.info (x.getName()+" = "+x.getValue());
    }

    log.info ("Post Body qs:");
    return post;

    }

    public HttpMethod factory4GetM (String serviceUrl, String data) {   
    GetMethod get = new GetMethod(serviceUrl);
    String ref = this.getProperty(Curl.CURLOPT_REFERER);
    get.setRequestHeader("Referer", ref);
    get.setQueryString(data);
    return get;
    }

    public void setOpt(String parId, Object val) {
            
           put (parId, val);           

    };


    public static void  init_ssl_check() throws Exception {
        
        TrustManager[] trustAllCerts = new TrustManager[] {
           new X509TrustManager() {
              public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
              }

              public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

              public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

           }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                
              log.info ("Hostname: "+hostname);  
              return true;

            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        /*
         * end of the fix
         */
        
    }
     
    public void close () {
        
    }
    
    
    public XSmartObject execute() {

       XSmartObject x = null;
       String serviceUrl = (String) this.get(CURLOPT_URL);
       
       if (serviceUrl.startsWith("tcp:")) {
           
             STCPClient cli  =  new STCPClient ();
             String data = Curl.this.getProperty(Curl.CURLOPT_POSTFIELDS);
             try {
                 
                 
               XUtils.ilog ("log/curl.log", "data: " + data); 
               Object v = cli.sendMessage(data.getBytes());
               x = new SimpleXSmartObject ("body", v);  

             } catch (Throwable ee) {

                 XUtils.ilog ("log/curl.log", XUtils.info(ee)); 

             }
           
       } else if (serviceUrl.startsWith("http")) {
    
          Integer i = (Integer)  get(Curl.CURLOPT_POST);
          String meth = (String) getProperty(Curl.CURLOPT_METHOD, "GET");
          System.out.println(i + "  :  " + meth);
          if (i != null && i.intValue() == 1) {
              meth = "POST"; 
          }

          if ("GET".equals(meth)) return send_get(serviceUrl);
          else if ("PUT".equals(meth)) return send_put(serviceUrl);
          else return send_post(serviceUrl);
       
       }

       return x; 
    
    }
    


    private XSmartObject send_post(String serviceUrl) {
    XSmartObject x = null;

    try {
    
    URL obj = new URL(serviceUrl);
    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
    Integer xi  = (Integer) this.get (Curl.CURLOPT_TIMEOUT);
    Boolean ssl = (Boolean) this.get(CURLOPT_SSL_VERIFYPEER); // this line makes it work under https
    String data = this.getProperty(Curl.CURLOPT_POSTFIELDS);
    log.debug ("HttpService:"+serviceUrl);
    log.debug ("SSL:"+ssl);
    log.debug ("Data:"+data);
    
    if (conn != null) {

    if (xi != null) conn.setConnectTimeout(xi);
    
    try {
    
          if (ssl != null && ssl.booleanValue()) init_ssl_check();
    
    } catch (Throwable ee) {
            
          ee.printStackTrace();    
          
    }
    


    String ref = this.getProperty(Curl.CURLOPT_REFERER);
    String userAg = this.getProperty(Curl.CURLOPT_USERAGENT);
    String cookie = this.getProperty(Curl.CURLOPT_COOKIE);
    String sslVer = this.getProperty(Curl.CURLOPT_SSL_VERIFYHOST); 
    String fail = this.getProperty(Curl.CURLOPT_FAILONERROR);
    String auth = this.getProperty(Curl.CURLOPT_AUTHORIZATION);
    if (auth != null) conn.setRequestProperty("Authorization", auth);
    
    String floc = this.getProperty(Curl.CURLOPT_FOLLOWLOCATION);
    String trn = this.getProperty(Curl.CURLOPT_RETURNTRANSFER);
    conn.setFollowRedirects(false);
    if (floc != null && floc.equals("1")) conn.setFollowRedirects(true);
    conn.setRequestProperty("User-Agent", userAg);
    if (ref != null) conn.setRequestProperty("Referer", ref);
    conn.setRequestProperty("Cookie", cookie);
    conn.setRequestProperty("Accept", "*/*");
    if (data != null) { 
    conn.setRequestProperty("Content-Encoding", "utf-8");
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes("utf-8").length));
    }
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setDoInput(true);

    /*
    String userpass = "user" + ":" + "pass";
    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
    conn.setRequestProperty ("Authorization", basicAuth);
    */
    
    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
    if (data != null) out.write(data);
    out.flush();
    out.close();
    int responseCode = conn.getResponseCode();
    log.info ("Response Code : " + responseCode);

    InputStreamReader xs = new InputStreamReader(conn.getInputStream(),"utf-8");
    String xss = IOUtils.toString(xs);
    String info = "See Error details in the Log.";
    xs.close();
    boolean err = true;

    if (xss != null) {
    err = false;
    }
    
    log.debug (xss);
    if (err) {

    try {
        
      x = new XCollection (null);  
      x.setFieldByName(IConstants._ERROR,1);                           
      x.setFieldByName(IConstants._DESCRIPTION,info);                           
                   
    } catch (WAPIException eex) {
      eex.printStackTrace();    
    }

    } else {

    x = new SimpleXSmartObject ("body", xss);  

    }
    
    }
    
    } catch (Exception e) {
    e.printStackTrace();
    }
    
    return x;
    
    }

    public XSmartObject send_get(String url) {
        
        XSmartObject x = null; 

        try { 
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");

                    //add request header
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    con.setRequestProperty("Accept-Charset", "utf-8");
                    String auth = this.getProperty(Curl.CURLOPT_AUTHORIZATION);
                    if (auth != null) con.setRequestProperty("Authorization", auth);

                    int responseCode = con.getResponseCode();
                    System.out.println("\nSending 'GET' request to URL : " + url);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(),"utf-8"));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                    }
                    in.close();

                    //print result
                    System.out.println(response.toString());
                    x = new SimpleXSmartObject ("body", response.toString());  
            
        } catch (Throwable ee) {
 
          x = new SimpleXSmartObject ("error", "{\"Error\":\""+ee.getMessage()+"\"}");  
          XUtils.ilog("log/curl.log", curl_info());
          XUtils.ilog("log/curl.log", XUtils.info(ee));
            
        }
            
        return x;
                    
    }


    private String curl_info() {
            String x = "CURLOPT_METHOD: "+this.getProperty(Curl.CURLOPT_METHOD)+"\n"+
                       "CURLOPT_AUTHORIZATION: "+this.getProperty(Curl.CURLOPT_AUTHORIZATION)+"\n"+
                       "CURLOPT_URL: "+this.getProperty(Curl.CURLOPT_URL)+"\n"+
                       "CURLOPT_POSTFIELDS: "+this.getProperty(Curl.CURLOPT_POSTFIELDS)+"\n";
            return x;
    }


    public XSmartObject send_put(String url) {
        
        XSmartObject x = null; 

        try { 
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("PUT");

                    //add request header
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    con.setRequestProperty("Accept-Charset", "utf-8");
                    String auth = this.getProperty(Curl.CURLOPT_AUTHORIZATION);
                    if (auth != null) con.setRequestProperty("Authorization", auth);
                    byte[] data = (byte[]) this.get(Curl.CURLOPT_POSTFIELDS);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    OutputStream out = con.getOutputStream();
                    if (data != null) out.write(data);
                    out.flush();
                    out.close();
            
                    int responseCode = con.getResponseCode();
                    System.out.println("\nSending 'GET' request to URL : " + url);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(),"utf-8"));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                    }
                    in.close();

                    //print result
                    System.out.println(response.toString());
                    x = new SimpleXSmartObject ("body", response.toString());  
            
        } catch (Throwable ee) {
    
            x = new SimpleXSmartObject ("error", "{\"Error\":\""+ee.getMessage()+"\"}");  
            XUtils.ilog("log/curl.log", curl_info());
            XUtils.ilog("log/curl.log", XUtils.info(ee));
            
        }
            
        return x;
                    
    }
    
    
    public Curl() {
        
           super();
           
    }
    
    public static void main1 (String[] args) {
        
           Curl c = new Curl();
           try {
           
             c.send_get("http://127.0.0.1:19741/xresource"); 
           
           } catch (Throwable ee) {
           
             ee.printStackTrace();    
           
           }
        
    }

    public static void main (String[] args) {
        
        
            Curl c = new Curl();
            c.setOpt(Curl.CURLOPT_URL, "tcp://127.0.0.1:18000");
            c.setOpt(Curl.CURLOPT_POSTFIELDS,"d1.mtt.ru:/3285.wav");
            XSmartObject oo = (XSmartObject) c.execute();
            
            try {             
            
            byte[] v = (byte[]) oo.getFieldByName("VALUE");
            System.out.println ("byte[]:  "+v.length);
            
            } catch (Throwable e) {
                
            }
        
        
    }
    
    public static void main3 (String[] args) {
        
        
            Curl c = new Curl();
            c.setOpt(Curl.CURLOPT_URL, "http://127.0.0.1:19741/xresource");
            c.setOpt(Curl.CURLOPT_POST, 0);
            c.setOpt(Curl.CURLOPT_TIMEOUT, 100000);
            System.out.println (c.execute());
            
        
        
    }
    
    
    public static void main2 (String[] args) {
        
        
            Curl c = new Curl();
            c.setOpt(Curl.CURLOPT_URL, "tcp://127.0.0.1:9091");
            String data = "{\"id\": \"1\", \"jsonrpc\": \"2.0\", \"method\": \"getFolderByPathAsMAP\",\"params\":{\"path\": \"/ru/mtt/icloud/bizrules\"}}";
            c.setOpt(Curl.CURLOPT_POSTFIELDS, data);
            c.setOpt(Curl.CURLOPT_TIMEOUT, 100000);
            System.out.println (c.execute());
            
        
        
    }
    
    class TCPClient extends IoHandlerAdapter {
        NioSocketConnector connector = new NioSocketConnector();
        IoSession session;

        public TCPClient () {
              connector.setConnectTimeoutMillis(12000);
              connector.getFilterChain().addLast("codec",
                      new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
              connector.setHandler(TCPClient.this);
        }
        
        public Object sendMessage(Object message) throws Exception {
            
            String url = Curl.this.getProperty(Curl.CURLOPT_URL);
            String[] pUrl = url.split("[:/]");
            String host = pUrl[3];
            String port = pUrl[4];
            ConnectFuture future = connector.connect(new InetSocketAddress(host, Integer.parseInt(port)));
            future.awaitUninterruptibly();
            session = future.getSession();
            session.getConfig().setUseReadOperation(true);
            WriteFuture wf = session.write(message);
            while (!wf.isWritten()) {
                   Thread.currentThread().sleep(20);
            }
            
            System.out.println (wf.isWritten()+" - "+wf.isDone()+ " - "+session.isConnected());
            ReadFuture rf = session.read(); 
            future.awaitUninterruptibly();
            Object m = rf.getMessage();
            
            System.out.println ("Mess:"+m);
            System.out.println (session.getReadBytes() + "  --  " +rf.isClosed()+" - "+rf.isDone()+" - "+rf.isRead());

            rf.setClosed();
            connector.dispose();
            return m;

        }
        
        
    }
    
    class STCPClient extends IOClient {
        
        public STCPClient () {
        }
        
        public Object sendMessage(Object message) throws Exception {
            
            String url = Curl.this.getProperty(Curl.CURLOPT_URL);
            String[] pUrl = url.split("[:/]");
            String host = pUrl[3];
            String port = pUrl[4];
            STCPClient.this.setHost(host);
            STCPClient.this.setPort(Integer.parseInt(port));
            return STCPClient.this.processRequest  (message);
            
        }
        
        
    }
    

    
}
