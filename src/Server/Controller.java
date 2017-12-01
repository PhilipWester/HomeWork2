/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author philip
 */

// Performs operations on the model-object 
public class Controller {
    private final Model model;
    
    public Controller(){
        model = new Model();
    }
    
    public int getScore(){
        return model.getScore();
    }
    
    public void changeWord(){
        model.changeWord();
    }
    // Testfunction
    public boolean inWord(char ch){
        String word = ch + "";
        return model.getWord().contains(word);
    }
    public String guess(String guess){
        guess = guess.substring(6);
        // Guessing a letter or word
        if(guess.length() == 1){
            if(inWord(guess.charAt(0))){
                String wordSoFar = model.guessLetter(guess.charAt(0));
                if(model.ifWon()){
                    return "You win, " + wordSoFar + " is the correct word! Score: " + model.getScore();
                }else{
                    return wordSoFar;
                }
            }else{
                model.decrementGuess();
                if(model.ifLose()){
                    model.lost();
                    return "Wrong! Out of guesses, you lose! Score: " + model.getScore();
                }
                else{
                    return guess.charAt(0) + " does not exist in the word, " + model.guessesLeft + " guesses left!";
                }
                
            }
        }else{
            if(model.getWord().equals(guess)){
                model.won();
                return "You win, " + guess + " is the correct word! Score:" + model.getScore();
            }else{
                model.decrementGuess();
                if(model.ifLose()){
                    model.lost();
                    return "Wrong! Out of guesses, you lose! Score: " + model.getScore();
                }
                return "Wrong, " + guess + " is not the correct word! " + model.guessesLeft + " guesses left!";
            }
        }
    }
}
