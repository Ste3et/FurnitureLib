package de.Ste3et_C0st.FurnitureLib.main;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.packet.WrapperPlayServerEntityDestroy;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class FurnitureManager extends ObjectIdManager{

	private static HashMap<String, BiFunction<Location, ObjectID, fEntity>> packetClasses = new HashMap<>();
    private static FurnitureManager manager;
    
    static {
        packetClasses.put("armor_stand", fArmorStand::new);
        packetClasses.put("pig", fPig::new);
        packetClasses.put("creeper", fCreeper::new);
        packetClasses.put("giant", fGiant::new);
    }

    public WrappedDataWatcher watcher = null;
    public HashMap<World, HashMap<EntityType, WrappedDataWatcher>> defaultWatchers = new HashMap<>();
    private HashMap<String, Project> projects = new HashMap<>();
    private static final List<UUID> ignoreList = new ArrayList<>();
    private HashSet<ChunkData> chunkList = new HashSet<>();

    public FurnitureManager() {
        manager = this;
    }

    public static List<fArmorStand> cloneList(List<fArmorStand> list) {
        return new ArrayList<fArmorStand>(list);
    }

    public static FurnitureManager getInstance() {
        return manager;
    }

    public HashSet<ChunkData> getChunkDataList() {
        return this.chunkList;
    }

    public List<UUID> getIgnoreList() {
        return ignoreList;
    }

    public void addProject(Project project) {
    	projects.put(project.getName().toLowerCase(), project);
    }

    public List<Project> getProjects() {
        return new ArrayList<Project>(this.projects.values());
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

//    private boolean isExist(String s) {
//        return projects.containsKey(s.toLowerCase());
//    }

    public Project getProject(String s) {
        return projects.getOrDefault(s.toLowerCase(), null);
    }

    public boolean isValid(ObjectID obj) {
        if (obj == null) return false;
        return SQLAction.REMOVE != obj.getSQLAction();
    }

    public fEntity spawnEntity(EntityType type, Location loc, ObjectID obj) {
        return this.spawnEntity(type.name(), loc, obj);
    }

    public fEntity createFromType(String str, Location loc, ObjectID obj) {
        return this.spawnEntity(str, loc, obj);
    }

    public fEntity spawnEntity(String str, Location loc, ObjectID obj) {
        fEntity entity = readEntity(str, loc, obj);
        if (Objects.nonNull(entity)) obj.addArmorStand(entity);
        return entity;
    }

    public fEntity readEntity(String str, Location loc, ObjectID obj) {
        BiFunction<Location, ObjectID, fEntity> packetClass = packetClasses.get(str.toLowerCase());
        return Objects.nonNull(packetClass) ? packetClass.apply(loc, obj) : null;
    }

    public boolean furnitureAlreadyExistOnBlock(Block block) {
    	int x = block.getX(), y = block.getY(), z = block.getZ();
    	return getInChunk(block.getChunk()).stream().anyMatch(entry -> entry.getBlockX() == x && entry.getBlockY() == y && entry.getBlockZ() == z);
    }

	public void killObject(ObjectID objectID, Player player) {
		WrapperPlayServerEntityDestroy.destroyPackets(objectID, player);
	}
	
	public void killObject(ObjectID objectID) {
		WrapperPlayServerEntityDestroy.destroyPackets(objectID, objectID.getPlayerList());
	}
}
