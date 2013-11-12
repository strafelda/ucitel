/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

/**
 *
 * @author peta
 */
public class GuiDirection extends DirectionOfTranslation {

    private static GuiDirection uniqueInstance;
    
 
    
    private GuiDirection(Language from, Language to){
        super(from,to);

    }	
    
    public static synchronized GuiDirection getInstance(){
        if ( null == uniqueInstance) {
            XML xml = XML.getInstance();
	    Language l = xml.getLanguage();
	    if (xml.isFromCzech()){
		uniqueInstance = new GuiDirection(Language.CZECH,l);
	    } else {
		uniqueInstance = new GuiDirection(l,Language.CZECH);
	    }
	    
        }
        return uniqueInstance;
    }
}
