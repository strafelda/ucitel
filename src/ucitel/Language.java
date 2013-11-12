/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

/**
 *
 * @author peta
 */
public enum Language {
    ENGLISH {
    public String toString() {
        return "Angličtina";
    }
},
    
FRENCH {
    public String toString() {
        return "Francouzština";
    }
},    

GERMAN {
    public String toString() {
        return "Němčina";
    }
},

JAPANESE {
    public String toString() {
        return "Japonština";
    }
},

FINNISH {
    public String toString() {
        return "Finština";
    }
},

CZECH {
    public String toString() {
        return "Čeština";
    }
}

}
