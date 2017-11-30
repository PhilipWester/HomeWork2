/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonblockingsockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author philip
 */
public class ChatServer {

    /**
     * @param args the command line arguments
     */
    private ClientHandler cliHandler;
    private final int PORT = 8080;
    private final int NumberOfThreads = 10;
    private Selector selector;
    private ServerSocketChannel serverChannel;
    // private boolean ifSend = false;
    public ExecutorService threadPool = Executors.newFixedThreadPool(NumberOfThreads);
    void sendMsg(String msg){
        // Kanske få selectorn in på ett spår där den skickar iväg svar... men hur vet den vilken klient de olika svaren ska till? :/
    }
    
    //  Handles all sending and receiving over the net.
    private void startServer() throws IOException{
        // Init selector    
        selector = Selector.open();
                
        // Init listening socket
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(PORT));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        while(true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()){
                    // Register channel and CLient object to selector 
                    regChannel(key);
                }
                else if(key.isReadable()){
                    // Read from channel and execute task on attached object
                    readChannel(key);
                }
                else if(key.isWritable()){
                    // Write (back) to channel 
                    writeChannel(key);
                }
            }
        }
    }
    
    // registers the Channel to the selector and creates the objects associated with the client
    private void regChannel(SelectionKey key) throws IOException{
        ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();  //Behövs detta?
        SocketChannel clientChannel = socketChannel.accept();
        clientChannel.configureBlocking(false);
        // Make object to be associated with the client (clientHandler)
        ClientHandler cliHand = new ClientHandler(clientChannel, this);
        clientChannel.register(selector, SelectionKey.OP_WRITE, cliHand);    // We expect to write first time (A response like "start game")
    }
    
    private void readChannel(SelectionKey key) throws IOException{
        cliHandler = (ClientHandler) key.attachment();
        cliHandler.readRequest();
    }
    
    private void writeChannel(SelectionKey key) throws IOException{
        cliHandler = (ClientHandler) key.attachment();
        cliHandler.sendResponse("Vet inte riktigt än");
    }
    
    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        server.startServer();
            
    /*
        SERVER:
            Selector: 
            - Registrerar serverns socket channel (den som den lyssnar på)
            - Vid registrearing av ny clientChannel så skapas ett nytt object "ClientHandler" där varje klients unika värden direkt och indirket ingår
            - När en registrerad channel får en key så tar selector-tråden och evaluerar ut requestet samt ger den till en annan tråd (ur en pool) som kör själva handlern
            ClientHandler-object:
            - Hanterar sin klients requests efter att Selector-tråden gett den meddelandet och tilldelat en tråd klienthandler
            - Ligger som en "attatchment" vid registreringen hos selectorn. ie. ClientHandler han hämtas ur Key:n.
        
        Client:
            ???
    */
    }
}
