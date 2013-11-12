/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ucitel;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class EmployeeReader{
   public static final String DRIVER_NAME =
          "sun.jdbc.odbc.JdbcOdbcDriver";
   public static final String DATABASE_URL = "jdbc:odbc:employee_xls";

   public static void main(String[] args)
      throws ClassNotFoundException, SQLException{
      Class.forName(DRIVER_NAME);
      Connection con = null;
      try {
         con = DriverManager.getConnection(DATABASE_URL);
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery
            ("select lastname, firstname, id from [Sheet1$]");
         while (rs.next()) {
            String lname = rs.getString(1);
            String fname = rs.getString(2);
            int id = rs.getInt(3);

            System.out.println(fname + " " + lname + "  id : " + id);
         }
         rs.close();
         stmt.close();
      }
      finally {
         if (con != null)
            con.close();
      }
   }
}
