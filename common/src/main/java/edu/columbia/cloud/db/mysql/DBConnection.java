package edu.columbia.cloud.db.mysql;

import java.sql.*;

public class DBConnection {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static String DB_URL ;
   private static Connection conn = null;
   
   //  Database credentials
   static String USER = "root";
   static String PASS = "";
   
   public static Connection getConnection(String dbUrl,String user, String pass)
   {
	      try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
			DB_URL=dbUrl;
			USER=user;
			PASS=pass;
		    conn = DriverManager.getConnection(DB_URL,USER,PASS);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return conn;
	     
   }
}