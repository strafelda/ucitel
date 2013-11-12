/*
 * UcitelApp.java
 */

package ucitel;

import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.synth.SynthLookAndFeel;
import org.apache.commons.io.FileUtils;
import ucitel.forms.GUI;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class Ucitel extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {

	//copyFiles();
	XML.getInstance();
        GUI gui = GUI.getInstance(this);
            //    new GUI(this);
        Teacher teacher = Teacher.getInstance();
        
        show(gui);
	gui.setUcitel(teacher);
	gui.loadThemes();
	gui.updateProgressBar();

    }

    private String getJarPath(){
        String decodedPath = "";
        String path = "";
        
        try {
            path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            decodedPath = URLDecoder.decode(path, "UTF-8");
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(Ucitel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Ucitel.class.getName()).log(Level.SEVERE, null, ex);
        }
        int endIndex = decodedPath.indexOf("Ucitel.jar");
        if (endIndex == -1){
            return "";
        } else {
            return decodedPath.substring(1, endIndex);
        }
    }
    
    private void copyFiles(){
        String jarPath = getJarPath();
        System.out.println("CopyFiles entered '"+jarPath+"'");
	
	/*File accounts = new File(jarPath+ "accounts.xml");
	if ( accounts.canRead()){
		//File parent_dir = new File("..");
		File from = new File("accounts.xml");
		File to = new File("./accounts.xml");
			try {
                            System.out.println("Copying from "+from.toString()+ "to "+to.toString());
                            FileUtils.copyFile(from, to);
			} catch (IOException ex) {
				System.out.println("Copy failed");
				Logger.getLogger(Ucitel.class.getName()).log(Level.SEVERE, null, ex);
			}
	}*/

    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }
    
@Override public void shutdown() {
        // the default shutdown saves session window state
        
        super.shutdown();
        try{
            if (ConnectionToDB.isConnected()){
                ConnectionToDB conn = ConnectionToDB.getInstance();
                conn.saveAllStats();
                ConnectionToDB.close();
            }
            System.exit(0);
            
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
        System.out.println("ucitel is being closed");
        // now perform any other shutdown tasks you need
        // ...
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of UcitelApp
     */
    public static Ucitel getApplication() {
        return Application.getInstance(Ucitel.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        
        launch(Ucitel.class, args);
        
    }
    

}
