package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public abstract class Database {
    FurnitureLib plugin;
    Connection connection;
    Statement statement;
    public Database(FurnitureLib instance){
        plugin = instance;
    }
    
    public abstract Connection getSQLConnection();

    public abstract void load();
    public abstract DataBaseType getType();
    
    public boolean isExist(String s){
    	try{
    		boolean query = statement.execute("SELECT * FROM `"+s+"`");
    		if(query){return true;}
    	}catch(Exception e){
    		return false;
    	}
    	return false;
    }
    
    public void initialize(){
        connection = getSQLConnection();
        try{
        	statement = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FurnitureLib_Objects");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
    public boolean save(ObjectID id){
    	String binary = FurnitureLib.getInstance().getSerializer().SerializeObjectID(id);
    	String query = "REPLACE INTO FurnitureLib_Objects (`ObjID`,`Data`) VALUES ('" + id.getID() + "', '" + binary + "');";
    	try{
    		statement.executeUpdate(query);
    		return true;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
    
//    public void reconnect(){
//    	try{
//    		if(connection.isClosed()){
//    			connection = getSQLConnection();
//    			statement = connection.createStatement();
//    		}else if(statement.isClosed()){
//    			statement = connection.createStatement();
//    		}
//    	}catch(SQLException e){
//    		e.printStackTrace();
//    	}
//    }
//    
    public void loadAll(SQLAction action){
    	long time1 = System.currentTimeMillis();
    	boolean b = FurnitureLib.getInstance().isAutoPurge();
    	try{
    		ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_Objects");
    		while (rs.next()){FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString(1), rs.getString(2), action, b);}
    		rs.close();
    		plugin.getLogger().info("FurnitureLib load " + FurnitureLib.getInstance().getFurnitureManager().getObjectList().size()  +  " Objects from: " + getType().name() + " Database");
    		long time2 = System.currentTimeMillis();
	    	long newTime = time2-time1;
	    	SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
	    	String timeStr = time.format(newTime);
	    	int ArmorStands = FurnitureLib.getInstance().getDeSerializer().armorStands;
	    	int purged = FurnitureLib.getInstance().getDeSerializer().purged;
	    	plugin.getLogger().info("FurnitureLib have loadet " + ArmorStands + " in " +timeStr);
	    	plugin.getLogger().info("FurnitureLib have purged " + purged + " Objects");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void delete(ObjectID objID){
    	try {
    		statement.execute("DELETE FROM FurnitureLib_Objects WHERE ObjID = '" + objID.getID() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
 

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void close(){
    	try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}