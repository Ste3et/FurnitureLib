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
        createTable();
    }
    
    public String Objects = "CREATE TABLE IF NOT EXISTS furnitureLibData (" +
			"`ObjID` varchar(255) NOT NULL," +
			"`Data` TEXT NOT NULL," +
			"`world` TEXT NOT NULL," +
			"`x` int NOT NULL," +
			"`z` int NOT NULL," +
			"`uuid` TEXT NOT NULL, PRIMARY KEY (ObjID)" +
    		");";
    
    public Connection getSQLConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://"+host+":"+port+"/"+DBname + "?autoReconnect=true&useSSL=" + FurnitureLib.getInstance().useSSL();
            FurnitureLib.debug("MySQL connectionString -> " + connectionString);
            return DriverManager.getConnection(connectionString,DBuser,DBPsw);
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
        	FurnitureLib.debug("MySQL createTable -> " + Objects);
        } catch (SQLException e) {
        	FurnitureLib.debug("MySQL createTable: Fail");
            e.printStackTrace();
        }
    }

	public DataBaseType getType() {return this.type;}
}