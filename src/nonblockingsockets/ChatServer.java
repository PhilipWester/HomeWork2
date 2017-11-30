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
    public ExecutorService threadPool = Executors.newFixedThreadPool(NumberOfThreads);
    
    //  Is this ok? The cliHandler will activate the OP_WRITE even though maybe that is the main threads task
    //  If not: Make a list with the keys that need to write with clihandler and let main thread go through them and make them OP_WRITE itself
    void activateSend(SelectionKey key){
        key.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
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
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        // Make object to be associated with the client (clientHandler)
        ClientHandler cliHand = new ClientHandler(clientChannel, this, key);
        clientChannel.register(selector, SelectionKey.OP_WRITE, cliHand);    // We expect to write first time (A response like "start game")
    }
    
    private void readChannel(SelectionKey key) throws IOException{
        cliHandler = (ClientHandler) key.attachment();
        cliHandler.readRequest();
    }
    
    private void writeChannel(SelectionKey key) throws IOException{
        cliHandler = (ClientHandler) key.attachment();
        cliHandler.sendResponse();
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
    
    /*
        Frågor och svar (inte hans bokstavliga svar):
            
            Varför behöver du köra "selector.wakeup()"? 
            För att sätta om selectorn till Write

            Varför gör du:
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            I "startHandler"? Är den inte redan initierad som "listeningSocketChannel"? 
            Det var onödigt, man behöver inte

            Varför måste man göra key.interestOps(SelectionKey.OP_READ/WRITE)?
            För att man måste säga vilken operation kanalen kommer ha nästa gång. (TIPS: Sätt till read efter varje write då du inte vet när klienten kommer skicka men du kan alltid göra "wakeup" när du själv ska skriva.  

            Var finns den tredje noden "Registry" för RMI? Om den varken finns hos caller eller callee, var då? Varför inte bara ha den hos callee?
            Alla noder finns på serversidan

            Var på kurshemsidan kan jag se vilka MySQL tutorials du rekommenderar? (Du nämnde det i videon men har inte hittat)
            Han hade glömt

            Kanske: Kan man få clientHandlern att lägg in i kön hos selectorn

            För att skicka tillbaka:
            1. clientHandlern lägger meddelandet den vill skicka i en publik variabel.
            2. Låt clientHandlern lägg in sig i en kö(typ array) hos selectortråden och göt wakeup.
            3. Selectortråden läser i kö:n och gör varje korresponderande key till writeable. 
            4. Selectortråden går till "select()" som hanterar alla writes som ville göras.
    */
    }
}
