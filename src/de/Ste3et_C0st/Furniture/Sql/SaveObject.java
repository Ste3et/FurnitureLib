package de.Ste3et_C0st.Furniture.Sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SaveObject {
	static final String WRITE_OBJECT_SQL = "INSERT INTO furniture_objects(name, object_value) VALUES (?, ?)";
	static final String CREATE_TABLE_SQL = "CREATE TABLE furniture_objects(name Long, object_value BLOB);";
	
	  static final String READ_OBJECT_SQL = "SELECT object_value FROM furniture_objects WHERE id = ?";

	  public static Connection getConnection(String driver, String url, String username, String password) throws Exception {
		if(driver==null)driver="com.mysql.jdbc.Driver";
		if(url==null)url="jdbc:mysql://localhost:3306/dbName";
		if(username==null)username="root";
		if(password==null)password="root";
	    Class.forName(driver);
	    Connection conn = DriverManager.getConnection(url, username, password);	    
	    return conn;
	  }
	  
	  public static void createTable(Connection conn){
		  try {
			Statement state = conn.createStatement();
			state.executeUpdate(CREATE_TABLE_SQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  }

	  public static long writeJavaObject(Connection conn, Object object) throws Exception {
	    String className = object.getClass().getName();
	    PreparedStatement pstmt = conn.prepareStatement(WRITE_OBJECT_SQL);

	    // set input parameters
	    pstmt.setString(1, className);
	    pstmt.setObject(2, object);
	    pstmt.executeUpdate();

	    // get the generated key for the id
	    ResultSet rs = pstmt.getGeneratedKeys();
	    int id = -1;
	    if (rs.next()) {
	      id = rs.getInt(1);
	    }

	    rs.close();
	    pstmt.close();
	    System.out.println("writeJavaObject: done serializing: " + className);
	    return id;
	  }

	  public static Object readJavaObject(Connection conn, long id) throws Exception {
	    PreparedStatement pstmt = conn.prepareStatement(READ_OBJECT_SQL);
	    pstmt.setLong(1, id);
	    ResultSet rs = pstmt.executeQuery();
	    rs.next();
	    Object object = rs.getObject(1);
	    String className = object.getClass().getName();

	    rs.close();
	    pstmt.close();
	    System.out.println("readJavaObject: done de-serializing: " + className);
	    return object;
	  }
}