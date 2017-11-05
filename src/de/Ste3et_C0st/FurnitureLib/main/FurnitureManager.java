package de.Ste3et_C0st.FurnitureLib.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fCreeper;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fGiant;
import de.Ste3et_C0st.FurnitureLib.main.entity.fPig;

public class FurnitureManager {
	private Integer i = 0;
	private HashSet<ObjectID> objecte = new HashSet<ObjectID>();
	private List<Project> projects = new ArrayList<Project>();
	private List<UUID> ignoreList = new ArrayList<UUID>();
	public void setLastID(Integer i){this.i = i;}
	public HashSet<ObjectID> getObjectList(){return this.objecte;}
	public List<Chunk> chunkList = new ArrayList<Chunk>();
	public List<UUID> getIgnoreList(){return this.ignoreList;}
	public void addProject(Project project){
		if(isExist(project.getName())){
			projects.remove(getProject(project.getName()));
			projects.add(project);
			return;}
		if(!projects.contains(project)){
		projects.add(project);}
	}
	public List<Project> getProjects(){return this.projects;}
	public int getLastID() {return i;}
	public HashMap<World, HashMap<EntityType, WrappedDataWatcher>> defaultWatchers = new HashMap<World, HashMap<EntityType, WrappedDataWatcher>>(); 
	public WrappedDataWatcher watcher=null;
	
	public void addObjectID(ObjectID id){if(!objecte.contains(id)) objecte.add(id);}
	
	public WrappedDataWatcher getDefaultWatcher(World w, EntityType type){
		if(defaultWatchers.containsKey(w)){if(defaultWatchers.get(w).containsKey(type)) return defaultWatchers.get(w).get(type).deepClone();}
		WrappedDataWatcher watcher = createNew(w, type);
		if(watcher==null) return null;
		HashMap<EntityType, WrappedDataWatcher> watcherMap = null;
		if(defaultWatchers.containsKey(w)) watcherMap = defaultWatchers.get(w);
		if(watcherMap == null) watcherMap = new HashMap<EntityType, WrappedDataWatcher>();
		watcherMap.put(type, watcher);
		defaultWatchers.put(w, watcherMap);
		return watcher;
	}
	
	private WrappedDataWatcher createNew(World w, EntityType type){
		Entity entity = w.spawnEntity(new Location(w, 0, 256, 0), type);
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
		entity.remove();
		return watcher;
	}
	
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
	
	public void saveAsynchron(final CommandSender sender){
		Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				long currentTime = System.currentTimeMillis();
				sender.sendMessage("§n§7--------------------------------------");
				sender.sendMessage("§7Furniture async saving started");
				FurnitureLib.getInstance().getSQLManager().save();
				long newTime = System.currentTimeMillis();
				long time = newTime - currentTime;
				SimpleDateFormat timeDate = new SimpleDateFormat("mm:ss.SSS");
		    	String timeStr = timeDate.format(time);
				sender.sendMessage("§7Furniture saving finish : §9" + timeStr);
				sender.sendMessage("§n§7--------------------------------------");
			}
		});
	}
	
	public void updatePlayerView(Player player) {
		if(this.objecte.isEmpty()){return;}
		for(ObjectID obj : objecte){obj.updatePlayerView(player);}
	}
	
	public void updateFurniture(ObjectID obj) {
		if(this.objecte.isEmpty()){return;}
		if(obj.isFromDatabase()){obj.setSQLAction(SQLAction.UPDATE);}
		obj.update();
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
		if(!id.getBlockList().isEmpty()){
			FurnitureLib.getInstance().getBlockManager().destroy(id.getBlockList(), false);
			id.getBlockList().clear();
		}
		List<fEntity> packetList = (List<fEntity>) ((ArrayList<fEntity>) id.getPacketList()).clone();
		for(fEntity asp : packetList){
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
		id.sendAll();
	}
	
	public boolean isArmorStand(Integer entityID){
		if(this.objecte.isEmpty()){return false;}
		for(ObjectID obj : objecte){
			for(fEntity packet : obj.getPacketList()){
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
	
	public fPig createPig(ObjectID id, Location loc){
		if(!objecte.contains(id)){this.objecte.add(id);}
		i++;
		fPig packet = new fPig(loc, id);
		id.addArmorStand(packet);
		return packet;		
	}
	
	public fGiant createGiant(ObjectID id, Location loc){
		if(!objecte.contains(id)){this.objecte.add(id);}
		i++;
		fGiant packet = new fGiant(loc, id);
		id.addArmorStand(packet);
		return packet;		
	}
	
	public fCreeper createCreeper(ObjectID id, Location loc){
		if(!objecte.contains(id)){this.objecte.add(id);}
		i++;
		fCreeper packet = new fCreeper(loc, id);
		id.addArmorStand(packet);
		return packet;		
	}	
	
	public void addArmorStand(Object obj){
		if(obj instanceof fArmorStand == false){return;}
		fArmorStand stand = (fArmorStand) obj;
		ObjectID id = stand.getObjID();
		if(!objecte.contains(id)){this.objecte.add(id);}
		i++;
		id.addArmorStand(stand);
	}

	public fEntity getfArmorStandByID(Integer entityID) {
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		for(ObjectID obj : objecte){
			for(fEntity packet : obj.getPacketList()){
				if(packet.getEntityID() == entityID){
					return packet;
				}
			}
		}
		return null;
	}
	
	public ObjectID getObjectIDByID(Integer entityID) {
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		for(ObjectID obj : objecte){
			for(fEntity packet : obj.getPacketList()){
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
		if(obj !=null){
			if(obj.getSQLAction().equals(SQLAction.REMOVE)){return null;}
		}
		return obj;
	}

	public void removeFurniture(Player player) {
		if(this.objecte.isEmpty()){return;}
		for(ObjectID obj : objecte){
			obj.removePacket(player);
		}
	}

	public void remove(fEntity armorStandPacket) {
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
			if(project.getName().equals(s)) return true;
		}
		return false;
	}
	
	public List<fEntity> getfArmorStandByObjectID(ObjectID id) {
		if(this.objecte.isEmpty()){return null;}
		return id.getPacketList();
	}
	
	public void deleteObjectID(ObjectID id){
		if(this.objecte.isEmpty() || id == null){return;}
		this.objecte.remove(id);
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
