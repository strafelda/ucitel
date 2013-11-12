/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

import ucitel.forms.GUI;
import java.util.*;
import javax.swing.JOptionPane;
import java.text.Normalizer;
import java.text.Normalizer.*;
import java.sql.SQLException;
import java.util.Iterator;

/**
 *
 * @author strafeldap
 */
public class Teacher extends Observable {

	ConnectionToDB dbconn;
	Example example;
	Example previousExample;
	int ratio;
	private static Teacher uniqueInstance;
	public static final String ANSWER_SEPARATOR = ",";
	String beginsWithBracketsRegex = "^\\(.*\\).*";

	public void resetExample() {
		example = null;
	}

	public boolean deleteCurrent() {
		try {
			dbconn.deleteCurrent(example.getID());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean reconnect(LoginInfo li) {

		try {
			if (dbconn != null) {
				ConnectionToDB.close();
			}
			dbconn = ConnectionToDB.getInstance(li);
			setChanged();
			CommandToGUI ctg = new CommandToGUI();
			ctg.setDBError(false);
			notifyObservers(ctg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			setChanged();
			CommandToGUI ctg = new CommandToGUI();
			ctg.setDBError(true);
			notifyObservers(ctg);
		}

		return false;
	}

	public static synchronized Teacher getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Teacher();
		}
		return uniqueInstance;
	}
	GuiDirection direction;

	private Teacher() {
		super();
		ratio = 0;
		GUI gui = GUI.getInstance();
		addObserver(gui);
		direction = GuiDirection.getInstance();
	}

	public ArrayList<String> getAnswersLength() {
		String odpovedi[] = example.getForeign().split(this.ANSWER_SEPARATOR);

		ArrayList<String> seznamOdpovedi = new ArrayList<String>();

		for (int i = 0; i < odpovedi.length; i++) {
			seznamOdpovedi.add(odpovedi[i]);
			if (odpovedi[i].startsWith("to ")) {
				seznamOdpovedi.add(odpovedi[i].substring(3, odpovedi[i].length()));
			}
		}

		ArrayList<String> delky = new ArrayList<String>();
		Iterator i = seznamOdpovedi.iterator();
		while (i.hasNext()) {
			String odpoved = (String) i.next();
			delky.add(String.valueOf(odpoved.length()));
		}

		return delky;
	}

	public boolean questionWasAsked() {
		if (null == example) {
			return false;
		} else {
			return true;
		}
	}

	public void setDBConn() {
		try {
			dbconn = ConnectionToDB.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isPossibleExamineAgain() {
		return false;
	}

	public boolean isQuestionAvailable() {
		try {
			dbconn = ConnectionToDB.getInstance();
			if (dbconn != null) {

				if (true == dbconn.isQuestionAvailable()) {
					return true;
				} else {
					if ( dbconn.allAsked() ){
						setChanged();
						CommandToGUI ctg = new CommandToGUI(CommandToGUI.ALL_ASKED);
						ctg.setAsk(false);
						notifyObservers(ctg);
						return false;
					} else {
						setChanged();
						CommandToGUI ctg = new CommandToGUI();
						ctg.setAsk(false);
						notifyObservers(ctg);
						return false;
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
			return false;
		}

		return true;
	}

	public boolean getQuestionFromDB() {
		if (example != null) {
			previousExample = example;
		}

		try {
			if (dbconn == null) {
				setDBConn();
			}

			if (example != null) {
				example = dbconn.getQuestion(example.getID());

			} else {
				example = dbconn.getQuestion(0);
			}

			if ((example == null) || (example.getID() == 0)) {
				CommandToGUI ctg = new CommandToGUI();
				ctg.setAsk(false);
				setChanged();
				notifyObservers(ctg);
				return false;
			}
			setChanged();
			CommandToGUI ctg = new CommandToGUI();
			ctg.setAsk(true);
			notifyObservers(ctg);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public String getPreviousHint() {
		return previousExample.getHint();
	}

	public boolean previousQuestionExists() {
		if (previousExample == null) {
			return false;
		} else {
			return true;
		}
	}

	public String getPreviousQuestion() {
		if (direction.isFromCzech()) {
			return dopln_mezery_za_carky(previousExample.getForeign());
		} else {
			return dopln_mezery_za_carky(previousExample.getCzech());
		}
	}

	public String getPreviousCzech() {
		if (direction.isFromCzech()) {
			return previousExample.getCzech();
		} else {
			return previousExample.getForeign();
		}

	}

	public boolean exampleExists(String question, String answer) {
		return dbconn.isPresent(question, answer);
	}

	public String getAnswer(String question) {
		return dbconn.getAnswer(question);
	}

	public String getCzech() {
		if (example == null) {
			return "";
		} else {
			return format(example.getCzech(),0);
		}
	}

	public int getPreviousId() {
		return example.getID();
	}

	public String removeTo(String withTo) {

		if (withTo.startsWith("to ")) {
			return withTo.substring(3);
		} else {
			return withTo;
		}
	}

	public String getQuestionHint() {
		if ((example == null) || (example.getHint() == null)) {
			return "";
		} else {
			String hint = this.example.getHint();
			String withoutTo = removeTo(this.example.getForeign());
			withoutTo = this.removeBrackets(withoutTo);

			int index = hint.indexOf(withoutTo);
			int len = withoutTo.length();
			StringBuffer dots = new StringBuffer();
			for (int i = 0; i < len; i++) {
				dots.append(".");
			}

			if (index == -1) {
				return hint;
			}

			return hint.substring(0, index) + dots.toString() + hint.substring(index + len);
		}
	}

	public String getNapoveda() {
		if (example == null) {
			return "";
		} else {
			return example.getHint();
		}
	}

	/*private void setAnswer(String ans){
	example.setAnswer(ans);
	}*/
	public ArrayList<String> getAnswers() {
		ArrayList<String> answers = new ArrayList<String>();
		if (example == null) {
			answers.add("");
		} else {
			answers = this.toAnswers(example.getForeign().toLowerCase());
		}

		return answers;
	}

	public void deleteExample() {
		example = null;
		return;
	}

	public int[] getHint() {
		int[] array = new int[1];
		if (example != null && example.getForeign() != null && example.getCzech() != null) {
			//if ( direction.getDirection() == DirectionOfTranslation.FROM_ENGLISH_TO_CZECH ){
			if (example.getForeign().contains(Teacher.ANSWER_SEPARATOR)) {
				array[0] = 0;
				return array;
			} else {
				array[0] = example.getForeign().length();
				return array;
			}

		}
		array[0] = 0;
		return array;
	}

	public int getAnswerCount() {
		int answerCount = 0;
		try {
			if ((dbconn != null) && (example != null)) {
				answerCount = dbconn.getAnswerCount(example.getID());
			} else {
				answerCount = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
		}
		return answerCount;
	}

	public int getPreviousAnswerCount() {
		int answerCount = 0;
		try {
			if ((dbconn != null) && (previousExample != null)) {
				answerCount = dbconn.getAnswerCount(previousExample.getID());
			} else {
				answerCount = -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
		}
		return answerCount;
	}

	public String doplnMezery(String retezec) {
		String novy = retezec;
		boolean change = false;

		if (retezec.matches(".*,\\w.*")) {
			boolean found = false;
			int index = retezec.indexOf(",");
			while (found == false) {
				if (retezec.substring(index + 1, index + 2).matches("\\w")) {
					found = true;
				} else {
					index = retezec.indexOf(",", index + 1);
				}
			}

			if (index != -1) {
				novy = retezec.substring(0, index + 1) + " " + retezec.substring(index + 1);
				change = true;
			}

                        retezec = novy;
		}

		if (change == true) {
			novy = doplnMezery(novy);
		}

		return novy;
	}

	public boolean fix_current(String newCzech, String newEnglish, String newHint, int newGrammar, String newTheme) {
		try {
			return dbconn.fix(example.getID(), newCzech, newEnglish, newHint, newGrammar, newTheme);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
			return false;
		}
	}

	public boolean fix_previous(String newCzech, String newEnglish, String newHint, int newGrammar, String newTheme) {
		try {
			return dbconn.fix(previousExample.getID(), newCzech, newEnglish, newHint, newGrammar, newTheme);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
			return false;
		}
	}

	public void resetOneAnsweredDate() {
		try {
			dbconn.resetOneAnsweredDate();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
		}
	}

	public int getQuestionsLeft() {
		int left = 0;
		try {
			if (dbconn == null) {
				dbconn = ConnectionToDB.getInstance();
			}

			left = dbconn.toAskCnt();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
		}
		return left;
	}

	public int getPercentDone(int nowtat) {

		float percent = 0;

		try {
			percent = 100 * (float) (nowtat) / (getQuestionsLeft());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
		}

		return (int) percent;
	}

	private int getNumberOfAnswers() {
		return getAnswers().size();
		/*String czech = this.getAnswer();
		int numberOfAnswers = 1;
		int index = 0;
		for (;;) {
			index = czech.indexOf(Teacher.ANSWER_SEPARATOR, index);
			if (index == -1) {
				if (czech.matches(beginsWithBracketsRegex)) {
					numberOfAnswers++;
				}
				return numberOfAnswers;
			} else {
				index++;
				if (czech.matches(beginsWithBracketsRegex)) {
					numberOfAnswers++;
				}
				numberOfAnswers++;
			}
		}*/
	}

	private int getNumberOfAnswers(String answer) {
		String czech = answer;
		int numberOfAnswers = 1;
		int index = 0;
		for (;;) {
			index = czech.indexOf(ANSWER_SEPARATOR, index);
			if (index == -1) {
				if (czech.matches(beginsWithBracketsRegex)) {
					numberOfAnswers++;
				}
				return numberOfAnswers;
			} else {
				index++;
				if (czech.matches(beginsWithBracketsRegex)) {
					numberOfAnswers++;
				}
				numberOfAnswers++;
			}
		}
	}

	private boolean chybi_mezery_za_carkama(String answer) {
		int index = answer.indexOf(",");
		if (index != -1) {
			if (!answer.substring(index + 1, index + 2).equals(" ")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	//vstup: "neco,neco jinyho"
	//vystup: "neco, neco jinyho"
	private String dopln_mezery_za_carky(String answer) {
		int index = answer.indexOf(",");

		while (chybi_mezery_za_carkama(answer)) {
			answer = answer.substring(0, index) + ", " + answer.substring(index + 1);

		}
		return answer;
	}

	private boolean separatorIsInBrackets(String answer, int index) {
		String partbefore = answer.substring(0, index);
		int opening = this.getCharCnt("(", partbefore);
		int closing = this.getCharCnt(")", partbefore);
		if (opening == closing) {
			return false;
		} else {
			return true;
		}

	}

	private int getPositionOfSeparator(String answer, int poradiSeparatoru) {

		int index = 0;

		for (int i = 0; i <= poradiSeparatoru; i++) {
			index = answer.indexOf(this.ANSWER_SEPARATOR, index + 1);
		}

		//kdyz neobsahuje oddelovac
		if (index == -1) {
			return -1;
		}

		if (separatorIsInBrackets(answer, index)) {
			return getPositionOfSeparator(answer, ++poradiSeparatoru);
		}

		int openingBrCnt = 0;
		int closingBrCnt = 0;
		int indexBR = 0;

		if (answer.indexOf("(", index) < index) {
			//spocitej oteviraci zavorky pred carkou
			while (true) {
				indexBR = answer.indexOf("(", index);
				if ((indexBR == -1) || (index > indexBR)) {
					break;
				}
				openingBrCnt++;
			}

			//spocitej zaviraci zavorky
			while (true) {
				indexBR = answer.indexOf(")", index);
				if ((indexBR == -1) || (index > indexBR)) {
					break;
				}
				closingBrCnt++;
			}
		}

		if (openingBrCnt == closingBrCnt) {
			return index;
		} else {
			return getPositionOfSeparator(answer, ++poradiSeparatoru);
		}


	}

	private int getPositionOfBracket(String answer, String bracket) {
		if (bracket.equals("(")) {
			return answer.indexOf(bracket);
		} else {
			int index = answer.indexOf(")");

			if (index == (answer.length() - 1)) {
				return index;
			}

			if (getCharCnt("(", answer.substring(0, index)) == (getCharCnt(")", answer.substring(0, index + 1)))) {
				return index;
			} else {
				return index + getPositionOfBracket(answer.substring(index + 1), bracket) + 1;
			}
		}

	}

	public String removeBrackets(String answer) {

		int separatorPos = getPositionOfSeparator(answer, 0);
		List<String> answers = new ArrayList<String>();

		if (separatorPos == -1) {
			answers.add(answer);
		} else {
			while (separatorPos != -1) {
				answers.add(answer.substring(0, separatorPos).trim());
				answer = answer.substring(separatorPos + 1).trim();
				separatorPos = getPositionOfSeparator(answer, 0);
			}
			answers.add(answer.substring(separatorPos + 1).trim());
		}

		int openingBR;
		int closingBR;
		List<String> ansNoBr = new ArrayList<String>();
		//Customer customer : customers

		for (String ans : answers) {
			if (ans.indexOf("(") != -1) {
				StringBuffer ansSB = new StringBuffer(ans);
				openingBR = getPositionOfBracket(ans, "(");
				closingBR = getPositionOfBracket(ans, ")");
				ansNoBr.add(ansSB.delete(openingBR, closingBR + 1).toString().trim());
				if (ans.length() >= closingBR + 2) {
					//if(!ans.substring(closingBR+1, closingBR+2).equals(" ")){
					//udelej z (vy)tvorit vytvorit a tvorit
					ansNoBr.add(ans.substring(0, openingBR) + ans.substring(openingBR + 1, closingBR) + ans.substring(closingBR + 1));
					//} else {
					//    ansNoBr.add(ans.substring(0, openingBR) + ans.substring(openingBR+1, closingBR) + ans.substring(closingBR+1));
					//}
				}
			} else {
				ansNoBr.add(ans);
			}
		}


		//sloz odpovedi
		String fin = "";
		for (String ans : ansNoBr) {
			if (fin.equals("")) {
				fin = ans;
			} else {
				fin = fin + ", " + ans;
			}
		}

		if (-1 != fin.indexOf("(")) {
			fin = removeBrackets(fin);
		}

		return fin;



	}

	private int getCharCnt(String what, String str) {
		int br = 0;
		int count = 0;


		while (br != -1) {
			br = str.indexOf(what, br);
			if (br != -1) {
				count++;
				br++;
			}
		}

		return count;
	}

	private int findLeftBracket(String str) {
		//int br = str.indexOf(")");
		int br = 0;
		int tmp = 0;

		// int charCnt = getCharCnt(str,")");

		while (br != -1) {
			br = str.indexOf(")", br + 1);

			if (getCharCnt(str.substring(0, br + 1), "(") == getCharCnt(str.substring(0, br + 1), ")")) {
				tmp = br;
				break;
			}

			if (br != -1) {
				tmp = br;
			}
		}



		return tmp;
	}

	private String removeDoubleSpaces(String input) {
		StringBuffer inputSB = new StringBuffer(input);
		int index = inputSB.indexOf(" ");
		while (index != -1) {
			if ( index == (inputSB.length() - 1)  ){
                                inputSB.delete(index, index + 1);
                        } else if (inputSB.substring(index + 1, index + 2).equals(" ")) {
				inputSB.delete(index + 1, index + 2);
			}

			index = inputSB.indexOf(" ", index + 1);
		}

		return inputSB.toString();
	}

	/*private List<String> toAnswers() {
		int numberOfAnswers = this.getNumberOfAnswers();
		String[] answersWithExtendables = new String[numberOfAnswers];
		String withoutBrackets = removeDoubleSpaces(removeBrackets(this.getAnswer()));

		if (withoutBrackets.isEmpty()) {
			return null;
		}
		//removeBrackets can change number of answers
		numberOfAnswers = this.getNumberOfAnswers(withoutBrackets);
		answersWithExtendables = withoutBrackets.split(ANSWER_SEPARATOR);

		List<String> odpovedi = new ArrayList<String>();
		for (int i = 0; i < answersWithExtendables.length; i++) {
			odpovedi.add(new StringBuffer(answersWithExtendables[i]).toString());
		}

		String[] answers = new String[odpovedi.size()];
		Iterator it = odpovedi.iterator();
		int iter = 0;
		while (it.hasNext()) {
			String o = (String) it.next();
			answers[iter] = o;
			iter++;
		}

		String[] answersBezDiakritiky = new String[answers.length];
		for (int i = 0; i < answers.length; i++) {

			String normalized;
			if (!direction.isToJapanese()) {
				normalized = Normalizer.normalize(answers[i].trim(), Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			} else {
				normalized = answers[i].trim();
			}

			answersBezDiakritiky[i] = normalized;
		}
		StringBuffer bezZavorek[] = new StringBuffer[numberOfAnswers];
		List<String> fin = new ArrayList<String>();

		for (int i = 0; i < answers.length; i++) {
			bezZavorek[i] = new StringBuffer(answersBezDiakritiky[i]);
			fin.add(bezZavorek[i].toString().trim());
		}

		String verbPrefix = "to ";
		if (direction.getLngTo() == Language.ENGLISH) {
			for (int i = 0; i < answers.length; i++) {
				if (fin.get(i).startsWith(verbPrefix)) {
					fin.add(fin.get(i).substring(verbPrefix.length()));
				}
			}
		}


		fin = rozlozLomitko(fin);

		return fin;
	}
*/
	private ArrayList<String> toAnswers(String word) {
		int numberOfAnswers = this.getNumberOfAnswers(word);
		String[] answersWithExtendables = new String[numberOfAnswers];
		String withoutBrackets = removeDoubleSpaces(removeBrackets(word));

		if (withoutBrackets.isEmpty()) {
			return null;
		}
		//removeBrackets can change number of answers
		numberOfAnswers = this.getNumberOfAnswers(withoutBrackets);
		answersWithExtendables = withoutBrackets.split(ANSWER_SEPARATOR);

		List<String> odpovedi = new ArrayList<String>();
		for (int i = 0; i < answersWithExtendables.length; i++) {
			odpovedi.add(new StringBuffer(answersWithExtendables[i]).toString());
		}

		String[] answers = new String[odpovedi.size()];
		Iterator it = odpovedi.iterator();
		int iter = 0;
		while (it.hasNext()) {
			String o = (String) it.next();
			answers[iter] = o;
			iter++;
		}

		String[] answersBezDiakritiky = new String[answers.length];
		for (int i = 0; i < answers.length; i++) {

			String normalized;
			if (!direction.isToJapanese()) {
				normalized = Normalizer.normalize(answers[i].trim(), Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			} else {
				normalized = answers[i].trim();
			}

			answersBezDiakritiky[i] = normalized;
		}
		StringBuffer bezZavorek[] = new StringBuffer[numberOfAnswers];
		ArrayList<String> fin = new ArrayList<String>();

		for (int i = 0; i < answers.length; i++) {
			bezZavorek[i] = new StringBuffer(answersBezDiakritiky[i]);
			fin.add(bezZavorek[i].toString().trim());
		}

		String verbPrefix = "to ";
		if (direction.getLngTo() == Language.ENGLISH) {
			for (int i = 0; i < answers.length; i++) {
				if (fin.get(i).startsWith(verbPrefix)) {
					fin.add(fin.get(i).substring(verbPrefix.length()));
				}
			}
		}


		fin = rozlozLomitko(fin);

		return fin;
	}

	private ArrayList<String> rozlozLomitko(ArrayList<String> answerList) {

		ArrayList<String> finalAnswer = new ArrayList<String>();
		boolean change = false;
		//rozklad vyrazu jako napr dotknout/dotykat na dve
		Iterator it = answerList.iterator();
		while (it.hasNext()) {
			String answer = (String) it.next();
			int rightIndex = answer.indexOf("/");
			if (rightIndex != -1) {
				int zbytekIndex = answer.indexOf(" ", rightIndex);
				String zacatek = "";
				int konecZacatku = 0;
				if (answer.indexOf(" ") < rightIndex) {
					konecZacatku = answer.indexOf(" ");
					if (konecZacatku >= 0) {
						zacatek = answer.substring(0, konecZacatku);
					} else {
						konecZacatku = 0;
						zacatek = "";
					}
				}

				String prvni;
				if (zbytekIndex == -1) {
					prvni = answer.substring(konecZacatku, rightIndex);
				} else {
					prvni = answer.substring(konecZacatku, rightIndex) + answer.substring(zbytekIndex);
				}

				finalAnswer.add(zacatek + prvni);
				finalAnswer.add(zacatek + " " + answer.substring(rightIndex + 1));
				change = true;
			} else {
				finalAnswer.add(answer);
			}
		}

		if (change == true) {
			finalAnswer = rozlozLomitko(finalAnswer);
		}

		return finalAnswer;

	}

	public String buildRegex(String regex, int from) {

		int index = regex.indexOf(" ", from);
		if (index != -1) {
			return buildRegex(regex.substring(0, index) + "[ -]" + regex.substring(index + 1), index + 4);
		}

		index = regex.indexOf("-", from);
		if (index != -1) {
			return buildRegex(regex.substring(0, index) + "[ -]" + regex.substring(index + 1), index + 4);
		}

		return regex;
	}

	public boolean checkPartial(int partialLen, String partialAnswer) throws UcitelException {
		String partialAnswerBezDiakritiky = null;

		if (!direction.isToJapanese()) {
			partialAnswerBezDiakritiky = Normalizer.normalize(partialAnswer, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		} else {
			partialAnswerBezDiakritiky = partialAnswer;
		}



		if (GUI.getInstance().getGrammar() == false) {
			partialAnswerBezDiakritiky = buildRegex(partialAnswerBezDiakritiky, 0);
		}

		String regex = partialAnswerBezDiakritiky + ".*";

		List<String> answList = null;
		if (GUI.getInstance().getGrammar() == true) {
			answList = new ArrayList<String>();
			answList.add(example.getForeign());
		} else {
			answList = getAnswers();
		}

		String[] fin = new String[answList.size()];
		Iterator it = answList.iterator();
		int counter = 0;
		while (it.hasNext()) {
			fin[counter] = (String) it.next();
			counter++;
		}

		int numberOfAnswers = fin.length;

		for (int i = 0; i < numberOfAnswers; i++) {
			if (GUI.getInstance().getGrammar() == true) {
				if (fin[i].matches(regex)) {
					return true;
				}
			} else {
				if (fin[i].trim().matches(regex)) {
					return true;
				}
			}

		}


		if (partialAnswer.endsWith(" ") || partialAnswer.endsWith("-")) {
			for (int i = 0; i < numberOfAnswers; i++) {

				if (fin[i].trim().matches(regex.substring(0, regex.length() - 6) + "-.*")) {
					throw new UcitelException("-");
				}

				if (fin[i].trim().matches(regex.substring(0, regex.length() - 6) + " .*")) {
					throw new UcitelException(" ");
				}
			}
		}

		return false;
	}

	public Example getCurrentExample() {
		return example;
	}

	public Example getPreviousExample() {
		return this.previousExample;
	}

	public void adjustWeight(boolean answer) {
		try {
			if (answer == true) {
				ratio++;
				dbconn.decreaseWeight(example.getID());
			} else {
				ratio -= dbconn.getNumberOfCorrectAnswers(example.getID());
				dbconn.increaseWeight(example.getID());
			}
			//dbconn.updateTimestamp(example.getID());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkAnswer(String word) {

		Answer answer = new Answer();
		setChanged();

		if (example != null) {
			try {
				List<String> answers;
				
				answers = getAnswers();

				answers = replaceDashes(answers);

				StringBuffer[] SBlist = new StringBuffer[answers.size()];
				String[] list = new String[answers.size()];
				Iterator it = answers.iterator();
				int counter = 0;
				while (it.hasNext()) {
					list[counter] = (String) it.next();
					counter++;
				}

				for (int i = 0; i < list.length; i++) {
					SBlist[i] = new StringBuffer(list[i]);
				}

				ArrayList<String> list2 = new ArrayList<String>();

				if (GUI.getInstance().getGrammar()) {
					list2.add(SBlist[0].toString());
				} else {
					for (int i = 0; i < list.length; i++) {
						list2.add(buildRegex(SBlist[i].toString().trim(), 0));
					}
				}


				ArrayList<String> list3 = new ArrayList<String>();

				Iterator i1 = list2.iterator();
				while (i1.hasNext()) {
					String word2 = (String) i1.next();
					list3.add(word2);
					if (direction.getLngTo() == Language.ENGLISH) {
						if (word2.startsWith("to ")) {
							list3.add(word2.substring(3, word2.length()));
						}
					}

				}

				boolean theAnswer = false;
				Iterator i = list3.iterator();
				while (i.hasNext()) {
					String word2 = (String) i.next();
					if (word2.length() > 0) {
						String normalized;
						if (!direction.isToJapanese()) {
							normalized = Normalizer.normalize(word.trim(), Form.NFD).replaceAll("[^\\p{ASCII}]", "");
						} else {
							normalized = word.trim();
						}

						if (true == normalized.matches(word2.trim())) {
							theAnswer = true;
							previousExample = example;
							break;
						}
					}
				}

				if (theAnswer == true) {
					answer.set(true);
					notifyObservers(answer);
					return true;
				} else {
					if (word.isEmpty()) {
						answer.set(false);
						answer.setCorrectAnswer(example.getForeign());
						setChanged();
						notifyObservers(answer);
					}
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "Database error", JOptionPane.OK_OPTION);
			}

		}
		return false;
	}

	public boolean checkWord(String word) {

		try {
			if (this.toAnswers(word) != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public String getQuestion(DirectionOfTranslation d) {
		Language from = d.getForeignLanguage();

		if (from == Language.CZECH) {
			return format(example.getForeign(),0);
		} else {
			return format(example.getCzech(),0);
		}

	}

	private String format(String word, int indexFrom){
		int index = word.indexOf(",",indexFrom);
		if ( (index!=-1) &&  (!word.substring(index+1, index+2).equals(" ")) ){
			word = format(word.substring(0, index+1) + " " + word.substring(index+1),index+1);
		}

                word = doplnMezery(word);
		return word;
	}

	public void cleanExample() {
		example = null;
	}

	private List<String> replaceDashes(List<String> answers) {
		List<String> newAnswers = new ArrayList<String>();
		for (String str: answers){
			int index = str.indexOf("-");
			StringBuffer sb = new StringBuffer(str);
			while (index > -1){
				sb.replace(index, index+1, " ");
				index = str.indexOf("-",index+1);
			}
			newAnswers.add(sb.toString());
		}

		return newAnswers;


	}

}
