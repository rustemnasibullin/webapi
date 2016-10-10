package ru.mtt.webapi.mina;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.HttpServerCodec;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.DemuxingProtocolDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.http.HttpServerDecoder;
import org.apache.mina.http.api.DefaultHttpResponse;
import org.apache.mina.http.api.HttpRequest;
import org.apache.mina.http.api.HttpVersion;
import org.apache.mina.http.api.HttpStatus;
import org.apache.mina.http.api.HttpEndOfContent;

import org.apache.mina.http.api.HttpMethod;

import ru.mtt.rservice.core.MIBControlObject;
import ru.mtt.webapi.core.XBUSConnector;
import ru.mtt.webapi.core.XConfigurableObject;
import ru.mtt.webapi.dispatcher.WebApiDispatcher;
import ru.mtt.webapi.utils.MultipartInputStream;
import ru.mtt.webapi.utils.XUtils;


/**
 *
 * Highly  Load  Web server based upon  Apache MINA framework.
 *
 * @author rnasibullin@mtt.ru
 */
public class HTTPMinaServer extends XConfigurableObject  {

    public static final int DONE = 1;
    boolean flDispatch = false;

    final Object lock = new Object();
    IoAcceptor acceptor = new NioSocketAcceptor();
    boolean useSequantial = false;
    int pp_max_size = 512000;
    LinkedBlockingQueue<Callable> posponedJobsQueue = null;
    ConcurrentHashMap<Long,Future> processorsExec = new ConcurrentHashMap<Long,Future>();
    ConcurrentHashMap<Long,Callable> processors = new ConcurrentHashMap<Long,Callable>();
    AtomicLong prcCounter = new AtomicLong(0);
    XBUSConnector instance = XBUSConnector.getInstance();
    MinaRequestHandler  requestHandler = null;
    WebApiDispatcher dispatcher;
    DemuxingProtocolDecoder protocolDecoder =  null;


    public void setFlDispatch(boolean flDispatch) {
        this.flDispatch = flDispatch;
    }

    public boolean isFlDispatch() {
        return flDispatch;
    }

    public void setDispatcher(WebApiDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public WebApiDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setRequestHandler(MinaRequestHandler requestHandler) {
           this.requestHandler = requestHandler;
    }

    org.apache.log4j.Logger logger = Logger.getLogger(HTTPMinaServer.class);
    org.apache.log4j.Logger rlogger = Logger.getRootLogger();

    int serverPort = 1001;
    int n_threads = 16;

    private final ExecutorService service = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(false).setPriority(Thread.MAX_PRIORITY)
                    .setNameFormat("HTTPD-%d")
                    .build());

    private ExecutorService serviceSecondary = null;


    public MinaRequestHandler getRequestHandlerInstance() {
           MinaRequestHandler  rHandler = requestHandler;
           if (rHandler == null || flDispatch) rHandler = dispatcher;
           return rHandler;
    }


    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() {

        rlogger.warn("start");
        instance.populate(this);
        XUtils.ilog("log/regService.log", "Start AMINA-HTTPD");
        ((NioSocketAcceptor) acceptor).setReuseAddress(true);
        HttpServerCodec codec = new HttpServerCodec();
        acceptor.getFilterChain().addLast("httpServer", codec);
        acceptor.setHandler(new IoHandlerAdapter() {
            
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {


                String message_body = null;
                HttpMethod meth = null;
                String content_type = "application/json;charset=utf-8";
                logger.debug(session.getId() + " X:   " + content_type+"  -  "+message.getClass().getName());    
                Callable processor = processors.get (session.getId());
                
                XUtils.ilog("log/regService.log", "AMINA-HTTPD message:" + message);
                   
                if (message instanceof HttpRequest) {
                    
                    HttpRequest qst = (HttpRequest) message;
                    meth = qst.getMethod();
                    content_type =  qst.getContentType();
                    logger.debug(" X:   " + content_type);    
                    if (processor == null) {
                        processor = new QuestProcessor (session, qst);
                        processors.put (session.getId(), processor); 
                    }
             
                    if (meth.equals(HttpMethod.GET)) {
                        Future fs = serviceSecondary.submit(processor);
                        if (fs == null) {
                        posponedJobsQueue.offer(processor);
                        } else {
                        processorsExec.put(session.getId(), fs);
                        }
                        return;
                    }
                    
                 }

                 if (message instanceof IoBuffer) {
                     
                            IoBuffer simbuf = (IoBuffer) message;
                            byte[] buff = simbuf.array();
                            message_body = new String(buff, "UTF-8");
                   
                            if (processor == null) {
                                
                                processor = new QuestProcessor (session, message_body);
                                processors.put (session.getId(), processor); 
                            
                            } else {
                                
                                if (processor instanceof IMinaHttpProcessor)  {
                                    IMinaHttpProcessor prc = (IMinaHttpProcessor) processor;
                                    prc.setMessageEncoded(message_body);
                                    
                                }
                                
                            }
                                      
                            Future fs = serviceSecondary.submit(processor);
                            
                            if (fs == null) {
                                posponedJobsQueue.offer(processor);
                            } else {
                                processorsExec.put(session.getId(), fs);
                            }

                }
               
            }
        });


        try {
            XUtils.ilog("log/regService.log", "AMINA-HTTPD serverPort:" + serverPort);

            acceptor.bind(new InetSocketAddress(serverPort));
            XUtils.ilog("log/regService.log", "AMINA-HTTPD serverPort:" + serverPort);

        } catch (IOException ee) {
            XUtils.ilog("log/regService.log", "serverPort: "+serverPort+"  "+XUtils.info(ee));
            ee.printStackTrace();
        }


        // start postponed  job provessing






    }


    @Override
    public void doConfig() {

        protocolDecoder =  new DemuxingProtocolDecoder();
        MessageDecoder messageDecoder = null;
        String s_n_threads = ps.getProperty("mina_threads");
        if (s_n_threads != null) {
            n_threads = Integer.parseInt (s_n_threads);
            serviceSecondary = Executors.newFixedThreadPool(n_threads, new ThreadFactoryBuilder()
                    .setDaemon(false).setPriority(Thread.MAX_PRIORITY)
                    .setNameFormat("WebAPI-%d")
                    .build());
        }


        String pp = ps.getProperty("pp_max_size");
        if (pp != null) {
            pp_max_size = Integer.parseInt (s_n_threads);
        }


        serviceSecondary.submit(new Runnable() {

            public void run() {

                int ticks = 0;
                Long toRemove = null;
                while (!Thread.currentThread().isInterrupted()) {

                    logger.debug("Process Markers: "+processors.size()+"/"+posponedJobsQueue.size());

                   
                    try {

                        toRemove = null;
                        for (Future f: processorsExec.values()) {


                             if (f.isDone() || f.isCancelled()) {
                                 Object o = f.get();
                                 if (o != null && o instanceof QuestProcessor) {

                                     QuestProcessor p = (QuestProcessor) o;

                                     logger.debug("Close session: "+p.getIdQP());
                                     if (p.session.isWriterIdle()) {
                                     CloseFuture clf = p.session.close(false);
                                     while (!clf.isDone());
                                             Thread.currentThread().sleep(100);
                                     }

                                     toRemove = p.getIdQP();
                                     logger.debug("Remove: "+toRemove);
                                     break;
  
                                 }
                             }




                        }


                        if (toRemove != null) {
                            processors.remove (toRemove);
                            processorsExec.remove (toRemove);
                        }

                        Thread.sleep(10);
                        ticks++;
                        
                        if (ticks == _TICKS_MAX) {
          
                            ticks = 0; 
                            rlogger.warn("releived");

                        }
                        

                    } catch (Throwable ee) {
                        XUtils.ilog("log/regService.log",XUtils.info (ee));
                        break;
                    }
                }

            };


        });


        posponedJobsQueue = new LinkedBlockingQueue<Callable> (pp_max_size);
        boolean useExternal = false;

        if (!useExternal) {
        service.submit(new Runnable() {

            public void run() {

                while (!Thread.currentThread().isInterrupted()) {
                try {

                    Callable processor = posponedJobsQueue.take();
                    
                        
                    if (processor instanceof IMinaHttpProcessor) {
                    IMinaHttpProcessor rs = (IMinaHttpProcessor) processor;
                    if (rs.getSession().isConnected()) {

                        Future fs = serviceSecondary.submit(processor);
                        if (fs == null) {
                            Thread.sleep(200);
                            posponedJobsQueue.offer(processor);
                        } else {
                            processorsExec.put(rs.getSession().getId(), fs);
                        }

                    }
                    
                    
                    }

                    break;

                } catch (Throwable ee) {
                    ee.printStackTrace();
                    break;
                }
                }

        };


        });
        }


    }



    public void stop() {

          try {

            acceptor.unbind();
            acceptor.dispose(true);
            posponedJobsQueue.clear();
            logger.info("HTTP MINA Server stopped");

            for (Future f: processorsExec.values()) {
                 f.cancel(true);
            }
            serviceSecondary.shutdownNow();

          } catch (Throwable ee) {

            logger.info("HTTP MINA Server stopping error");

          }

            rlogger.warn("stop");


    }

    public void questProcessingEvent(int eventId, Object processorObject) {
        
           QuestProcessor Ps = (QuestProcessor)  processorObject;
           Future f = processorsExec.get(Ps.getIdQP());
           logger.debug("Closed Data: "+Ps.getIdQP());
           if (eventId == DONE) {
       //        processors.remove(Ps.getIdQP());
           }

    }

    class QuestProcessor implements Callable, IMinaHttpProcessor {

        IoSession session;

        String messageDecoded;
        String messageEncoded;
        Long idQP = 0L;
        HttpRequest httpRequest;
        Properties props = new Properties();
            
        public QuestProcessor() {

        }

        public void setMessageEncoded(String messageEncoded) {
            
            this.messageEncoded = messageEncoded;
            
            if (httpRequest != null) {
            String contentType = httpRequest.getContentType();
            String [] cType = contentType.split("[;]");
            for (String x: cType) {
    
                 int nx = x.indexOf("=");
                 if (nx>0) {
                     
                     String key =  x.substring(0, nx).trim();
                     String val =  x.substring(nx+1).trim();
                     props.put (key, val);
                     
                 }
                
            }

            
            if ("multipart/form-data".equals(cType[0].trim())) {

            try {
                               
            String boundary = props.getProperty ("boundary"); 
            String charSet  = props.getProperty ("charset","utf-8"); 

            MultipartInputStream  mi = new MultipartInputStream  (messageEncoded.getBytes(), boundary.getBytes());
            
           
            byte[] ds = mi.decode();
            setMessageDecoded (new String(ds,charSet));

            } catch (Throwable ee) {
            
            ee.printStackTrace();
                
            }

            } else {

            setMessageDecoded (messageEncoded);
                
            }

        }
            
        }

        public String getMessageEncoded() {
            return messageEncoded;
        }

        public QuestProcessor(IoSession sess, String messDecoded) {

               idQP = sess.getId();
               session = sess;
               messageDecoded = messDecoded;

        }

        public QuestProcessor(IoSession sess, HttpRequest mess) {

               idQP = sess.getId();
               session = sess;
               httpRequest = mess;
               
        }


        public void setSession(IoSession session) {
            this.session = session;
        }

        public IoSession getSession() {
            return session;
        }

        public void setMessageDecoded(String messageDecoded) {
            this.messageDecoded = messageDecoded;
        }

        public String getMessageDecoded() {
            return messageDecoded;
        }

        public Long getIdQP() {
               return idQP;
        }

        public void setIdQP(Long idQP) {
               this.idQP = idQP;
        }

        public Object call () {


            try {
                
            HTTPResponseWrapper rs = null;    
            if (httpRequest != null && XUtils.isEmpty(messageDecoded)) {
              rs = getRequestHandlerInstance().acceptRequest(session, httpRequest);        
            } else {
              rs = getRequestHandlerInstance().acceptRequest(session, messageDecoded);
            }
                
            String data = rs.getContent();
            String chSet = rs.getCharSet();
            String contentType = rs.getContentType();
            byte[] bytes = data.getBytes(chSet);
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Length", Integer.toString(bytes.length));
            headers.put("Content-Type", contentType);
            DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SUCCESS_OK,headers);
            session.write(resp);
            session.write(IoBuffer.wrap(bytes));
            session.write(new HttpEndOfContent());

            } catch (Throwable ee) {

                ee.printStackTrace();
            
            }

            HTTPMinaServer.this.questProcessingEvent(DONE, this);
            return this;


        }


    }



}
