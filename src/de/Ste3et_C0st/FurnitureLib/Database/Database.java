package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.EOFException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public abstract class Database {
	FurnitureLib plugin;
    Connection connection;
    Statement statement;
    DataBaseCallBack callBack;
    boolean result = false;
    public Database(FurnitureLib instance){
        this.plugin = instance;
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

    public void loadAll(final SQLAction action, final DataBaseCallBack callBack){
    	final long time1 = System.currentTimeMillis();
    	final boolean b = FurnitureLib.getInstance().isAutoPurge();
//    	try{
//    		new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try{
//						ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_Objects");
//			    		while (rs.next()){FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString(1), rs.getString(2), action, b);}
//			    		if(!rs.next()){
//			    			rs.close();
//			    			plugin.getLogger().info("FurnitureLib load " + FurnitureLib.getInstance().getFurnitureManager().getObjectList().size()  +  " Objects from: " + getType().name() + " Database");
//			        		long time2 = System.currentTimeMillis();
//			    	    	long newTime = time2-time1;
//			    	    	SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
//			    	    	String timeStr = time.format(newTime);
//			    	    	int ArmorStands = FurnitureLib.getInstance().getDeSerializer().armorStands;
//			    	    	int purged = FurnitureLib.getInstance().getDeSerializer().purged;
//			    	    	plugin.getLogger().info("FurnitureLib have loadet " + ArmorStands + " in " +timeStr);
//			    	    	plugin.getLogger().info("FurnitureLib have purged " + purged + " Objects");
//			    	    	callBack.onResult(true);
//			    		}
//					}catch(Exception e){
//			    		e.printStackTrace();
//			    		callBack.onResult(false);
//			    	}
//				}
//    		}).start();
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
    	
    	this.callBack = callBack;
    	loadFurnitures(0, b, action);
    	this.callBack2 = new DataBaseCallBack() {
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
		        	for(ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
		        		id.sendAll();
		        	}
		        	
		        	callBack.onResult(true);
				}
			}
		};
    }
    

    private DataBaseCallBack callBack2;
    
    public boolean loadFurnitures(final int i, final boolean b, final SQLAction action){
    	if(result) return false;
    	new Thread(new Runnable() {
    		@Override
			public void run() {
    			try{
    				int count = FurnitureLib.getInstance().getStepSize();
    				int offset = i, j = 0;
        			String query = "SELECT * FROM FurnitureLib_Objects LIMIT " + count + " OFFSET " + offset;
        			ResultSet rs = statement.executeQuery(query);
        			while (rs.next()){
        				FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString(1), rs.getString(2), action, b);
        				j++;
        			}
        			if(!rs.next()){
		    			rs.close();
		    			if(j != count){
		    				result = true;
		    				callBack2.onResult(true);
		    				return;
		    			}else{
		    				loadFurnitures(i + count, b, action);
		    				return;
		    			}
		    		}
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    	}).start();
    	return false;
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