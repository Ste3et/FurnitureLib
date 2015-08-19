package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;

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
    
    public void initialize(){
        connection = getSQLConnection();
        try{
        	statement = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FurnitureLib_ArmorStand");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
    public void save(ObjectID id){
    	FurnitureManager manager = FurnitureLib.getInstance().getFurnitureManager();
    	set(id);
    	for(ArmorStandPacket as : manager.getArmorStandPacketByObjectID(id)){
    		set(as);
    		System.out.println(as.toString());
//    		String s = FurnitureLib.getInstance().getSerialize().SerializeArmorStand(as);
//    		System.out.println(s);
//    		plugin.getConfig().set("Packet." + as.getArmorID(), s);
//			plugin.saveConfig();
    	}
    }
    
    private void set(ObjectID obj){
    	String[] a = FurnitureLib.getInstance().getSerialize().toObjectIDString(obj);
    	String query = "REPLACE INTO FurnitureLib_ObjectID (`ObjID`,`PlayerUUID`,`PublicMode`,`Members`,`EVMode`) VALUES(" +
    			"'" + a[0] + "', "
    			+ "'" + a[1] + "', "
    			+ "'" + a[2] + "', "
    			+ "'" + a[3] + "', "
    			+ "'" + a[4] + "');";
    	try{
    		statement.executeUpdate(query);
    	}catch(Exception e){
    		
    	}
    }
    
    private void set(ArmorStandPacket as){
    	String[] a = FurnitureLib.getInstance().getSerialize().toArmorStandString(as);
    	String query = "REPLACE INTO FurnitureLib_ArmorStand (`ID`,`ObjID`,`Metadata`,`Boolean`,`Location`,`ObjIDLocation`,`InHand`,`boots`,`chestplate`,`leggings`,`helm`, `EA_Head`,`EA_Body`,`EA_Left_Arm`,`EA_Right_Arm`,`EA_Left_Leg`,`EA_Right_Leg`) VALUES(" +
    			"'" + as.getArmorID() + "', "
    			+ "'" + a[0] + "', "
    			+ "'" + a[1] + "', "
    			+ "'" + a[2] + "', "
    			+ "'" + a[3] + "', "
    			+ "'" + a[4] + "', "
    			+ "'" + a[5] + "', "
    			+ "'" + a[6] + "', "
    			+ "'" + a[7] + "', "
    			+ "'" + a[8] + "', "
    			+ "'" + a[9] + "', "
    			+ "'" + a[10] + "', "
    			+ "'" + a[11] + "', "
    			+ "'" + a[12] + "', "
    			+ "'" + a[13] + "', "
    			+ "'" + a[14] + "', "
    			+ "'" + a[15] + "');";
    	try{
    		statement.executeUpdate(query);
    	}catch(Exception e){
    		
    	}
    }
    
    public void loadAll(Boolean b){
    	try{
    		ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_ArmorStand");
    		List<String[]> asList = new ArrayList<String[]>();
    		while (rs.next()) {
				String a[] = new String[17];
				for(int i = 0; i<=16;i++){
						a[i] = rs.getString(i+1);
				}
				
				if(!asList.contains(a)){
					asList.add(a);
				}
				
			}
    		plugin.getLogger().info("FurnitureLib load " + asList.size()  +  " ArmorStandPackets from: " + getType().name() + " Database");
    		
    		rs.close();
    		for(String[] l : asList){
    			FurnitureLib.getInstance().getSerialize().fromArmorStandString(l);
    		}
    		
//    		for(String s : plugin.getConfig().getConfigurationSection("Packet").getKeys(false)){
//    			System.out.println(s);
//    			if(s!=null){
//    				System.out.println("OK");
//    				String a = plugin.getConfig().getString("Packet." + s);
//    				System.out.println(a);
//        			ArmorStandPacket packet = FurnitureLib.getInstance().getSerialize().fArmorStandString(a);
//        			//Bukkit.broadcastMessage("ObjID: " + packet.getObjectId().getID());
//        			Bukkit.broadcastMessage("EntityID:" + packet.getEntityId());
//        			Bukkit.broadcastMessage("ArmorID:" + packet.getArmorID());
//        			Bukkit.broadcastMessage("Location: " + packet.getLocation());
//        			Bukkit.broadcastMessage("Name: " + packet.getName());
//        			Bukkit.broadcastMessage("Fire: " + packet.isFire());
//        			Bukkit.broadcastMessage("Invisible: " + packet.isInvisible());
//        			Bukkit.broadcastMessage("Mini: " + packet.isMini());
//        			Bukkit.broadcastMessage("BasePlate: " + packet.hasBasePlate());
//        			Bukkit.broadcastMessage("Arms: " + packet.hasArms());
//        			Bukkit.broadcastMessage("Gravity: " + packet.hasGravity());
//        			
//        			//Bukkit.broadcastMessage("RightArm: " + packet.getAngle(BodyPart.RIGHT_ARM).toString());
//    			}
//    		}
    		
    		if(b){
    			plugin.getFurnitureManager().getPreLoadetList().clear();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void loadAllObjIDs(){
    	try {
        	ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_ObjectID");
        	List<String[]> objList = new ArrayList<String[]>();
			while (rs.next()) {
				String a[] = new String[5];
				for(int i = 0; i<=4;i++){
						a[i] = rs.getString(i+1);
				}
				
				if(!objList.contains(a)){
					objList.add(a);
				}
			}
			rs.close();
			
			for(String[] l : objList){
				FurnitureLib.getInstance().getSerialize().fromArmorObjectString(l);
			}
		}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void delete(ObjectID objID){
    	try {
			statement.execute("DELETE FROM FurnitureLib_ArmorStand WHERE ObjID = '" + objID.getID() + "'");
			statement.execute("DELETE FROM FurnitureLib_ObjectID WHERE ObjID = '" + objID.getID() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
 

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
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