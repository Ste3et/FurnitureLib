package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.EOFException;
import java.net.SocketException;
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
	public FurnitureLib plugin;
	public Connection connection;
    private Statement statement;
    boolean result = false;
    public Database(FurnitureLib instance){
        this.plugin = instance;
    }
    
    public abstract Connection getSQLConnection();

    public abstract void load();
    public abstract DataBaseType getType();
    
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
//    
//    private void addNewCollums(){
//    	String q1 = "ALTER TABLE `FurnitureLib_Objects` ADD COLUMN `id` INTEGER PRIMARY KEY AUTOINCREMENT;";
//    	//String q2 = "ALTER TABLE `FurnitureLib_Objects` ADD COLUMN IF NOT EXISTS `world` TINYTEXT;";
//    	try {
//    		statement.executeUpdate(q1);
//    		//statement.executeUpdate("ALTER TABLE `FurnitureLib_Objects` ADD COLUMN `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;");
//			//statement.executeUpdate(q2);dfsagfdsakhjfd sabnkfldsöa
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }
//    
    public boolean save(ObjectID id){
    	String binary = FurnitureLib.getInstance().getSerializer().SerializeObjectID(id);
    	//String query = "REPLACE INTO FurnitureLib_Objects (`id`,`ObjID`,`Data`,`world`) VALUES ('', '" + id.getID() + "', '" + binary + "', '"+ id.getWorld().getUID().toString() + "');";
    	String query = "REPLACE INTO FurnitureLib_Objects (`ObjID`,`Data`) VALUES ('" + id.getID() + "', '" + binary + "');";
    	try{
    		statement.executeUpdate(query);
    		return true;
    	}catch(Exception e){
    		if(e instanceof SocketException || e instanceof EOFException){
    			initialize();
    			try{
    				statement.executeUpdate(query);
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    			return false;
    		}
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public void loadAll(SQLAction action){
    	long time1 = System.currentTimeMillis();
    	boolean b = FurnitureLib.getInstance().isAutoPurge();
    	try{    		
    		ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_Objects");
    		while (rs.next()){
    			if(rs != null){
    				String a = rs.getString(1), c = rs.getString(2);
    				if(!(a.isEmpty() || c.isEmpty())) {
    					FurnitureLib.getInstance().getDeSerializer().Deserialze(a, c, action, b);
    				}
    			}
    		}
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
		} catch (Exception e) {
    		if(e instanceof SocketException || e instanceof EOFException){
    			initialize();
    			try{
    				statement.execute("DELETE FROM FurnitureLib_Objects WHERE ObjID = '" + objID.getID() + "'");
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    			return;
    		}
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