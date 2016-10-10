package ru.mtt.webapi.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

import ru.mtt.webapi.core.IProcessor;
import ru.mtt.webapi.core.IProcessorFactory;
import ru.mtt.webapi.utils.XUtils;

public class IOServer  {
    
    IProcessor proc = new ProcessorImpl ();
    IProcessorFactory factory ;
    volatile boolean stop = false;
    ArrayList <Thread> processes = new ArrayList <Thread> ();
    String host = "127.0.0.1";
    int port = 14666;

    public void setFactory(IProcessorFactory factory) {
        this.factory = factory;
    }

    public IProcessorFactory getFactory() {
        return factory;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }


    public IOServer() {
        super();
    }

    public static void main(String[] args) throws IOException {
           IOServer v = new IOServer ();
           v.start ();
    }

    public void setProc(IProcessor proc) {
        this.proc = proc;
    }

    public IProcessor getProc() {
        return proc;
    }

    public void stop() throws IOException {
             stop = true;
             for (Thread t: processes ) {
                  try {
                      
                    t.join();
                  
                  } catch (InterruptedException ee) {

                      XUtils.ilog("log/IOSERVER.log", XUtils.info (ee));
   
                  }
             }
    }
      
      public void start() throws IOException {
        System.out.println("Welcome to Server side");

        ServerSocket servers = null;
        Socket       fromclient = null;

        // create server socket
        try {
          servers = new ServerSocket(port);
        } catch (IOException e) {
          e.printStackTrace();  
          XUtils.ilog("log/IOSERVER.log", XUtils.info (e));
          System.out.println("Couldn't listen to port "+port);
          System.exit(-1);
        }

        while (true) {

        try {
          System.out.print("Waiting for a client...");
          fromclient = servers.accept();
          System.out.println("Client connected");
        } catch (IOException e) {
          XUtils.ilog("log/IOSERVER.log", XUtils.info (e));
          System.out.println("Can't accept");
          System.exit(-1);
        }

        RequestProcessor p = new RequestProcessor ();
        IProcessor pr = proc;    
        if (factory!= null) pr = factory.createProcessor(null);
        p.setProcessor(pr);    
        p.setClient(fromclient);
        Thread t = new Thread(p);
        t.start();
        processes.add (t);
        
            
        if (stop) break;    
            
            
        }
        servers.close();
        
    }

    class RequestProcessor implements Runnable {
    
    
        IProcessor processor = null;
        Socket client = null;

        public void setProcessor(IProcessor processor) {
            this.processor = processor;
        }

        public IProcessor getProcessor() {
            return processor;
        }

        public void setClient(Socket client) {
            this.client = client;
        }

        public void run () {

            try {
            InputStream in  = client.getInputStream();
            OutputStream out = client.getOutputStream();

            System.out.println("Wait for messages");
            byte[] buf = new byte[4096];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            while (in.available()>0) {

                int nm = in.read(buf);
                outputStream.write(buf, 0, nm);

            }
            
            try  {
            
            byte[] outd = (byte[])processor.processRequest (outputStream.toByteArray());
            System.out.println("Write for messages: "+outd.length);
            out.write(outd);
            } catch (Throwable ee) {
            ee.printStackTrace();    
            }
            
            
            out.close();
            in.close();
            client.close();

            } catch (Throwable ee) {
            ee.printStackTrace();    
            }
                       
        }
    
    
    }
    
    class ProcessorImpl implements IProcessor {
        
        public synchronized Object processRequest (Object in) throws Exception {
            
               byte[] bin = (byte[]) in;
               String xs = new String (bin);
               System.out.println (xs);
               String path = "c:/home/jrc/fstorage/fst1/song.mp3";
               byte[] DATA = FileUtils.readFileToByteArray(new File(path));  
               return DATA;    
            
        }
        
        
    }
       
    
}
