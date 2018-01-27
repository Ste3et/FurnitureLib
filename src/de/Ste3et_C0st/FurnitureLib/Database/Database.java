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

import org.bukkit.Bukkit;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public abstract class Database {
	public FurnitureLib plugin;
	public Connection connection;
    private Statement statement;
    @SuppressWarnings("unused")
	private CallBack callBack, callBack2;
    private Thread t = null;
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

    public void loadAll(final SQLAction action, final CallBack callBack){
    	final long time1 = System.currentTimeMillis();
    	final boolean b = FurnitureLib.getInstance().isAutoPurge();
    	this.callBack = callBack;
    	this.callBack2 = new CallBack() {
			@Override
			public void onResult(boolean b) {
				if(b){
					plugin.getLogger().info("FurnitureLib load " + FurnitureLib.getInstance().getFurnitureManager().getObjectList().size()  +  " Objects from: " + getType().name() + " Database");
		        	long time2 = System.currentTimeMillis();
		        	long newTime = time2-time1;
		        	SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
		        	String timeStr = time.format(newTime);
		        	int ArmorStands = FurnitureLib.getInstance().getDeSerializer().armorStands;
		        	int purged = FurnitureLib.getInstance().getDeSerializer().purged;
		        	plugin.getLogger().info("FurnitureLib have loadet " + ArmorStands + " in " +timeStr);
		        	plugin.getLogger().info("FurnitureLib have purged " + purged + " Objects");
		        	callBack.onResult(true);
				}
			}
		};
		loadFurnitures(b, action);
    }
    
    public void loadFurnitures(final boolean b, final SQLAction action){
    	if(result || this.t != null) return;
    	this.t = new Thread(new Runnable() {
    		@Override
			public void run(){
    			try{
        			String query = "SELECT * FROM FurnitureLib_Objects";
        			ResultSet rs = statement.executeQuery(query);
        			while (rs.next()) {
//        				String world = rs.getString("world");
//        				if(world == null || world.isEmpty()) {
//        					FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString("ObjID"), rs.getString("Data"), SQLAction.UPDATE, b);
//        				}else {
//        					FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString("ObjID"), rs.getString("Data"), action, b);
//        				}
        				FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString("ObjID"), rs.getString("Data"), action, b);
        			}
        			if(!rs.next()){
        				//
		    			rs.close();
		    			stop();
		    		}
    			}catch(Exception ex){ex.printStackTrace();}
    		}
    	});
    	t.start();
    	return;
	}
    
    private void stop() {
    	if(t!=null) {
    		t.interrupt();
    		t= null;
    		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					callBack2.onResult(true);
				}
			});
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