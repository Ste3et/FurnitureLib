package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ObjectIdManager {

	//World List<ObjectID>
	private final static HashSet<ObjectID> objectList = new HashSet<ObjectID>();
	private final static Predicate<ObjectID> predicate = objectID -> SQLAction.REMOVE != objectID.getSQLAction();
	//private final HashMap<UUID, Set<ObjectID>> playerSet = new HashMap<UUID, Set<ObjectID>>();
	
	public HashSet<ObjectID> loadWorld(World world) {
		return FurnitureLib.getInstance().getSQLManager().getDatabase().loadWorld(SQLAction.NOTHING, world);
	}
	
	public HashSet<ObjectID> getObjectSet(){
		return objectList;
	}
	
	public ObjectID getObjectID(Location location) {
		return this.getObjectID(location.getWorld(), location.toVector());
	}
	
	public ObjectID getObjectID(World world, Vector vector) {
		return getObjectStreamFromWorld(world).filter(entry -> entry.getStartLocation().toVector().equals(vector)).findFirst().orElse(null);
	}
	
	public ObjectID getObjectID(String worldName, Vector vector) {
		return getObjectStreamFromWorld(worldName).filter(entry -> entry.getStartLocation().toVector().equals(vector)).findFirst().orElse(null);
	}
	
	public ObjectID getObjectID(World world, String string) {
		return getObjectStreamFromWorld(world).filter(entry -> entry.getSerial().equalsIgnoreCase(string)).findFirst().orElse(null);
	}
	
	public ObjectID getObjectID(String worldName, String string) {
		return getObjectStreamFromWorld(worldName).filter(entry -> entry.getSerial().equalsIgnoreCase(string)).findFirst().orElse(null);
	}
	
	public List<fEntity> getObjectIDByPassanger(Player player) {
		Integer entityID = player.getEntityId();
		return getObjectStreamFromWorld(player.getWorld()).flatMap(entry -> entry.getPacketList().stream()).filter(entry -> entry.getPassenger().contains(entityID)).collect(Collectors.toList());
	}
	
	public List<fEntity> getArmorStandFromPassenger(Player p) {
		return getObjectIDByPassanger(p);
	}
	
	public fEntity getByArmorStandID(World world, int entityID) {
		Optional<ObjectID> objectID = getObjectStreamFromWorld(world).filter(entry -> entry.containsEntity(entityID)).findFirst();
		return objectID.isPresent() ? objectID.get().getByID(entityID) : null;
	}
	
	public Stream<ObjectID> getObjectStreamFromWorld(World world){
		return getObjectStreamFromWorld(world.getName());
	}
	
	public Stream<ObjectID> getObjectStreamFromWorld(String worldName){
		return getObjectSet().stream().filter(predicate).filter(entry -> entry.getWorldName().equalsIgnoreCase(worldName));
	}
	
	public List<ObjectID> getInWorld(World world) {
		return getObjectStreamFromWorld(world).collect(Collectors.toList());
	}
	
	public List<ObjectID> getInWorld(String worldName) {
		return getObjectStreamFromWorld(worldName).collect(Collectors.toList());
	}

	public void updatePlayerView(Player player) {
		if(player.isOnline()) {
			getAllExistObjectIDs().filter(entry -> entry.getPlayerList().contains(player) || entry.canSee(player)).forEach(entry -> entry.updatePlayerView(player));
			//this.updateModels(player);
		}
	}
	
	public void sendAllInView(Player player) {
		if(player.isOnline()) {
			String worldName = player.getWorld().getName();
			getAllExistObjectIDs().filter(entry -> entry.getWorldName().equalsIgnoreCase(worldName) && entry.isInRange(player)).forEach(entry -> entry.sendArmorStands(player));
		}
	}
	
	public void removeAllNotInView(Player player) {
		getAllExistObjectIDs().filter(entry -> entry.getPlayerList().contains(player)).forEach(entry -> entry.removeArmorStands(player));
	}
	
	
	public HashSet<ObjectID> getFromPlayer(UUID uuid) {
		return new HashSet<ObjectID>(getAllExistObjectIDs().filter(entry -> entry.getUUID().equals(uuid)).collect(Collectors.toList()));
	}
	
	public ObjectID getObjectIDByString(String objID) {
        return getAllExistObjectIDs().filter(entry -> entry.getID().equalsIgnoreCase(objID)).findFirst().orElse(null);
    }
	
	public HashSet<ObjectID> getInChunk(Chunk c) {
		int x = c.getX();
		int z = c.getZ();
		return new HashSet<ObjectID>(getObjectStreamFromWorld(c.getWorld()).filter(entry -> entry.getBlockX() >> 4 == x && entry.getBlockZ() >> 4 == z).collect(Collectors.toList()));
	}
	
	public HashSet<fEntity> getfArmorStandByObjectID(ObjectID id) {
        return id.getPacketList();
    }
	
	public ObjectID getObjectIDByEntityID(int entityID) {
		return getAllExistObjectIDs().filter(entry -> entry.containsEntity(entityID)).findFirst().orElse(null);
	}
	
	public ObjectID getfArmorStandByID(int entityID) {
		return getObjectIDByEntityID(entityID);
	}
	
	public Stream<ObjectID> getAllExistObjectIDs(){
		return objectList.stream().filter(predicate);
	}
	
	public Stream<ObjectID> getAllObjectIDs(){
		return objectList.stream().filter(Objects::nonNull);
	}
	
	public List<ObjectID> getObjectList(){
		return getAllObjectIDs().collect(Collectors.toList());
	}
	
	public void remove(ObjectID objectID) {
		if(Objects.nonNull(objectID)) {
			objectID.setSQLAction(SQLAction.REMOVE);
			
			if(!objectID.getBlockList().isEmpty()) {
				FurnitureLib.getInstance().getBlockManager().destroy(objectID.getBlockList(), false);
				objectID.getBlockList().clear();
			}
			
			objectID.getPacketList().stream().forEach(fEntity::kill);
			objectID.getPacketList().clear();
		}
	}
	
	public void deleteObjectID(ObjectID id) {
		objectList.remove(id);
	}
	
	public void deleteObjectID(Collection<ObjectID> objCollection) {
		objectList.removeAll(objCollection);
	}
	
	public void remove(fEntity armorStandPacket) {
		ObjectID objectID = armorStandPacket.getObjID();
		objectID.getPacketList().remove(armorStandPacket);
	}
	
	public void removeFurniture(Player player) {
		getAllExistObjectIDs().forEach(entry -> entry.removePacket(player));
	}
	
	public void updateFurniture(ObjectID obj) {
		if (obj.isFromDatabase()) {
            obj.setSQLAction(SQLAction.UPDATE);
        }
        obj.update();
	}
	
	public void addObjectIDs(ObjectID... objArray) {
		for(ObjectID objectID : objArray) {
			this.addObjectID(objectID);
		}
	}
	
	public boolean addObjectID(ObjectID obj) {
        return objectList.add(obj);
    }
	
	public void addObjectID(Collection<ObjectID> objI) {
	    objectList.addAll(objI);
	}
	
	public void send(ObjectID id) {
	   if (Objects.nonNull(id)) {
	       return;
	   }
	   id.sendAll();
	}
	
	public void sendAll() {
        this.getAllExistObjectIDs().forEach(this::send);
    }
	
//	public void updateModels(Player player) {
//		if(Objects.nonNull(player)) {
//			UUID uuid = player.getUniqueId();
//			if (FurnitureManager.getInstance().getIgnoreList().contains(uuid)) return;
//			if(player.isOnline()) {
//				Set<ObjectID> objSet = getAllInRangeByPlayer(player);
//				Set<ObjectID> workingSet = this.playerSet.getOrDefault(uuid, new HashSet<>());
//				Set<ObjectID> removedSend = new HashSet<>();
//				
//				Location loaction = player.getLocation();
//				workingSet.stream().filter(entry -> entry.isInRange(loaction) == false).forEach(entry -> {
//					entry.removeArmorStands(player);
//					removedSend.add(entry);
//				});
//				
//				workingSet.removeAll(removedSend);
//				
//				objSet.stream().filter(entry -> workingSet.contains(entry) == false).forEach(entry -> {
//					entry.sendArmorStands(player);
//					workingSet.add(entry);
//				});
//				
//				this.playerSet.put(uuid, workingSet);
//			}else {
//				this.playerSet.remove(uuid);
//			}
//		}
//	}
//	
//	public void updateModels(Player player, ObjectID objectID) {
//		if(Objects.nonNull(player) && Objects.nonNull(objectID)) {
//			UUID uuid = player.getUniqueId();
//			if(player.isOnline()) {
//				Set<ObjectID> workingSet = this.playerSet.getOrDefault(uuid, new HashSet<>());
//				if(objectID.canSee(player)) {
//					objectID.sendArmorStands(player);
//					
//					workingSet.add(objectID);
//					this.playerSet.put(uuid, workingSet);
//				}else if(workingSet.contains(objectID)) {
//					objectID.removeArmorStands(player);
//					
//					workingSet.remove(objectID);
//					this.playerSet.put(uuid, workingSet);
//				}
//			}
//		}
//	}
	
	public Set<ObjectID> getAllInRangeByPlayer(Player player) {
		return getAllInRangeByLoc(player.getLocation());
	}
	
	public Set<ObjectID> getAllInRangeByLoc(Location location) {
		HashSet<ObjectID> hashSet = new HashSet<ObjectID>();
		if(Objects.isNull(location)) return hashSet;
		World world = location.getWorld();
		if(Objects.isNull(world)) return hashSet;
		getObjectStreamFromWorld(world).filter(entry -> entry.isInRange(location)).forEach(hashSet::add);
		return hashSet;
	}
}
