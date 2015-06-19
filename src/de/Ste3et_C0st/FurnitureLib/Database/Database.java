package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public abstract class Database {
    FurnitureLib plugin;
    Connection connection;
    Statement statement;
    public Database(FurnitureLib instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();
    
    public void initialize(){
        connection = getSQLConnection();
        try{
        	statement = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM ArmorStand_Info WHERE ID = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
         
            ps = connection.prepareStatement("SELECT * FROM ArmorStand_Rotation WHERE ID = ?");
            rs = ps.executeQuery();
            close(ps,rs);
            
            ps = connection.prepareStatement("SELECT * FROM ArmorStand_Inventory WHERE ID = ?");
            rs = ps.executeQuery();
            close(ps,rs);
            
            ps = connection.prepareStatement("SELECT * FROM ArmorStand_Metadata WHERE ID = ?");
            rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
    public void save(ObjectID id){
    	FurnitureManager manager = FurnitureLib.getInstance().getFurnitureManager();
    	for(ArmorStandPacket as : manager.getArmorStandPacketByObjectID(id)){
    		setPacketInfo(as);
    		setPacketMetadata(as);
    		for(int i = 0; i<=4;i++){
    			setPacketInv(as, i);
    		}
    		for(BodyPart part : BodyPart.getList()){
    			setPacketRotation(as, part);
    		}
    	}
    }
    
    public void delete(ObjectID objID){
    	try {
			statement.execute("DELETE FROM ArmorStand_Info WHERE ObjID ='" + objID.getID() + "'");
			statement.execute("DELETE FROM ArmorStand_Rotation WHERE ObjID ='" + objID.getID() + "'");
			statement.execute("DELETE FROM ArmorStand_Inventory WHERE ObjID ='" + objID.getID() + "'");
			statement.execute("DELETE FROM ArmorStand_Metadata WHERE ObjID ='" + objID.getID() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void loadALL(){
    	FurnitureManager manager = FurnitureLib.getInstance().getFurnitureManager();
    	try {
            ResultSet rs = statement.executeQuery("SELECT * FROM ArmorStand_Info");
            HashMap<Integer, ArmorStandPacket> asList = new HashMap<Integer, ArmorStandPacket>();
    		while (rs.next()) {
    			ObjectID ObjID = new ObjectID("new", "new");
    			ObjID.setID(rs.getString("ObjID"));
    			Integer ID = rs.getInt("ArmorID");
    			String name = rs.getString("Name");
    			Double X = rs.getDouble("X");
    			Double Y = rs.getDouble("Y");
    			Double Z = rs.getDouble("Z");
    			Float Yaw = rs.getFloat("Yaw");
    			Float Pitch = rs.getFloat("Pitch");
    			World w = Bukkit.getWorld(rs.getString("World"));
    			Location loc = new Location(w, X, Y, Z);
    			loc.setYaw(Yaw);
    			loc.setPitch(Pitch);
    			manager.setLastID(ID);
    			ArmorStandPacket asPacket = manager.createArmorStand(ObjID, loc);
    			asPacket.setName(name);
    			asList.put(ID, asPacket);
    		}
    		rs.close();
    		
    		for(Integer i : asList.keySet()){
    			loadMetadata(asList.get(i), i);
    			loadInventory(asList.get(i), i);
    			loadRotataion(asList.get(i), i);
    		}
    	} catch (Exception ex) {
            ex.printStackTrace();
        }
        return;  
    }
    
    private void loadRotataion(ArmorStandPacket packet, Integer ID) throws SQLException{
    	ResultSet result = statement.executeQuery("SELECT * FROM ArmorStand_Rotation WHERE ArmorID =" + ID);
    	
    	while (result.next()) {
    		BodyPart part = BodyPart.valueOf(result.getString("BodyPart"));
			Double x = result.getDouble("X");
			Double y = result.getDouble("Y");
			Double z = result.getDouble("Z");
			packet.setPose(new EulerAngle(x, y, z), part);
		}
    	result.close();
    }
    
    private void loadInventory(ArmorStandPacket packet, Integer ID) throws SQLException{
    	ResultSet result = statement.executeQuery("SELECT * FROM ArmorStand_Inventory WHERE ArmorID =" + ID);
    	
    	while (result.next()) {
			Integer slot = result.getInt("Slot");
			String base64 = result.getString("ItemStack");
			ItemStack is = FurnitureLib.getInstance().getSerialize().fromBase64(base64);
			packet.getInventory().setSlot(slot, is);
		}
    	result.close();
    }
    
    private void loadMetadata(ArmorStandPacket packet, Integer ID) throws SQLException{
    	ResultSet result = statement.executeQuery("SELECT * FROM ArmorStand_Metadata WHERE ArmorID =" + ID);
    	packet.setArms(intToBool(result.getInt("Arms")));
    	packet.setSmall(intToBool(result.getInt("Small")));
    	packet.setGravity(intToBool(result.getInt("Gravity")));
    	packet.setBasePlate(intToBool(result.getInt("BasePlate")));
    	packet.setInvisible(intToBool(result.getInt("Invisible")));
    	packet.setNameVasibility(intToBool(result.getInt("Customname")));
    	packet.setFire(intToBool(result.getInt("Fire")));
    	result.close();
    }

    private void setPacketMetadata(ArmorStandPacket asPacket) {
        try {
            String qString = "REPLACE INTO ArmorStand_Metadata (`ID`,`ArmorID`,`ObjID`,`Arms`,`Small`,`Gravity`,`BasePlate`,`Invisible`,`Customname`,`Fire`) VALUES(" +
            		null + ", " +
            		  asPacket.getEntityId() + ", " +
            		  "'" + asPacket.getObjectId().getID() + "', " +
            		  boolToInt(asPacket.hasArms()) + ", " +
            		  boolToInt(asPacket.isMini()) + ", " +
            		  boolToInt(asPacket.hasGravity()) + ", " +
            		  boolToInt(asPacket.hasBasePlate()) + ", " +
            		  boolToInt(asPacket.isInvisible()) + ", " +
            		  boolToInt(asPacket.isNameVisible()) + ", " +
            		  boolToInt(asPacket.isFire()) +
            		");";
            statement.executeUpdate(qString);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;             
    }
    
    private int boolToInt(boolean b){
    	if(b) return 1;
    	return 0;
    }
    
    private boolean intToBool(int i){
    	if(i==1) return true;
    	return false;
    }
    
    private void setPacketInfo(ArmorStandPacket asPacket) {
        try {
            String qString = "REPLACE INTO ArmorStand_Info (`ID`,`ArmorID`,`ObjID`,`Name`,`X`,`Y`,`Z`,`Yaw`,`Pitch`,`World`) VALUES(" +
            		null + ", " +
            		  asPacket.getEntityId() + ", " +
            		  "'" + asPacket.getObjectId().getID() + "', " +
            		  "'" + asPacket.getName() + "', " +
            		  asPacket.getLocation().getX() + ", " +
            		  asPacket.getLocation().getY() + ", " +
            		  asPacket.getLocation().getZ() + ", " +
            		  asPacket.getLocation().getYaw() + ", " +
            		  asPacket.getLocation().getPitch() + ", " +
            		  "'" + asPacket.getLocation().getWorld().getName() + "'" + 
            		");";
            statement.executeUpdate(qString);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return;             
    }
    
    private void setPacketRotation(ArmorStandPacket asPacket, BodyPart part) {
        try {
        	if(asPacket.getAngle(part)!=null){
        		String qString = "REPLACE INTO ArmorStand_Rotation (`ID`,`ArmorID`,`ObjID`,`BodyPart`,`X`,`Y`,`Z`) VALUES(" +
        				null + "," +
        				  asPacket.getEntityId() + ", " +
        				  "'" + asPacket.getObjectId().getID() + "', " +
        				  "'" + part.toString() + "', " +
        				  asPacket.getAngle(part).getX() + ", " +
        				  asPacket.getAngle(part).getY() + ", " +
        				  asPacket.getAngle(part).getZ() + 
                		");";
        		statement.executeUpdate(qString);
                return;
        	}
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return;             
    }
    
    private void setPacketInv(ArmorStandPacket asPacket, int Slot) {
        try { 
            String qString = "REPLACE INTO ArmorStand_Inventory (`ID`,`ArmorID`,`ObjID`,`Slot`,`ItemStack`) VALUES(" +
            		null + "," +
            		  asPacket.getEntityId() + ", " +
            		  "'" + asPacket.getObjectId().getID() + "', " +
            		  Slot + ", " +
            		"'" +  FurnitureLib.getInstance().getSerialize().toBase64(asPacket.getInventory().getSlot(Slot)) + "'" + 
            		");";
            statement.executeUpdate(qString);
            return;
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return;             
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
}