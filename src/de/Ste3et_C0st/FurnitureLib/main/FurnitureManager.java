package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class FurnitureManager {
	private Integer i = 0;
	private List<ArmorStandPacket> asPackets = new ArrayList<ArmorStandPacket>();
	
	private List<ObjectID> objecte = new ArrayList<ObjectID>();
	private List<ObjectID> preLoadet = new ArrayList<ObjectID>();
	private List<ObjectID> removeList = new ArrayList<ObjectID>();
	private List<ObjectID> updateList = new ArrayList<ObjectID>();
	private List<Project> projects = new ArrayList<Project>();
	
	public void setLastID(Integer i){this.i = i;}
	public List<ArmorStandPacket> getAsList(){return this.asPackets;}
	public List<ObjectID> getPreLoadetList(){return this.preLoadet;}
	public List<ObjectID> getObjectList(){return this.objecte;}
	public List<ObjectID> getUpdateList(){return this.updateList;}
	public List<ObjectID> getRemoveList(){return this.removeList;}
	
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
		preLoadet.remove(obj);
		updateList.add(obj);
		for(ArmorStandPacket packet : asPackets){
			if(packet.getObjectId().equals(obj)){
				for(Player player : Bukkit.getOnlinePlayers()){
					if(packet.isInRange(player)){
						packet.update(player);
					}
				}
			}
		}
	}
	
	public void sendAll(){
		for(ObjectID objID : objecte){
			send(objID);
		}
	}

	@SuppressWarnings("unchecked")
	public void remove(ObjectID id){
		if(this.asPackets.isEmpty()){return;}
		removeList.add(id);
		List<ArmorStandPacket> aspClone = ((List<ArmorStandPacket>) ((ArrayList<ArmorStandPacket>) asPackets).clone());
		Collections.copy(asPackets, aspClone);
		for(ArmorStandPacket asp : aspClone){
			if(asp.getObjectId().equals(id)){
				asp.destroy();
				asp.delete();
			}
		}
		if(getPreLoadetList().contains(id)) getPreLoadetList().remove(id);
		FurnitureLib.getInstance().getLimitationManager().remove(id.getStartLocation(), getProject(id.getProject()));
		updateList.remove(id);
		objecte.remove(id);
		
	}
	
	public void send(ObjectID id){
		if(this.asPackets.isEmpty()){return;}
		if(id==null){System.out.println("OBJID not found");return;}
		for(ArmorStandPacket packet : asPackets){
			if(packet.getObjectId().equals(id)){
				for(Player p : Bukkit.getOnlinePlayers()){
					packet.send(p);
				}
			}
		}
	}
	
	public void send(ArmorStandPacket asp){
		if(this.asPackets.isEmpty()){return;}
			for(Player p : Bukkit.getOnlinePlayers()){
				asp.send(p);
			}
	}
	
	public boolean isArmorStand(Integer entityID){
		if(this.asPackets.isEmpty()){return false;}
		for(ArmorStandPacket asp : this.asPackets){
			if(asp.getEntityId() == entityID) return true;
		}
		return false;
	}
	
	public ArmorStandPacket createArmorStand(ObjectID id, Location loc){
		i++;
		ArmorStandPacket packet = new ArmorStandPacket(loc, id, i);
		if(!objecte.contains(id)){
			this.objecte.add(id);
		}
		this.asPackets.add(packet);
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
		if(this.asPackets.isEmpty()){return null;}
		if(entityID==null) return null;
		for(ArmorStandPacket asp : this.asPackets){
			if(asp==null) continue;
			if(asp.getEntityId() == entityID) return asp;
		}
		return null;
	}
	
	public List<ArmorStandPacket> getArmorStandPacketByObjectID(ObjectID id) {
		List<ArmorStandPacket> aspList = new ArrayList<ArmorStandPacket>();
		if(this.asPackets.isEmpty()){return null;}
		for(ArmorStandPacket asp : this.asPackets){
			if(asp.getObjectId().equals(id)) aspList.add(asp);
		}
		return aspList;
	}

	public ObjectID getObjectIDByID(Integer entityID) {
		if(this.asPackets.isEmpty()){return null;}
		for(ArmorStandPacket asp : this.asPackets){
			if(asp.getEntityId() == entityID) return asp.getObjectId();
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
	
	public static List<ArmorStandPacket> cloneList(List<ArmorStandPacket> list) {
	    List<ArmorStandPacket> clone = new ArrayList<ArmorStandPacket>(list.size());
	    for(ArmorStandPacket item: list) clone.add(item);
	    return clone;
	}
	
	public void addProject(Project project){
		if(isExist(project.getName())){
			//pl.getLogger().info("Project: " + project.getName() + " from PL " + project.getPlugin() + " found");
			return;
		}
		if(!projects.contains(project)){
			//pl.getLogger().info("new Project: " + project.getName() + " from PL " + project.getPlugin() + " registred");
			projects.add(project);
		}
	}
	
	private boolean isExist(String s){
		for(Project project : projects){
			if(project.getName().equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public List<Project> getProjects(){
		return this.projects;
	}
	
	public Project getProject(String name){
		for(Project pro : projects){
			if(pro.getName().equalsIgnoreCase(name)){
				return pro;
			}
		}
		return null;
	}
	
	public int getLastID() {
		return i;
	}
}
