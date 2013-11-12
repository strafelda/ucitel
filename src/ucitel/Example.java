/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;
import ucitel.DirectionOfTranslation;
import java.text.Normalizer;
import java.text.Normalizer.*;
/**
 *
 * @author strafeldap
 */
public class Example {
    private String foreign;
    private String czech;
    private String hint;
    private String theme;
    private Language foreignLanguage;
    private int id;
    boolean grammar;
        
    Example(String hint, String answer,String question){
        this(answer,question);
        this.setHint(hint);
    }

    public Example(String foreign, String czech) {
        super();
	this.czech = czech;
	this.foreign = foreign;
    }

    public Language getForeignLanguage(){
        return foreignLanguage;
    }
    
    public void setHint(String hint){
        this.hint = hint;
    }

    public String getHint(){
        return this.hint;
    }

    public String getForeign(){
        return foreign;
    }

    public boolean isGrammar(){
        return grammar;
    }

    public String getCzech(){

        return czech;
        
    }

    public void setCzech(String czech){
	this.czech = czech;
    }

    public void setForeign(String foreign){
	this.foreign = foreign;
    }

    public void setGrammar(boolean grammar){
        this.grammar = grammar;
    }

    Example(int id,String answer,String question, int grammar){
        //super();
        this(answer,question);
        if (grammar == 1){
            this.setGrammar(true);
        } else {
            this.setGrammar(false);
        }

        this.setID(id);
        //this.setAnswer(answer);
        //this.setQuestion(question);
        //this.setJazyk(jazyk);
    }

    private Example(int id,String answer,String question){
        //super();
        this(answer,question);
        this.setID(id);
        //this.setAnswer(answer);
        //this.setQuestion(question);
        //this.setJazyk(jazyk);
    }

    public Example(int id,String answer,String question,String hint, int grammar){
        this(id,answer,question,hint);
        if (grammar == 1){
            this.setGrammar(true);
        } else {
            this.setGrammar(false);
        }
        //this.setID(id);
        //this.setAnswer(answer);
        //this.setQuestion(question);
        //this.setJazyk(jazyk);
        //this.setHint(hint);
    }

    private Example(int id,String answer,String question,String hint){
        this(id,answer,question);
        //this.setID(id);
        //this.setAnswer(answer);
        //this.setQuestion(question);
        //this.setJazyk(jazyk);
        this.setHint(hint);
    }

    public Example(String foreign, String czech, Language l){
        super();
       // this.setID(id);
        this.czech = czech;
	this.foreign = foreign;
        //this.setJazyk(jazyk);
        this.setForeignLanguage(l);
    }


    public Example(String foreign, String czech, Language l, String theme, String hint){
        super();
        this.setTheme(theme);
        this.czech = czech;
	this.foreign = foreign;
        this.setForeignLanguage(l);
        this.setHint(hint);
    }

    public Example(String foreign, String czech, Language l, String theme){
        super();
       // this.setID(id);
        this.setTheme(theme);
        //this.setCzech(answer);
        //this.setForeign(question);
	this.czech = czech;
	this.foreign = foreign;
        //this.setJazyk(jazyk);
        this.setForeignLanguage(l);
    }

    public void setForeignLanguage(Language l){
        this.foreignLanguage = l;
    }

    private void setTheme(String theme){
        this.theme = theme;
    }

    private void setID(int ID){
        id = ID;
    }
    
    /*public Jazyk getLanguage(){
        return language;
    }
    
    public void setJazyk(Jazyk lang){
        language=lang;
    }*/
    
    public int getID(){
        return id;
    }
    
    public String getNormalizedCzech(){
        return Normalizer.normalize(czech, Form.NFD).replaceAll("[^\\p{ASCII}]","");
    }
    

    static String format(String a){
        StringBuffer result = new StringBuffer(a);
        int index = result.indexOf(",");

        while(index != -1){
            if ((!result.substring(index+1,index+2).equals(" ")) && ((index+1)!=result.length()) ){
                result.insert(index+1, " ");
            }
            index = result.indexOf(",",index+1);
        }

        return result.toString();
    }

    /*private void setCz(String english) {
        question = format(english.trim());
    }
    
    public void setAnswer(String czech) {
        answer = czech.trim();
    }*/

    int getCharCnt(String word, String character){
        int index=word.indexOf(character);
        int cnt=0;
        if (index != -1){
            cnt++;
        }
        
        while (index != -1){
            index = word.indexOf(character,index+1);
            if (index != -1){
                cnt++;
            }
        }
        return cnt;
    }
    
    public String getQuestion(DirectionOfTranslation d){
	if ( d.isToCzech() ){
	    return this.getForeign();
	} else {
	    return this.getCzech();
	}
    }
    
    public String getAnswer(DirectionOfTranslation d){
	if ( d.isToCzech() ){
	    return this.getCzech();
	} else {
	    return this.getForeign();
	}
    }

    public boolean check(){
        if ((getCharCnt(this.foreign,"(") == getCharCnt(this.foreign,")")) && ((getCharCnt(czech,"(") == getCharCnt(czech,")"))) ){
            if(foreign.substring(this.foreign.length()-1).equals(",") || czech.substring(this.czech.length()-1).equals(",")){
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    
}
