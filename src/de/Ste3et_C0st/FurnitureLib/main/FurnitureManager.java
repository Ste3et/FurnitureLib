package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class FurnitureManager {
	private Integer i = 0;
	private List<ObjectID> objecte = new ArrayList<ObjectID>();
	private List<Project> projects = new ArrayList<Project>();
	
	public void setLastID(Integer i){this.i = i;}
	public List<ObjectID> getObjectList(){return this.objecte;}
	public void addProject(Project project){if(isExist(project.getName())){return;}if(!projects.contains(project)){projects.add(project);}}
	public List<Project> getProjects(){return this.projects;}
	public int getLastID() {return i;}
	
	public ObjectID getObjBySerial(String serial){
		for(ObjectID obj : getObjectList()){
			if(obj.getSerial().equalsIgnoreCase(serial)){
				if(!obj.getSQLAction().equals(SQLAction.REMOVE)){
					return obj;
				}
			}
		}
		return null;
	}
	
	public void updatePlayerView(Player player) {
		if(this.objecte.isEmpty()){return;}
		for(ObjectID obj : objecte){
			if(obj.isInRange(player)){
				for(ArmorStandPacket packet :obj.getPacketList()){
					packet.send(player);
				}
			}else{
				for(ArmorStandPacket packet :obj.getPacketList()){
					packet.destroy(player);
				}
			}
		}
	}
	
	public void updateFurniture(ObjectID obj) {
		if(this.objecte.isEmpty()){return;}
		if(obj.isFromDatabase()){obj.setSQLAction(SQLAction.UPDATE);}
		for(ArmorStandPacket packet : obj.getPacketList()){
			if(packet.getObjectId().equals(obj)){
				for(Player player : Bukkit.getOnlinePlayers()){
					if(obj.isInRange(player)){
						packet.update(player);
					}
				}
			}
		}
	}
	
	public void sendAll(){
		for(ObjectID objID : objecte){
			if(!objID.getSQLAction().equals(SQLAction.REMOVE)){
				send(objID);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void remove(ObjectID id){
		if(this.objecte.isEmpty()){return;}
		id.setSQLAction(SQLAction.REMOVE);
		List<ArmorStandPacket> packetList = (List<ArmorStandPacket>) ((ArrayList<ArmorStandPacket>) id.getPacketList()).clone();
		for(ArmorStandPacket asp : packetList){
			if(asp.getObjectId().equals(id)){
				asp.destroy();
				asp.delete();
			}
		}
	}
	
	public void send(ObjectID id){
		if(this.objecte.isEmpty()){return;}
		if(id==null){System.out.println("OBJID not found");return;}
		for(ArmorStandPacket packet : id.getPacketList()){
			if(packet.getObjectId().equals(id)){
				for(Player p : Bukkit.getOnlinePlayers()){
					packet.send(p);
				}
			}
		}
	}
	
	public boolean isArmorStand(Integer entityID){
		if(this.objecte.isEmpty()){return false;}
		for(ObjectID obj : objecte){
			for(ArmorStandPacket packet : obj.getPacketList()){
				if(packet.getEntityId() == entityID) return true;
			}
		}
		return false;
	}
	
	public ArmorStandPacket createArmorStand(ObjectID id, Location loc){
		if(!objecte.contains(id)){this.objecte.add(id);}
		i++;
		ArmorStandPacket packet = new ArmorStandPacket(loc, id, i);
		id.addArmorStand(packet);
		return packet;
	}
	
	public void setMetadata(ArmorStandPacket packet, boolean Arms, boolean Invisible, boolean BasePlate, boolean Gravity){
		packet.setArms(Arms);
		packet.setBasePlate(BasePlate);
		packet.setGravity(Gravity);
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
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		for(ObjectID obj : objecte){
			for(ArmorStandPacket packet : obj.getPacketList()){
				if(packet.getEntityId() == entityID){
					return packet;
				}
			}
		}
		return null;
	}
	
	public List<ArmorStandPacket> getArmorStandPacketByObjectID(ObjectID id) {
		if(this.objecte.isEmpty()){return null;}
		return id.getPacketList();
	}

	public ObjectID getObjectIDByID(Integer entityID) {
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		for(ObjectID obj : objecte){
			for(ArmorStandPacket packet : obj.getPacketList()){
				if(packet.getEntityId() == entityID){
					return obj;
				}
			}
		}
		return null;
	}
	
	public ObjectID getObjectIDByString(String objID){
		ObjectID obj = null;
		for(ObjectID objects : objecte){
			if(objects.getID().equalsIgnoreCase(objID)){
				obj = objects;
				break;
			}
		}
		if(obj.getSQLAction().equals(SQLAction.REMOVE)){return null;}
		return obj;
	}

	public void removeFurniture(Player player) {
		if(this.objecte.isEmpty()){return;}
		for(ObjectID obj : objecte){
			for(ArmorStandPacket packet : obj.getPacketList()){
				packet.destroy(player);
			}
		}
	}

	public void remove(ArmorStandPacket armorStandPacket) {
		if(this.objecte.isEmpty()){return;}
		for(ObjectID obj : objecte){
			if(obj.getPacketList().contains(armorStandPacket)){
				obj.getPacketList().remove(armorStandPacket);
			}
		}
	}
	
	public static List<ArmorStandPacket> cloneList(List<ArmorStandPacket> list) {
	    List<ArmorStandPacket> clone = new ArrayList<ArmorStandPacket>(list.size());
	    for(ArmorStandPacket item: list) clone.add(item);
	    return clone;
	}
	
	private boolean isExist(String s){
		for(Project project : projects){
			if(project.getName().equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public Project getProject(String name){
		for(Project pro : projects){
			if(pro.getName().equalsIgnoreCase(name)){
				return pro;
			}
		}
		return null;
	}
	
	
}
