package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelHandler;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DoubleKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class ObjectID {

    private Furniture functionObject = null;
    private static int viewRange = 10;
    private String ObjectID, serial, Project, plugin, worldName;
    private HashSet<Location> locList = new HashSet<Location>();
    private DoubleKey<Integer> chunkKey = null;
    private Location loc;
    private UUID uuid;
    private HashSet<UUID> uuidList = new HashSet<UUID>();
    private DefaultKey<PublicMode> publicMode = new DefaultKey<Type.PublicMode>(FurnitureLib.getInstance().getDefaultPublicType());
    private DefaultKey<EventType> memberType = new DefaultKey<Type.EventType>(FurnitureLib.getInstance().getDefaultEventType());
    private SQLAction sqlAction = SQLAction.SAVE;
    private HashSet<fEntity> packetList = new HashSet<fEntity>();
   
    //private HashSet<Player> players = new HashSet<>();
    
    private BlockFace placedFace = BlockFace.NORTH;
    private int chunkX, chunkZ;
    private boolean finish = false, fixed = false, fromDatabase = false, Private = false;

    public ObjectID(String name, String plugin, Location startLocation) {
        try {
            this.Project = name;
            this.plugin = plugin;
            this.serial = RandomStringGenerator.generateRandomString(10, RandomStringGenerator.Mode.ALPHANUMERIC);
            this.ObjectID = name + ":" + this.serial + ":" + plugin;
            if (Objects.nonNull(startLocation)) setStartLocation(startLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObjectID(String name) {
    	this.setID(name);
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public void setWorldName(String worldName) {
    	this.worldName = worldName;
    }

    public String getID() {
        return this.ObjectID;
    }

    public void setID(String s) {
        this.ObjectID = s;
        try {
            if (s.contains(":")) {
                String[] l = s.split(":");
                this.Project = l[0];
                this.serial = l[1];
                this.plugin = l[2];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProject() {
        return this.Project;
    }

    public Project getProjectOBJ() {
        return FurnitureManager.getInstance().getProject(this.Project);
    }

    public String getPlugin() {
        return this.plugin;
    }

    public String getSerial() {
        return this.serial;
    }

    public Location getStartLocation() {
        return this.loc;
    }
    
    public BlockFace getBlockFace() {
    	return this.placedFace;
    }

    public void setStartLocation(Location loc) {
        this.loc = loc;
        this.worldName = loc.getWorld().getName();
        this.chunkX = loc.getBlockX() >> 4;
        this.chunkZ = loc.getBlockZ() >> 4;
        this.chunkKey = new DoubleKey<Integer>(chunkX, chunkZ);
        this.placedFace = LocationUtil.yawToFace(loc.getYaw());
    }

    public EventType getEventType() {
        return this.memberType.getOrDefault();
    }

    public SQLAction getSQLAction() {
        return this.sqlAction;
    }

    public void setSQLAction(SQLAction action) {
        this.sqlAction = action;
    }

    public boolean isFixed() {
        return this.fixed;
    }

    public void setFixed(boolean b) {
        fixed = b;
    }

    public boolean isFinish() {
        return this.finish;
    }

    public void setFinish() {
        this.finish = true;
    }

    public void setEventTypeAccess(EventType type) {
        this.memberType.setValue(type);
    }

    public HashSet<UUID> getMemberList() {
        return this.uuidList;
    }

    public void setMemberList(HashSet<UUID> uuidList) {
    	this.uuidList.addAll(uuidList);
    }

    public PublicMode getPublicMode() {
        return this.publicMode.getOrDefault();
    }

    public void setPublicMode(PublicMode publicMode) {
        this.publicMode.setValue(publicMode);
    }
    
    public boolean hasPublicMode() {
    	return !this.publicMode.isDefault();
    }
    
    public boolean hasEventType() {
    	return !this.memberType.isDefault();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public World getWorld() {
    	if(Objects.isNull(this.loc)) return null;
        return this.loc.getWorld();
    }

    public Chunk getChunk() {
        return this.loc.getChunk();
    }
    
    public DoubleKey<Integer> getChunkKey(){
    	if(Objects.isNull(this.chunkKey)) this.chunkKey = new DoubleKey<Integer>(getStartLocation().getBlockX() >> 4, getStartLocation().getBlockZ() >> 4);
    	return chunkKey;
    }
    
    public int getBlockX() {
    	return getStartLocation().getBlockX();
    }
    
    public int getBlockY() {
    	return getStartLocation().getBlockY();
    }
    
    public int getBlockZ() {
    	return getStartLocation().getBlockZ();
    }

    public HashSet<Player> getPlayerList() {
    	HashSet<Player> playerSet = new HashSet<Player>();
    	Bukkit.getOnlinePlayers().stream().filter(entry -> isInWorld(entry)).filter(entry -> isInRange(entry)).forEach(playerSet::add);
        return playerSet;
    }

    public boolean isMember(UUID uuid) {
        return uuidList.contains(uuid);
    }

    public boolean isFromDatabase() {
        return this.fromDatabase;
    }

    public void setFromDatabase(boolean b) {
        this.fromDatabase = b;
    }

    public boolean isPrivate() {
        return this.Private;
    }

    public void setPrivate(boolean b) {
        this.Private = b;
    }

    public void addMember(UUID uuid) {
        uuidList.add(uuid);
    }

    public void remMember(UUID uuid) {
        uuidList.remove(uuid);
    }

    public HashSet<fEntity> getPacketList() {
        return packetList;
    }

    public void setPacketList(HashSet<fEntity> packetList) {
        this.packetList = packetList;
    }
    
    public boolean containsEntity(int entityID) {
    	return Objects.nonNull(getEntity(entityID));
    }
    
    public fEntity getEntity(int entityID) {
    	return this.packetList.stream().filter(entry -> entry.getEntityID() == entityID).findFirst().orElse(null);
    }

    public boolean isInRange(Player player) {
        return isInRange(player.getLocation());
    }
    
    public boolean isInRange(Location location) {
    	return distanceSquared(location) <= viewRange;
    }
    
    private int distanceSquared(Location location) {
    	int x = location.getBlockX() >> 4;
        int z = location.getBlockZ() >> 4;
    	return distanceSquared(x, z);
    }
    
    private int distanceSquared(int chunkX, int chunkZ) {
    	return square(this.chunkX - chunkX) + square(this.chunkZ - chunkZ);
    }
    
    private static int square(int num) {
        return num * num;
    }
    
    public boolean canSee(Player player) {
    	return isInWorld(player) ? isInRange(player) : false;
    }

    public boolean isInWorld(Player player) {
    	if(Objects.isNull(getWorld())) return false;
    	if(Objects.isNull(player.getWorld())) return false;
        return getWorldName().equalsIgnoreCase(player.getWorld().getName());
    }

    public void addEntity(fEntity packet) {
        packetList.add(packet);
    }

    public void addEntities(Collection<? extends fEntity> collection) {
        packetList.addAll(collection);
    }

    public void addArmorStand(fEntity packet) {
        packetList.add(packet);
    }

    public void updatePlayerView(Player player) {
        if (FurnitureManager.getInstance().getIgnoreList().contains(player.getUniqueId())) {
            return;
        }
        if (isPrivate()) {
            return;
        }
        if (getPacketList().isEmpty()) {
            return;
        }
        if (getSQLAction().equals(SQLAction.REMOVE)) {
            return;
        }
        
        if(isInWorld(player) == false) {
        	return;
        }
        
//        if(isInRange(player) == false) {
//        	return;
//        }
        
        this.sendArmorStands(player);
        
        // player.sendMessage(getID());
        // if(!isFinish()) return;
//        if (!isInWorld(player)) {
//			players.remove(player);
//            return;
//        } else {
//            if (isInRange(player)) {
//                if (players.contains(player)) {
//                    return;
//                }
//                this.sendArmorStands(player);
//            } else {
//                if (!players.contains(player))
//                    return;
//                removeArmorStands(player);
//            }
//        }
    }
    
    public void sendArmorStands(Player player) {
    	this.packetList.forEach(stand -> stand.send(player));
        //players.add(player);
    }
    
    public void removeArmorStands(Player player) {
    	this.packetList.forEach(stand -> stand.kill(player, false));
        //players.remove(player);
    }

    public fEntity getByID(int entityID) {
    	return getPacketList().stream().filter(entry -> entityID == entry.getEntityID()).findFirst().orElse(null);
    }
    
    
    public void sendAllInView() {
//        if (isPrivate())
//            return;
//        if (getPacketList().isEmpty())
//            return;
//        if (getSQLAction().equals(SQLAction.REMOVE))
//            return;
//        getWorld().getPlayers().forEach(player -> {
//            if (isInRange(player)) {
//                if (!players.contains(player)) {
//                    players.add(player);
//                    this.packetList.forEach(stand -> stand.send(player));
//                }
//            } else {
//                if (players.contains(player)) {
//                    players.remove(player);
//                    this.packetList.forEach(stand -> stand.kill(player, false));
//                }
//            }
//        });
    }
    
    public static void setRange(int chunkRange) {
    	viewRange = chunkRange;
    }
    
    public void setFurnitureObject(Furniture furniture) {
    	this.functionObject = furniture;
    }
    
    public Furniture getFurnitureObject() {
    	return this.functionObject;
    }
    
    public void removePacket(Player p) {
        if (isPrivate()) {
            return;
        }
        if (getPacketList().isEmpty()) {
            return;
        }
        if (getSQLAction().equals(SQLAction.REMOVE)) {
            return;
        }
        this.packetList.forEach(stand -> stand.kill(p, false));
        //players.remove(p);
    }

    public void addBlock(List<Block> bl) {
        if (bl == null || bl.isEmpty()) {
            return;
        }
        for (Block b : bl) {
            if (Objects.nonNull(b)) {
                FurnitureLib.getInstance().getBlockManager().addBlock(b);
                this.locList.add(b.getLocation());
            }
        }
    }
    
    public void addBlockLocations(List<Location> bl) {
    	if(bl.isEmpty()) return;
    	this.locList.addAll(bl);
    }

    public void addBlock(Location loc) {
        FurnitureLib.getInstance().getBlockManager().addBlock(loc);
        this.locList.add(loc);
    }

    public void loadBlocks() {
        if (getBlockList().isEmpty()) {
            if (Objects.isNull(getProjectOBJ())) return;
            ModelHandler modelschematic = getProjectOBJ().getModelschematic();
            if (Objects.nonNull(modelschematic)) {
                BlockFace direction = LocationUtil.yawToFace(this.getStartLocation().getYaw()).getOppositeFace();
                this.addBlock(modelschematic.addBlocks(this.getStartLocation(), direction));
            }
        }
    }
    
    public void registerBlocks() {
    	if (getBlockList().isEmpty()) {
            if (Objects.isNull(getProjectOBJ())) return;
            ModelHandler modelschematic = getProjectOBJ().getModelschematic();
            if (Objects.nonNull(modelschematic)) {
                BlockFace direction = LocationUtil.yawToFace(this.getStartLocation().getYaw()).getOppositeFace();
                List<Location> locList = modelschematic.getBlockLocations(this.getStartLocation(), direction);
                this.addBlockLocations(locList);
            }
        }
    	FurnitureLib.getInstance().getBlockManager().getList().addAll(locList);
    }

    public void remove(Player p) {
        remove(p, true, true);
    }

    public void remove() {
        remove(null, false, false);
    }

    public void remove(boolean effect) {
        remove(null, false, effect);
    }

    public void remove(Player p, boolean dropItem, boolean deleteEffect) {
        this.packetList.stream().filter(fEntity::isFire).forEach(entity -> entity.setFire(false));
        if (p != null)
            if (dropItem)
                dropItem(p, loc.clone().add(0, 1, 0), getProjectOBJ());
        if (deleteEffect)
            deleteEffect(packetList);
        removeAll();
        FurnitureManager.getInstance().remove(this);
        this.setFurnitureObject(null);
    }

    public void dropItem(Player p, Location loc, Project project) {
        if (FurnitureLib.getInstance().useGamemode() && p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        World w = loc.getWorld();
        w.dropItemNaturally(loc, project.getCraftingFile().getRecipe().getResult());
    }

    public void deleteEffect(HashSet<fEntity> asList) {
        if (getProjectOBJ().isSilent()) {
            return;
        }
        asList.stream().filter(entity -> entity.getHelmet() != null && entity.getHelmet().getType().isBlock()).limit(6)
                .forEach(fEntity::sendParticle);
    }

    public String getPlayerName() {
        String name = "§cUNKNOWN";
        if (uuid != null) {
            Optional<DiceOfflinePlayer> player = FurnitureLib.getInstance().getPlayerCache().getPlayer(getUUID());
            if(player.isPresent()) name = player.get().getName();
        }
        return name;
    }

    public HashSet<Location> getBlockList() {
        return this.locList;
    }
    
    public boolean containsBlock(Location location) {
    	return Objects.nonNull(getPresetLocation(location));
    }
    
    public Location getPresetLocation(Location location) {
    	Predicate<Location> predicate = entry -> 
    		entry.getWorld().getName().equals(location.getWorld().getName()) && 
    			location.getBlockX() == entry.getBlockX() && 
    			location.getBlockY() == entry.getBlockY() && 
    			location.getBlockZ() == entry.getBlockZ();
    	return this.locList.stream().filter(predicate).findFirst().orElse(null);
    }

    public void send(Player p) {
        updatePlayerView(p);
    }
    
    public void send(Collection<Player> player) {
    	player.stream().forEach(this::send);
    }

    public void sendAll() {
        for (Player p : Bukkit.getOnlinePlayers())
            send(p);
    }

    public void update() {
        if (isPrivate()) {
            return;
        }
        if (getPacketList().isEmpty()) {
            return;
        }
        if (getSQLAction().equals(SQLAction.REMOVE)) {
            return;
        }
        for (Player p : getPlayerList())
            getPacketList().forEach(entity -> entity.update(p));
    }

    public void removeAll() {
        if (isPrivate()) {
            return;
        }
        if (getPacketList().isEmpty()) {
            return;
        }
        if (getSQLAction().equals(SQLAction.REMOVE)) {
            return;
        }
        for (Player p : getPlayerList()) getPacketList().forEach(entity -> entity.kill(p, false));
        //this.players.clear();
    }

    @Override
    public String toString() {
        return this.getID();
    }
    
    @Deprecated
    public void setFunctionObject(Object obj) {
        if(Furniture.class.isInstance(obj)) {
        	this.functionObject = Furniture.class.cast(obj);
        }
    }
    
    public Optional<fEntity> getEntityByVector(Vector vector){
    	return this.packetList.stream().filter(entry -> entry.getLocation().toVector().equals(vector)).findFirst();
    }

	public boolean isInChunk(int chunkX, int chunkZ) {
		return this.chunkX == chunkX && this.chunkZ == chunkZ;
	}

}
