package de.Ste3et_C0st.FurnitureLib.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
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
	
	private HashSet<ObjectID> objecte = new HashSet<ObjectID>();
	private List<Project> projects = new ArrayList<Project>();
	private List<UUID> ignoreList = new ArrayList<UUID>();
	
	private HashMap<String, Class<? extends fEntity>> packetClasses = new HashMap<String, Class<? extends fEntity>>(){{
		put(EntityType.ARMOR_STAND.name().toLowerCase(), fArmorStand.class);
		put(EntityType.PIG.name().toLowerCase(), fPig.class);
		put(EntityType.CREEPER.name().toLowerCase(), fCreeper.class);
		put(EntityType.GIANT.name().toLowerCase(), fGiant.class);
	}};
	
	private HashSet<ChunkData> chunkList = new HashSet<ChunkData>();
	
	private static FurnitureManager manager;
	public HashSet<ObjectID> getObjectList(){return this.objecte;}
	public HashSet<ChunkData> getChunkDataList(){return this.chunkList;}
	public List<UUID> getIgnoreList(){return this.ignoreList;}

	public FurnitureManager() {
		manager = this;
	}
	
	public void addProject(Project project){
		if(isExist(project.getName())){
			projects.remove(getProject(project.getName()));
			projects.add(project);
			return;}
		if(!projects.contains(project)){
		projects.add(project);}
	}
	public List<Project> getProjects(){return this.projects;}
	public WrappedDataWatcher watcher=null;
	
	public void addObjectID(ObjectID id){if(!objecte.contains(id)) objecte.add(id);}
	
	public ObjectID getObjBySerial(String serial){
		return getObjectList().stream()
				.filter(obj -> obj.getSerial().equalsIgnoreCase(serial)).findFirst()
				.filter(obj -> !obj.getSQLAction().equals(SQLAction.REMOVE)).orElse(null);
	}
	
	public void saveAsynchron(final CommandSender sender){
		Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), () -> {
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
		});
	}
	
	public void updatePlayerView(Player player) {
		if(this.objecte.isEmpty() || !player.isOnline()){return;}
		objecte.stream().filter(obj -> obj != null && obj.isFinish()).forEach(obj -> obj.updatePlayerView(player));
	}
	
	public void updateFurniture(ObjectID obj) {
		if(this.objecte.isEmpty()){return;}
		if(obj.isFromDatabase()){obj.setSQLAction(SQLAction.UPDATE);}
		obj.update();
	}
	
	public void sendAll(){
		this.objecte.stream().filter(obj -> !obj.getSQLAction().equals(SQLAction.REMOVE)).forEach(obj -> send(obj));
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
		packetList.stream().filter(entity -> entity.getObjID().equals(id)).forEach(entity -> {
			entity.kill();
			entity.delete();
		});
		
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
		return getfArmorStandByID(entityID) != null;
	}
	
	public fArmorStand createArmorStand(ObjectID id, Location loc){
		return (fArmorStand) spawnEntity("armor_stand", loc, id);	
	}
	
	public fPig createPig(ObjectID id, Location loc){
		return (fPig) spawnEntity("pig", loc, id);	
	}
	
	public fGiant createGiant(ObjectID id, Location loc){
		return (fGiant) spawnEntity("giant", loc, id);
	}
	
	public fCreeper createCreeper(ObjectID id, Location loc){
		return (fCreeper) spawnEntity("creeper", loc, id);
	}	
	
	public void addArmorStand(Object obj){
		if(obj instanceof fArmorStand == false){return;}
		fArmorStand stand = (fArmorStand) obj;
		ObjectID id = stand.getObjID();
		if(!objecte.contains(id)){this.objecte.add(id);}
		id.addArmorStand(stand);
	}

	public fEntity getfArmorStandByID(Integer entityID) {
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		return objecte.stream().flatMap(obj -> obj.getPacketList().stream()).filter(e -> entityID.equals(e.getEntityID())).findFirst().orElse(null);
	}
	
	public List<fEntity> getArmorStandFromPassanger(Player p) {
		if(this.objecte.isEmpty()){return null;}
		if(p==null) return null;
		List<fEntity> entityList = objecte.stream().flatMap(obj -> obj.getPacketList().stream()).filter(e -> e.getPassanger().isEmpty() == false && e.getPassanger().contains(p.getEntityId())).collect(Collectors.toList());
		return entityList == null ? new ArrayList<fEntity>() : entityList;
	}
	
	public ObjectID getObjectIDByID(Integer entityID) {
		if(this.objecte.isEmpty()){return null;}
		if(entityID==null) return null;
		fEntity e = getfArmorStandByID(entityID);
		return e != null ? e.getObjID() : null;
	}
	
	public ObjectID getObjectIDByString(String objID){
		ObjectID obj = objecte.stream().filter(objects -> objects.getID().equalsIgnoreCase(objID)).findFirst().orElse(null);
		if(obj !=null) if(obj.getSQLAction().equals(SQLAction.REMOVE)){return null;}
		return obj;
	}

	public void removeFurniture(Player player) {this.objecte.stream().forEach(obj -> obj.removePacket(player));}

	public void remove(fEntity armorStandPacket) {
		if(this.objecte.isEmpty()){return;}
		objecte.stream().filter(obj -> obj.getPacketList().contains(armorStandPacket)).forEach(obj -> {
			obj.getPacketList().remove(armorStandPacket);
		});
	}
	
	public static List<fArmorStand> cloneList(List<fArmorStand> list) {
	    List<fArmorStand> clone = new ArrayList<fArmorStand>(list.size());
	    for(fArmorStand item: list) clone.add(item);
	    return clone;
	}
	
	private boolean isExist(String s){return projects.stream().filter(projects -> projects.getName().equalsIgnoreCase(s)).findFirst().isPresent();}
	
	public List<fEntity> getfArmorStandByObjectID(ObjectID id) {
		if(this.objecte.isEmpty()){return null;}
		return id.getPacketList();
	}
	
	public void deleteObjectID(ObjectID id){
		if(this.objecte.isEmpty() || id == null){return;}
		this.objecte.remove(id);
	}
	
	public Project getProject(String s){
		return projects.stream().filter(projects -> projects.getName().equalsIgnoreCase(s)).findFirst().orElse(null);
	}
	
	public List<ObjectID> getFromPlayer(UUID uuid){
		return this.objecte.stream().filter(obj -> isValid(obj) && obj.getUUID().equals(uuid)).collect(Collectors.toList());
	}
	
	public List<ObjectID> getInChunk(Chunk c){
		return this.objecte.stream().filter(obj -> isValid(obj) && obj.getChunk().equals(c)).collect(Collectors.toList());
	}
	
	public List<ObjectID> getInWorld(World w) {
		return this.objecte.stream().filter(obj -> isValid(obj) && obj.getWorld().equals(w)).collect(Collectors.toList());
	}
	
	public boolean isValid(ObjectID obj) {
		if(obj == null) return false;
		if(obj.getSQLAction().equals(SQLAction.REMOVE)) return false;
		return true;
	}
	
	public static FurnitureManager getInstance() {return manager;}
	
	
	public Class<? extends fEntity> getPacketClass(String str, Class<? extends fEntity> clas){
		try {
			if(clas.getField("type").get(null).toString().equalsIgnoreCase(str)) return clas;
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public fEntity spawnEntity(EntityType type, Location loc, ObjectID obj) {
		return this.spawnEntity(type.name(), loc, obj);
	}
	
	public fEntity createFromType(String str, Location loc, ObjectID obj) {
		return this.spawnEntity(str, loc, obj);
	}
	
	public fEntity spawnEntity(String str, Location loc, ObjectID obj) {
		if(!objecte.contains(obj)){this.objecte.add(obj);}
		Class<? extends fEntity> packetClass = this.packetClasses.getOrDefault(str.toLowerCase(), null);
		if(Objects.nonNull(packetClass)) {
			try {
				fEntity e = packetClass.getConstructor(Location.class, ObjectID.class).newInstance(loc, obj);
				obj.addArmorStand(e);
				return e;
			}catch (Exception e) {
				return null;
			}
		}
		return null;
	}
}
