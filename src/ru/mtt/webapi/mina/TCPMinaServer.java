package ru.mtt.webapi.mina;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.http.HttpServerCodec;
import org.apache.mina.http.api.*;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import ru.mtt.webapi.core.XConfigurableObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 *  TCP Mina server implmentatoion based upon Apache MINA Framework
 * 
 *  @author rnasibullin@mtt.ru
 */
public class TCPMinaServer  extends XConfigurableObject  {

    MinaRequestHandler requestHandler = null;
    final Object lock = new Object();
    IoAcceptor acceptor = new NioSocketAcceptor();

    org.apache.log4j.Logger logger = Logger.getLogger(HTTPMinaServer.class);

    int serverPort = 1001;
    int n_threads = 16;



    public MinaRequestHandler getRequestHandler() {
           return requestHandler;
    }


    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() {

        System.out.println ("Start : ");
        ((NioSocketAcceptor) acceptor).setReuseAddress(true);
         acceptor.setHandler(new IoHandlerAdapter() {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {

                   System.out.println("Came To: "+serverPort+" Info: "+message);

            }
        });


        try {

            acceptor.bind(new InetSocketAddress(serverPort));

        } catch (IOException ee) {
            ee.printStackTrace();
        }


        // start postponed  job provessing






    }


    @Override
    public void doConfig() {

        String s_n_threads = ps.getProperty("mina_threads");



    }



    public void stop() {

        try {

            acceptor.unbind();
            acceptor.dispose(true);
            logger.info("MINA Server stopped");


        } catch (Throwable ee) {

            logger.info("MINA Server stopping error");

        }

    }



    public void  questProcessingEvent(int evetId, Object processorObject) {


    }


}
