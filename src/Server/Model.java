/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author philip
 */
public class Model {
    private String word;
    private char [] wordSoFar;
    private int lettersRevealed;
    public int guessesLeft;
    private int score = 0;
    private BufferedReader br;
    private FileReader fr;
    private File file = new File("/home/philip/NetBeansProjects/Sockets/words.txt");
    
    public Model(){
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void changeWord(){
        int random = (int) (Math.random() * 51529);
        try {
            for(int i = 0; i < random + 1; i++){
                this.word = br.readLine().toUpperCase();
            }
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        wordSoFar = new char[word.length()];
        for(int i = 0; i < wordSoFar.length; i++){
            wordSoFar[i] = "_".charAt(0);
        }
        this.guessesLeft = word.length();
        lettersRevealed = 0;
    }
    public String guessLetter(char Char){
        for(int i = 0; i < word.length(); i++){
            if(this.word.charAt(i) == Char){
                this.wordSoFar[i] = Char;
                this.lettersRevealed++;
            }
        }
        return new String(wordSoFar);
    }
    public boolean ifWon(){
        if(lettersRevealed == word.length()){
            return true;
        }
        else 
            return false;
    }
    public boolean ifLose(){
        if(guessesLeft == 0){
            return true;
        }
        else
            return false;
    }
    
    public int getScore(){
        return this.score;
    }
    
    public String getWord(){
        return this.word;
    }
    public void won(){
        this.score++;
    }
    public void lost(){
        this.score--;
    }
    public void decrementGuess(){
        this.guessesLeft--;
    }
    
}
