package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelHandler;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.packet.WrapperPlayServerEntityDestroy;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ObjectID extends ObjectData{

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

    public HashSet<Player> getPlayerList() {
    	HashSet<Player> playerSet = new HashSet<Player>();
    	Bukkit.getOnlinePlayers().stream().filter(entry -> isInWorld(entry)).filter(entry -> isInRange(entry)).forEach(playerSet::add);
        return playerSet;
    }
    
    public HashSet<Player> getPlayerListWorld() {
    	HashSet<Player> playerSet = new HashSet<Player>();
    	Bukkit.getOnlinePlayers().stream().filter(entry -> isInWorld(entry)).forEach(playerSet::add);
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
        this.sendArmorStands(player);
    }
    
    private HashSet<Player> players = new HashSet<>();
    
    public void updatePlayerViewWithRange(Player player, Location location) {
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
        if (!isInWorld(player)) {
			players.remove(player);
            return;
        } else {
            if (isInRange(location)) {
                if (players.contains(player)) {
                    return;
                }
                
                if(this.getFurnitureObjectOpt().isPresent() == false) {
                	FurnitureLib.debug(this.getID() + " have no function object", 100);
                	FurnitureLib.debug("Project: " + this.getProject(), 100);
                }
                
                this.getFurnitureObjectOpt().ifPresent(entry -> {
                	this.sendArmorStands(player);
                	entry.receive(player);
                });
                
            } else {
                if (!players.contains(player)) return;
                this.removeArmorStands(player);
            }
        }
    }
    
    public void sendArmorStands(Player player) {
    	this.packetList.forEach(stand -> stand.send(player));
        players.add(player);
    }
    
    public void removeArmorStands(Player player) {
    	WrapperPlayServerEntityDestroy.destroyPackets(this, player);
        players.remove(player);
    }

    public fEntity getByID(int entityID) {
    	return getPacketList().stream().filter(entry -> entityID == entry.getEntityID()).findFirst().orElse(null);
    }
    
    @Deprecated
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
    
    public void removePacket(Player player) {
        if (isPrivate()) {
            return;
        }
        if (getPacketList().isEmpty()) {
            return;
        }
        if (getSQLAction().equals(SQLAction.REMOVE)) {
            return;
        }
        FurnitureManager.getInstance().killObject(this, player);
        playerSet.remove(player);
    }

    public void addBlock(List<Block> locationList) {
        if (locationList == null || locationList.isEmpty()) return;
        this.addBlockLocations(locationList.stream().map(Block::getLocation).collect(Collectors.toList()));
    }
    
    public void addBlockLocations(List<Location> bl) {
    	if(bl.isEmpty()) return;
    	this.locList.addAll(bl);
    	FurnitureLib.getInstance().getBlockManager().getList().addAll(bl);
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
                this.addBlockLocations(modelschematic.addBlocks(this.getStartLocation(), direction));
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
        FurnitureManager.getInstance().remove(this);
        this.removeAll();
        this.setFurnitureObject(null);
    }

    public void dropItem(Player p, Location loc, Project project) {
        if (FurnitureConfig.getFurnitureConfig().useGamemode() && p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        World w = loc.getWorld();
        w.dropItemNaturally(loc, project.getCraftingFile().getRecipe().getResult());
    }

    public void deleteEffect(HashSet<fEntity> asList) {
        if (getProjectOBJ().isSilent()) {
            return;
        }
        asList.stream().filter(entity -> entity.haveDestroyMaterial()).limit(6).forEach(fEntity::sendParticle);
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
        
        HashSet<Player> actuallyPlayers = getPlayerList();
        HashSet<Player> removePlayers = new HashSet<Player>(this.playerSet.stream().filter(entry -> actuallyPlayers.contains(entry) == false).collect(Collectors.toSet()));
        
        for (Player p : actuallyPlayers) {
            getPacketList().forEach(entity -> entity.update(p));
        }
        
        WrapperPlayServerEntityDestroy.destroyPackets(this, removePlayers);
        
        this.playerSet.addAll(actuallyPlayers);
        this.playerSet.removeAll(removePlayers);
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
        FurnitureManager.getInstance().killObject(this);
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
}
