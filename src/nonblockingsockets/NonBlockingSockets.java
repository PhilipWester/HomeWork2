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
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author philip
 */
public class NonBlockingSockets {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        String IPstring = "localhost";
        int PORT = 8080;
        InetSocketAddress IP = new InetSocketAddress(IPstring, PORT);
        
        SocketChannel channel = SocketChannel.open(IP);
        channel.configureBlocking(false);
        ByteBuffer buf = ByteBuffer.wrap("TEST".getBytes());
        channel.write(buf);
        
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
