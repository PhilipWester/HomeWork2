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
    
    public ClientHandler(SocketChannel clientChannel) {
        controller = new Controller();
        this.clientChannel = clientChannel;        
    }

    @Override
    public void run() {
        
        try {
            boolean connected = true;
            boolean inGame = false;
            String request;
            sendResponse("To start a game, type: Start Game");
            while(connected){
                              
                if(!inGame){
                    request = readRequest();
                    if(request.toLowerCase().equals("start game")){
                        controller.changeWord();
                        sendResponse("Game started");
                        inGame = true;
                        continue;
                }
                    else{
                        if(request.toLowerCase().equals("exit")){
                            sendResponse("exit");
                            disconnect();
                            connected = false;
                        }else{
                            sendResponse("To start a game, type: Start Game");
                        }
                        
                        continue;
                    }
                }
                
                request = readRequest();
                if(request.toLowerCase().equals("exit")){
                    sendResponse("exit");
                    disconnect();
                    connected = false;
                }
                
                else if (request.toLowerCase().startsWith("guess ")){
                    String response = controller.guess(request.toUpperCase());
                    if (response.contains("win") || response.contains("lose")){ //Everything is uppercase -> no risc of accidentally getting any other "win" or "lose"
                        inGame = false;
                        sendResponse(response + " To start a game, type: Start Game");
                        continue;
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
    private String readRequest() throws IOException{
        // Load the buffer from the channel
        buf.clear();
        clientChannel.read(buf);
        buf.flip();
        // Put the content of the buffer in a string
        byte [] msg = new byte[buf.remaining()];
        buf.get(msg);
        String string = Arrays.toString(msg);
        return string;
    }
    // Sends feedback to client
    private void sendResponse(String response) throws IOException{
        buf.clear();
        buf = ByteBuffer.wrap(response.getBytes());
        this.clientChannel.write(buf);
    }
    
    private void disconnect() throws IOException{
        // TODO
    }
    
}
