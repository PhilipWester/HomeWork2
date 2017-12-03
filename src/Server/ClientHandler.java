/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author philip
 */

//  Handles one client
// Every time a operation is done (change a word, make a guess. Make a controller that takes the model 
class ClientHandler implements Runnable {
    private final SocketChannel clientChannel;
    private final Controller controller;
    private ByteBuffer buf = ByteBuffer.allocateDirect(8192);
    private String request = "default";
    private boolean inGame = false;
    private final ChatServer server;
    private SelectionKey key;
    private String response = "To start a game, type: Start Game";
    
    
    public ClientHandler(SocketChannel clientChannel, ChatServer server, SelectionKey key) {
        controller = new Controller();
        this.clientChannel = clientChannel;        
        this.server = server;
        this.key = key;
    }

    @Override
    public void run() {
        try {                  
            if(!inGame){
                //requestResponse("To start a game, type: Start Game");
                // TODO: Om inget av de nedanstående. Printa det ovanstående istället
                switch (request){
                    case "start game":
                        controller.changeWord();
                        requestResponse("Game started");
                        inGame = true;
                        break;
                    case "exit":
                        requestResponse("exit");
                        disconnect();
                        break;
                    case "default":
                        requestResponse("this shouldn't happen");
                        break;
                }
            } else{
                    if(request.toLowerCase().equals("exit")){
                        requestResponse("exit");
                        disconnect();
                    }

                    else if (request.toLowerCase().startsWith("guess ")){
                        String response = controller.guess(request.toUpperCase());
                        if (response.contains("win") || response.contains("lose")){ //Everything is uppercase -> no risc of accidentally getting any other "win" or "lose"
                            inGame = false;
                            requestResponse(response + " To start a game, type: Start Game");
                        }
                        else{
                            requestResponse(response);
                        }
                    }else{
                        requestResponse("To guess, type: guess [word].");
                    }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //  Reads input from client
    void readRequest() throws IOException{
        // Load the buffer from the channel
        buf.clear();
        clientChannel.read(buf);
        buf.flip();
        // Puts the content of the buffer in a string
        byte [] msg = new byte[buf.remaining()];
        buf.get(msg);
        request = new String(msg);
        // Takes a thread out of the pool and lets it run clientHandler
        server.threadPool.execute(this);
        
    }
    //  Problem: Might overwrite the response if the main thread is too slow
    void requestResponse(String message){
        this.response = message;
        //  Go to ChatServer and activate key
        server.activateSend(key);
    }
    // Sends feedback to client
    void sendResponse() throws IOException{
        // The response, run by the main thread (ChatServer) 
        buf.clear();
        buf = ByteBuffer.wrap(response.getBytes());
        this.clientChannel.write(buf);
    }
    
    private void disconnect() throws IOException{
        // TODO
    }
    
}
