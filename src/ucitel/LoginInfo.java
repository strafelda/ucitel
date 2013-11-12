package ucitel;

import ucitel.forms.GUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import javax.swing.JOptionPane;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author strafeldap
 */
public class LoginInfo {

        private String serverLogin;
        private String serverPassword;
        private String host;
        private String port;
        private String mysqlpath;
        private String conName;
        private boolean isLocalDB;
        public static String URL = "http://localhost/urls.txt";

        public String getRootLogin() {
                File servers = new File("servers.txt");

                try {
                        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(servers));
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        while (bufferedReader.ready()) {
                                String line = bufferedReader.readLine();
                                int index = line.indexOf(";");
                                String servers_host = line.substring(0, index);

                                if (servers_host.substring(0, this.host.length()).matches(this.host)) {
                                        int portIndex = line.indexOf(";", index + 1);
                                        return line.substring(portIndex + 1, line.indexOf(";", portIndex + 1));
                                }
                        }

                        bufferedReader.close();
                        inputStreamReader.close();
                } catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "File ' servers.txt wasn't found'", JOptionPane.OK_OPTION);
                } catch (IOException e) {
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "File ' servers.txt wasn't found'", JOptionPane.OK_OPTION);
                }

                return null;
        }

        public String getRootPassword() {
                File servers = new File("servers.txt");

                try {
                        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(servers));
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                        while (bufferedReader.ready()) {
                                String line = bufferedReader.readLine();
                                int index = line.indexOf(";");
                                String servers_host = line.substring(0, index);

                                if (servers_host.substring(0, host.length()).matches(this.host)) {
                                        int portIndex = line.indexOf(";", index + 1);
                                        int hostIndex = line.indexOf(";", portIndex + 1);
                                        return line.substring(hostIndex + 1);
                                }
                        }

                        bufferedReader.close();
                        inputStreamReader.close();
                } catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(GUI.getInstance().getComponent(), e.getMessage(), "File 'servers.txt wasn't found'", JOptionPane.OK_OPTION);
                } catch (IOException e) {
                        e.printStackTrace();
                }

                return null;
        }

        public LoginInfo(String host, String port) {
                super();

                this.host = host;
                this.port = port;
        }

        public LoginInfo(String host, String port, String mysqlpath, boolean isLocal) {
                super();
                this.host = host;
                this.port = port;
                this.mysqlpath = mysqlpath;
                this.isLocalDB = isLocal;
        }
/*
        public LoginInfo(String login, String password, String host, String port, String mysqlpath, boolean localDB) throws Exception {
                super();
                this.conName = null;
                this.serverLogin = login;
                this.serverPassword = password;
                this.host = host;
                this.port = port;
                this.mysqlpath = mysqlpath;
                this.isLocalDB = localDB;
                if ((host == null) || (port == null)) {
                        if (mysqlpath == null) {
                                throw new Exception("Required records not set");
                        }
                }

                if (host.isEmpty()) {
                        File mysql = new File(this.mysqlpath + "\\mysqld-nt.exe");
                        if (!mysql.exists()) {
                                throw new Exception("Invalid MySQL path");
                        }
                }
        }
*/
        public LoginInfo(String con, String login, String password, String host, String port, String mysqlpath, boolean localDB) throws Exception {
                this.conName = con;
                this.serverLogin = login;
                this.serverPassword = password;
                this.host = host;
                this.port = port;
                this.mysqlpath = mysqlpath;
                this.isLocalDB = localDB;
                
                if ((host == null) || (port == null)) {
                        if (mysqlpath == null) {
                                throw new Exception("Required records not set");
                        }
                }

                if (host.isEmpty()) {
                        File mysql = new File(this.mysqlpath + "\\mysqld-nt.exe");
                        if (!mysql.exists()) {
                                throw new Exception("Invalid MySQL path");
                        }
                }
        }
/*
        public LoginInfo(String serverLogin, String srverPassword, String userLogin, String userPassword, String host, String port) throws Exception {
                this.serverLogin = serverLogin;
                this.serverPassword = srverPassword;
                this.host = host;
                this.port = port;

                if ((host == null) || (port == null)) {
                        if (mysqlpath == null) {
                                throw new Exception("Required records not set");
                        }
                }

                if (host.isEmpty()) {
                        File mysql = new File(this.mysqlpath + "\\mysqld-nt.exe");
                        if (!mysql.exists()) {
                                throw new Exception("Invalid MySQL path");
                        }
                }
        }
*/
        public String getConName() {
                return conName;
        }

        public boolean isDBLocal() {
                return isLocalDB;
        }

        public String getMysqlPath() {
                return this.mysqlpath;
        }

        public void setLogin(String login) {
                this.serverLogin = login;
        }

        public void setPassword(String password) {
                this.serverPassword = password;
        }

        public String getPassword() {
                return this.serverPassword;
        }

        public String getLogin() {
                return this.serverLogin;
        }

        public String getHost() {
                if (null == this.host) {
                        XML.getInstance().getDBhost();
                }
                return this.host;
        }

        public String getPort() {
                if ((null == this.port) || (this.port.isEmpty())) {
                        this.port = XML.getInstance().getDBport();
                }

                return this.port;
        }
}
