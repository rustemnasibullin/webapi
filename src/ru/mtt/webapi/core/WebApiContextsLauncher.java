package ru.mtt.webapi.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

import ru.mtt.rservice.core.SystemLogger;
import ru.mtt.webapi.utils.XUtils;

/**
 * User: R. Nasibullin
 * Launch WebApi Service context
 * @author rnasibullin@mtt.ru
 */

public class WebApiContextsLauncher  extends Thread {

       public WebApiContextsLauncher() {
              super();
       }
    
        public static final String SHUTDOWN_COMMAND = "shutdown";

        private static final int DEFAULT_SHUTDOWN_SOCKET = 1666;
        private static final byte[] SHUTDOW_KEY = "s1h2u3t4d5o6w7n8".getBytes(Charset.forName("UTF-8"));

        private static List<ClassPathXmlApplicationContext> contexts;
        private static List<String> contexts_ids = new ArrayList<String>();
        private static Logger logger = Logger.getLogger(WebApiContextsLauncher.class.getName());
        private static ServerSocket shutdownSSocket;
        private static Thread shutdownListenerThread;
        private static AtomicBoolean shuttingDown = new AtomicBoolean(false);

        public static void main(String[] args) throws InterruptedException
        {
            if (args.length < 1) System.out.println("Invalid command line");

            System.out.println ("Start: "+args);

            if (SHUTDOWN_COMMAND.equals(args[0])) {
                sendShutdown(args);
            } else {
                runServer(args);
            }

        }

        private static void sendShutdown(String[] args)
        {
            int port = DEFAULT_SHUTDOWN_SOCKET;
            if (args.length >= 2)
            {
                try
                {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e)
                {
                    System.err.println("failed to get shutdown socket port from arguments");
                    e.printStackTrace(System.err);
                }
            }
            logger.info("sending shutdown command to port " + port);
            try
            {
                Socket s = new Socket("127.0.0.1", port);
                s.getOutputStream().write(SHUTDOW_KEY);
                s.getOutputStream().flush();
                s.close();
                logger.info("shutdown command sent");
            } catch (IOException e)
            {
                System.err.println("failed to send shutdown command");
                e.printStackTrace(System.err);
            }
        }

        public static ApplicationContext findContext (String nameOf) {

            ApplicationContext xc = null;
            int i = 0;
            for (String xs: contexts_ids) {
                if (nameOf.equals(xs)) {
                    xc = contexts.get (i);
                    break;
                }
                i++;
            };
            return xc;

        }

        public static Object findBean (String nameOfBean) {

            Object xc = null;
            int i = 0;
            for (ApplicationContext xs: contexts) {
                xc = xs.getBean(nameOfBean);
                if (xc != null) break;
            };
            return xc;

        }


        public static boolean loadContexts(String cLst) {
            String[] cnames = cLst.split(",|;");
            boolean allContextsStarted = true;
            contexts = new ArrayList<ClassPathXmlApplicationContext>();

            for (String cname : cnames)
            {
                if (cname != null)
                {
                    String cnameCorrect = cname.replace('.', '/');

                    if (cnameCorrect.endsWith("/xml"))
                        cnameCorrect = cnameCorrect.substring(0, cnameCorrect.length() - 4) + ".xml";
                    else
                        cnameCorrect += ".xml";
                    try
                    {

                        logger.info("starting context " + cnameCorrect);
                        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(cnameCorrect);
                        contexts.add(context);
                        XUtils.ilog ("launcher.log","Launch  APP Context: "+cnameCorrect);  
                        contexts_ids.add (cnameCorrect);

                    } catch (Throwable e) {

                        e.printStackTrace();
                        XUtils.ilog ("launcher.log","Launch  APP Context: "+XUtils.info(e));  
                        logger.info("shutting down server due to startup errors");
                        allContextsStarted = false;
                        break;

                    }
                }
            }


            return allContextsStarted;


        }



        private static void runServer(String[] args)
                throws InterruptedException
        {
            // args[0] - contexts' names

            XUtils.ilog ("launcher.log","Launch  APP Contexts: "+args[0]);  

            String arg = args[0];

            boolean allContextsStarted = loadContexts(arg);



            if (allContextsStarted)
            {

                if (args.length>=2 && args[1].equals("QA"))  return;
                
                Runtime.getRuntime().addShutdownHook(new WebApiContextsLauncher());

                // args[1] - shutdown socket port
                startShutdownSocket(args);
                synchronized (WebApiContextsLauncher.class)
                {
                    WebApiContextsLauncher.class.wait();
                }

                logger.info("main thread exit");

            } else
            {
                doShutdown();
            }
        }

        @Override
        public void run()
        {

            boolean b = shuttingDown.get();
            if (!b) {

                doShutdown();

            }

        }

        private static void doShutdown()
        {
            //  logger.error(null, new Exception("doShutdown: shutting down server"));
            //   close  neo4j db server instnces:

            if (XUtils.hasDebugMode()) System.exit(0); 
            System.out.println ("@Stop Command.");

            for (ClassPathXmlApplicationContext context : contexts)
            {
                logger.info("stopping context " + context.getDisplayName());
                try
                {
                    context.close();
                    context.destroy();
                    logger.info("context " + context.getDisplayName() + " stopped successfuly");
                } catch (Throwable e)
                {
                    
                    logger.error("failed to stop context " + context.getDisplayName(), e);
                }
            }
            
            SystemLogger.getInstance().warn("stop");
            
            if (shutdownSSocket != null)
                try
                {
                    shutdownSSocket.close();
                    logger.info("shutdown socket closed, joining listener thread");
                    shutdownListenerThread.join();
                    logger.info("shutdown listener thread joined");
                } catch (Exception e)
                {
                    logger.error("failed to close shutdown socket and join listener thread", e);
                }
            
            
            
            System.out.println ("@Commit WebApiContextsLauncher class.");
            
            synchronized (WebApiContextsLauncher.class)
            {
                WebApiContextsLauncher.class.notify();
            }
 
            System.out.println ("@Exit");
           
            System.exit(0);
            
        }

        private static void startShutdownSocket(String[] args)
        {
            int port = DEFAULT_SHUTDOWN_SOCKET;
            if (args.length >= 2)
            {
                try
                {
                    
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e)
                {
                    logger.error("failed to get shutdown socket port from arguments", e);
                }
            }
            logger.info("starting shutdown server socket on port " + port);
            try
            {
                shutdownSSocket = new ServerSocket(port, 0, InetAddress.getByName("127.0.0.1"));
                Runnable shutdownRunnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Socket s;
                        while (true)
                        {
                            try
                            {
                                s = shutdownSSocket.accept();
                            } catch (Exception e)
                            {
                                logger.info("shutdown socket closed");
                                break; // socket was closed?
                            }
                            try
                            {
                                InputStream is = s.getInputStream();
                                byte[] buff = new byte[SHUTDOW_KEY.length];
                                int offset = 0;
                                int n;
                                do
                                {
                                    n = is.read(buff, offset, buff.length - offset);
                                    if (n != -1) offset += n;
                                } while (n > 0 && offset < buff.length);
                                if (is.read() == -1 && offset == buff.length && Arrays.equals(SHUTDOW_KEY, buff))
                                {
                                    if (!shuttingDown.getAndSet(true))
                                    {
                                        logger.info("shutdown command accepted");
                                        Thread t = new Thread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                doShutdown();
                                                logger.info("shutdown performed");
                                            }
                                        });
                                        t.setDaemon(false);
                                        t.start();
                                        return;
                                    } else
                                    {
                                        logger.info("second shutdown command accepted");
                                        logger.info("performing System.exit");
                                        System.exit(1);
                                    }
                                } else
                                    logger.info("fake shutdown command detected");
                                logger.info("socket will be closed");
                                s.close();
                            } catch (IOException e)
                            {
                                logger.error("shutdown socket error", e);
                            }
                        }
                    }
                };
                
                shutdownListenerThread = new Thread(shutdownRunnable, "shutdownListener");
                shutdownListenerThread.setDaemon(false);
                shutdownListenerThread.start();
                logger.info("shutdown socket started");
                
            } catch (IOException e)
            {
                logger.error("failed to start shutdown socket", e);
            }
        }
    
    
}
