/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;

/**
 *
 * @author strafeldap
 */
public class Word {

	String from;
	String to;
	int id;

	public String getFrom() {
		return from;
	}

	public String getCzech(){
		if ( GuiDirection.getInstance().isFromCzech() ){
			return from;
		} else {
			return to;
		}
	}

	public String getForeign(){
		if ( GuiDirection.getInstance().isFromCzech() ){
			return to;
		} else {
			return from;
		}
	}

	public String getTo() {
		return to;
	}
	
	public int getId(){
		return id;
	}

	public Word(Language from,Language to) {
		this.from = from.toString();
		this.to   = to.toString();
	}

	public Word(String from,String to, int id) {
		this.from = from;
		this.to   = to;
		this.id	  = id;

	}

}
