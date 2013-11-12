/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;


import javax.sound.sampled.*;
import java.io.*;
/**
 *
 * @author strafeldap
 */
public class WavPlayer implements Runnable{

    //static SourceDataLine sourceDataLine;
    //static AudioFormat audioFormat;
    //static AudioInputStream audioInputStream;

    String filename;

    public void setFilename(String filename){
        this.filename = filename;
    }

    @Override
    public void run(){

        WavDownloader.exists(filename);

        byte tempBuffer[] = new byte[10000];
        try{
            File soundFile = new File("wav\\" + filename + ".wav");

            if (!soundFile.exists()){
                WavDownloader.downLoad(filename);
                
                if (!soundFile.exists()){
                    return;
                }
 
            }

            if (soundFile.length() == 0){
                soundFile.delete();
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info( SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
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


