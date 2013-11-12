/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

import org.jdom.Document;
import java.util.List;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.Element;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import org.jdom.JDOMException;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 *
 * @author strafeldap
 */
public class XML {

	private static XML uniqueInstance;
	public final String passwordElementName = "Password";
	private int defaultAnswerCount = 8;
	private String mysqlbinpath;
	private String profileName;
	private String downloadWav = "downloadWav";
	private String sound = "sound";
	private String defaultMySQLPath = "";
	private SAXBuilder parser;
	private String lengthOfListString = "LengthOfList";
	private Document doc;
	private boolean dummy;
	private String defaultLanguage = "english";
	private String defaultNumber_of_answers = "10";
	private String defaultLength_of_list = "8";
	private String defaultmysqlbinpath = "";
	private final String lastConnectionNameString = "LastConnectionName";
	private final String userString = "User";
	private final String passwordHashString = "PasswordHash";
	private final String connectionNameString = "ConnectionName";
	private final String loginString = "Login";
	private final String defaultHost = "localhost";
	private final String mysqlbinpathString = "MySQLPath";
	public final String grammar_true = "true";
	private final String directionString = "direction";
	private final String defaultDirection = "english2czech";
	private final String GRAMMAR = "grammar";
	private String connection;
	private String theme = "Last_theme";
	private String proxy = "proxy";
	private String proxy_port = "proxy_port";
	private String use_proxy = "use_proxy";
	private String languageString = "Language";

	public String getPasswordElementName() {
		return this.passwordElementName;
	}

	public void addUser(LoginInfo li, boolean storePassword) {
		Element accounts = doc.getRootElement();
		//Element users = accounts.getChild(this.userString);
		Element user = new Element(this.userString);
		Element lu = accounts.getChild(this.lastConnectionNameString);
		//lu.addContent();
		lu.setText(li.getConName());

		Element loginElement = new Element("Login").addContent(li.getLogin());
		Element passwordElement = null;
		if (storePassword == true) {
			passwordElement = new Element("Password").addContent(li.getPassword());
		} else {
			passwordElement = new Element("Password").addContent("");
		}
		Element passwordHashElement = new Element(passwordHashString).addContent(Integer.toString(li.getPassword().hashCode()));
		Element conNameElement = new Element(this.connectionNameString).addContent(li.getConName());
		Element portElement = new Element("Port").addContent(li.getPort());
		Element hostElement = new Element("Host").addContent(li.getHost());
		Element mysqlpathElement = new Element("MySQLPath").addContent(li.getMysqlPath());
		Element languageElement = new Element("Language").addContent(defaultLanguage);
		Element number_of_answersElement = new Element("Number_of_answers").addContent(defaultNumber_of_answers);
		Element lengthOfListElement = new Element("LengthOfList").addContent(defaultLength_of_list);
		Element directionElement = new Element(this.directionString).addContent(DirectionOfTranslation.getDefaultDirection());

		user.addContent(loginElement);
		user.addContent(conNameElement);
		user.addContent(passwordElement);
		user.addContent(passwordHashElement);
		user.addContent(portElement);
		user.addContent(hostElement);
		user.addContent(mysqlpathElement);
		user.addContent(languageElement);
		user.addContent(number_of_answersElement);
		user.addContent(lengthOfListElement);
		user.addContent(directionElement);
		accounts.addContent(user);
		this.writeXML();
	}

	public ArrayList<String> getConNames() {
		ArrayList<String> al = new ArrayList<String>();
		Element accounts = doc.getRootElement();

		List seznamUzivatelu = accounts.getChildren();
		Iterator i = seznamUzivatelu.iterator();
		while (i.hasNext()) {
			Element element = (Element) i.next();
			if (element.getName().equalsIgnoreCase(this.userString)) {
				Element e = element.getChild(this.connectionNameString);
				if (e != null) {
					String conName = e.getText();
					if (conName != null) {
						al.add(conName);
					}
				}
			}
		}

		return al;
	}

	public void setLastConnection(String con) {

		this.connection = con;
		Element accounts = doc.getRootElement();
		accounts.getChild(this.lastConnectionNameString).setText(con);
	}

	public void setShutDownDatabaseOnExit(boolean shutdown) {
		String shutdownString;
		if (shutdown == true) {
			shutdownString = "yes";
		} else {
			shutdownString = "no";
		}
		modifyXML("ShutDownDatabaseOnExit", shutdownString);
	}

	public void setGrammar(String grammar) {
		modifyXML(GRAMMAR, grammar);
	}

	public void setDirection(DirectionOfTranslation direction) {
		if (direction.isFromCzech()) {
			modifyXML(this.directionString, "to");
		} else {
			modifyXML(this.directionString, "from");
		}
	}

	public void setLanguage(Language l) {
		if (l == Language.CZECH) {
			throw new UnsupportedOperationException();
		}
		modifyXML(this.languageString, l.name());

		return;
	}

	public void setAnswerCnt(int answers) {
		modifyXML("Number_of_answers", Integer.toString(answers));
	}

	public void setListLen(int listLen) {
		modifyXML(this.lengthOfListString, Integer.toString(listLen));
	}

	private void setLastUser(String lastUser) {
		Element accounts = doc.getRootElement();
		accounts.getChild("LastUser").setText(lastUser);
	}

	public void setDBuser(String DBuser) {
		setLastUser(DBuser);
		modifyXML("Login", DBuser);
		return;
	}

	public void setSoundOn(boolean sound) {
		if (sound == true) {
			modifyXML(this.sound, "true");
		} else {
			modifyXML(this.sound, "false");
		}
	}

	public void setDownloadWav(boolean download) {
		if (download == true) {
			modifyXML(this.downloadWav, "true");
		} else {
			modifyXML(this.downloadWav, "false");
		}

	}

	public void setConName(String conName) {
		modifyXML(this.connectionNameString, conName);
		return;
	}

	public void setDBhost(String DBhost) {
		//this.DBhost=DBhost;
		modifyXML("Host", DBhost);
		return;
	}

	public void setDBport(String port) {
		//this.port=port;
		modifyXML("Port", port);
		return;
	}

	public void setPasswordHash(String password) {
		//this.password = password;
		try {
			modifyXML(passwordHashString, Integer.toString(password.hashCode()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public void setPassword(String password) {
		try {
			modifyXML("Password", password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public boolean getSoundOn() {
		String sound = getValue(this.sound);

		if (sound == null) {
			this.setSoundOn(true);
			this.writeXML();
			return true;
		}

		if (sound.matches("true")) {
			return true;
		} else {
			return false;
		}

	}

	public boolean getDownloadWav() {
		String download = getValue(this.downloadWav);

		if (download == null) {
			this.setDownloadWav(true);
			this.writeXML();
			return true;
		}

		if (download.matches("true")) {
			return true;
		} else {
			return false;
		}

	}

	public String getLastTheme() {
		String theme = getValue(this.theme);

		if (theme == null) {
			return "";
		} else {
			return theme;
		}
	}

	public String getProxy_port() {
		String proxyPort = getValue(this.proxy_port);

		if (proxyPort == null) {
			return "";
		} else {
			return proxyPort;
		}
	}

	public boolean isFromCzech() {
		
		if (dummy == true) {
			return false;
		}

		try{
			String dir = getValue(this.directionString);
			if (dir.equals("from")) {
			return false;
			} else {
				return true;
			}
		} catch (Exception e){
			return true;
		}
		
	}

	public Language getLanguage() {
		String lang = getValue(this.languageString);


		if (lang == null) {
			return Language.ENGLISH;
		} else {
			Language language;
			try {
				language = Language.valueOf(lang);
			} catch (Exception e) {
				language = Language.ENGLISH;
			}
			return language;
		}
	}

	public String getUseProxy() {
		String use_proxy = getValue(this.use_proxy);

		if (use_proxy == null) {
			return "";
		} else {
			return use_proxy;
		}

	}

	public String getGrammar() {
		String grammar = getValue(GRAMMAR);

		if (grammar == null) {
			return "false";
		} else {
			return grammar;
		}
	}

	public String getProxy() {
		String proxy = getValue(this.proxy);

		if (proxy == null) {
			return "";
		} else {
			return proxy;
		}
	}

	public void setLastTheme(String theme) {
		try {
			modifyXML(this.theme, theme);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public void setProxy(String proxy) {
		try {
			modifyXML(this.proxy, proxy);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public void setProxyPort(String proxy_port) {
		try {
			modifyXML(this.proxy_port, proxy_port);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public void setUseProxy(String proxy) {
		try {
			modifyXML(this.use_proxy, proxy);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public String getPassword(String login) {
		try {
			Element accounts = doc.getRootElement();
			List seznamUzivatelu = accounts.getChildren(this.userString);

			Iterator uzivatelIt = seznamUzivatelu.iterator();
			while (uzivatelIt.hasNext()) {
				Element uzivatel = (Element) uzivatelIt.next();
				if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(login)) {
					return uzivatel.getChild(this.passwordElementName).getText();
				}
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

		return "";
	}

	public String getPasswordHash() {
		try {
			Element accounts = doc.getRootElement();
			List seznamUzivatelu = accounts.getChildren();
			Iterator uzivatelIt = seznamUzivatelu.iterator();
			while (uzivatelIt.hasNext()) {
				Element uzivatel = (Element) uzivatelIt.next();
				if (uzivatel.getName().equals("User")) {
					if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(this.getLastConnectionName())) {
						return uzivatel.getChild(this.passwordHashString).getText();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public String getPassword() {
		try {
			Element accounts = doc.getRootElement();
			List seznamUzivatelu = accounts.getChildren();
			Iterator uzivatelIt = seznamUzivatelu.iterator();
			while (uzivatelIt.hasNext()) {
				Element uzivatel = (Element) uzivatelIt.next();
				if (uzivatel.getName().equals("User")) {
					if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(this.getLastConnectionName())) {
						return uzivatel.getChild(this.passwordElementName).getText();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public void deleteConnection(String userToDelete) {
		Element accounts = doc.getRootElement();
		List uzivatele = accounts.getChildren(this.userString);
		Iterator i = uzivatele.iterator();
		while (i.hasNext()) {
			Element user = (Element) i.next();
			if (userToDelete.equalsIgnoreCase(user.getChildText(this.connectionNameString))) {
				//user.removeContent();
				user.getParent().removeContent(user);
				setLastConnection(doc.getRootElement().getChild(this.userString).getChild(this.connectionNameString).getText());
				writeXML();
				break;
			}
		}
	}

	private String getValue(String parameter) {
		String vysledek = null;
		try {
			Element accounts = doc.getRootElement();
			List seznamUzivatelu = accounts.getChildren();
			Iterator uzivatelIt = seznamUzivatelu.iterator();
			while (uzivatelIt.hasNext()) {
				Element uzivatel = (Element) uzivatelIt.next();
				if (uzivatel.getName().equals(userString)) {
					if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(this.getLastConnectionName())) {
						vysledek = uzivatel.getChild(parameter).getText();
						if (vysledek.isEmpty()) {
							throw new Exception();
						} else {
							return vysledek;
						}
					}
				}
			}

		} catch (Exception e) {
			String retVal = null;
			ErrorHandler.debug("XML::getValue():"+e.toString());
			if (parameter.equalsIgnoreCase(this.directionString)) {
				Element directionElement = new Element(this.directionString).addContent(DirectionOfTranslation.getDefaultDirection());
				Element accounts = doc.getRootElement();
				List seznamUzivatelu = accounts.getChildren();
				Iterator uzivatelIt = seznamUzivatelu.iterator();
				while (uzivatelIt.hasNext()) {
					Element uzivatel = (Element) uzivatelIt.next();
					if (uzivatel.getName().equals(userString)) {
						if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(this.getLastConnectionName())) {
							uzivatel.addContent(directionElement);
							break;
						}
					}
				}

				ErrorHandler.debug("XML::getValue(): Element added");
				writeXML();
				retVal = this.defaultDirection;
			}

			if (parameter.equalsIgnoreCase("Language")) {
				retVal = defaultLanguage;
			}
			if (parameter.equalsIgnoreCase("Number_of_answers")) {
				retVal = defaultNumber_of_answers;
			}

			if (parameter.equalsIgnoreCase("Length_of_list")) {
				retVal = defaultLength_of_list;
			}

			if (parameter.equalsIgnoreCase("Host")) {
				retVal = defaultHost;
			}

			if (parameter.equalsIgnoreCase(this.mysqlbinpathString)) {
				retVal = this.defaultmysqlbinpath;
			}
			writeXML();
			return retVal;
			//e.printStackTrace();
		}

		if (vysledek == null) {
			if (parameter.equalsIgnoreCase("Language")) {
				return defaultLanguage;
			}
			if (parameter.equalsIgnoreCase("MySQLPath")) {
				return this.defaultMySQLPath;
			}
		}

		return vysledek;
	}

	public String getDBhost() {
		return getValue("Host");
	}

	public DirectionOfTranslation getDirection() {
		String language = getValue(this.languageString);
		String dir = getValue(this.directionString);
		Language from;
		Language to;
		if (dir.equals("to")) {
			to = Language.valueOf(language);
			from = Language.CZECH;
		} else {
			to = Language.CZECH;
			from = Language.valueOf(language);
		}

		DirectionOfTranslation direction = new DirectionOfTranslation(from, to);
		return direction;
	}

	public String getConName() {
		return getValue(this.connectionNameString);
	}

	public Language getForeignLanguage() {
		return this.getLanguage();
	}

	public String getDBport() {
		return getValue("Port");
	}

	private String getLastConnectionName() {

		try {
			/*Element accounts = doc.getRootElement();
			Element child = accounts.getChild(this.lastConnectionNameString);
			return child.getTextTrim();*/
			return this.connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getMySQLPath() {
		String mysqlpath = getValue("MySQLPath");
		ErrorHandler.debug("XML::getMySQLPath(): returns " + mysqlpath);

		return mysqlpath;
	}

	public void modifyXML(String parameter, String value) {
		Element uzivatel = null;
		if (parameter == null) {
			return;
		}

		try {
			Element accounts = doc.getRootElement();
			List seznamUzivatelu = accounts.getChildren();
			Iterator uzivatelIt = seznamUzivatelu.iterator();
			while (uzivatelIt.hasNext()) {
				uzivatel = (Element) uzivatelIt.next();
				if (uzivatel.getName().equals("User")) {
					if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(this.getLastConnectionName())) {
						uzivatel.getChild(parameter).setText(value);
						writeXML();
						break;
					}
				}
			}

		} catch (NullPointerException e) {
			uzivatel.addContent(new Element(parameter).addContent(value));
			//uzivatel.addContent(new Element(parameter).addContent(value));
			//uzivatel.getChild("parameter").;
			modifyXML(parameter, value);
			e.printStackTrace();
		}
	}

	public void setMySQLPath(String path) {
		this.mysqlbinpath = path;
		try {
			Element accounts = doc.getRootElement();
			List seznamUzivatelu = accounts.getChildren();
			Iterator uzivatelIt = seznamUzivatelu.iterator();
			while (uzivatelIt.hasNext()) {
				Element uzivatel = (Element) uzivatelIt.next();
				if (uzivatel.getName().equals("User")) {
					if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(this.getLastConnectionName())) {
						uzivatel.getChild("MySQLPath").setText(path);
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ErrorHandler.debug("XML::setMySQLPath(): set to " + path);
		return;
	}

	public void setProfileName(String profileName) {
		//this.profileName = profileName;
		try {
			Element accounts = doc.getRootElement();
			List seznamUzivatelu = accounts.getChildren();
			Iterator uzivatelIt = seznamUzivatelu.iterator();
			while (uzivatelIt.hasNext()) {
				Element uzivatel = (Element) uzivatelIt.next();
				if (uzivatel.getName().equals("User")) {
					if (uzivatel.getChild(this.connectionNameString).getText().equalsIgnoreCase(this.getLastConnectionName())) {
						uzivatel.getChild("ConnectionName").setText(profileName);

						Iterator uzivatelIt2 = seznamUzivatelu.iterator();
						while (uzivatelIt.hasNext()) {
							Element uzivatel2 = (Element) uzivatelIt2.next();
							if (uzivatel2.getName().equals("LastConnectionName")) {
								uzivatel2.setText(profileName);
								this.setLastConnection(profileName);
								break;
							}
						}
						break;
					}
				}


			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	public void writeXML() {
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream outf = new FileOutputStream("accounts.xml");
			out.output(doc, outf);
			outf.close();
		} catch (Exception e) {

			System.out.println(e.getMessage());
		}

	}

	public int getLengthOfList() {
		if (getValue(this.lengthOfListString) == null) {
			return Integer.parseInt(this.defaultLength_of_list);
			//this.defaultLengthOfList;
		} else {
			return Integer.parseInt(getValue(this.lengthOfListString));
		}
	}

	public int getAnswerCnt() {
		if (getValue("Number_of_answers") == null) {
			return this.defaultAnswerCount;
		} else {
			return Integer.parseInt(getValue("Number_of_answers"));
		}
	}

	public int getListLen() {
		return Integer.parseInt(getValue(this.lengthOfListString));
	}

	public String getLastConnection() {
		return getLastConnectionName();
	}

	private XML() {
		ErrorHandler.debug("XML::XML() entered");
		try {
			parser = new SAXBuilder();
			String accountsXml = System.getProperty("user.dir") + "\\accounts.xml";
			ErrorHandler.debug("XML::XML() accountsXml: "+accountsXml);
			
			File a = new File(accountsXml);
			if (!a.exists()) {
				dummy = true;
				return;
			} else {
				dummy = false;
			}
			ErrorHandler.debug("XML::XML() accounts: " + accountsXml);
			doc = parser.build(accountsXml);

			Element accounts = doc.getRootElement();
			Element child = accounts.getChild(this.lastConnectionNameString);
			this.connection = child.getTextTrim();

		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
			File accounts = new File("accounts.xml");
			try {
				Document newdoc = new Document();
				Element root = new Element("Accounts");
				Element lastUserElement = new Element("LastUser");
				Element usersElement = new Element(this.userString);
				root.addContent(lastUserElement);
				root.addContent(usersElement);
				newdoc.setRootElement(root);
				XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
				FileOutputStream outf = new FileOutputStream(accounts);
				out.output(newdoc, outf);
				outf.close();
				doc = parser.build("accounts.xml");
			} catch (FileNotFoundException e2) {
				e.printStackTrace();
			} catch (IOException e2) {
				e.printStackTrace();
			} catch (JDOMException e2) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		uniqueInstance = this;
	}

	public boolean isEmpty() {
		return this.getLastConnectionName().isEmpty();
	}

	public static synchronized XML getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new XML();
		}
		return uniqueInstance;
	}

	public String getLogin() {
		return getValue(this.loginString);
	}

	public static void main(String args[]) {
		XML xml = new XML();
		if (xml.isFromCzech()){
			ErrorHandler.debug("is from czech");
		} else {
			ErrorHandler.debug("is not from czech");
		}
	}
}
