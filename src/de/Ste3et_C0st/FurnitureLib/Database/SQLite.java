package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;


public class SQLite extends Database{
    String dbname;
    private DataBaseType type = DataBaseType.SQLite;
    
    public SQLite(FurnitureLib instance, String dbName){
        super(instance);
        this.dbname = dbName;
    }
    
    public String Objects = "CREATE TABLE IF NOT EXISTS FurnitureLib_Objects (" +
    		"`ObjID` STRING NOT NULL," +
    		"`Data` STRING NOT NULL" +
    		");";
    
    public String DatabaseSchema = "CREATE TABLE IF NOT EXISTS FurnitureLib (" +
    		"`WorldInfo` STRING NOT NULL," +
    		"`ObjID` STRING NOT NULL," +
    		"`Data` STRING NOT NULL," +
    		"`PlaceTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
    		");";

    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
 
    public void load() {
        connection = getSQLConnection();     
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(Objects);
            //s.executeUpdate(DatabaseSchema);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
    
    public void close(){
    	try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	public DataBaseType getType() {return this.type;}

	public void create(){
		connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(Objects);
            // s.executeUpdate(DatabaseSchema);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
//	public void load(String query, SQLAction action) {
//		loadAll(query, action);
//	}
}