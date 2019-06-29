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
        createTable();
    }
    
    public String Objects = "CREATE TABLE IF NOT EXISTS furnitureLibData (" +
    		"`ObjID` STRING NOT NULL PRIMARY KEY," +
    		"`Data` STRING NOT NULL," +
    		"`world` STRING NOT NULL," +
    		"`x` int NOT NULL," +
    		"`z` int NOT NULL," +
    		"`uuid` STRING NOT NULL" +
    		");";
 
    public Connection getSQLConnection() {
        try {
        	if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        	File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        	if (!dataFolder.exists()){
                try {
                    dataFolder.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
                }
            }
        	Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"MySQL exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the MySQL JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }
 
    public void createTable() {
        try (Connection con = getSQLConnection(); Statement stmt = con.createStatement()){
        	stmt.executeUpdate(Objects);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public DataBaseType getType() {return this.type;}
}