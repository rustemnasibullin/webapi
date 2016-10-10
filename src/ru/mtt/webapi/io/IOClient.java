package ru.mtt.webapi.io;

import java.io.BufferedReader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.Socket;

import ru.mtt.webapi.core.IProcessor;

public class IOClient implements IProcessor {
    
    int port = 14666;
    String host = "127.0.0.1";

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public IOClient() {
        super();
    }
    
    public static void main(String[] args) throws IOException {
           new IOClient(). processRequest ("mtt:client.wav".getBytes());
    }
    
    public Object processRequest  (Object r) throws IOException {

        System.out.println("Welcome to Client side");

        Socket fromserver = null;

        byte[] bin = (byte[]) r;

        fromserver = new Socket(host,port);

        fromserver.getOutputStream().write (bin);
        InputStream ins = fromserver.getInputStream();
        
        try {
        
        Thread.currentThread().sleep (10000);         

        } catch (Throwable ee) {
                
        }
        
        byte [] buf =  new byte[4096];
     //   FileOutputStream fs = new FileOutputStream ("test.wav");
        int ns;

           ByteArrayOutputStream ba = new ByteArrayOutputStream( );
           while ((ns=ins.read(buf))>0) {
               
               ba.write(buf,0, ns);

               
           }

        byte[] buff = ba.toByteArray();
    //    fs.write(buff);
        System.out.println("Finishing "+buff.length);
       
    //    fs.close();
        System.out.println("Finish");

          
        ins.close();
        fromserver.close();

        return buff;
      }    
    
} 

