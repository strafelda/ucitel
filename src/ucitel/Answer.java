/*
 * test modification To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;

/**
 *
 * @author strafeldap
 */
public class Answer {
    
    boolean odpoved;
    String correctAnswer;
    
    public void setCorrectAnswer(String english) {
        this.correctAnswer = english;
    }
    
    public String getCorrectAnswer() {
        return this.correctAnswer;
    }
    
    public void set(boolean odp){
        odpoved = odp;
    }
    
    public boolean get(){
        return odpoved;
    }
}
