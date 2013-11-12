/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

/**
 *
 * @author strafeldap
 */
public class WavDownloader implements Runnable{
    static SourceDataLine sourceDataLine;
    static AudioFormat audioFormat;
    static AudioInputStream audioInputStream;

    public void run(){

    }

    class PlayThread extends Thread{
        byte tempBuffer[] = new byte[10000];

    public void run(){
        try{
          sourceDataLine.open(audioFormat);
          sourceDataLine.start();
          boolean stopPlayback = false;

          int cnt;
          //Keep looping until the input read method
          // returns -1 for empty stream or the
          // user clicks the Stop button causing
          // stopPlayback to switch from false to
          // true.
          while((cnt = audioInputStream.read(
               tempBuffer,0,tempBuffer.length)) != -1
                           && stopPlayback == false){
            if(cnt > 0){
              //Write data to the internal buffer of
              // the data line where it will be
              // delivered to the speaker.
              sourceDataLine.write(
                                 tempBuffer, 0, cnt);
            }//end if
          }//end while
          //Block and wait for internal buffer of the
          // data line to empty.
          sourceDataLine.drain();
          sourceDataLine.close();

          //Prepare to playback another file
          //stopBtn.setEnabled(false);
          //playBtn.setEnabled(true);
          stopPlayback = false;
        }catch (Exception e) {
          e.printStackTrace();
          System.exit(0);
        }//end catch
      }//end run
}//end inner class PlayThread
//===================================//
    final static int size=1024;

    private static String getURL(String word) throws MalformedURLException, IOException{
        URL dictionary = new URL("http://dictionary.reference.com/browse/" + word);
	BufferedReader in = new BufferedReader(
				new InputStreamReader(
				dictionary.openStream()));

	String inputLine;

	while ((inputLine = in.readLine()) != null){

            if (inputLine.matches(".*audio.html.*")){
                int leftIndex = inputLine.indexOf("http://dictionary.reference.com/audio.html");
                String url;
                
      
                
                if (leftIndex == -1){
                    url = null;
                } else {
                    int rightIndex = inputLine.indexOf("\\",leftIndex);
                    if (rightIndex == -1){
                        rightIndex = inputLine.indexOf("\"", leftIndex);
                    }
                    url = inputLine.substring(leftIndex,rightIndex);
                }
                return url;
            }
        }
            

	in.close();

        dictionary = new URL("http://slovnik.seznam.cz/?q="+word+"&lang=en_cz");
        in = new BufferedReader(
				new InputStreamReader(
				dictionary.openStream()));

        while ((inputLine = in.readLine()) != null){

            //System.out.println(inputLine);
            if (inputLine.matches(".*sound.*")){
                int leftIndex = inputLine.indexOf("/sound");
                int rightIndex = inputLine.indexOf("\"",leftIndex);
                String url = inputLine.substring(leftIndex,rightIndex);

                return url;
            }
        }

        return null;
    }

    public static void downLoad(String word){
        
        if (-1 != word.indexOf(" ")){
            return;
        }

        if (word.endsWith("ly")){
            return;
        }

        GuiDirection direction = GuiDirection.getInstance();

        if (!direction.isFromEnglish() && !direction.isToEnglish()){
            return;
        }
        
        XML ini = XML.getInstance();
        if (ini.getDownloadWav()==false ){
            return;
        }

        String fileAddress;// = "http://cache.lexico.com/dictionary/audio/lunaWAV/S08/S0857400.wav";
        String localFileName = word + ".wav";
        String destinationDir = "wav";

        new File("wav").mkdir();

        OutputStream os = null;
        URLConnection URLConn = null;
        InputStream is = null;
        
        
        if (ini.getUseProxy().matches("true")){
            System.setProperty("http.proxyHost", ini.getProxy());
            System.setProperty("http.proxyPort", ini.getProxy_port());
        } else {
            System.setProperty("http.proxyHost", "");
            System.setProperty("http.proxyPort", "");
        }
        

        try {
            URL fileUrl;
            byte[] buf;
            int ByteRead;
            int ByteWritten=0;
            
            fileAddress = getURL(word);
            if (fileAddress == null){
                return;
            }

            ErrorHandler.debug("WavDownloader: fileAddress: " + fileAddress );
            fileUrl= new URL(fileAddress);
            String filename = destinationDir+"\\"+localFileName;
            os = new BufferedOutputStream(new FileOutputStream(filename));

            URLConn = fileUrl.openConnection();
            is = URLConn.getInputStream();
            buf = new byte[size];
            while ((ByteRead = is.read(buf)) != -1) {
                os.write(buf, 0, ByteRead);
                ByteWritten += ByteRead;
            }
            os.close();


        } catch (MalformedURLException e){
            System.out.println("MalformedURLException: " + e.toString());
            e.toString();
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("IOException: " + e.toString());
            e.toString();
            e.printStackTrace();
        }

        //*/
    }

    public static void main(String[] args) {
    
        WavDownloader wd = new WavDownloader();
        wd.downLoad("get out");
        //wd.downLoad();

    }

    public static boolean exists(String word){
        Teacher ucitel = Teacher.getInstance();

        word = ucitel.removeTo(word);
        word = ucitel.removeBrackets(word);

        File file = new File("wav\\"+word+".wav");

        if (file.exists() == true){
            return true;
        } else {
            WavDownloader.downLoad(word);
            if (file.exists()==true){
                return true;
            } else {
                return false;
            }
        }


    }


}



