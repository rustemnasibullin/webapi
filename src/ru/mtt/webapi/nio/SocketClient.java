package ru.mtt.webapi.nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SocketClient {
    public SocketClient() {
        super();
    }


 
     
        public void startClient()
                throws IOException, InterruptedException {
            Selector selector = Selector.open();

            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8090);
            SocketChannel client = SocketChannel.open(hostAddress);
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
     
            System.out.println("Client... started");
            
            String threadName = Thread.currentThread().getName();
     
            // Send messages to server
            String [] messages = new String [] {"mtt.ru:/test1.wav"};
     
            for (int i = 0; i < messages.length; i++) {
                byte [] message = new String(messages [i]).getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(message);
                client.write(buffer);
                System.out.println(messages [i]);
                
                buffer.clear();
                ByteBuffer buffer2 = ByteBuffer.allocate(4096);
                FileOutputStream fs = new FileOutputStream ("test.wav");
                int ts = 0;
                while (true) {
                
                int nb = client.read(buffer2);
                buffer2.flip();    
                byte[] tt = buffer2.array();
                ts = ts + tt.length;   
                System.out.println ("Came back   "+ ts);
                buffer2.clear(); 
                fs.write(tt, 0, nb);
                    if (nb == -1) {
                        break;
                    }
                }
                
                System.out.println("Client... stoped");
            }
            client.close();            
        }
    }
    
        