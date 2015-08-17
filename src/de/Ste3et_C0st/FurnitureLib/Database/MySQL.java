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

    public String ArmorStandTable = "CREATE TABLE IF NOT EXISTS FurnitureLib_ArmorStand (" +
    		"`ID` TEXT NOT NULL," +
            "`ObjID` TEXT NOT NULL," +
    		"`Metadata` TEXT NOT NULL," +
    		"`Boolean` TEXT NOT NULL," + 
    		"`Location` TEXT NOT NULL," + 
    		"`ObjIDLocation` TEXT NOT NULL," + 
    		"`InHand` TEXT NOT NULL," + 
    		"`boots` TEXT NOT NULL," + 
    		"`chestplate` TEXT NOT NULL," + 
    		"`leggings` TEXT NOT NULL," + 
    		"`helm` TEXT NOT NULL," + 
    		"`EA_Head` TEXT NOT NULL," + 
    		"`EA_Body` TEXT NOT NULL," + 
    		"`EA_Left_Arm` TEXT NOT NULL," + 
    		"`EA_Right_Arm` TEXT NOT NULL," + 
    		"`EA_Left_Leg` TEXT NOT NULL," + 
    		"`EA_Right_Leg` TEXT NOT NULL" + 
    		");";
    
    public String ObjectIDString = "CREATE TABLE IF NOT EXISTS FurnitureLib_ObjectID (" +
    		"`ObjID` TEXT NOT NULL," +
    		"`PlayerUUID` TEXT NOT NULL," +
    		"`PublicMode` TEXT NOT NULL," +
    		"`Members` TEXT NOT NULL," +
    		"`EVMode` TEXT NOT NULL" +
    		");";

    public Connection getSQLConnection() {
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+DBname+"?user="+DBuser+"&password=" + DBPsw + "&autoReconnect=true");
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
            s.executeUpdate(ArmorStandTable);
            s.executeUpdate(ObjectIDString);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }

	public DataBaseType getType() {return this.type;}
}