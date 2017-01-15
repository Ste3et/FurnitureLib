package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;


public class MySQL extends Database{
	private String host, DBname, DBuser,DBPsw, port;
	private DataBaseType type = DataBaseType.MySQL;
	
	public MySQL(FurnitureLib instance, String host, String DBname, String DBPsw, String DBUser, String Port){
        super(instance);
        this.host = host;
        this.DBname = DBname;
        this.DBPsw = DBPsw;
        this.DBuser = DBUser;
        this.port = Port;
    }
    
    public String Objects = "CREATE TABLE IF NOT EXISTS FurnitureLib_Objects (" +
    		"`ObjID` TEXT NOT NULL," +
    		"`Data` TEXT NOT NULL" +
    		");";

    public Connection getSQLConnection() {
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://"+host+":"+port+"/"+DBname + "?autoReconnect=true";
            connection = DriverManager.getConnection(connectionString,DBuser,DBPsw);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"MySQL exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the MySQL JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
 
    public void load() {
        connection = getSQLConnection();     
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(Objects);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

	public DataBaseType getType() {return this.type;}
}