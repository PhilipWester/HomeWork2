/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonblockingsockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author philip
 */
public class ChatServer {

    /**
     * @param args the command line arguments
     */
    private int PORT = 8080;
    private Selector selector;
    private ServerSocketChannel serverChannel;
    
    void startServer() throws IOException{
        // Init selector    
        selector = Selector.open();
        
        // Init listening socket
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        while(true){
            selector.select();
            
        }
    }
    
    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        server.startServer();
        
        
        
       /*
        SERVER:
            Selector: 
            - Registrerar sig sjäv?
            - Vid registrearing av ny clientChannel så skapas ett nytt object "Client" som äger en "clientHandler"
            - När en registrerad channel får en key så tar selector-tråden och evaluerar ut requestet samt ger den till en annan tråd (ur en pool) som kör själva handlern
            Client-object:
            - Äger en egen "clientHandler" med egna värden. Genom att hantera Client så hanterar man clientHandler indirekt.
            - Client ligger som en "attatchment" vid registreringen hos selectorn. ie. Client han hämtas ur Key:n.
            Handle-tråden:
            - Använder sig av funktioner och värden som finns i "Client"
        
        Client:
            ???
            
        */
        
        //  REKONSTRUKTION AV HTTP GET CHANNEL (TEST)
        /*
        String IPstring = "www.kth.se";
        int PORT = 80;
        InetSocketAddress IP = new InetSocketAddress(IPstring, PORT);
        
        SocketChannel channel = SocketChannel.open();
        channel.connect(IP);
        channel.configureBlocking(false);
        ByteBuffer buf = ByteBuffer.wrap("GET / HTTP/1.1\n".getBytes());
        channel.write(buf);
        buf = ByteBuffer.wrap("Host: www.kth.se \n\n".getBytes());
        channel.write(buf);
        buf = ByteBuffer.allocate(1024);
        
        WritableByteChannel out = Channels.newChannel(System.out);
        while(buf.hasRemaining() && channel.read(buf) != -1){
            buf.flip();
            out.write(buf);
            buf.clear();
        }
        */
    }
}
