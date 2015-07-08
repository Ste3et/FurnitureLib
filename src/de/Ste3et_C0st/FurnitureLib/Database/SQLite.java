package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;


public class SQLite extends Database{
    String dbname;
    public SQLite(FurnitureLib instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "furniture");
    }

    //(ID,ArmorID,ObjID,Name,X,Y,Z,Yaw,Pitch,World)
    public String SQLiteCreateArmorStandInfoTable = "CREATE TABLE IF NOT EXISTS ArmorStand_Info (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`ArmorID` INT NOT NULL," +
            "`ObjID` STRING NOT NULL," +
            "`Name` STRING NOT NULL," +
            "`X` DOUBLE NOT NULL," +
            "`Y` DOUBLE NOT NULL," +
            "`Z` DOUBLE NOT NULL," +
            "`Yaw` FLOAT NOT NULL," +
            "`Pitch` FLOAT NOT NULL," +
            "`World` STRING NOT NULL" +
            ");";
    
    public String SQLiteCreateArmorStandObjectIDS = "CREATE TABLE IF NOT EXISTS ArmorStand_ObjectIDS (" +
    		"`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
    		"`ObjID` STRING NOT NULL," +
            "`X` DOUBLE NOT NULL," +
            "`Y` DOUBLE NOT NULL," +
            "`Z` DOUBLE NOT NULL," +
            "`Yaw` FLOAT NOT NULL," +
            "`Pitch` FLOAT NOT NULL," +
            "`World` STRING NOT NULL" +
            ");";
    
    public String SQLiteCreateArmorStandMetadata = "CREATE TABLE IF NOT EXISTS ArmorStand_Metadata (" +
            "`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`ArmorID` INT NOT NULL," +
            "`ObjID` STRING NOT NULL," +
            "`Arms` INT NOT NULL," +
            "`Small` INT NOT NULL," +
            "`Gravity` INT NOT NULL," +
            "`BasePlate` INT NOT NULL," +
            "`Invisible` INT NOT NULL," +
            "`Customname` INT NOT NULL," +
            "`Fire` INT NOT NULL" +
            ");";
    
    public String SQLiteCreateArmorStandRotation = "CREATE TABLE IF NOT EXISTS ArmorStand_Rotation (" +
    		"`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
    		"`ArmorID` INT NOT NULL," +
            "`ObjID` STRING NOT NULL," +
    		"`BodyPart` STRING NOT NULL," +
    		"`X` DOUBLE NOT NULL," +
    		"`Y` DOUBLE NOT NULL," +
    		"`Z` DOUBLE NOT NULL" +
            ");";
    
    public String SQLiteCreateArmorStandInventory = "CREATE TABLE IF NOT EXISTS ArmorStand_Inventory (" +
    		"`ID` INTEGER PRIMARY KEY AUTOINCREMENT," +
    		"`ArmorID` INT NOT NULL," +
            "`ObjID` STRING NOT NULL," +
    		"`Slot` INT NOT NULL," +
    		"`ItemStack` STRING NOT NULL" + 
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
            s.executeUpdate(SQLiteCreateArmorStandInfoTable);
            s.executeUpdate(SQLiteCreateArmorStandMetadata);
            s.executeUpdate(SQLiteCreateArmorStandRotation);
            s.executeUpdate(SQLiteCreateArmorStandInventory);
            s.executeUpdate(SQLiteCreateArmorStandObjectIDS);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}