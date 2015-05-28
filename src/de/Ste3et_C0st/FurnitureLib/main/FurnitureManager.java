package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class FurnitureManager {

	private Integer i = 407;
	private List<ArmorStandPacket> asPackets = new ArrayList<ArmorStandPacket>();
	
	public void updatePlayerView(Player player) {
		if(this.asPackets.isEmpty()){return;}
		for(ArmorStandPacket asp : asPackets){
			if(asp.isInRange(player)){
				asp.send(player);
			}else{
				asp.destroy(player);
			}
		}
	}
	
	public void updateFurniture(ObjectID obj) {
		if(this.asPackets.isEmpty()){return;}
		for(ArmorStandPacket packet : asPackets){
			if(packet.getObjectID().equals(obj)){
				for(Player player : Bukkit.getOnlinePlayers()){
					if(packet.isInRange(player)){
						packet.send(player);
					}
				}
			}
		}
	}

	public void removeFurniture(ObjectID id){
		if(this.asPackets.isEmpty()){return;}
		List<ArmorStandPacket> aspClone = new ArrayList<ArmorStandPacket>();
		Collections.copy(asPackets, aspClone);
		for(ArmorStandPacket asp : aspClone){
			if(asp.getObjectID().equals(id)){
				asPackets.remove(id);
			}
		}
	}
	
	public void save(){
		/*Connection con = FurnitureLib.getInstance().getConnection();
		try {
			long objID = SaveObject.writeJavaObject(con, this.asPackets);
			FurnitureLib.getInstance().getConfig().set("Furniture.save.dataID", objID);
			FurnitureLib.getInstance().saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public void load(){
		/*Connection con = FurnitureLib.getInstance().getConnection();
		SaveObject.createTable(con);
		if(FurnitureLib.getInstance().getConfig().isSet("Furniture.save.dataID")){
			long objID = FurnitureLib.getInstance().getConfig().getLong("Furniture.save.dataID");
			try {
				this.asPackets = (List<ArmorStandPacket>) SaveObject.readJavaObject(con, objID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
	}
	
	public void send(ObjectID id){
		if(this.asPackets.isEmpty()){return;}
		for(ArmorStandPacket packet : asPackets){
			if(packet.getObjectID().equals(id)){
				for(Player p : Bukkit.getOnlinePlayers()){
					packet.send(p);
				}
			}
		}
	}
	
	public boolean isArmorStand(Integer entityID){
		if(this.asPackets.isEmpty()){return false;}
		for(ArmorStandPacket asp : this.asPackets){
			if(asp.getID() == entityID) return true;
		}
		return false;
	}
	
	public ArmorStandPacket createArmorStand(ObjectID id, Location loc){
		i++;
		ArmorStandPacket packet = new ArmorStandPacket(loc, i, id);
		this.asPackets.add(packet);
		return packet;
	}
	
	public void setMetadata(ArmorStandPacket packet, boolean Arms, boolean Invisible, boolean BasePlate, boolean Grafiti){
		packet.setArms(Arms);
		packet.setBasePlate(BasePlate);
		packet.setGrafiti(Grafiti);
		packet.setInvisible(Invisible);
	}
	
	public void setName(ArmorStandPacket packet, String Name, boolean Show){
		packet.setName(Name);
		packet.setNameVasibility(Show);
	}
	
	public void setPose(ArmorStandPacket packet, EulerAngle angle, BodyPart part){
		packet.setPose(angle, part);
	}

	public ArmorStandPacket getArmorStandPacketByID(Integer entityID) {
		if(this.asPackets.isEmpty()){return null;}
		for(ArmorStandPacket asp : this.asPackets){
			if(asp.getID() == entityID) return asp;
		}
		return null;
	}

	public ObjectID getObjectIDByID(Integer entityID) {
		if(this.asPackets.isEmpty()){return null;}
		for(ArmorStandPacket asp : this.asPackets){
			if(asp.getID() == entityID) return asp.getObjectID();
		}
		return null;
	}

	public void removeFurniture(Player player) {
		if(this.asPackets.isEmpty()){return;}
		for(ArmorStandPacket packet : asPackets){
			packet.destroy(player);
		}
		
	}

	public void remove(ArmorStandPacket armorStandPacket) {
		if(!this.asPackets.contains(armorStandPacket)) return;
		this.asPackets.remove(armorStandPacket);
	}
}
