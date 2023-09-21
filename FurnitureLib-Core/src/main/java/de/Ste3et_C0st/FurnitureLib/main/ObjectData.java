package de.Ste3et_C0st.FurnitureLib.main;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DoubleKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ObjectData {

	protected Furniture functionObject = null;
	protected static int viewRange = 10;
	protected String ObjectID, serial, Project, plugin, worldName;
	protected HashSet<Location> locList = new HashSet<Location>();
	protected DoubleKey<Integer> chunkKey = null;
	protected Location loc;
    protected UUID uuid;
    protected HashSet<UUID> uuidList = new HashSet<UUID>();
    protected DefaultKey<PublicMode> publicMode = new DefaultKey<Type.PublicMode>(FurnitureConfig.getFurnitureConfig().getDefaultPublicType());
    protected DefaultKey<EventType> memberType = new DefaultKey<Type.EventType>(FurnitureConfig.getFurnitureConfig().getDefaultEventType());
    protected SQLAction sqlAction = SQLAction.SAVE;
    protected HashSet<fEntity> packetList = new HashSet<fEntity>();
   
    //private HashSet<Player> players = new HashSet<>();
    
    protected BlockFace placedFace = BlockFace.NORTH;
    protected int chunkX, chunkZ;
    protected boolean finish = false, fixed = false, fromDatabase = false, Private = false;
    protected final HashSet<Player> playerSet = new HashSet<Player>();
	
    
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
    
    public boolean hasProjectOBJ() {
    	return Objects.nonNull(this.getProjectOBJ());
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
    
    public DoubleKey<Integer> getChunkKey(){
    	if(Objects.isNull(this.chunkKey)) this.chunkKey = DoubleKey.of(chunkX, chunkZ);
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
    
    public Optional<fEntity> getEntityByVector(Vector vector){
    	return this.packetList.stream().filter(entry -> entry.getLocation().toVector().equals(vector)).findFirst();
    }

	public boolean isInChunk(int chunkX, int chunkZ) {
		return this.chunkX == chunkX && this.chunkZ == chunkZ;
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
    
    public Optional<Furniture> getFurnitureObjectOpt() {
    	return Optional.ofNullable(this.functionObject);
    }
}
