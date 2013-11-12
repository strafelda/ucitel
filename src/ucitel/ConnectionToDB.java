/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;

//import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.ConnectionPoolDataSource;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplateExt;
import ucitel.entity.Slovicka;
import ucitel.forms.GUI;

/**
 *
 * @author strafeldap
 */
public class ConnectionToDB implements Observer {

	private static boolean isConnected = false;
	private static ConnectionToDB uniqueInstance;
	private static Connection connection;
	private int userID;
	boolean stopDatabaseOnExit = false;
	boolean databaseIsRunning = false;
	boolean secondAttempt = false;
	int frequencyOfAsking = 2;
	private int askInRowCnt;
	private DirectionOfTranslation direction;
	private int startupCounter = 0;
	int numberOfConnectRetries = 0;
	String password = "heslo";

	public void update(Observable obj, Object arg) {
		if (arg instanceof DirectionOfTranslation) {
			direction = (DirectionOfTranslation) arg;
		}
	}

	public void setDirection(DirectionOfTranslation direction) {
		this.direction = direction;
	}

	private ConnectionToDB(LoginInfo li) throws SQLException, Exception {
		super();
		XML ini = XML.getInstance();
		askInRowCnt = ini.getAnswerCnt();
		connect(li);
		userID = getUserID(li.getLogin());
	}

	private ConnectionToDB() throws SQLException, Exception {
		super();
		direction = GuiDirection.getInstance();

	}

	public int getNumberOfNewWords(DirectionOfTranslation direction) throws Exception {
		int current = getNumberOfWords(direction);
		int previous = 0;
		for (int i = 0; i < 10; i++) {
			previous += getWordCntYesterday(i + 1);
		}
		return current - previous;
	}

	public void deleteUser(String user) {
		try {
			int id = getUserID(user);
			String query = "delete from " + this.getSlovickaTableName() + " where userID=" + id;
			Statement stat = connection.createStatement();
			stat.executeUpdate(query);
			stat.close();
			String query2 = "delete from slovicka.users where userID=" + id;
			stat.executeUpdate(query2);
			stat.close();
			String query3 = "delete from slovicka.slovicka_stats where userID=" + id;
			stat.executeUpdate(query3);
			stat.close();
		} catch (SQLException e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
			try {
				reconnect();
			} catch (Exception ex) {
				System.out.println(ex.toString());
				System.out.println("Cannot reconnect");
				ex.printStackTrace();
			}


		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}
	}

	public int getNumberOfWords(DirectionOfTranslation direction) throws SQLException {
		int wordCnt;

		Statement stat = connection.createStatement();

		stat.execute("select count(*) from " + this.getSlovickaTableName() + " where userID = " + userID + " and language = '" + direction.getForeignLanguage() + "'");
		ResultSet rs = stat.getResultSet();
		if (true == rs.next()) {
			wordCnt = rs.getInt(1);
		} else {
			wordCnt = 0;
		}
		rs.close();
		stat.close();

		return wordCnt;
	}

	public void resetLastAsked(int cnt) {

		Statement stat = null;
		try {
			stat = connection.createStatement();
			if (direction.isFromCzech()) {
				String query = "update " + this.getSlovickaTableName() + " "
					+ "set last_asked=TIMESTAMP('1970-01-01 01:01:01') "
					+ "where theme_id=" + this.getThemeID(GUI.getInstance().getTheme()) + " "
					+ "and language='" + direction.getForeignLanguage() + "' "
					+ "and CA_from_czech=" + cnt;
				stat.execute(query);
			} else {
				String query = "update " + this.getSlovickaTableName() + " "
					+ "set last_asked_to_czech=TIMESTAMP('1970-01-01 01:01:01') "
					+ "where theme_id=" + this.getThemeID(GUI.getInstance().getTheme()) + " "
					+ "and language='" + direction.getForeignLanguage() + "' "
					+ "and correctly_answered=" + cnt;
				stat.execute(query);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			System.out.println(ex.toString());
			ErrorHandler.logError(ex);
		} finally {
			try {
				stat.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				System.out.println(ex.toString());
				ErrorHandler.logError(ex);
			}
		}

	}

	public int getLeardnedWordsCnt() throws Exception {
		Statement stat = connection.createStatement();
		int wordCnt = 0;

		String getQuestionQuery = "select count(1) "
			+ "from " + this.getSlovickaTableName() + " "
			+ "where "
			+ "("
			+ "userID=" + userID + " "
			+ "and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
			+ "and language=\"" + direction.getForeignLanguage() + "\") "
			+ "or "
			+ "(userID=" + userID + " "
			+ "and CURDATE()<(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-4," + frequencyOfAsking + ")) DAY) "
			+ "and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
			+ "and language=\"" + direction.getForeignLanguage() + "\") "
			+ "order by " + direction.getCAString() + ",weight desc";

		stat.execute(getQuestionQuery);
		ResultSet rs = stat.getResultSet();
		if (true == rs.next()) {
			wordCnt = rs.getInt(1);
		}

		rs.close();
		return getNumberOfWords(direction) - wordCnt;
	}

	public int getWordCntMoreOrEqual(int answerCount) throws Exception {
		Statement stat = connection.createStatement();
		int wordCnt = 0;

		stat.execute("select count(*) from " + this.getSlovickaTableName() + " where userID=" + userID + " and " + direction.getCAString() + ">=" + Integer.toString(answerCount) + " and language='" + direction.getForeignLanguage() + "'");
		ResultSet rs = stat.getResultSet();
		if (true == rs.next()) {
			wordCnt = rs.getInt(1);
		}

		rs.close();
		return wordCnt;
	}

	private boolean isZeroQuestionAvailable() {

		boolean ret = false;

		try {
			Statement stat = connection.createStatement();


			String grammar;
			if (GUI.getInstance().getGrammar()) {
				grammar = " grammar=1 and ";
			} else {
				grammar = " grammar=0 and ";
			}

			int theme_id = this.getThemeID(GUI.getInstance().getTheme());
			String themeQuery = null;
			if (theme_id > 0) {
				themeQuery = " and theme_id=" + theme_id + " ";
			} else {
				themeQuery = "";
			}

			String query = "select count(*) from " + this.getSlovickaTableName() + " where "
				+ grammar
				+ " userID=" + userID + ""
				+ themeQuery
				+ // " and "+direction.getLastAsked()+"<CURDATE()"+
				" and " + direction.getCAString() + "=0"
				+ " and language='" + direction.getForeignLanguage() + "'";


			stat.execute(query);

			ResultSet rs = stat.getResultSet();
			if (true == rs.next()) {
				if (rs.getInt(1) == 0) {
					ret = false;
				} else {
					ret = true;
				}
			}
			rs.close();
			stat.close();
		} catch (SQLException e) {
			ErrorHandler.logError(e);
			System.out.println(e.toString());
		} catch (Exception e) {
			ErrorHandler.logError(e);
			System.out.println(e.toString());
		}

		return ret;
	}

		String getSlovickaTableName() {
		return "3_slovicka";
	}

	public int getWordCnt(int answerCount, DirectionOfTranslation direction) {

		int wordCnt = 0;

		try {

			Statement stat = connection.createStatement();
			String grammar;
			if (GUI.getInstance().getGrammar()) {
				grammar = "";
			} else {
				grammar = " grammar=0 and ";
			}


			String query = "select count(*) from " + this.getSlovickaTableName() + " where"
				+ " userID=" + userID + " and"
				+ grammar
				+ " " + direction.getCAString() + "=" + Integer.toString(answerCount) + ""
				+ " and language='" + direction.getForeignLanguage() + "'";

			PreparedStatement prest = connection.prepareStatement(query);

			ResultSet rs = prest.executeQuery();


			/*stat.execute(query);

			ResultSet rs = stat.getResultSet();*/
			if (true == rs.next()) {
				wordCnt = rs.getInt(1);
			}
			rs.close();
			stat.close();
			prest.close();

		} catch (Exception e) {
			ErrorHandler.logError(e);
		}

		return wordCnt;

	}

	public int[] getWordCnts(int from, int to, DirectionOfTranslation direction) {
		ErrorHandler.debug("ConnectionToDB::getWordCnts(): entered");

		int[] wordCounts = new int[to];
		try {
			String grammar;
			if (GUI.getInstance().getGrammar()) {
				grammar = "";
			} else {
				grammar = " grammar=0 and ";
			}

			String query = "select count(*) from " + this.getSlovickaTableName() + " where"
				+ " userID=" + userID + " and"
				+ grammar
				+ " " + direction.getCAString() + "=?"
				+ " and language='" + direction.getForeignLanguage() + "'";

			PreparedStatement prest = connection.prepareStatement(query);

			for (int i = from; i < to; i++) {
				prest.setInt(1, i);
				ResultSet rs = prest.executeQuery();
				if (true == rs.next()) {
					wordCounts[i] = rs.getInt(1);
				}
				rs.close();
			}

			prest.close();

		} catch (Exception e) {
			ErrorHandler.logError(e);
		}

		ErrorHandler.debug("ConnectionToDB()::getWordCnts() returning");
		return wordCounts;

	}
	@PersistenceContext
	private EntityManager entityManager;

	@Deprecated
	protected Session getCurrentSession() {
		return (Session) entityManager.getDelegate();
	}

	public int getLearnedWordCnt(DirectionOfTranslation direction) throws Exception {
		int count = 0;
		try {
			Session session = getSession();
			//Transaction tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(Slovicka.class).setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq("userId", userID));
			criteria.add(Restrictions.gt(direction.getCA(), 10));
			criteria.add(Restrictions.eq("language", direction.getForeignLanguage().toString()));
			count = (Integer) criteria.uniqueResult();
			//tx.commit();

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return count;
	}

	/*public int askedTodayAnsweredWrong() {
		Statement stat;
		int wordCnt = 0;
		try {
			stat = connection.createStatement();
			stat.execute("select count(*) from " + this.getSlovickaTableName() + " where userID=" + userID + " and " + direction.getCAString() + "=0 and language='" + direction.getForeignLanguage() + "' and " + direction.getLastAsked() + " > CURDATE()");
			ResultSet rs = stat.getResultSet();
			if (true == rs.next()) {
				wordCnt = rs.getInt(1);
			}

			rs.close();
		} catch (Exception ex) {
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}
		

		
		return wordCnt;
	}*/

	public int getWordCntYesterday(int answerCount) throws Exception {
		Statement stat = connection.createStatement();
		int wordCnt = 0;
		String query = "select * from slovicka.slovicka_stats where userID=" + userID + " and date < CURDATE() order by date desc";
		stat.execute(query);
		ResultSet rs = stat.getResultSet();
		if (true == rs.next()) {
			wordCnt = rs.getInt(answerCount + 1);
			rs.close();
			return wordCnt;
		} else {
			wordCnt = 0;
		}

		rs.close();
		return wordCnt;
	}

	public boolean fix(int id, String newCzech, String newEnglish, String newHint, int newGrammar, String newTheme) throws SQLException, Exception {
		Statement stat = connection.createStatement();
		int newThemeId = this.getThemeID(newTheme);

		String hint = "";
		if (newHint != null) {
			hint = " hint=\"" + newHint + "\",";
		}

		String query = "update " + this.getSlovickaTableName() + " set"
			+ hint
			+ " czech=\"" + newCzech + "\","
			+ " lang=\"" + newEnglish + "\","
			+ " theme_id=\"" + newThemeId + "\","
			+ " grammar=" + newGrammar + ","
			+ " " + direction.getLastAsked() + "=NOW() where userID=" + userID + " and id=" + id + " and language='" + direction.getForeignLanguage() + "'";

		stat.executeUpdate(query);
		stat.close();
		return true;
	}

	public void updateTimestamp(int id) throws SQLException, Exception {
		try {
			Statement stat = connection.createStatement();
			String query = "update " + this.getSlovickaTableName() + " set " + direction.getLastAsked() + "=NOW() where id=" + id;
			stat.executeUpdate(query);
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ErrorHandler.logError(e);
		}
	}

	private void startDatabase() {

		ErrorHandler.debug("ConnectionToDB::startDatabase() entered");

		try {
			String mysqld_exe = XML.getInstance().getMySQLPath() + "\\bin\\mysqld-nt.exe";

			if (XML.getInstance().getMySQLPath().isEmpty()) {
				mysqld_exe = System.getProperty("user.dir") + "\\mysql\\bin\\mysqld-nt.exe";
			}

			ErrorHandler.debug("ConnectionToDB::startDatabase() mysqld_exe: " + mysqld_exe);
			File mysql = new File(mysqld_exe);
			String[] program = new String[2];
			
			if (!mysql.canExecute()) {
				if (XML.getInstance().getMySQLPath().isEmpty()) {
					program[0] = System.getProperty("user.dir") + "\\mysql\\bin\\mysqld-nt.exe";
				} else {
					program[0] = System.getProperty("user.dir") + XML.getInstance().getMySQLPath() + "\\bin\\mysqld-nt.exe";
				}

				program[1] = "--standalone";
			} else {
				program[0] = mysqld_exe;
				program[1] = "--standalone";
			}

			if (databaseIsRunning == false) {
				ErrorHandler.debug("ConnectionToDB::startDatabase() executing '" + program[0] + "'");
				execProgram(program, false);
				stopDatabaseOnExit = true;
				databaseIsRunning = true;
			}
		} catch (Exception e) {
			ErrorHandler.debug("ConnectionToDB::startDatabase() exception caught: " + e.toString());
			ErrorHandler.logError(e);
		}
		ErrorHandler.debug("ConnectionToDB::startDatabase() exitting");
	}

	private String getPortFromINI() throws IOException {

		try {
			String mysqlpath = XML.getInstance().getMySQLPath();
			File my_ini = new File(mysqlpath + "\\my.ini");
			String my_ini_s;
			if (!my_ini.canRead()) {
				ErrorHandler.debug("ConnectionToDB::getPortFromINI() can't read my.ini");
				my_ini_s = System.getProperty("user.dir") + "\\mysql\\my.ini";
				ErrorHandler.debug("ConnectionToDB::getPortFromINI() trying with " + my_ini_s);
				my_ini = new File(my_ini_s);
			} else {
				my_ini_s = mysqlpath + "\\my.ini";
			}

			while (!my_ini.canRead()) {
				ErrorHandler.debug("ConnectionToDB::getPortFromINI() still can't read");
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setDialogTitle("Vyberte cestu k MySQL");
				int selection = chooser.showOpenDialog(GUI.getInstance().getComponent());
				if (selection == JFileChooser.APPROVE_OPTION) {

					mysqlpath = chooser.getSelectedFile().toString();
					my_ini = new File(chooser.getSelectedFile().toString() + "\\my.ini");
					my_ini_s = mysqlpath + "\\my.ini";

					XML.getInstance().setMySQLPath(mysqlpath);
					XML.getInstance().writeXML();

				} else {
					this.disconnect();
					System.exit(-1);
				}
			}

			FileReader fr = new FileReader(my_ini_s);
			BufferedReader d = new BufferedReader(fr);

			String record;
			while ((record = d.readLine()) != null) {
				if (record.contains("[client]")) {
					while ((record = d.readLine()) != null) {
						if (record.contains("port")) {
							return record.substring(5, record.length()).trim();
						}
					}
					break;
				}
			}

		} catch (IOException e) {
			System.out.println("getPortFromINI: " + e.toString());
			throw e;
		} catch (Exception e) {
			ErrorHandler.logError(e);
		}

		return "";
	}

	public int getUserID(String userName) throws Exception {

		ErrorHandler.debug("ConnectionToDB::getUserID executed, numberOfConnectRetries= " + numberOfConnectRetries);
		this.numberOfConnectRetries++;

		if (numberOfConnectRetries > 1) {
			try {
				this.reconnect();
			} catch (Exception e) {
				ErrorHandler.debug("ConnectionToDB::getUserID(): reconnect failed");
			}
		}


		ErrorHandler.debug("ConnectionToDB::getUserID(): numberOfConnectRetries incremented ");

		if (connection == null) {
			ErrorHandler.debug("ConnectionToDB::getUserID(): connection == null, calling reconnect ");
			try {
				this.reconnect();
			} catch (Exception e) {
				ErrorHandler.debug("ConnectionToDB::getUserID(): reconnect failed");
			}

			ErrorHandler.debug("ConnectionToDB::getUserID(): returned from reconnect, connection= " + connection);
		}

		try {
			Statement stat = connection.createStatement();

			stat.execute("select userID from slovicka.users where username='" + userName + "'");
			ResultSet rs = stat.getResultSet();
			if (true == rs.next()) {
				int userId = rs.getInt(1);
				rs.close();
				stat.close();
				ErrorHandler.debug("ConnectionToDB::getUserID() returning userId: " + userId);
				numberOfConnectRetries = 0;
				return userId;
			} else {
				ErrorHandler.debug("ConnectionToDB::getUserID() throwing exception");
				throw new Exception();

			}
		} catch (SQLException e) {
			ErrorHandler.debug("ConnectionToDB::getUserID() caught exception");
			if (numberOfConnectRetries < 10) {
				getUserID(userName);
			}

			ErrorHandler.debug("getUserID can't connect");
			ErrorHandler.logError(e);
			ErrorHandler.debug("ConnectionToDB::getUserID() returning 0");
			return 0;
		}
	}

	public boolean reconnect() throws Exception {
		ErrorHandler.debug("ConnectionToDB::reconnect() entered");
		disconnect();
		secondAttempt = false;
		isConnected = false;

		connect();

		ErrorHandler.debug("ConnectionToDB::reconnect() finished");
		return true;
	}

	public boolean connect(LoginInfo li) throws SQLException, Exception {

		ErrorHandler.debug("ConnectionToDB::connect() entered");
		String port = null;

		//dataSource = new MysqlConnectionPoolDataSource();

		if (isLocal() == true) {
			port = getPortFromINI();
		}

		com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds = null;
		ds = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();
		ds.setUser(li.getRootLogin());
		ds.setPassword(li.getRootPassword());
		ds.setServerName(li.getHost());
		ds.setPortNumber(Integer.decode(li.getPort()));
		ds.setDatabaseName("slovicka");
		ds.setCharacterEncoding("UTF-8");
		ds.setAutoReconnect(true);

		//String url = "jdbc:mysql://" + host + ":" + port + "/slovicka?useUnicode=true&characterEncoding=UTF-8";
		if (li.getRootLogin() == null || li.getRootPassword() == null || Integer.decode(port) == null || li.getHost() == null) {
			ErrorHandler.debug("ConnectionToDB::connect() throwing exception 1");
			throw new Exception();
		}

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			ErrorHandler.debug("ConnectionToDB::connect() trying to get connection...");
			connection = ds.getConnection();
			ErrorHandler.debug("ConnectionToDB::connect() got connection: " + connection);
			ErrorHandler.debug("ConnectionToDB::connect() calling getUserID");
		} catch (SQLException e) {
			ErrorHandler.debug("ConnectionToDB::connect() exception caught");
			int errorCode = e.getErrorCode();
			if (errorCode == 0) {//database is stopped
				ErrorHandler.debug("ConnectionToDB::connect() database is not started yet");
				if ((li.isDBLocal() == true) && (secondAttempt == false)) {

					if (this.isLocal()) {
						startDatabase();
					}

					secondAttempt = true;
					connect(li);
				} else {
					ErrorHandler.debug("ConnectionToDB::connect() database is not local");
					//Thread.sleep(500);
					Thread.currentThread().sleep(500);
					startupCounter = startupCounter + 500;
					System.out.println("Waiting for 500 milliseconds");
					if (startupCounter > 50000) {
						ErrorHandler.debug("ConnectionToDB::connect() throwing exception 2");
						throw e;
					}
					connect(li);

				}
			}
			if (errorCode == 1045) {
				ErrorHandler.debug("ConnectionToDB::connect() throwing exception 3");
				throw new Exception("Database: Access denied");
			}

		} catch (ClassNotFoundException e) {
			ErrorHandler.debug("ConnectionToDB::connect() throwing exception 4");
			ErrorHandler.logError(e);
		} catch (InstantiationException e) {
			ErrorHandler.debug("ConnectionToDB::connect() throwing exception 5");
			ErrorHandler.logError(e);
		} catch (IllegalAccessException e) {
			ErrorHandler.debug("ConnectionToDB::connect() throwing exception 6");
			ErrorHandler.logError(e);
		}

		isConnected = true;
		ErrorHandler.debug("ConnectionToDB::connect() returns true");
		return true;
	}
	XML xml;

	public boolean connect() {

		ErrorHandler.debug("ConnectionToDB::connect() entered");

		//startDatabase();

		com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds = null;
		ds = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();
		ds.setUser("strafelda");
		ds.setPassword(password);
		ErrorHandler.debug("ConnectionToDB::connect() getting xml instance");
		xml = XML.getInstance();
		ds.setServerName(xml.getDBhost());
		ds.setPortNumber(Integer.decode(xml.getDBport()));
		ds.setDatabaseName("slovicka");
		ds.setCharacterEncoding("UTF-8");
		ds.setAutoReconnect(true);

		try {
			ErrorHandler.debug("ConnectionToDB::connect() creating db instance");
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			ErrorHandler.debug("ConnectionToDB::connect() trying to get connection...");
			connection = ds.getConnection();

			ErrorHandler.debug("ConnectionToDB::connect() got connection: " + connection);

			ErrorHandler.debug("ConnectionToDB::connect() calling getUserID");

		} catch (SQLException e) {
			ErrorHandler.debug("ConnectionToDB::connect() exception caught");
			int errorCode = e.getErrorCode();
			if (errorCode == 0) {//database is stopped
				ErrorHandler.debug("ConnectionToDB::connect() database is not started yet");
				if (secondAttempt == false) {


					startDatabase();


					secondAttempt = true;
					return connect();

				} else {
					ErrorHandler.debug("ConnectionToDB::connect() database is not local");
					try {
						//Thread.sleep(500);
						Thread.currentThread().sleep(500);
					} catch (InterruptedException ex) {
						Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
					}
					startupCounter = startupCounter + 500;
					System.out.println("Waiting for 500 milliseconds");
					if (startupCounter > 50000) {
						ErrorHandler.debug("ConnectionToDB::connect() can't connect to database - 1");
						System.exit(-1);
					}
					connect();

				}
			}
			if (errorCode == 1045) {
				ErrorHandler.debug("ConnectionToDB::connect() can't connect to database - 2");
			}

		} catch (ClassNotFoundException e) {
			ErrorHandler.debug("ConnectionToDB::connect() throwing exception 4");
			ErrorHandler.logError(e);
		} catch (InstantiationException e) {
			ErrorHandler.debug("ConnectionToDB::connect() throwing exception 5");
			ErrorHandler.logError(e);
		} catch (IllegalAccessException e) {
			ErrorHandler.debug("ConnectionToDB::connect() throwing exception 6");
			ErrorHandler.logError(e);
		}

		isConnected = true;
		//connectHibernate();

		askInRowCnt = xml.getListLen();
		try {
			this.userID = this.getUserID(xml.getLogin());
		} catch (Exception ex) {
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}
		updateLastAsked();
		askInRowCnt = xml.getAnswerCnt();
		ErrorHandler.debug("ConnectionToDB::connect() returns true");
		return true;
	}

	private void loadFilesFromInputDir() {
		return;
	}

	private void stopDatabase() {
		ErrorHandler.debug("ConnectionToDB::stopDatabase() entered");

		try {
			String[] program = {System.getProperty("user.dir") + "\\mysql\\bin\\mysqladmin",
				"-u", "root",
				"-P", xml.getDBport(),
				"shutdown"};
			execProgram(program, true);
		} catch (Exception e) {
			System.out.println("stopDatabase: " + e.toString());
			ErrorHandler.logError(e);
		}
		ErrorHandler.debug("ConnectionToDB::stopDatabase() returning");
	}

	public void deleteCurrent(int id) throws SQLException {
		Statement stat = connection.createStatement();

		String query = "delete from " + this.getSlovickaTableName() + " where userID=" + userID + " and id=" + id;
		stat.executeUpdate(query);
		stat.close();

	}

	/*private void commitHibernate(){
	ManagedSessionContext.unbind(HibernateUtil.getSessionFactory());
	if (session.isOpen()){
	session.flush();
	session.getTransaction().commit();
	if (session.isOpen()){
	session.close();
	}
	}

	}*/
	private boolean disconnect() {

		try {
			//commitHibernate();
			if (connection != null) {
				ErrorHandler.debug("ConnectionToDB.java: disconnect: Databese connection " + connection + " closed");
				connection.close();
				connection = null;
				/*if (session.isOpen()) {
				session.close();
				}*/

			} else {
				ErrorHandler.debug("ConnectionToDB.java: disconnect: Database connection == null");
			}



		} catch (Exception e) {
			ErrorHandler.logError(e);
		}

		if (isExportEnabled()) {
			exportDB();
		}


		if (stopDatabaseOnExit == true) {
			ErrorHandler.debug("ConnectionToDB.java: disconnect: Stopping database");
			stopDatabase();
		}

		ErrorHandler.debug("ConnectionToDB.java: disconnect: Returning true");
		return true;
	}

	private void execProgram(String[] program, boolean wait) {
		Runtime rt = Runtime.getRuntime();
		Process p;

		try {
			p = rt.exec(program);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			while ((line = bri.readLine()) != null) {
				System.out.println(line);
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.out.println(line);
			}
			bre.close();
			try {
				if (wait == true) {
					p.waitFor();
				}
			} catch (InterruptedException ex) {
				Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (IOException ex) {
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private static boolean isExportEnabled() {
		boolean enabled = true;

		Properties props = new Properties();
		try {
			props.load(new FileInputStream("properties"));
			if (props.getProperty("export_db").equals("true")) {
				enabled = true;
			} else {
				enabled = false;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (enabled) {
			return true;
		} else {
			return false;
		}
	}

	private void exportDB() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String homedir = System.getProperty("user.dir");
		new File(homedir + "\\backup").mkdir();
		String export = homedir + "\\backup\\export_" + dateFormat.format(date) + ".sql";
		try {
			String[] program = new String[9];
			program[0] = System.getProperty("user.dir") + "\\mysql\\bin\\mysqldump";
			program[1] = "--user=strafelda";
			program[2] = "--port=" + xml.getDBport();
			program[3] = "slovicka";

			program[4] = "--password=" + this.password;
			program[5] = "--skip-extended-insert";
			program[6] = "--create-options";
			program[7] = "-r";
			program[8] = export;
			execProgram(program, true);

			compress(export, export + ".gzip");
			deleteFile(export);

		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}
	}

	private void deleteFile(String exp) {
		File f1 = new File(exp);
		f1.delete();
	}

	private void compress(String inFilename, String outFilename) {
		try {
			// Create the GZIP output stream
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(outFilename));

			// Open the input file
			FileInputStream in = new FileInputStream(inFilename);

			// Transfer bytes from the input file to the GZIP output stream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();

			// Complete the GZIP file
			out.finish();
			out.close();
		} catch (IOException e) {
		}
	}

	public ArrayList<String> getThemes() {
		ArrayList<String> themes = new ArrayList<String>();
		try {
			Statement stat = connection.createStatement();
			String language = direction.getForeignLanguage().toString();
			String query = "select theme from themes where user_id =" + userID + " and language=\"" + language + "\"";
			stat.execute(query);
			ResultSet rs = stat.getResultSet();
			while (true == rs.next()) {
				themes.add(rs.getString("theme"));
			}
			rs.close();
			stat.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.toString(), "Error", JOptionPane.OK_OPTION);
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}

		return themes;
	}

	public void addTheme(String theme) {

		try {
			Statement stat = connection.createStatement();
			String language = direction.getForeignLanguage().toString();
			String query = "insert into themes (theme, user_id, language) values (\"" + theme + "\", " + userID + ",\"" + language + "\")";
			stat.execute(query);
			stat.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.toString(), "Error", JOptionPane.OK_OPTION);
			e.toString();
			ErrorHandler.logError(e);
		}
	}

	public boolean addExample(Example example, boolean overwrite) throws UcitelException, Exception {

		Statement stat = connection.createStatement();
		String czech;
		String inLanguage;
		GUI gui = GUI.getInstance();
		String theme = gui.getTheme();


		czech = example.getCzech();
		inLanguage = example.getForeign();
		if ((czech.indexOf(";") != -1) || (inLanguage.indexOf(";") != -1)){
			throw new UcitelException("Slovíčko nesmí obsahovat ;");
		}

		String hint = example.getHint();
		example = null;

		String query = "select id,czech,lang from " + this.getSlovickaTableName() + " where userID=" + userID + " and lang = \"" + inLanguage + "\" and  czech = \"" + czech + "\" and language=\"" + direction.getForeignLanguage() + "\"";

		//existuje uz ve slovniku anglicka nebo ceska verze?
		stat.execute(query);
		ResultSet rs = stat.getResultSet();
		if (false == rs.next()) {
			String query2;
			int grammar;
			if (gui.getGrammar()) {
				grammar = 1;
			} else {
				grammar = 0;
			}
			if ((hint == null) || (hint.isEmpty())) {
				if (theme == null) {

					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID,"
						+ " language,"
						+ " czech,"
						+ " lang,"
						+ " weight,"
						+ " last_asked_to_czech,"
						+ " grammar,"
						+ " theme_id, "
						+ "last_asked) "
						+ "values ( "
						+ "\"" + userID + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "\"" + czech + "\", "
						+ "\"" + inLanguage + "\", "
						+ "1000, "
						+ "NOW(), "
						+ "0, "
						+ grammar + ","
						+ "CURRENT_TIMESTAMP)";
				} else {

					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID, "
						+ "language, "
						+ "czech, "
						+ "lang, "
						+ "weight, "
						+ "last_asked_to_czech, "
						+ "grammar, "
						+ "theme_id, "
						+ "last_asked"
						+ ") values "
						+ "( \"" + userID + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "\"" + czech + "\", "
						+ "\"" + inLanguage + "\", "
						+ "1000, "
						+ "NOW(), "
						+ "" + grammar + ","
						+ Integer.toString(this.getThemeID(theme)) + ", "
						+ "CURRENT_TIMESTAMP)";
				}

			} else {
				if (theme == null) {
					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID, "
						+ "language, "
						+ "czech, "
						+ "lang, "
						+ "weight, "
						+ "last_asked_to_czech, "
						+ "grammar, "
						+ "hint, "
						+ "theme_id, "
						+ "last_asked) values ( "
						+ "\"" + userID + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "\"" + czech + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "1000, "
						+ "NOW(),"
						+ grammar + ","
						+ "\"" + hint + "\", "
						+ "0, "
						+ "CURRENT_TIMESTAMP)";
				} else {
					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID, language, czech, lang, weight, last_asked_to_czech, grammar, hint, theme_id, last_asked) values ( \"" + userID + "\", \"" + direction.getForeignLanguage() + "\", \"" + czech + "\", \"" + direction.getForeignLanguage() + "\", 1000, NOW(),\"" + hint + "\", " + this.getThemeID(theme) + ", " + grammar + ",CURRENT_TIMESTAMP)";
				}

			}

			try {
				stat.execute("SET NAMES 'utf8'");
				stat.executeUpdate(query2);
				stat.close();

			} catch (SQLException e) {
				ErrorHandler.debug("ConnectionToDB::addExample() " + e.toString());
				return false;
			}
			rs.close();
			stat.close();
			return true;
		} else {
			if ((rs.getString(2).equalsIgnoreCase(czech)) && (rs.getString(3).equalsIgnoreCase(inLanguage))) {
				//slovicko je v databazi ulozeno presne tak, jak vypada pridavane slovicko
				if (overwrite == true){
					String query2 = "update " + this.getSlovickaTableName() + " set " + direction.getCAString() + "=0, " + direction.getLastAsked() + "=NOW(),weight=1000 where id=" + Integer.toString(rs.getInt(1));
					stat.executeUpdate(query2);
					stat.close();
					rs.close();
					ErrorHandler.debug("ConnectionToDB::addExample() query2: '" + query2 + "'");
					ErrorHandler.debug("ConnectionToDB::addExample() Slovicko uz je v databazi");
				}

				throw new UcitelException("Slovicko uz je v databazi");
			} else {
				if (rs.getString(2).equalsIgnoreCase(czech)) {
					//cesky vyraz existuje, cizi je jiny
					String query2 = "update " + this.getSlovickaTableName() + " set lang=\"" + inLanguage + Teacher.ANSWER_SEPARATOR + rs.getString(3) + "\"," + direction.getCAString() + "=0, " + direction.getLastAsked() + "=NOW(),weight=1000 where id=" + Integer.toString(rs.getInt(1));
					stat.executeUpdate(query2);
					rs.close();
					stat.close();
				} else {
					//cizi vyraz existuje, pridava se vyznam v cestine
					String query2 = "update " + this.getSlovickaTableName() + " set czech=\"" + czech + Teacher.ANSWER_SEPARATOR + rs.getString(2) + "\"," + direction.getCAString() + "=0, " + direction.getLastAsked() + "=NOW(),weight=1000 where id=" + Integer.toString(rs.getInt(1));
					stat.executeUpdate(query2);
					rs.close();
					stat.close();
				}
			}
		}
		rs.close();
		return true;

	}

	private void enableAutocommit() {
		try {
			Statement stat = connection.createStatement();
			stat.execute("set autocommit=1");
		} catch (SQLException ex) {
			ex.printStackTrace();
			System.out.println(ex.toString());
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void disableAutocommit(Statement stat) {
		try {
			stat.execute("START TRANSACTION");
		} catch (SQLException ex) {
			ex.printStackTrace();
			System.out.println(ex.toString());
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean addBatch(List<Example> examples) throws Exception {


		Statement stat = connection.createStatement();
		String czech;
		String inLanguage;
		GUI gui = GUI.getInstance();
		String theme = gui.getTheme();

		//disableAutocommit(stat);
		Iterator i = examples.iterator();
		while (i.hasNext()) {
			Example example = (Example) i.next();

			if (direction.isFromCzech()) {
				czech = example.getCzech();
				inLanguage = example.getForeign();
			} else {
				czech = example.getForeign();
				inLanguage = example.getCzech();
			}

			String hint = example.getHint();
			example = null;



			String query2;
			int grammar;
			if (gui.getGrammar()) {
				grammar = 1;
			} else {
				grammar = 0;
			}
			if ((hint == null) || (hint.isEmpty())) {
				if (theme == null) {

					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID,"
						+ " language,"
						+ " czech,"
						+ " lang,"
						+ " weight,"
						+ " last_asked_to_czech,"
						+ " grammar,"
						+ " theme_id, "
						+ "last_asked) "
						+ "values ( "
						+ "\"" + userID + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "\"" + czech + "\", "
						+ "\"" + inLanguage + "\", "
						+ "1000, "
						+ "NOW(), "
						+ "0, "
						+ grammar + ","
						+ "CURRENT_TIMESTAMP)";
				} else {

					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID, "
						+ "language, "
						+ "czech, "
						+ "lang, "
						+ "weight, "
						+ "last_asked_to_czech, "
						+ "grammar, "
						+ "theme_id, "
						+ "last_asked"
						+ ") values "
						+ "( \"" + userID + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "\"" + czech + "\", "
						+ "\"" + inLanguage + "\", "
						+ "1000, "
						+ "NOW(), "
						+ "" + grammar + ","
						+ Integer.toString(this.getThemeID(theme)) + ", "
						+ "CURRENT_TIMESTAMP)";
				}

			} else {
				if (theme == null) {
					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID, "
						+ "language, "
						+ "czech, "
						+ "lang, "
						+ "weight, "
						+ "last_asked_to_czech, "
						+ "grammar, "
						+ "hint, "
						+ "theme_id, "
						+ "last_asked) values ( "
						+ "\"" + userID + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "\"" + czech + "\", "
						+ "\"" + direction.getForeignLanguage() + "\", "
						+ "1000, "
						+ "NOW(),"
						+ grammar + ","
						+ "\"" + hint + "\", "
						+ "0, "
						+ "CURRENT_TIMESTAMP)";
				} else {
					query2 = "insert into " + this.getSlovickaTableName() + " "
						+ "(userID, language, czech, lang, weight, last_asked_to_czech, grammar, hint, theme_id, last_asked) values ( \"" + userID + "\", \"" + direction.getForeignLanguage() + "\", \"" + czech + "\", \"" + direction.getForeignLanguage() + "\", 1000, NOW(),\"" + hint + "\", " + this.getThemeID(theme) + ", " + grammar + ",CURRENT_TIMESTAMP)";
				}

			}

			try {
				stat.execute("SET NAMES 'utf8'");
				stat.executeUpdate(query2);


			} catch (SQLException e) {
				ErrorHandler.debug("ConnectionToDB::addExample() " + e.toString());
				return false;
			}
			//rs.close();




		}

		stat.close();
		return true;

	}

	public int getNumberOfDays(Language from, Language to) throws SQLException {

		if (connection == null) {
			return 1;
		}

		Statement stat = connection.createStatement();
		String query = "select to_days(curdate())-to_days(date) from slovicka_stats where userID=" + userID + " and lngFrom='" + from + "' and lngTo='" + to + "' order by date limit 1";

		stat.execute(query);
		ResultSet rs = stat.getResultSet();
		if (false != rs.next()) {
			return rs.getInt(1);
		}
		rs.close();
		stat.close();
		return 0;
	}

	public int getNumberOfDaysWithoutLearnedWords(String lngFrom, String lngTo) {

		try {
			Statement stat = connection.createStatement();
			String query = "select count(*) from slovicka.slovicka_stats where userID=" + userID + " and more=0 and lngFrom='" + lngFrom + "' and lngTo='" + lngTo + "'";
			stat.execute(query);
			ResultSet rs = stat.getResultSet();
			if (false != rs.next()) {
				int nodwlw = rs.getInt(1);
				rs.close();
				stat.close();
				return nodwlw;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}

		return 0;
	}

	public int getMaxLearnedWords() throws SQLException {
		if (connection == null) {
			return 1;
		}

		Statement stat = connection.createStatement();
		int max;

		try {
			String query = "select count(*) from " + this.getSlovickaTableName() + " where "
				+ "userID=" + userID
				+ " and language='" + direction.getLngTo() + "'"
				+ " and " + direction.getCAString() + ">=10";

			stat.execute(query);
			ResultSet rs = stat.getResultSet();
			if (false != rs.next()) {
				max = rs.getInt(1);
			} else {
				max = 0;
			}

			stat.execute("select max(more+ten) from slovicka.slovicka_stats where "
				+ "userID=" + userID
				+ " and lngFrom='" + direction.getLngFrom() + "'"
				+ " and lngTo='" + direction.getLngTo() + "'");

			rs = stat.getResultSet();

			if (false != rs.next()) {
				if (rs.getInt(1) >= max) {
					int mlw = rs.getInt(1);
					rs.close();
					stat.close();
					return mlw;
				} else {
					rs.close();
					stat.close();
					return max;
				}
			}

		} catch (SQLException sqle) {
			System.out.println(sqle.toString());
			ErrorHandler.logError(sqle);
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}

		return 0;
	}

	public int getNumberOfLearnedWords(int days) throws SQLException {

		if (days == 0) {
			this.saveStats(direction);
		}

		if (connection == null) {
			return 1;
		}

		Statement stat = connection.createStatement();
		try {
			//String query = "select count(*) from slovicka.slovicka where userID="+userID+" and "+DirectionOfTranslation.getInstance().getCAString()+">=10 and last_asked<adddate(curdate()," + Integer.toString(-days) +")";
			String query = "select ten+more from slovicka.slovicka_stats "
				+ "where userID=" + userID + " "
				+ "and lngFrom='" + direction.getLngFrom() + "' "
				+ "and lngTo='" + direction.getLngTo() + "'"
				+ "and to_days(date)=to_days(adddate(curdate()," + Integer.toString(-days) + "))";

			stat.execute(query);
			ResultSet rs = stat.getResultSet();
			if (false != rs.next()) {
				int nolw = rs.getInt(1);
				rs.close();
				stat.close();
				return nolw;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}

		return 0;
	}

	public boolean isPresent(String question, String answer) {

		String theme = GUI.getInstance().getTheme();
		int theme_id = this.getThemeID(theme);

		if (direction.isFromCzech()) {
			//otazkou je ceske slovicko
			//carky muzou, ale nemusi byt
			String query = "select lang from " + this.getSlovickaTableName() + " where theme_id=" + theme_id + " and userID=\"" + this.userID + "\" and language=\"" + direction.getForeignLanguage() + "\" and czech = \"" + question + "\" and lang=\"" + answer + "\"";
			try {
				Statement stat = connection.createStatement();
				stat.execute(query);
				ResultSet rs = stat.getResultSet();
				if (true == rs.next()) {
					rs.close();
					stat.close();
					return true;
				}
			} catch (SQLException e) {
				ErrorHandler.logError(e);
			}
		} else {
			String query = "select czech from " + this.getSlovickaTableName() + " where theme_id=" + theme_id + " and userID=\"" + this.userID + "\" and language=\"" + direction.getForeignLanguage() + "\" and lang =\"" + question + "\"";
			try {
				Statement stat = connection.createStatement();
				stat.execute(query);
				ResultSet rs = stat.getResultSet();
				if (true == rs.next()) {
					rs.close();
					stat.close();
					return true;
				}
			} catch (SQLException e) {
				System.out.print(e.toString());
				e.printStackTrace();
				ErrorHandler.logError(e);
			}
		}

		return false;
	}

	public String getAnswer(String question) {


		if (direction.isFromCzech()) {
			//otazkou je ceske slovicko
			//carky muzou, ale nemusi byt
			String query = "select lang from " + this.getSlovickaTableName() + " where czech like \"%" + question + "%\"";
			try {
				Statement stat = connection.createStatement();
				stat.execute(query);
				ResultSet rs = stat.getResultSet();
				if (true == rs.next()) {
					String answer = rs.getString("lang");
					rs.close();
					stat.close();
					return answer;
				}
			} catch (SQLException e) {
				ErrorHandler.logError(e);
			}
		} else {
			String query = "select czech from " + this.getSlovickaTableName() + " where lang like \"%" + question + "%\"";
			try {
				Statement stat = connection.createStatement();
				stat.execute(query);
				ResultSet rs = stat.getResultSet();
				if (true == rs.next()) {
					String answer = rs.getString("czech");
					rs.close();
					stat.close();
					return answer;
				}
			} catch (SQLException e) {
				ErrorHandler.logError(e);
			}
		}

		return null;
	}

	private int getThemeID(String theme) {
		int theme_id = 0;
		if (theme == null) {
			return theme_id;
		}

		try {
			Statement stat = connection.createStatement();
			String language = direction.getForeignLanguage().toString();
			String query = "select theme_id from themes where theme=\"" + theme + "\" and language=\"" + language + "\"";
			stat.executeQuery(query);
			ResultSet rs = stat.getResultSet();
			if (rs.next()) {
				theme_id = rs.getInt("theme_id");
				rs.close();
				stat.close();
				return theme_id;
			}

		} catch (SQLException e) {

			e.toString();
			JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.toString(), "Error", JOptionPane.OK_OPTION);
			//e.printStackTrace();
			ErrorHandler.logError(e);
		}

		return theme_id;
	}

	public int getNumberOfWordsToAskToday() {
		ErrorHandler.debug("GUI::getNumberOfWordsToAskToday(): entered");
		String theme = GUI.getInstance().getTheme();
		int theme_id = getThemeID(theme);

		try {
			Statement stat = connection.createStatement();
			String getQuestionQuery;
			int grammar;
			if (GUI.getInstance().getGrammar()) {
				grammar = 1;
			} else {
				grammar = 0;
			}

			if (theme == null) {

				if (grammar == 1) {
					getQuestionQuery = "select "
						+ "id,"
						+ "lang,"
						+ "czech,"
						+ "language, "
						+ direction.getLastAsked() + ", "
						+ "hint "
						+ "from " + this.getSlovickaTableName() + " "
						+ "where grammar=1 and userID=" + userID + " and language=\"" + direction.getForeignLanguage() + "\" "
						+ "and ( "
						+ direction.getLastAsked() + " < CURDATE() "
						+ "and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
						+ "or "
						+ "(userID=" + userID + " "
						+ "and CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-5," + frequencyOfAsking + ")) DAY) "
						+ "and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
						+ "and language=\"" + direction.getForeignLanguage() + "\") "
						+ "or (" + direction.getCAString() + "=0 ))"
						+ "order by " + direction.getCAString() + ",weight desc ";
				} else {
					getQuestionQuery = "select "
						+ "id,"
						+ "lang,"
						+ "czech,"
						+ "language, "
						+ direction.getLastAsked() + ", "
						+ "hint "
						+ "from " + this.getSlovickaTableName() + " "
						+ "where grammar=0 "
						+ "and userID=" + userID + " "
						+ "and language=\"" + direction.getForeignLanguage() + "\" "
						+ "and ( "
						+ direction.getLastAsked() + " < CURDATE() "
						+ "and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
						+ "or "
						+ "( "
						+ " CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-5," + frequencyOfAsking + ")) DAY) "
						+ " and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
						+ ") "
						+ "or (" + direction.getCAString() + "=0 ))"
						+ "order by " + direction.getCAString() + ",weight desc ";
				}

			} else {

				if (grammar == 1) {
					getQuestionQuery = "select "
						+ "id,"
						+ "lang,"
						+ "czech,"
						+ "language,"
						+ direction.getLastAsked() + ", "
						+ "hint "
						+ "from " + this.getSlovickaTableName() + " "
						+ "where grammar=1 and theme_id =\"" + theme_id + "\" and userID=" + userID + " and language=\"" + direction.getForeignLanguage() + "\" "
						+ "and ( "
						+ direction.getLastAsked() + " < CURDATE() "
						+ "and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
						+ "or "
						+ "(userID=" + userID + " "
						+ "and CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(this.askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
						+ "and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
						+ "and language=\"" + direction.getForeignLanguage() + "\") "
						+ "or (" + direction.getCAString() + "=0 ))"
						+ "order by " + direction.getCAString() + ",weight desc ";
				} else {
					getQuestionQuery = "select "
						+ "id,"
						+ "lang,"
						+ "czech,"
						+ "language,"
						+ direction.getLastAsked() + ", "
						+ "hint "
						+ "from " + this.getSlovickaTableName() + " "
						+ "where grammar=0 and theme_id =\"" + theme_id + "\" and userID=" + userID + " and language=\"" + direction.getForeignLanguage() + "\" "
						+ "and ( "
						+ direction.getLastAsked() + " < CURDATE() "
						+ "and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
						+ "or "
						+ "(userID=" + userID + " "
						+ "and CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(this.askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
						+ "and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
						+ "and language=\"" + direction.getForeignLanguage() + "\") "
						+ "or (" + direction.getCAString() + "=0 ))"
						+ "order by " + direction.getCAString() + ",weight desc ";
				}

			}

			String query = "select "
				+ "count(*) "
				+ "from (" + getQuestionQuery + ") as t1";

			stat.executeQuery(query);

			ResultSet rs = stat.getResultSet();
			if (true == rs.next()) {
				int nowtat = rs.getInt(1);
				rs.close();
				stat.close();
				ErrorHandler.debug("GUI::getNumberOfWordsToAskToday(): returning");
				return nowtat + getWronglyAnsweredCnt();
			}
		} catch (Exception e) {
			ErrorHandler.logError(e);
			e.printStackTrace();
			System.out.print(e.toString());
		}

		ErrorHandler.debug("GUI::getNumberOfWordsToAskToday(): returning");
		return 1;
	}

	public int getAnswerCount(int id) {

		try {
			Statement stat = connection.createStatement();
			stat.execute("select " + direction.getCAString() + " from " + this.getSlovickaTableName() + " where userID=" + userID + " and id=\"" + id + "\"");
			ResultSet rs = stat.getResultSet();
			if (false != rs.next()) {
				int ic = rs.getInt(direction.getCAString());
				rs.close();
				stat.close();
				return ic;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}
		return 0;
	}

	public int getAnswerCount(String english) throws SQLException {
		try {
			Statement stat = connection.createStatement();
			stat.execute("select " + direction.getCAString() + " from " + this.getSlovickaTableName() + " where userID=" + userID + " and lang=\"" + english + "\"");
			ResultSet rs = stat.getResultSet();

			if (false != rs.next()) {
				int ac = rs.getInt(direction.getCAString());
				rs.close();
				stat.close();
				return ac;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}

		return 0;
	}

	public static synchronized ConnectionToDB getInstance(LoginInfo li) throws SQLException, Exception {
		ErrorHandler.debug("ConnectionToDB.java: getInstance(): calling constructor");
		if (uniqueInstance == null) {
			if (li == null) {
				uniqueInstance = new ConnectionToDB();
			} else {
				uniqueInstance = new ConnectionToDB(li);
			}

		}

		ErrorHandler.debug("ConnectionToDB.java: getInstance(): returning uniqueInstance:" + uniqueInstance);
		return uniqueInstance;
	}

	public static boolean isConnected() {
		return isConnected;
	}

	public static synchronized ConnectionToDB getInstance() {
		ErrorHandler.debug("ConnectionToDB::getInstance(): entered");

		if (null == uniqueInstance) {
			try {
				uniqueInstance = new ConnectionToDB();
			} catch (SQLException ex) {
				Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
			} catch (Exception ex) {
				Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
			}
			//JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), "Error", "Database connection error", JOptionPane.OK_OPTION);
			ErrorHandler.debug("ConnectionToDB::getInstance(): uniqueInstance == null => exitting...");
		}
		ErrorHandler.debug("ConnectionToDB::getInstance() finished");
		return uniqueInstance;
	}

	public static void close() {
		ErrorHandler.debug("ConnectionToDB::close() entered");

		try {
			if (uniqueInstance != null) {

				//ConnectionToDB con = ConnectionToDB.getInstance();
				uniqueInstance.disconnect();
				if (uniqueInstance.getStopDatabaseOnExit()) {
					uniqueInstance.stopDatabase();
				}

				ErrorHandler.debug("ConnectionToDB::close(): calling disconnect");

			}
		} catch (Exception e) {
			ErrorHandler.logError(e);
		}

	}

	private boolean getStopDatabaseOnExit() {
		if (stopDatabaseOnExit == true) {
			ErrorHandler.debug("ConnectionToDB::getStopDatabaseOnExit(): returns true");
		} else {
			ErrorHandler.debug("ConnectionToDB::getStopDatabaseOnExit(): returns false");
		}
		return stopDatabaseOnExit;
	}

	public int getNumberOfCorrectAnswers(int id) throws SQLException {
		try {
			Statement stat = connection.createStatement();
			//stat.executeQuery("use slovicka");
			String query = "select " + direction.getCAString() + " from " + this.getSlovickaTableName() + " where userID=" + userID + " and id=" + Integer.toString(id);
			stat.executeQuery(query);

			ResultSet rs = stat.getResultSet();
			if (true == rs.next()) {
				int noca = rs.getInt(1);
				rs.close();
				stat.close();
				return noca;
			} else {
				throw new SQLException();
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
			return 0;
		}

	}

	private int getNCA(int id) {
		int count = 0;
		try {
			Session session = getSession();

			Criteria criteria = session.createCriteria(Slovicka.class);
			criteria.add(Restrictions.eq("userId", userID));

			//String SQL_QUERY = "from Slovicka";
			String SQL_QUERY = "from Slovicka where id=" + id;

			Query query = session.createQuery(SQL_QUERY);

			for (Iterator it = query.iterate(); it.hasNext();) {
				Slovicka sl = (Slovicka) it.next();
				if (direction.isFromCzech()) {
					count = sl.getCaFromCzech();
				} else {
					count = sl.getCorrectlyAnswered();
				}
			}


		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return count;
	}

	public void decreaseWeight(int id) throws SQLException {
		try {
			Statement stat = connection.createStatement();

			String query = "update " + this.getSlovickaTableName() + " set "
				+ direction.getLastAsked() + "=NOW(),"
				+ " weight=weight-2,"
				+ " " + direction.getCAString() + "=" + direction.getCAString() + "+1"
				+ " where id=" + Integer.toString(id);
			stat.executeUpdate(query);
			stat.close();
		} catch (Exception e) {
			ErrorHandler.logError(e);
		}

	}

	public void resetOneAnsweredDate() throws SQLException {
		try {
			Statement stat = connection.createStatement();
			stat.executeUpdate("update " + this.getSlovickaTableName() + " set " + direction.getLastAsked() + "=TIMESTAMP('2000-01-01') where userID=" + userID + " and " + direction.getCAString() + "=1 and language='" + direction.getForeignLanguage() + "'");
			stat.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}
	}

	private String getIsAvailableQuery() throws SQLException, Exception {

		String query;
		GUI gui = GUI.getInstance();
		String theme = gui.getTheme();

		String s_grammar = "";
		if (!gui.getGrammar()) {
			s_grammar = " grammar=0 and";
		}

		if (theme == null) {
			query = "select "
				+ "1 "
				/*	+ "id,"
				+ "lang,"
				+ "czech,"
				+ "language,"
				+ direction.getLastAsked() + ", "
				+ "hint "*/
				+ "from " + this.getSlovickaTableName() + " "
				+ "where " + s_grammar + " userID=" + userID + " and language=\"" + direction.getForeignLanguage() + "\""
				+ " and (( "
				+ direction.getLastAsked() + " < CURDATE() "
				+ " and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + ") "
				+ "or "
				+ "(CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(this.askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
				+ " and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
				+ ")) "
				+ "order by " + direction.getCAString() + ",weight desc limit 1";
		} else {
			String themeQuery = null;

			if (this.getThemeID(theme) > 0) {
				themeQuery = " and theme_id = " + this.getThemeID(theme) + " ";
			} else {
				themeQuery = "";
			}

			query = "select "
				+ "id,"
				+ "lang,"
				+ "czech,"
				+ "language,"
				+ direction.getLastAsked() + ", "
				+ "hint "
				+ "from " + this.getSlovickaTableName() + " "
				+ "where "
				+ " userID=" + userID + " " + themeQuery + " and "
				+ "(( " + direction.getLastAsked() + " < CURDATE() "
				+ "and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
				+ "and language=\"" + direction.getForeignLanguage() + "\") "
				+ "or "
				+ "( CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(this.askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
				+ "and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
				+ "and language=\"" + direction.getForeignLanguage() + "\")) "
				+ "order by " + direction.getCAString() + ",weight desc ";
		}



		return query;
		//"order by "+direction.getCAString()+",weight desc limit " + Integer.toString(lengthOflist);
	}

	//vraci pocet slovicek na ktere se ma ucitel zeptat
	public int toAskCnt() throws Exception {
		ErrorHandler.debug("ConnectionToDB::toAskCnt(): entered");
		Statement stat = connection.createStatement();
		GUI gui = GUI.getInstance();
		String theme = gui.getTheme();
		String where;


		if (theme == null) {
			where = "where userID=" + userID + " and language=\"" + direction.getForeignLanguage() + "\""
				+ " and grammar=" + gui.getGrammar() + " and ( ( " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
				+ ") "
				+ "or "
				+ "(CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(this.askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
				+ "and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
				+ ") "
				+ "or ( "
				+ direction.getLastAsked() + ">CURDATE())) ";
		} else {
			int theme_id = this.getThemeID(theme);
			where = "where theme_id=" + Integer.toString(theme_id) + " and grammar=" + gui.getGrammar() + " and "
				+ "((userID=" + userID + " "
				+ "and " + direction.getCAString() + "<" + Integer.toString(this.askInRowCnt) + " "
				+ "and language=\"" + direction.getForeignLanguage() + "\") "
				+ "or "
				+ "(userID=" + userID + " "
				+ "and CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(this.askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
				+ "and " + direction.getCAString() + ">=" + Integer.toString(this.askInRowCnt) + " "
				+ "and language=\"" + direction.getForeignLanguage() + "\") "
				+ "or (userID=" + userID + " and "
				+ "language=\"" + direction.getForeignLanguage() + "\""
				+ " and " + direction.getLastAsked() + ">CURDATE())) ";
		}

		String query = "select "
			+ "count(1) "
			+ "from (select "
			+ "id,"
			+ "lang,"
			+ "czech,"
			+ "language,"
			+ direction.getLastAsked() + ", "
			+ "hint from " + this.getSlovickaTableName() + " "
			+ where
			+ "order by " + direction.getCAString() + ",weight desc ) as t1";


		stat.executeQuery(query);
		ResultSet rs = stat.getResultSet();
		if (true == rs.next()) {
			int to_ask = rs.getInt(1);
			rs.close();
			stat.close();
			ErrorHandler.debug("ConnectionToDB::toAskCnt(): returning");
			return to_ask;
		} else {
			throw new Exception();
		}

	}

	public int getNotAskedToday() throws Exception {
		Statement stat = connection.createStatement();

		String query = "select count(*) from " + this.getSlovickaTableName() + " where userID=" + userID + ""
			+ " and " + direction.getLastAsked() + " < CURDATE()"
			+ " and language='" + direction.getForeignLanguage() + "'"
			+ " and " + direction.getCAString() + "<10";

		stat.executeQuery(query);
		ResultSet rs = stat.getResultSet();
		if (true == rs.next()) {
			int nat = rs.getInt(1);
			rs.close();
			stat.close();
			return nat;
		} else {
			throw new Exception();
		}

	}

	public void saveAllStats() {
		if (connection != null) {
			saveStats(new DirectionOfTranslation(Language.CZECH, Language.ENGLISH));
			saveStats(new DirectionOfTranslation(Language.ENGLISH, Language.CZECH));
			saveStats(new DirectionOfTranslation(Language.CZECH, Language.FRENCH));
			saveStats(new DirectionOfTranslation(Language.FRENCH, Language.CZECH));
			saveStats(new DirectionOfTranslation(Language.CZECH, Language.GERMAN));
			saveStats(new DirectionOfTranslation(Language.GERMAN, Language.CZECH));
			saveStats(new DirectionOfTranslation(Language.CZECH, Language.GERMAN));

			saveStats(new DirectionOfTranslation(Language.JAPANESE, Language.CZECH));
			saveStats(new DirectionOfTranslation(Language.CZECH, Language.JAPANESE));

			saveStats(new DirectionOfTranslation(Language.FINNISH, Language.CZECH));
			saveStats(new DirectionOfTranslation(Language.CZECH, Language.FINNISH));
		}

	}

	public void saveStats(DirectionOfTranslation direction) {

		try {
			String que;
			Statement stat = connection.createStatement();
			if (direction.isFromCzech()) {

				que = "select zero from slovicka_stats "
					+ "where userID=" + userID + ""
					+ " and date>CURDATE()"
					+ " and lngFrom=\"" + direction.getLngFrom() + "\""
					+ " and lngTo=\"" + direction.getLngTo() + "\"";
			} else {
				que = "select zero from slovicka_stats "
					+ "where userID=" + userID + ""
					+ " and date>CURDATE()"
					+ " and lngFrom=\"" + direction.getLngFrom() + "\""
					+ " and lngTo=\"" + direction.getLngTo() + "\"";
			}

			stat.executeQuery(que);
			ResultSet rs = stat.getResultSet();
			if (true == rs.next()) {
				String query = "update slovicka_stats set "
					+ "zero=" + Integer.toString(getWordCnt(0, direction)) + ","
					+ "one=" + Integer.toString(getWordCnt(1, direction)) + ","
					+ "two=" + Integer.toString(getWordCnt(2, direction)) + ","
					+ "three=" + Integer.toString(getWordCnt(3, direction)) + ","
					+ "four=" + Integer.toString(getWordCnt(4, direction)) + ","
					+ "five=" + Integer.toString(getWordCnt(5, direction)) + ","
					+ "six=" + Integer.toString(getWordCnt(6, direction)) + ","
					+ "seven=" + Integer.toString(getWordCnt(7, direction)) + ","
					+ "eight=" + Integer.toString(getWordCnt(8, direction)) + ","
					+ "nine=" + Integer.toString(getWordCnt(9, direction)) + ","
					+ "ten=" + Integer.toString(getWordCnt(10, direction)) + ","
					+ "more=" + Integer.toString(getLearnedWordCnt(direction)) + ", "
					+ "date=NOW() where date>CURDATE() and "
					+ "userID=" + userID + " and "
					+ "lngFrom='" + direction.getLngFrom() + "' and "
					+ "lngTo='" + direction.getLngTo() + "'";

				stat.executeUpdate(query);
				rs.close();
				stat.close();
			} else {
				String query;
				if (direction.isFromCzech()) {
					query = "insert into slovicka_stats ("
						+ "userID,"
						+ "zero,"
						+ "one,"
						+ "two,"
						+ "three,"
						+ "four,"
						+ "five,"
						+ "six,"
						+ "seven,"
						+ "eight,"
						+ "nine,"
						+ "ten,"
						+ "more,"
						+ "date,"
						+ "lngFrom,"
						+ "lngTo) values "
						+ "(" + userID + ","
						+ Integer.toString(getWordCnt(0, direction)) + ","
						+ Integer.toString(getWordCnt(1, direction)) + ","
						+ Integer.toString(getWordCnt(2, direction)) + ","
						+ Integer.toString(getWordCnt(3, direction)) + ","
						+ Integer.toString(getWordCnt(4, direction)) + ","
						+ Integer.toString(getWordCnt(5, direction)) + ","
						+ Integer.toString(getWordCnt(6, direction)) + ","
						+ Integer.toString(getWordCnt(7, direction)) + ","
						+ Integer.toString(getWordCnt(8, direction)) + ","
						+ Integer.toString(getWordCnt(9, direction)) + ","
						+ Integer.toString(getWordCnt(10, direction)) + ","
						+ Integer.toString(getLearnedWordCnt(direction)) + ","
						+ "NOW(), '" + direction.getLngFrom() + "', '" + direction.getLngTo() + "')";
				} else {
					query = "insert into slovicka_stats (userID,zero,one,two,three,four,five,six,seven,eight,nine,ten,more,date,lngFrom,lngTo) values "
						+ "(" + userID + ","
						+ Integer.toString(getWordCnt(0, direction)) + ","
						+ Integer.toString(getWordCnt(1, direction)) + ","
						+ Integer.toString(getWordCnt(2, direction)) + ","
						+ Integer.toString(getWordCnt(3, direction)) + ","
						+ Integer.toString(getWordCnt(4, direction)) + ","
						+ Integer.toString(getWordCnt(5, direction)) + ","
						+ Integer.toString(getWordCnt(6, direction)) + ","
						+ Integer.toString(getWordCnt(7, direction)) + ","
						+ Integer.toString(getWordCnt(8, direction)) + ","
						+ Integer.toString(getWordCnt(9, direction)) + ","
						+ Integer.toString(getWordCnt(10, direction)) + ","
						+ Integer.toString(getLearnedWordCnt(direction)) + ","
						+ "NOW(), '" + direction.getLngFrom() + "', '" + direction.getLngTo() + "')";
				}
				rs.close();
				stat.executeUpdate(query);
				stat.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErrorHandler.logError(e);
		}
	}

	public boolean themeExists(String theme) {
		boolean ret = false;

		try {
			Statement stat = connection.createStatement();
			String language = direction.getForeignLanguage().toString();
			String query = "select theme_id from themes where theme = \"" + theme + "\" and language=\"" + language + "\"";

			stat.execute(query);
			ResultSet rs = stat.getResultSet();
			if (rs.next() == true) {

				ret = true;
			} else {
				ret = false;
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}

		return ret;
	}

	public void addUser(LoginInfo li) throws Exception {

		Statement stat = connection.createStatement();

		stat.execute("show tables like 'slovicka_stats'");
		ResultSet rs3 = stat.getResultSet();
		if (rs3.next() == false) {
			String query = "create table slovicka_stats (zero smallint(6), one smallint(6),two smallint(6),three smallint(6),four smallint(6),five smallint(6),six smallint(6),seven smallint(6),eight smallint(6),nine smallint(6),ten smallint(6),date timestamp default current_timestamp,userID integer)";
			stat.execute(query);
			rs3.close();
			stat.close();
		}

		stat.execute("show tables like 'users'");
		ResultSet rs2 = stat.getResultSet();
		if (true == rs2.next()) {
			String query = "select username from users where username='" + li.getLogin() + "'";
			stat.execute(query);
			ResultSet rs = stat.getResultSet();
			if (false == rs.next()) {
				String query2 = "insert into users (username,password) values ( '" + li.getLogin() + "','" + li.getPassword() + "')";
				try {
					stat.executeUpdate(query2);
					System.out.println("User added");
				} catch (SQLException e) {
					System.out.println(e.toString());
				}
			} else {
				throw new Exception("User exists");
			}

			rs2.close();

		} else {
			//create table users
			System.out.println("Creating table users...");
			String query = "create table users (userID int(11) not null auto_increment, username varchar(45) character set utf8 collate utf8_czech_ci not null, primary key(userID))";
			stat.execute(query);
			addUser(li);
		}

		String query = "CREATE TABLE `" + this.getUserID(li.getConName()) + "_slovicka` ("
			+ "id int(10) unsigned NOT NULL auto_increment,"
			+ "czech varchar(128) NOT NULL,"
			+ "lang varchar(128) default NULL,"
			+ "weight int(11) default NULL,"
			+ "last_asked timestamp NOT NULL default \"0000-00-00 00:00:00\","
			+ "correctly_answered int(11) default \"0\","
			+ "language varchar(45) NOT NULL,"
			+ "userID int(10) unsigned default \"0\","
			+ "CA_from_czech int(10) unsigned NOT NULL default \"0\","
			+ "hint varchar(255) default NULL,"
			+ "theme_id int(10) unsigned default NULL,"
			+ "last_asked_to_czech timestamp NOT NULL default \"0000-00-00 00:00:00\","
			+ "grammar smallint(5) unsigned default NULL,"
			+ "PRIMARY KEY  (id))";
		stat.execute(query);
		stat.close();
	}

	public void updateLastAsked(){
		String query1 = "update " + this.getSlovickaTableName()
			+ " set last_asked = '1990-01-01 00:00:00'"
			+ " where"
			+ " last_asked = '2038-01-01 00:00:00'";

		String query2 = "update " + this.getSlovickaTableName()
			+ " set last_asked_to_czech = '1990-01-01 00:00:00'"
			+ " where"
			+ " last_asked_to_czech = '2038-01-01 00:00:00'";

		String query3 = "update " + this.getSlovickaTableName()
			+ " set last_asked = '1990-01-01 00:00:01'"
			+ " where"
			+ " last_asked = '1990-01-01 00:00:00'";

		String query4 = "update " + this.getSlovickaTableName()
			+ " set last_asked_to_czech = '1990-01-01 00:00:01'"
			+ " where"
			+ " last_asked_to_czech = '1990-01-01 00:00:00'";
		try {
			Statement stat = connection.createStatement();
			stat.executeUpdate(query1);
			stat.executeUpdate(query2);
			stat.executeUpdate(query3);
			stat.executeUpdate(query4);
			stat.close();
		} catch (SQLException ex) {
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void increaseWeight(int id) throws SQLException {
		try {
			Statement stat = connection.createStatement();
			String query;

			int noca = getNumberOfCorrectAnswers(id);
			int answerCntRequired = XML.getInstance().getAnswerCnt();

			if (noca >= answerCntRequired) {
			
				query = "update " + this.getSlovickaTableName() + ""
					+ " set "
					+ direction.getLastAsked() + "='2038-01-01 00:00:00',"
					+ " weight=weight+3, " + direction.getCAString() + "=" + Integer.toString(noca - 1) +
					" where id=" + Integer.toString(id);
			} else {
				query = "update " + this.getSlovickaTableName() + " set " + direction.getLastAsked() + "=NOW(), weight=weight+3, " + direction.getCAString() + "=0 where id=" + Integer.toString(id);
			}

			stat.executeUpdate(query);
			stat.close();
		} catch (Exception e) {
			ErrorHandler.logError(e);
		}

	}

	public boolean isQuestionAvailable() {
		ErrorHandler.debug("ConnectionToDB::isQuestionAvailable(): entered");
		try {
			Statement stat = connection.createStatement();
			String query = this.getIsAvailableQuery();
			ErrorHandler.debug("ConnectionToDB::isQuestionAvailable(): executing query");
			stat.executeQuery(query);
			ErrorHandler.debug("ConnectionToDB::isQuestionAvailable(): query executed");
			ResultSet rs = stat.getResultSet();

			if (true == rs.next()) {
				if (rs.getInt(1) > 0) {
					rs.close();
					stat.close();
					ErrorHandler.debug("ConnectionToDB::isQuestionAvailable(): returning");
					return true;
				} else {
					rs.close();
					stat.close();
					return false;
				}


			} else {
				String theme = GUI.getInstance().getTheme();
				String query2;

				if (theme == null) {
					if (!GUI.getInstance().getGrammar()) {
						query2 = "select id,lang,czech from (select * from " + this.getSlovickaTableName() + " where grammar=0 and userID=" + userID + " and " + direction.getCAString() + " = 0 and language='" + direction.getForeignLanguage() + "' order by weight desc limit 1) as t1 order by " + direction.getLastAsked();
					} else {
						query2 = "select id,lang,czech from (select * from " + this.getSlovickaTableName() + " where grammar=1 and userID=" + userID + " and " + direction.getCAString() + " = 0 and language='" + direction.getForeignLanguage() + "' order by weight desc limit 1) as t1 order by " + direction.getLastAsked();
					}

				} else {
					if (!GUI.getInstance().getGrammar()) {
						query2 = "select id,lang,czech from (select * from " + this.getSlovickaTableName() + " where grammar=0 and userID=" + userID + " and " + direction.getCAString() + " = 0 and theme_id=" + this.getThemeID(theme) + " and language='" + direction.getForeignLanguage() + "' order by weight desc limit 1) as t1 order by " + direction.getLastAsked();
					} else {
						query2 = "select id,lang,czech from (select * from " + this.getSlovickaTableName() + " where grammar=1 and userID=" + userID + " and " + direction.getCAString() + " = 0 and theme_id=" + this.getThemeID(theme) + " and language='" + direction.getForeignLanguage() + "' order by weight desc limit 1) as t1 order by " + direction.getLastAsked();
					}

				}

				stat.executeQuery(query2);
				ResultSet rs2 = stat.getResultSet();
				if (true == rs2.next()) {
					rs2.close();
					stat.close();
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
			try {
				reconnect();
			} catch (Exception ex) {
				ErrorHandler.debug("ConnectionToDB::isQuestionAvailable(): reconnect failed");
			}
			ErrorHandler.debug("ConnectionToDB::isQuestionAvailable(): entered");
		}

		return false;

	}

	public Example getQuestion(int previousId) throws SQLException {
		ErrorHandler.debug("ConnectionToDB::getQuestion(): entered");
		Example example = null;
		String theme = GUI.getInstance().getTheme();

		try {
			Statement stat = connection.createStatement();
			String query;
			ResultSet rs;
			String where;

			if ((!direction.isAllZeroAsked()) && (isZeroQuestionAvailable())) {

				if (theme == null) {
					if (!GUI.getInstance().getGrammar()) {
						where = "where "
							+ "( grammar=0 and userID=" + userID + " "
							+ "and " + direction.getCAString() + "=0 "
							+ "and language=\"" + direction.getForeignLanguage() + "\") ";
					} else {
						where = "where "
							+ "( grammar=1 and userID=" + userID + " "
							+ "and " + direction.getCAString() + "=0 "
							+ "and language=\"" + direction.getForeignLanguage() + "\") ";
					}

				} else {
					if (!GUI.getInstance().getGrammar()) {
						where = "where "
							+ "( grammar=0 and userID=" + userID + " "
							+ "and " + direction.getCAString() + "=0 "
							+ "and language=\"" + direction.getForeignLanguage() + "\" "
							+ "and theme_id=\"" + this.getThemeID(theme) + "\") ";
					} else {
						where = "where "
							+ "( grammar=1 and userID=" + userID + " "
							+ "and " + direction.getCAString() + "=0 "
							+ "and language=\"" + direction.getForeignLanguage() + "\" "
							+ "and theme_id=\"" + this.getThemeID(theme) + "\") ";
					}

				}



				query = "select "
					+ "id,"
					+ "lang,"
					+ "czech,"
					+ "hint,"
					+ "language,"
					+ direction.getLastAsked() + ","
					+ "grammar"
					+ " from (select "
					+ "id,"
					+ "lang,"
					+ "czech,"
					+ "hint,"
					+ "language,"
					+ direction.getLastAsked() + ","
					+ "grammar"
					+ " from " + this.getSlovickaTableName() + " "
					+ where
					+ "order by " + direction.getLastAsked() + " desc limit " + Integer.toString(this.askInRowCnt) + ") as t1";
				stat.executeQuery(query);
				rs = stat.getResultSet();

				if (true == rs.last()) {
					String hint = rs.getString("hint");
					if (direction.isFromCzech()) {

						if ((hint == null) || (hint.isEmpty())) {
							Example ex = new Example(rs.getInt("id"), rs.getString("lang"), rs.getString("czech"), rs.getString("hint"), rs.getInt("grammar"));
							rs.close();
							stat.close();
							ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
							return ex;
						} else {
							//return example = new Example(rs.getInt(1), rs.getString(2),rs.getString(3), hint, rs.getString(4));
							Example ex = new Example(rs.getInt("id"), rs.getString("lang"), rs.getString("czech"), hint, rs.getInt("grammar"));
							rs.close();
							stat.close();
							ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
							return example = ex;
						}

					} else {
						if ((hint == null) || (hint.isEmpty())) {
							Example ex = new Example(rs.getInt("id"), rs.getString("czech"), rs.getString("lang"), null, rs.getInt("grammar"));
							rs.close();
							stat.close();
							ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
							return ex;
						} else {
							//return example = new Example(rs.getInt(1), rs.getString(3), rs.getString(2), hint, rs.getString(4));
							Example ex = new Example(rs.getInt("id"), rs.getString("czech"), rs.getString("lang"), hint, rs.getInt("grammar"));
							rs.close();
							stat.close();
							ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
							return ex;
						}

					}
				}

			} else {
				direction.allZeroAsked();

				//get results
				String foreignLanguage = direction.getForeignLanguage().toString();

				if (theme == null) {
					String gram;
					if (GUI.getInstance().getGrammar()) {
						gram = " grammar=1 and";
					} else {
						gram = " grammar=0 and ";
					}
					where = "where" + gram
						+ " userID=" + userID
						+ " and language=\"" + foreignLanguage + "\""
						+ " and (("
						+ " " + direction.getLastAsked() + " < CURDATE() "
						+ "and " + direction.getCAString() + "<" + Integer.toString(askInRowCnt) + " "
						+ ") "
						+ "or "
						+ "( "
						+ "CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
						+ "and " + direction.getCAString() + ">=" + Integer.toString(askInRowCnt) + " "
						+ ")) ";
				} else {
					where = "where "
						+ "((userID=" + userID + ""
						+ " and language=\"" + foreignLanguage + "\")"
						+ " and (("
						+ " " + direction.getLastAsked() + " < CURDATE() "
						+ "and " + direction.getCAString() + "<" + Integer.toString(askInRowCnt) + " ) "
						+ " or "
						+ " (CURDATE()>(" + direction.getLastAsked() + " + INTERVAL (pow(" + direction.getCAString() + "-" + Integer.toString(this.askInRowCnt) + "," + frequencyOfAsking + ")) DAY) "
						+ " and " + direction.getCAString() + ">=" + Integer.toString(askInRowCnt) + " "
						+ " ) ) and theme_id=\"" + this.getThemeID(theme) + "\")";
				}

				String getQuestionQuery = "select "
					+ "id,"
					+ "lang,"
					+ "czech,"
					+ "language,"
					+ direction.getLastAsked() + ", "
					+ "hint,"
					+ " grammar "
					+ "from " + this.getSlovickaTableName() + " " + where
					+ " order by " + direction.getCAString() + ",weight desc limit " + Integer.toString(this.askInRowCnt);

				query = "select "
					+ "id,"
					+ "lang,"
					+ "czech,"
					+ "language,"
					+ direction.getLastAsked() + ", "
					+ "hint, grammar "
					+ "from (" + getQuestionQuery + ") as t1";


				ErrorHandler.debug(query);
				stat.executeQuery(query);
				rs = stat.getResultSet();
			}

			if (true == rs.next()) {
				String hint = rs.getString("hint");
				if (direction.isFromCzech()) {

					if ((hint == null) || (hint.isEmpty())) {
						Example ex = new Example(rs.getInt("id"), rs.getString("lang"), rs.getString("czech"), rs.getString("hint"), rs.getInt("grammar"));
						rs.close();
						stat.close();
						ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
						return ex;
					} else {
						//return example = new Example(rs.getInt(1), rs.getString(2),rs.getString(3), hint, rs.getString(4));
						Example ex = new Example(rs.getInt("id"), rs.getString(2), rs.getString(3), hint, rs.getInt("grammar"));
						rs.close();
						stat.close();
						ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
						return ex;
					}

				} else {
					if ((hint == null) || (hint.isEmpty())) {
						Example ex = new Example(rs.getInt("id"), rs.getString("czech"), rs.getString("lang"), null, rs.getInt("grammar"));
						rs.close();
						stat.close();
						ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
						return ex;
					} else {
						//return example = new Example(rs.getInt(1), rs.getString(3), rs.getString(2), hint, rs.getString(4));
						Example ex = new Example(rs.getInt("id"), rs.getString("czech"), rs.getString("lang"), hint, rs.getInt("grammar"));
						rs.close();
						stat.close();
						ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
						return ex;
					}

				}
			} else {

				direction.resetAllZeroAsked();


				ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
				return getQuestion(previousId);
			}

		} catch (SQLException e) {

			try {
				System.out.print(e.toString());
				JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), e.toString(), JOptionPane.OK_OPTION);
				reconnect();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw e;
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		}

		ErrorHandler.debug("ConnectionToDB::getQuestion(): returning");
		return example;
	}

	public boolean isLocal() {
		if (XML.getInstance().getMySQLPath() == null) {
			return false;
		} else {
			return true;
		}
	}


	public Example getTheMostDifficult() throws Exception {
		String foreign, czech;
		Statement stat = connection.createStatement();
		String query = "select lang,czech from " + this.getSlovickaTableName() + ""
			+ " where userID=" + userID + ""
			+ " and " + direction.getCAString() + "<=5"
			+ " and language='" + direction.getForeignLanguage() + "'"
			+ " order by weight desc limit 1";

		stat.executeQuery(query);

		ResultSet rs = stat.getResultSet();

		if (true == rs.next()) {
			foreign = rs.getString(1);
			czech = rs.getString(2);
		} else {
			rs.close();
			stat.close();
			return null;
		}

		stat.close();
		rs.close();
		return new Example(czech, foreign);
	}

	public static Session getSession() {
		return getSessionFactory().openSession();
	}
	private static org.hibernate.SessionFactory sessionFactory;

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			initSessionFactory();
		}
		return sessionFactory;
	}

	private static synchronized void initSessionFactory() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	public static void main(String args[]) {
		System.out.println("test of DB connection");
		try {
			/*LoginInfo li = new LoginInfo("strafelda", "heslo", "localhost", "", "D:\\Dropbox\\Ucitel\\mysql", true);
			li.setLogin("petr");
			//ConnectionToDB conn = null;
*/
			//conn = new ConnectionToDB(li);
			Session session = getSession();
			Transaction transaction = session.beginTransaction();

			/*conn.stopDatabase();
			for (int i = 0; i < 1; i++) {
			conn.reconnect();
			int id = conn.getUserID("petr");

			System.out.println("ID je: " + id);
			}*/
		} catch (Exception e) {
			e.toString();
		}
	}

	public void deleteWords() {
		Session session = getSession();
		session.createQuery("delete from Slovicka where language = '" + direction.getForeignLanguage() + "'").executeUpdate();
	}

	public void deleteWord(String czech, String foreign) {

		Session session = getSession();
		session.createQuery("delete from Slovicka "
			+ "where "
			+ "czech = '" + czech + "' and "
			+ "lang = '" + foreign + "' and "
			+ "language = '" + direction.getForeignLanguage() + "'").executeUpdate();

	}

	public void resetStats() {
		Statement stat = null;
		try {
			String user = XML.getInstance().getLogin();
			int id = getUserID(user);
			stat = connection.createStatement();

			String query3 = "delete from slovicka.slovicka_stats where userID=" + id + " and lngTo=\"" + direction.getLngTo() + "\"";
			stat.executeUpdate(query3);

		} catch (SQLException e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
			try {
				reconnect();
			} catch (Exception ex) {
				System.out.println(ex.toString());
				System.out.println("Cannot reconnect");
				ex.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			ErrorHandler.logError(e);
		} finally {
			try {
				stat.close();
			} catch (SQLException ex) {
				Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}


	public ArrayList<Word> getVocabulary() {
		return getVocabulary(direction.getLngFrom(),direction.getLngTo(),null,100);
	}

	public ArrayList<Word> getVocabulary(Language from, Language to, Date fromDate, int maxAnswers) {
		ArrayList<Word> words = new ArrayList<Word>();
		String foreign, czech;
		Statement stat;
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		String fromDateS = "";
		if (fromDate != null) {
			if (direction.isFromCzech()) {
				fromDateS = " and last_asked > \"" + sdf.format(fromDate) + "\" ";
			} else {
				fromDateS = " and last_asked_to_czech >\"" + sdf.format(fromDate) + "\" ";
			}
		}

		String grammar = "";
		if (GUI.getInstance().getGrammar() == true) {
			grammar = " and grammar = 1 ";
		} else {
			grammar = " and grammar = 0 ";
		}

		String maxAnswersS = null;
		if (direction.isFromCzech()) {
			maxAnswersS = " and CA_from_czech < " + maxAnswers + " ";
		} else {
			maxAnswersS = " and correctly_answered < " + maxAnswers + " ";
		}



		try {
			stat = connection.createStatement();

			String query = "select lang,czech,id from " + this.getSlovickaTableName() + ""
				+ " where userID=" + userID + ""
				+ grammar
				+ fromDateS
				+ maxAnswersS
				+ " and language='" + direction.getForeignLanguage() + "'"
				+ " order by last_asked desc";
			//+ " limit 1";

			stat.executeQuery(query);

			ResultSet rs = stat.getResultSet();

			while (true == rs.next()) {
				foreign = rs.getString(1);
				czech = rs.getString(2);
				int id = rs.getInt(3);

				if (direction.isFromCzech()) {
					words.add(new Word(czech, foreign, id));
				} else {
					words.add(new Word(foreign, czech, id));
				}
			}

			stat.close();
			rs.close();
		} catch (SQLException ex) {
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}

		return words;
	}

	boolean allAsked() {
		try {
			if ((isQuestionAvailable() == false) && (this.getNotAskedToday() > 0)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}

		return false;
	}

	public int getWronglyAnsweredCnt() {
		int count = 0;
		Session session = getSession();
		try {
			Long d = new Long("2145913200000");
			Date date = new Date(d);

			/*Criteria criteria = session.createCriteria(Slovicka.class).setProjection(Projections.rowCount());
			criteria.add(Restrictions.eq(direction.getLastAskedProperty(), date));
                        criteria.add(Restrictions.eq("language", direction.getForeignLanguage().toString()));
                        count = (Integer) criteria.uniqueResult();
			*/
			d = new Long("631148400000"); //'1990-01-01 00:00:00' from http://www.timestampconvert.com/?go2=true&offset=-1&timestamp=631148400&Submit=++++++Convert+to+Date++++++
			Date date2 = new Date(d);
			
			Criteria criteria = session.createCriteria(Slovicka.class).setProjection(Projections.rowCount());
			criteria.add(Restrictions.disjunction().add(Restrictions.eq(direction.getLastAskedProperty(), date)).add(Restrictions.eq(direction.getLastAskedProperty(), date2)));
                        criteria.add(Restrictions.eq("language", direction.getForeignLanguage().toString()));

                        count = (Integer) criteria.uniqueResult();

		} catch (Exception ex) {
			Logger.getLogger(ConnectionToDB.class.getName()).log(Level.SEVERE, null, ex);
		}

		return count;

	}
}
