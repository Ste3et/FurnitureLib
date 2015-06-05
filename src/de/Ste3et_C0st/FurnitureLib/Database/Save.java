package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class Save {
	public Save(Plugin plugin){
		try {
			addDefaults();
		} catch (SQLException e) {
			Bukkit.getPluginManager().disablePlugin(plugin);
			e.printStackTrace();
			return;
		}
	}
	
	private void addDefaults() throws SQLException{
		if(!FurnitureLib.getInstance().getDB().isTable("as_packet")){
			FurnitureLib.getInstance().getDB().query("CREATE TABLE as_packet(id INTEGER, ObjectID TEXT, Name TEXT);");
		}
		if(!FurnitureLib.getInstance().getDB().isTable("as_packet_location")){
			FurnitureLib.getInstance().getDB().query("CREATE TABLE as_packet_location(id INTEGER, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT);");
		}
		
		if(!FurnitureLib.getInstance().getDB().isTable("as_packet_Inventory")){
			FurnitureLib.getInstance().getDB().query("CREATE TABLE as_packet_Inventory(id INTEGER, Slot INTEGER, ItemStack BLOB);");
		}
		
		if(!FurnitureLib.getInstance().getDB().isTable("as_Packet_rotation")){
			FurnitureLib.getInstance().getDB().query("CREATE TABLE as_Packet_rotation(id INTEGER, BodyPart TEXT, x DOUBLE, y DOUBLE, z DOUBLE);");
		}
	}
	
	public void saveAsPacket(ArmorStandPacket asPacket) throws SQLException{
			FurnitureLib.getInstance().getDB().getConnection().setAutoCommit(false);
			PreparedStatement statment = FurnitureLib.getInstance().getDB().prepare("INSERT INTO as_packet(id, ObjectID, Name) VALUES (?, ?, ?)");
			statment.setInt(1, asPacket.getEntityId());
			statment.setString(2, asPacket.getObjectId().getID());
			statment.setString(3, asPacket.getName());
			FurnitureLib.getInstance().getDB().query(statment);
			
			statment = FurnitureLib.getInstance().getDB().prepare("INSERT INTO as_packet_location(id, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)");
			statment.setInt(1, asPacket.getEntityId());
			statment.setDouble(2, asPacket.getLocation().getX());
			statment.setDouble(3, asPacket.getLocation().getY());
			statment.setDouble(4, asPacket.getLocation().getZ());
			statment.setFloat(5, asPacket.getLocation().getYaw());
			statment.setFloat(6, asPacket.getLocation().getPitch());
			FurnitureLib.getInstance().getDB().query(statment);
			
			for(ItemStack is : asPacket.getInventory().getIS()){
				if(is!=null){
					statment = FurnitureLib.getInstance().getDB().prepare("INSERT INTO as_packet_Inventory(id, Slot, ItemStack) VALUES (?, ?, ?)");
					statment.setInt(1, asPacket.getEntityId());
					statment.setInt(2, asPacket.getInventory().getSlot(is));
					statment.setObject(3, serealize(is));
					FurnitureLib.getInstance().getDB().query(statment);
				}
			}
			
			for(BodyPart part : BodyPart.getList()){
				if(part!=null){
					if(asPacket.getAngle(part) != null){
						statment = FurnitureLib.getInstance().getDB().prepare("INSERT INTO as_Packet_rotation(id, BodyPart, x, y, z) VALUES (?, ?, ?, ?, ?)");
						statment.setInt(1, asPacket.getEntityId());
						statment.setString(2, part.getName());
						statment.setDouble(3, asPacket.getAngle(part).getX());
						statment.setDouble(4, asPacket.getAngle(part).getY());
						statment.setDouble(5, asPacket.getAngle(part).getZ());
						FurnitureLib.getInstance().getDB().query(statment);
					}
				}
			}
			FurnitureLib.getInstance().getDB().getConnection().commit();
			FurnitureLib.getInstance().getDB().getConnection().setAutoCommit(true);
	}
	
	private Map<String, Object> serealize(ItemStack itemStack){
		if (!itemStack.hasItemMeta()) itemStack.setItemMeta(Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
		return itemStack.serialize();
	}
}
