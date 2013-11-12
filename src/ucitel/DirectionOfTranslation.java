/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;

import java.util.Observable;

/**
 *
 * @author strafeldap
 */
public class DirectionOfTranslation extends Observable{
    
    private Language from;
    private Language to;
    
    public final static int UNDEFINED = 0;
    public final static int FROM_ENGLISH_TO_CZECH = 1;
    public final static int FROM_CZECH_TO_ENGLISH = 2;
   
    public final static int FROM_GERMAN_TO_CZECH = 3;
    public final static int FROM_CZECH_TO_GERMAN = 4;
    
    public final static int FROM_FRANCAIS_TO_CZECH = 5;
    public final static int FROM_CZECH_TO_FRANCAIS = 6;

    public final static int FROM_JAPANESE_TO_CZECH = 7;
    public final static int FROM_CZECH_TO_JAPANESE = 8;

    public final static String czech = "czech";
    public final static String english = "english";
    public final static String german = "deutsch";
    public final static String francais = "francais";
    public final static String japanese = "japanese";
    public final static String error = null;

    private boolean allZeroAsked = false;
    
    Language foreign;

    public boolean isAllZeroAsked(){
        return allZeroAsked;
    }

    public void resetAllZeroAsked(){
        allZeroAsked = false;
    }

    public void allZeroAsked(){
        allZeroAsked=true;
    }

    public boolean matches(DirectionOfTranslation dir){
        if (dir == this){
            return true;
        } else{
            return false;
        }
    }

    private DirectionOfTranslation(){
        super();
	addObserver(ConnectionToDB.getInstance());
	
    }
    
    public Language getLanguageOfQuestion(){
        return from;
    }
    
    public Language getLngTo(){
        return to;

    }

    public boolean isToEnglish(){
        if (to == Language.ENGLISH){
            return true;
        } else {
            return false;
        }
    }

    public boolean isToCzech(){
        if (to == Language.CZECH){
            return true;
        } else {
            return false;
        }
    }

    public boolean isToDeutsch(){
        if (to == Language.GERMAN){
            return true;
        } else {
            return false;
        }
    }

    public boolean isToFrancais(){
        if (to == Language.FRENCH){
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isToJapanese(){
        if (to == Language.JAPANESE){
            return true;
        } else {
            return false;
        }
    }

    public Language getForeignLanguage(){
       if ((to == Language.ENGLISH) || ( from == Language.ENGLISH)){
            return Language.ENGLISH;
        } else if ((to == Language.FRENCH) || (from == Language.FRENCH)){
            return Language.FRENCH;
        } else if ((to == Language.GERMAN) || (from == Language.GERMAN)){
            return Language.GERMAN;
        } else if ((to == Language.JAPANESE) || (from == Language.JAPANESE)){
            return Language.JAPANESE;
        } else if ((to == Language.FINNISH) || (from == Language.FINNISH)) {
	    return Language.FINNISH;
	}
		else {
            throw new UnsupportedOperationException();
        }
    }

    public Language getLngFrom(){
        return from;
    }

    public boolean isFromFrancais(){
        if ( from == Language.FRENCH ){
            return true;
        } else {
            return false;
        }
    }

    public boolean isFromDeutsch(){
        if ( from == Language.GERMAN ){
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isFromJapanese(){
        if ( from == Language.JAPANESE ){
            return true;
        } else {
            return false;
        }
    }

    public boolean isFromEnglish(){
        if ( from == Language.ENGLISH ){
            return true;
        } else {
            return false;
        }
    }

    public boolean isFromCzech(){
        if ( from == Language.CZECH ){
            return true;
        } else {
            return false;
        }
    }
    
    public DirectionOfTranslation(Language from, Language to){
        super();
	this.to = to;
	this.from = from;
    }


    
    public static String getDefaultDirection(){
        return Language.ENGLISH.toString();
    }
    
    String getLastAskedProperty(){
	String la = null;

	if ( to == Language.CZECH){
	    la = "lastAskedToCzech";
	} else {
	    la = "lastAsked";
	}

        return la;
    }

    String getLastAsked() throws Exception{
        String la = null;

	if ( to == Language.CZECH){
	    la = "last_asked_to_czech";
	} else {
	    la = "last_asked";
	}

        return la;
    }
    
    String getCAString() throws Exception{
        String ca = null;
        
	if (to == Language.CZECH){
	    ca = "correctly_answered"; 
	} else {
	    ca = "CA_from_czech";
	}
        
        return ca;
    }

    String getCA() throws Exception{
        String ca = null;
	
	if ( to == Language.CZECH){
	    ca = "correctlyAnswered";
	} else {
	    ca = "caFromCzech";
	}
	
        

        return ca;
    }
    
   /* public static DirectionOfTranslation getDirection(String from, String to) throws Exception {
        if ( czech.equals(from)){
            if ( to.equals(german) ){
                return new DirectionOfTranslation(FROM_CZECH_TO_GERMAN);
            } else if (to.equals(english)){
                return new DirectionOfTranslation(FROM_CZECH_TO_ENGLISH);
            } else if (to.equals(japanese)){
                return new DirectionOfTranslation(FROM_CZECH_TO_JAPANESE);
            } else if (to.equals(francais)){
                return new DirectionOfTranslation(FROM_CZECH_TO_FRANCAIS);
            } else {
                throw new Exception("DirectionOfTranslation::getDirection() can't return direction");
            }
        } else {
            if ( from.equals(german) ){
                return new DirectionOfTranslation(FROM_GERMAN_TO_CZECH);
            } else if (from.equals(english)){
                return new DirectionOfTranslation(FROM_ENGLISH_TO_CZECH);
            } else if (from.equals(japanese)){
                return new DirectionOfTranslation(FROM_JAPANESE_TO_CZECH);
            } else if (from.equals(francais)){
                return new DirectionOfTranslation(FROM_FRANCAIS_TO_CZECH);
            } else {
                throw new Exception("DirectionOfTranslation::getDirection() can't return direction");
            }
        }

    }*/
    
    public void setDirection(Language from, Language to){
        this.to = to;
	this.from = from;
        setChanged();
        notifyObservers( this );
    }
    
    /*@Override public String toString(){
        String dirStr = null;
        
        
        if ( this.direction == DirectionOfTranslation.FROM_ENGLISH_TO_CZECH ){
            dirStr = "english2czech";
        } else if ( this.direction == DirectionOfTranslation.FROM_CZECH_TO_ENGLISH ) {
            dirStr = "czech2english";
        } else if ( this.direction == DirectionOfTranslation.FROM_GERMAN_TO_CZECH ){
            dirStr = DirectionOfTranslation.german2czechString;
        } else if (this.direction == DirectionOfTranslation.FROM_CZECH_TO_GERMAN){
            dirStr = DirectionOfTranslation.czech2germanString;
        } else if (this.direction == DirectionOfTranslation.FROM_CZECH_TO_FRANCAIS){
            dirStr = DirectionOfTranslation.czech2francaisString;
        } else if (this.direction == DirectionOfTranslation.FROM_FRANCAIS_TO_CZECH){
            dirStr = DirectionOfTranslation.francais2czechString;
        } else if (this.direction == DirectionOfTranslation.FROM_JAPANESE_TO_CZECH){
            dirStr = DirectionOfTranslation.japanese2czechString;
        } else if (this.direction == DirectionOfTranslation.FROM_CZECH_TO_JAPANESE){
            dirStr = DirectionOfTranslation.czech2japaneseString;
        } else {
            System.out.println("neznamy jazyk");
        }
        
        return dirStr;
    }*/
    
   
    
}

