/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author strafeldap
 */
public class ErrorHandler {

    static boolean debugEnabled;
    static boolean initialised = false;
    FileHandler hand;
    Logger log;

    ErrorHandler() {
        try {
            hand = new FileHandler("vk.log");
        } catch (IOException ex) {
            System.out.print("Can't open file. " + ex.toString());
        } catch (SecurityException ex) {
            System.out.print(ex.toString());
        }
        log = Logger.getLogger("log_file");

    }

	private static boolean isDebugEnabled() {
		boolean enabled = true;

		if (initialised == false) {
			Properties props = new Properties();
			try {
				props.load(new FileInputStream("properties"));
				if (props.getProperty("debug").equals("true")) {
					enabled = true;
				} else {
					enabled = false;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			initialised = true;
		} else {
			enabled = debugEnabled;
		}

		if (enabled) {
			debugEnabled = true;
			return true;
		} else {
			debugEnabled = false;
			return false;
		}
	}

    public static void debug(String message) {

        if (!isDebugEnabled()) {
            return;
        }

	/*long timeInMillis = System.currentTimeMillis();
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(timeInMillis);
	java.util.Date date = cal.getTime();

	String time = new SimpleDateFormat("yyyy-MM-dd HHmmssSS").format(date);
	message = time + " " + message;*/
        try {
            FileWriter fstream = new FileWriter(getDate() + "_debug.log", true);
            BufferedWriter out = new BufferedWriter(fstream);

            out.append(getTime() + " DEBUG: " + message + "\n");
            System.out.println(getTime() + " " + message);
            //Close the output stream
            out.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logError(String errMsg) {
        if (!isDebugEnabled()) {
            return;
        }
        try {
            FileWriter fstream = new FileWriter(getTime() + "err.log");
            BufferedWriter out = new BufferedWriter(fstream);
            out.append(getTime() + " " + errMsg + "\n");

            //Close the output stream
            out.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void logError(Exception e) {
        if (!isDebugEnabled()) {
            return;
        }
        try {
            System.out.println(e.toString());
            e.printStackTrace();

            PrintWriter pw = new PrintWriter(getTime() + "_err.log");
            pw.print("----------------------START---------------------------" + "\n");
            pw.print(getTime() + " " + "Error: '" + e.toString() + "'\n");
            e.printStackTrace(pw);
            pw.print("-----------------------END------------------------------" + "\n");
            pw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public static void main(String[] args) {

        //logError(new Exception("chyba"));
        try {
            FileHandler hand = new FileHandler("vk.log");
            Logger log = Logger.getLogger("log_file");
            log.addHandler(hand);
            log.warning("Doing carefully!");
            log.info("Doing something ...");
            log.severe("Doing strictily ");
            System.out.println(log.getName());
        } catch (IOException e) {
        }


    }

    private static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }

    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }
}
