/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
/**
 *
 * @author strafeldap
 */
public class InternetConnectionTest {

    public static void main(String[] args){
        test();
    }
    
    public static boolean test(){
    Properties systemSettings = System.getProperties();
    systemSettings.put("http.proxyHost", "158.234.170.80");
    systemSettings.put("http.proxyPort", "3128");
    /*systemSettings.put("socks.proxyHost","158.234.170.80"); 
    systemSettings.put("socks.proxyPort","3128");*/
    System.setProperties(systemSettings);
    
    try{ 
        URL yahoo = new URL("http://www.yahoo.com/");
            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    yahoo.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();
            return true;
        } catch (Exception e){
            System.out.println("Error");
            e.printStackTrace();
            return false;
        } 
    }
}
