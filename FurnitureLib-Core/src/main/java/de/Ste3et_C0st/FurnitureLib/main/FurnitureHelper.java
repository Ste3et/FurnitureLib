package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class FurnitureHelper {

    private BlockFace b;
    private World w;
    private ObjectID obj;
    private FurnitureManager manager;
    private FurnitureLib lib;
    private LocationUtil lutil;
    private Plugin plugin;

    public FurnitureHelper(ObjectID id) {
        if (id == null) return;
        this.lib = FurnitureLib.getInstance();
        this.lutil = lib.getLocationUtil();
        this.manager = lib.getFurnitureManager();
        this.b = LocationUtil.yawToFace(id.getStartLocation().getYaw());
        this.w = id.getStartLocation().getWorld();
        this.plugin = id.getProjectOBJ().getPlugin();
        this.obj = id;
    }
    
    public fEntity spawnEntity(Location location, EntityType type) {
    	return getManager().spawnEntity(type, location, obj);
    }

    @Deprecated
    public fArmorStand spawnArmorStand(Location loc) {
        return getManager().createArmorStand(getObjID(), loc);
    }

    public Location getLocation() {
        Location loc = obj.getStartLocation().getBlock().getLocation();
        loc.setYaw(obj.getStartLocation().getYaw());
        return loc;
    }

    public BlockFace getBlockFace() {
        return this.b;
    }

    public World getWorld() {
        return this.w;
    }

    public ObjectID getObjID() {
        return this.obj;
    }

    public FurnitureManager getManager() {
        return this.manager;
    }

    public FurnitureLib getLib() {
        return this.lib;
    }

    public LocationUtil getLutil() {
        return this.lutil;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public HashSet<fEntity> getfAsList() {
        return Objects.nonNull(this.getObjID()) ? this.getObjID().getPacketList() : null;
    }

    public HashSet<fEntity> getEntitySet() {
    	return this.getfAsList();
    }
    
    public boolean isFinish() {
        return getObjID().isFinish();
    }

    public float getYaw() {
        return getLutil().FaceToYaw(getBlockFace());
    }

    public Location getRelative(Location loc, BlockFace face, double z, double x) {
        return getLutil().getRelative(loc, b, z, x);
    }

    public Location getRelative(Location loc, double z, double x) {
        return getLutil().getRelative(loc, getBlockFace(), z, x);
    }

    public void destroy(Player p) {
        getObjID().remove(p);
    }

    public void send() {
        getManager().send(obj);
    }

    public void update() {
        getManager().updateFurniture(obj);
    }

    public void delete() {
        this.obj = null;
    }

    public void consumeItem(Player p) {
        if (p.getGameMode().equals(GameMode.CREATIVE) && FurnitureConfig.getFurnitureConfig().useGamemode())
            return;
        ItemStack is = p.getInventory().getItemInMainHand();
        if ((is.getAmount() - 1) <= 0) {
            is.setType(Material.AIR);
        } else {
            is.setAmount(is.getAmount() - 1);
        }

        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), is);
        p.updateInventory();
    }

    public boolean canBuild(Player p, boolean echo) {
        return FurnitureLib.getInstance().canBuild(p, getObjID(), Type.EventType.BREAK, echo);
    }

    public boolean canBuild(Player p) {
        return canBuild(p, true);
    }

    public boolean canInteract(Player p) {
        return canInteract(p, true);
    }

    public boolean canInteract(Player p, boolean echo) {
        return FurnitureLib.getInstance().canBuild(p, getObjID(), Type.EventType.INTERACT, echo);
    }

    public Location getCenter() {
        Location loc = getLutil().getCenter(getLocation());
        loc.setYaw(getLutil().FaceToYaw(getBlockFace()));
        return loc;
    }

    public fEntity entityByCustomName(String str) {
        for (fEntity entity : getfAsList()) {
            if (entity.getCustomName().equalsIgnoreCase(str)) {
                return entity;
            }
        }
        return null;
    }

    public List<fEntity> entitiesByCustomName(String str) {
        return getfAsList().stream().filter(e -> e.getCustomName().equalsIgnoreCase(str)).collect(Collectors.toList());
    }

    public void toggleLight(boolean change) {
    	Collection<fEntity> fEntities = getfAsList().stream()
    										.filter(fEntity::hasCustomName)
    										.filter(entry -> entry.getName().toUpperCase().startsWith("#FIRE:"))
    										.collect(Collectors.toList());
    	if(fEntities.isEmpty() == false) {
    		this.toggleLight(fEntities, change);
    	}
    }
    
    public void toggleLight(Collection<fEntity> fEntityCollection,boolean change) {
    	AtomicBoolean needUpdate = new AtomicBoolean(false);
    	fEntityCollection.forEach(entity -> {
    		if(changeLight(entity, change)) needUpdate.set(true);
    	});
    	
    	if(needUpdate.get()) {
    		update();
    		this.getObjID().setSQLAction(SQLAction.UPDATE);
    	}
    }
    
    private boolean changeLight(fEntity entity, boolean change) {
    	if(entity.getName().contains(":")) {
    		String[] str = entity.getName().split(":");
            String lightBool = str[2];
            if (Objects.nonNull(change) && change) {
                if (lightBool.equalsIgnoreCase("off#")) {
                	entity.setName(entity.getName().replace("off#", "on#"));
                    if (!entity.isFire()) {
                    	entity.setFire(true);
                    	return true;
                    }
                } else if (lightBool.equalsIgnoreCase("on#")) {
                	entity.setName(entity.getName().replace("on#", "off#"));
                    if (entity.isFire()) {
                    	entity.setFire(false);
                    	return true;
                    }
                }
            } else {
                if (lightBool.equalsIgnoreCase("on#")) {
                    if (!entity.isFire()) {
                    	entity.setFire(true);
                    	return true;
                    }
                }
            }
    	}
        return false;
    }
    
    private boolean switchLight(fEntity entity) {
    	if(entity.getName().contains(":")) {
    		String[] str = entity.getName().split(":");
            String lightBool = str[2];
            if (lightBool.equalsIgnoreCase("off#")) {
            	entity.setName(entity.getName().replace("off#", "on#"));
                if (!entity.isFire()) {
                	entity.setFire(true);
                	return true;
                }
            } else if (lightBool.equalsIgnoreCase("on#")) {
            	entity.setName(entity.getName().replace("on#", "off#"));
                if (entity.isFire()) {
                	entity.setFire(false);
                	return true;
                }
            }
    	}
        return false;
    }
    
    public void toggleLight() {
    	final HashSet<fEntity> updateSet = new HashSet<fEntity>();
    	getfAsList().stream()
			.filter(fEntity::hasCustomName)
			.filter(entry -> entry.getName().toUpperCase().startsWith("#LIGHT:"))
			.forEach(fentity -> {
				if(switchLight(fentity)) {
					updateSet.add(fentity);
				}
			}
		);;
		
		if(updateSet.isEmpty() == false) {
			updateSet.stream().forEach(fEntity::update);
    		this.getObjID().setSQLAction(SQLAction.UPDATE);
    	}
    }

    public abstract void onClick(Player player);
    public abstract void onBreak(Player player);
}
