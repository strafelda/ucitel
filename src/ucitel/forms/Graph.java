/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel.forms;

import java.awt.*;
import java.awt.Color;
import javax.swing.JPanel;
import ucitel.ConnectionToDB;
import ucitel.GuiDirection;
import ucitel.Language;

/**
 *
 * @author strafeldap
 */
public class Graph extends JPanel {

	private float max;
	boolean counted = false;
	private int[] array;
	GuiDirection direction;

	public Graph() {
		direction = GuiDirection.getInstance();
	}

	private void countLine() {
		int okraj = 20;
		Dimension d = this.getSize();
		int nolw = 0;
		int sirka = d.width - (2 * okraj);
		array = new int[sirka / 4];


		try {
			ConnectionToDB conn = ConnectionToDB.getInstance();
			int numberOfDays = conn.getNumberOfDays(direction.getLngFrom(), direction.getLngTo());
			if (numberOfDays == 0) {
				return;
			}

			float x1 = 20;
			int y1 = d.height - okraj;
			int y2 = d.height - okraj;
			int inc = 4;

			setMaximum(conn.getMaxLearnedWords());

			int lastValue = d.height - okraj;

			for (int i = 0; i < sirka / inc; i++) {
				float progress;
				progress = (float) i / (float) sirka;

				nolw = getRelativeHeight(conn.getNumberOfLearnedWords(numberOfDays - (int) (numberOfDays * progress)));
				if (i > 0) {
					x1 += inc;
					y1 = y2;
				}

				y2 = d.height - okraj - nolw;
				if (y2 == (d.height - okraj)) {
					y2 = lastValue;
				}
				array[i] = y2;
				//g.drawLine((int) x1, y1, (int) (x1 + inc), y2);
				lastValue = y2;
			}

			//g.drawString(Integer.toString(conn.getNumberOfLearnedWords(0)), 25, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setMaximum(int max) {
		this.max = max;
	}

	public static void main(String args[]) {
		Graph g = new Graph();
	}

	private int getRelativeHeight(int absH) {
		Dimension d = this.getSize();
		float height = (float) d.height - 40;
		float ret = ((height / max) * (float) absH);
		return (int) ret;
	}

	@Override
	public void paint(Graphics g) {
		if (counted == false) {
			countLine();
			counted = true;
		}

		Dimension d = this.getSize();

		int okraj = 20;
		g.setColor(Color.white);
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.black);
		g.drawLine(okraj, okraj, okraj, d.height - okraj);
		g.drawLine(okraj, d.height - okraj, d.width - okraj, d.height - 20);
		int nolw = 0;

		try {
			ConnectionToDB conn = ConnectionToDB.getInstance();
			int numberOfDays = conn.getNumberOfDays(direction.getLngFrom(), direction.getLngTo());
			if (numberOfDays == 0) {
				return;
			}

			float x1 = 20;
			int y1 = d.height - okraj;
			int y2 = d.height - okraj;
			int inc = 4;

			setMaximum(conn.getMaxLearnedWords());

			int lastValue = d.height - okraj;
			int sirka = d.width - (2 * okraj);
			for (int i = 0; i <= sirka; i = i + inc) {
				if (i > 0) {
					x1 += inc;
				}

				y2 = d.height - okraj - nolw;
				if (y2 == (d.height - okraj)) {
					y2 = lastValue;
				}
				if ((i / inc + 1) < array.length) {
					g.drawLine((int) x1, array[i / inc], (int) (x1 + inc), array[(i / inc) + 1]);
				}
			}

			g.drawString(Integer.toString(conn.getNumberOfLearnedWords(0)), 25, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
