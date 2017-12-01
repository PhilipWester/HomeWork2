/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 *
 * @author philip
 */
public class clientInput {
    private final ClientSelector cliSelector;
    private String messageFromServer;
    private ByteBuffer buf = ByteBuffer.allocateDirect(8192);
    private SocketChannel socketChannel;

    public clientInput(ClientSelector cliSelector, SocketChannel socketChannel){
        this.cliSelector = cliSelector;
        this.socketChannel = socketChannel;
    }
    
    
    public void run(){
        System.out.println(messageFromServer);
    }
    void notifyPrint() throws IOException{
        buf.clear();
        socketChannel.read(buf);
        buf.flip();
        // Puts the content of the buffer in a string
        byte [] msg = new byte[buf.remaining()];
        buf.get(msg);
        messageFromServer = Arrays.toString(msg);
        cliSelector.threadPool.equals(this);
    }
}
