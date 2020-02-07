package de.Ste3et_C0st.FurnitureLib.main;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FurnitureManager {

    private static HashMap<String, Class<? extends fEntity>> packetClasses = new HashMap<>();
    private static FurnitureManager manager;

    static {
        packetClasses.put("armor_stand", fArmorStand.class);
        packetClasses.put("pig", fPig.class);
        packetClasses.put("creeper", fCreeper.class);
        packetClasses.put("giant", fGiant.class);
    }

    public WrappedDataWatcher watcher = null;
    public HashMap<World, HashMap<EntityType, WrappedDataWatcher>> defaultWatchers = new HashMap<>();
    private HashSet<ObjectID> objecte = new HashSet<>();
    private List<Project> projects = new ArrayList<>();
    private List<UUID> ignoreList = new ArrayList<>();
    private HashSet<ChunkData> chunkList = new HashSet<>();

    public FurnitureManager() {
        manager = this;
    }

    public static List<fArmorStand> cloneList(List<fArmorStand> list) {
        List<fArmorStand> clone = new ArrayList<>(list.size());
		clone.addAll(list);
        return clone;
    }

    public static FurnitureManager getInstance() {
        return manager;
    }

    public HashSet<ObjectID> getObjectList() {
        return this.objecte;
    }

    public HashSet<ChunkData> getChunkDataList() {
        return this.chunkList;
    }

    public List<UUID> getIgnoreList() {
        return this.ignoreList;
    }

    public void addProject(Project project) {
        if (isExist(project.getName())) {
            projects.remove(getProject(project.getName()));
            projects.add(project);
            return;
        }
        if (!projects.contains(project)) {
            projects.add(project);
        }
    }

    public List<Project> getProjects() {
        return this.projects;
    }

    public ObjectID getObjBySerial(String serial) {
        return getObjectList().stream()
                .filter(obj -> obj.getSerial().equalsIgnoreCase(serial)).findFirst()
                .filter(obj -> !obj.getSQLAction().equals(SQLAction.REMOVE)).orElse(null);
    }

    public void saveAsynchron(final CommandSender sender) {
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
        if (this.objecte.isEmpty() || !player.isOnline()) {
            return;
        }
        try {
			for (ObjectID obj : objecte) {
				if (Objects.nonNull(obj) && obj.isFinish()) {
					obj.updatePlayerView(player);
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WrappedDataWatcher getDefaultWatcher(World w, EntityType type) {
        if (defaultWatchers.containsKey(w)) {
            if (defaultWatchers.get(w).containsKey(type)) return defaultWatchers.get(w).get(type).deepClone();
        }
        WrappedDataWatcher watcher = createNew(w, type);
        if (watcher == null) return null;
        HashMap<EntityType, WrappedDataWatcher> watcherMap = null;
        if (defaultWatchers.containsKey(w)) watcherMap = defaultWatchers.get(w);
        if (watcherMap == null) watcherMap = new HashMap<EntityType, WrappedDataWatcher>();
        watcherMap.put(type, watcher);
        defaultWatchers.put(w, watcherMap);
        return watcher;
    }

    private WrappedDataWatcher createNew(World w, EntityType type) {
        Entity entity = w.spawnEntity(new Location(w, 0, 256, 0), type);
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
        entity.remove();
        return watcher;
    }

    public void updateFurniture(ObjectID obj) {
        if (this.objecte.isEmpty()) {
            return;
        }
        if (obj.isFromDatabase()) {
            obj.setSQLAction(SQLAction.UPDATE);
        }
        obj.update();
    }

    public void sendAll() {
        this.objecte.stream().filter(obj -> !obj.getSQLAction().equals(SQLAction.REMOVE)).forEach(this::send);
    }

    @SuppressWarnings("unchecked")
    public void remove(ObjectID id) {
        if (this.objecte.isEmpty()) {
            return;
        }
        id.setSQLAction(SQLAction.REMOVE);
        if (!id.getBlockList().isEmpty()) {
            FurnitureLib.getInstance().getBlockManager().destroy(id.getBlockList(), false);
            id.getBlockList().clear();
        }
        List<fEntity> packetList = (List<fEntity>) ((ArrayList<fEntity>) id.getPacketList()).clone();
        packetList.stream().filter(entity -> entity.getObjID().equals(id)).forEach(entity -> {
            entity.kill();
            entity.delete();
        });

        if (!id.getBlockList().isEmpty()) {
            FurnitureLib.getInstance().getBlockManager().destroy(id.getBlockList(), false);
            id.getBlockList().clear();
        }
    }

    public void send(ObjectID id) {
        if (this.objecte.isEmpty()) {
            return;
        }
        if (id == null) {
            return;
        }
        id.sendAll();
    }

    public boolean isArmorStand(Integer entityID) {
        return getfArmorStandByID(entityID) != null;
    }

    public fArmorStand createArmorStand(ObjectID id, Location loc) {
        return (fArmorStand) spawnEntity("armor_stand", loc, id);
    }

    public fPig createPig(ObjectID id, Location loc) {
        return (fPig) spawnEntity("pig", loc, id);
    }

    public fGiant createGiant(ObjectID id, Location loc) {
        return (fGiant) spawnEntity("giant", loc, id);
    }

    public fCreeper createCreeper(ObjectID id, Location loc) {
        return (fCreeper) spawnEntity("creeper", loc, id);
    }

    public void addArmorStand(Object obj) {
        if (!(obj instanceof fArmorStand)) {
            return;
        }
        fArmorStand stand = (fArmorStand) obj;
        ObjectID id = stand.getObjID();
        //if(!objecte.contains(id)){this.objecte.add(id);}
        id.addArmorStand(stand);
    }

    public fEntity getfArmorStandByID(Integer entityID) {
        if (this.objecte.isEmpty()) {
            return null;
        }
        if (entityID == null) return null;
        return objecte.stream().flatMap(obj -> obj.getPacketList().stream()).filter(e -> entityID.equals(e.getEntityID())).findFirst().orElse(null);
    }

    public List<fEntity> getArmorStandFromPassenger(Player p) {
        if (this.objecte.isEmpty()) {
            return null;
        }
        if (p == null) return null;
        List<fEntity> entityList = objecte.stream().flatMap(obj -> obj.getPacketList().stream()).filter(e -> !e.getPassenger().isEmpty() && e.getPassenger().contains(p.getEntityId())).collect(Collectors.toList());
        return entityList == null ? new ArrayList<fEntity>() : entityList;
    }

    public ObjectID getObjectIDByEntityID(Integer entityID) {
        if (this.objecte.isEmpty()) {
            return null;
        }
        if (entityID == null) return null;
        fEntity e = getfArmorStandByID(entityID);
        return e != null ? e.getObjID() : null;
    }

    public ObjectID getObjectIDByString(String objID) {
        ObjectID obj = objecte.stream().filter(objects -> objects.getID().equalsIgnoreCase(objID)).findFirst().orElse(null);
        if (obj != null) if (obj.getSQLAction().equals(SQLAction.REMOVE)) {
            return null;
        }
        return obj;
    }

    public void removeFurniture(Player player) {
        this.objecte.forEach(obj -> obj.removePacket(player));
    }

    public void remove(fEntity armorStandPacket) {
        if (this.objecte.isEmpty()) {
            return;
        }
        objecte.stream().filter(obj -> obj.getPacketList().contains(armorStandPacket)).forEach(obj -> {
            obj.getPacketList().remove(armorStandPacket);
        });
    }

    private boolean isExist(String s) {
        return projects.stream().anyMatch(projects -> projects.getName().equalsIgnoreCase(s));
    }

    public List<fEntity> getfArmorStandByObjectID(ObjectID id) {
        if (this.objecte.isEmpty()) {
            return null;
        }
        return id.getPacketList();
    }

    public void deleteObjectID(ObjectID id) {
        if (this.objecte.isEmpty() || id == null) {
            return;
        }
        this.objecte.remove(id);
    }

    public Project getProject(String s) {
        return projects.stream().filter(projects -> projects.getName().equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    public List<ObjectID> getFromPlayer(UUID uuid) {
        return this.objecte.stream().filter(obj -> isValid(obj) && obj.getUUID().equals(uuid)).collect(Collectors.toList());
    }

    public List<ObjectID> getInChunk(Chunk c) {
        return this.objecte.stream().filter(obj -> isValid(obj) && obj.getChunk().equals(c)).collect(Collectors.toList());
    }

    public List<ObjectID> getInWorld(World w) {
        return this.objecte.stream().filter(obj -> isValid(obj) && obj.getWorld().equals(w)).collect(Collectors.toList());
    }

    public boolean isValid(ObjectID obj) {
        if (obj == null) return false;
        return !obj.getSQLAction().equals(SQLAction.REMOVE);
    }

    public Class<? extends fEntity> getPacketClass(String str, Class<? extends fEntity> clas) {
        try {
            if (clas.getField("type").get(null).toString().equalsIgnoreCase(str)) return clas;
        } catch (Exception e) {
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
        fEntity entity = readEntity(str, loc, obj);
        if (Objects.nonNull(entity)) {
            obj.addArmorStand(entity);
        }
        return entity;
    }

    public fEntity readEntity(String str, Location loc, ObjectID obj) {
        Class<? extends fEntity> packetClass = packetClasses.getOrDefault(str.toLowerCase(), null);
        if (Objects.nonNull(packetClass)) {
            try {
                fEntity e = packetClass.getConstructor(Location.class, ObjectID.class).newInstance(loc, obj);
                return e;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public boolean addObjectID(ObjectID obj) {
        if (!objecte.contains(obj)) {
            this.objecte.add(obj);
            return true;
        }
        return false;
    }

    public void addObjectIDs(ObjectID... obj) {
        for (ObjectID o : obj) addObjectID(o);
    }

    public void addObjectID(Iterable<ObjectID> objI) {
        objI.forEach(this::addObjectID);
    }

    public boolean furnitureAlreadyExistOnBlock(Block block) {
        return getInChunk(block.getChunk()).stream().anyMatch(entry -> entry.getStartLocation().getBlock().equals(block));
    }
}
