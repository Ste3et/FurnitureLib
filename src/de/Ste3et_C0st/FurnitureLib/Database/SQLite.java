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
    
    public String ArmorStandTable = "CREATE TABLE IF NOT EXISTS FurnitureLib_ArmorStand (" +
    		"`ID` STRING NOT NULL," +
            "`ObjID` STRING NOT NULL," +
    		"`Metadata` STRING NOT NULL," +
    		"`Boolean` STRING NOT NULL," + 
    		"`Location` STRING NOT NULL," + 
    		"`ObjIDLocation` STRING NOT NULL," + 
    		"`InHand` STRING NOT NULL," + 
    		"`boots` STRING NOT NULL," + 
    		"`chestplate` STRING NOT NULL," + 
    		"`leggings` STRING NOT NULL," + 
    		"`helm` STRING NOT NULL," + 
    		"`EA_Head` STRING NOT NULL," + 
    		"`EA_Body` STRING NOT NULL," + 
    		"`EA_Left_Arm` STRING NOT NULL," + 
    		"`EA_Right_Arm` STRING NOT NULL," + 
    		"`EA_Left_Leg` STRING NOT NULL," + 
    		"`EA_Right_Leg` STRING NOT NULL" + 
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
            s.executeUpdate(ArmorStandTable);
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
}