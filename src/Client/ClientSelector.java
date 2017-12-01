/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author philip
 */
public class ClientSelector {
    private final int PORT = 8080;
    private final String IP = "127.0.0.1";
    private final InetSocketAddress SERVERADDRESS = new InetSocketAddress(IP, PORT);
    private SocketChannel socketChannel;
    private Selector selector;
    private final int NumberOfThreads = 4;
    private String messageToServer;
    public ExecutorService threadPool = Executors.newFixedThreadPool(NumberOfThreads);
    
    void clientSelector() throws IOException{
        
        initConnection();
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        boolean connected = true;
        
        while(connected){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isConnectable()){
                    initOutThread(key);
                    // Register channel and input object to selector and start an output thread
                    
                }
                else if(key.isReadable()){
                    ClientOutput cliInput = (ClientOutput) key.attachment();
                    cliInput.notifyPrint();
                    // Read from channel and execute task on attached object
                }
                else if(key.isWritable()){
                    // Write (back) to channel 
                }
            }
        }
    }

    private void initOutThread(SelectionKey key) throws ClosedChannelException, IOException{
        socketChannel.finishConnect();
        key.channel().register(selector, SelectionKey.OP_READ, new ClientOutput(this, socketChannel));
        Thread clientOutput = new Thread(new ClientInput(this, key));
        clientOutput.start();
    }
    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(SERVERADDRESS);
    }
    void notifySend(String msg, SelectionKey key){
        this.messageToServer = msg;
        key.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }    
}   

/*
    PLAN:
        ClientSelector opens up a connection and channel to the server while starting an output thread and an attached inputObject.
            OutputThread: Will read scanner, store outgoing message in selector and notify selector with wakeup()
            InputObject: Will get started by the cliSelector (from a threadPool) who stores the message in the object. InputObject simply prints the message.
    OBS! I picked weird names for input/output. I feel like it should be the opposite
*/
