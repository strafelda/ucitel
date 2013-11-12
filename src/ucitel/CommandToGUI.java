/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

/**
 *
 * @author strafeldap
 */
public class CommandToGUI {

	boolean ask = false;
	boolean dberror = false;
	String command = "";
	final public static String ALL_ASKED = "ALL_ASKED";

	public CommandToGUI() {

	}

	public String getCommand(){
		return command;
	}

	public CommandToGUI(String command) {
		super();
		this.command = command;
	}

	public void setAsk(boolean ask) {

		this.ask = ask;
	}

	public boolean getAsk() {
		return ask;
	}

	public void setDBError(boolean dberror) {
		this.dberror = dberror;
	}

	public boolean getDBError() {
		return dberror;
	}
}
