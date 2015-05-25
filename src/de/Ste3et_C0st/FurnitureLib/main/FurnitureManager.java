package de.Ste3et_C0st.FurnitureLib.main;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class FurnitureManager {

	private Integer i = 407;
	private HashMap<ObjectID,List<ArmorStandPacket>> furnitureList = new HashMap<ObjectID,List<ArmorStandPacket>>();
	
	public void addFurniture(ObjectID id, List<ArmorStandPacket> asP){
		furnitureList.put(id, asP);
	}

	public void removeFurniture(ObjectID id){
		if(furnitureList.containsKey(id)){
			furnitureList.remove(id);
		}
	}
	
	public void send(ObjectID id, List<Player> pList){
		if(!furnitureList.containsKey(id)){FurnitureLib.getInstance().getLogger().warning("[FurnitureLib] Object not found"); return;}
		for(Player p : pList){
			for(ArmorStandPacket packet : furnitureList.get(id)){
				packet.send(p);
			}
		}
	}
	
	public boolean isArmorStand(Integer entityID){
		if(furnitureList.isEmpty()){return false;}
		for(ObjectID id : furnitureList.keySet()){
			if(furnitureList.get(id)!=null){
				for(ArmorStandPacket packet : furnitureList.get(id)){
					if(packet.getID() == entityID){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void kill(ObjectID id, List<Player> pList){
		
	}
	
	public ArmorStandPacket createArmorStand(Location loc){
		i++;
		ArmorStandPacket packet = new ArmorStandPacket(loc, i);
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
		if(furnitureList.isEmpty()){return null;}
		for(ObjectID id : furnitureList.keySet()){
			if(furnitureList.get(id)!=null){
				for(ArmorStandPacket packet : furnitureList.get(id)){
					if(packet.getID() == entityID){
						return packet;
					}
				}
			}
		}
		return null;
	}

	public ObjectID getObjectIDByID(Integer entityID) {
		if(furnitureList.isEmpty()){return null;}
		for(ObjectID id : furnitureList.keySet()){
			if(furnitureList.get(id)!=null){
				for(ArmorStandPacket packet : furnitureList.get(id)){
					if(packet.getID() == entityID){
						return id;
					}
				}
			}
		}
		return null;
	}
}
