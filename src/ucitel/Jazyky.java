/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author petr
 */
public class Jazyky {
    public static List getLanguageList(){
        
        ArrayList<String> jazyky = new ArrayList<String>();
    
        jazyky.add("czech");
        jazyky.add("english");
        jazyky.add("deutsch");
        jazyky.add("francais");
        jazyky.add("japanese");

        return jazyky;
    
    }
}
