/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.nio.channels.SelectionKey;
import java.util.Scanner;

/**
 *
 * @author philip
 */
class clientOutput implements Runnable {
    private final ClientSelector cliSelector;
    private final SelectionKey key;
    public clientOutput(ClientSelector cliSelector, SelectionKey key) {
        this.cliSelector = cliSelector;
        this.key = key;
    }

    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String message;
        while(true){
            message = scanner.nextLine();
            // Write message to the selectorThread and wakeup
            cliSelector.notifySend(message, key);
                /*
                if(message.equals("exit")){
                    break;
                }
                */
            
        }
    }
    
}
