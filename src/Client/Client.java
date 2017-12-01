/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.IOException;

/**
 *
 * @author philip
 */

public class Client {
    
    public static void main(String [] args) throws IOException{
        ClientSelector client = new ClientSelector();
        client.clientSelector();
    }


}
