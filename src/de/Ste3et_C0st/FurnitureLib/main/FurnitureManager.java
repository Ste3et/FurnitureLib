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
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public class FurnitureManager {
	private Integer i = 0;
	private List<ObjectID> objecte = new ArrayList<ObjectID>();
	private List<Project> projects = new ArrayList<Project>();
	
	public void setLastID(Integer i){this.i = i;}
	public List<ObjectID> getObjectList(){return this.objecte;}
	public void addProject(Project project){
		if(isExist(project.getName())){
			getProject(project.getName()).setPlugin(project.getPlugin());
			getProject(project.getName()).setClass(project.getclass());
			return;}
		if(!projects.contains(project)){
		projects.add(project);}
	}
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
				for(fArmorStand packet :obj.getPacketList()){
					packet.send(player);
				}
			}else{
				for(fArmorStand packet :obj.getPacketList()){
					packet.kill(player);
				}
			}
		}
	}
	
	public void updateFurniture(ObjectID obj) {
		if(this.objecte.isEmpty()){return;}
		if(obj.isFromDatabase()){obj.setSQLAction(SQLAction.UPDATE);}
		for(fArmorStand packet : obj.getPacketList()){
			if(packet.getObjID().equals(obj)){
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
		List<fArmorStand> packetList = (List<fArmorStand>) ((ArrayList<fArmorStand>) id.getPacketList()).clone();
		for(fArmorStand asp : packetList){
			if(asp.getObjID().equals(id)){
				asp.kill();
				asp.delete();
			}
		}
		
		if(!id.getBlockList().isEmpty()){
			FurnitureLib.getInstance().getBlockManager().destroy(id.getBlockList(), false);
			id.getBlockList().clear();
		}
	}
	
	public void send(ObjectID id){
		if(this.objecte.isEmpty()){return;}
		if(id==null){return;}
		for(fArmorStand packet : id.getPacketList()){
			if(packet.getObjID().equals(id)){
				for(Player p : Bukkit.getOnlinePlayers()){
					packet.send(p);
				}
			}
		}
	}
	
	public boolean isArmorStand(Integer entityID){
		if(this.objecte.isEmpty()){return false;}
		for(ObjectID obj : objecte){
			for(fArmorStand packet : obj.getPacketList()){
				if(packet.getEntityID() == entityID) return true;
			}
		}
		return false;
	}
	
	public fArmorStand createArmorStand(ObjectID id, Location loc){
		if(!objecte.contains(id)){this.objecte.add(id);}
		i++;
		fArmorStand packet = new fArmorStand(loc, id);
		id.addArmorStand(packet);
		return packet;
	}

	public void setName(fArmorStand packet, String Name, boolean Show){
		packet.setName(Name);
		packet.setNameVasibility(Show);
	}
	
	public void setPose(fArmorStand packet, EulerAngle angle, BodyPart part){
		packet.setPose(angle, part);
	}

	public fArmorStand getfArmorStandByID(Integer entityID) {
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		for(ObjectID obj : objecte){
			for(fArmorStand packet : obj.getPacketList()){
				if(packet.getEntityID() == entityID){
					return packet;
				}
			}
		}
		return null;
	}
	
	public List<fArmorStand> getfArmorStandByObjectID(ObjectID id) {
		if(this.objecte.isEmpty()){return null;}
		return id.getPacketList();
	}

	public ObjectID getObjectIDByID(Integer entityID) {
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		for(ObjectID obj : objecte){
			for(fArmorStand packet : obj.getPacketList()){
				if(packet.getEntityID() == entityID){
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
			for(fArmorStand packet : obj.getPacketList()){
				packet.kill(player);
			}
		}
	}

	public void remove(fArmorStand armorStandPacket) {
		if(this.objecte.isEmpty()){return;}
		for(ObjectID obj : objecte){
			if(obj.getPacketList().contains(armorStandPacket)){
				obj.getPacketList().remove(armorStandPacket);
			}
		}
	}
	
	public static List<fArmorStand> cloneList(List<fArmorStand> list) {
	    List<fArmorStand> clone = new ArrayList<fArmorStand>(list.size());
	    for(fArmorStand item: list) clone.add(item);
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
