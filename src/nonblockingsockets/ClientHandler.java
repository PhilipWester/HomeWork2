/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonblockingsockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
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
    
    public ClientHandler(SocketChannel clientChannel) {
        controller = new Controller();
        this.clientChannel = clientChannel;        
    }

    @Override
    public void run() {
        try {                  
            if(!inGame){
                sendResponse("To start a game, type: Start Game");
                switch (request){
                    case "start game":
                        controller.changeWord();
                        sendResponse("Game started");
                        inGame = true;
                        break;
                    case "exit":
                        sendResponse("exit");
                        disconnect();
                        break;
                    case "default":
                        sendResponse("this shouldn't happen");
                        break;
                }
            } else{
                    if(request.toLowerCase().equals("exit")){
                        sendResponse("exit");
                        disconnect();
                    }

                    else if (request.toLowerCase().startsWith("guess ")){
                        String response = controller.guess(request.toUpperCase());
                        if (response.contains("win") || response.contains("lose")){ //Everything is uppercase -> no risc of accidentally getting any other "win" or "lose"
                            inGame = false;
                            sendResponse(response + " To start a game, type: Start Game");
                        }
                        else{
                            sendResponse(response);
                        }
                    }else{
                        sendResponse("To guess, type: guess [word].");
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
        request = Arrays.toString(msg).toLowerCase();
        // TODO make a Thread from a pool run clientHandler 
        
    }
    // Sends feedback to client
    void sendResponse(String response) throws IOException{
        // TODO Gör så att den här säger åt Selector-tråden att gör detta åt clientHandlern (clientHandlern ska ju inte sköta kommunikationen) 
        buf.clear();
        buf = ByteBuffer.wrap(response.getBytes());
        this.clientChannel.write(buf);
    }
    
    private void disconnect() throws IOException{
        // TODO
    }
    
}
