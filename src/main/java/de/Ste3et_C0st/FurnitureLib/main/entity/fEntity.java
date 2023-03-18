package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntityID;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SkullMetaPatcher;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.function.BiFunction;

public abstract class fEntity extends fSerializer implements Cloneable {

    /*
     * Field a = EntityID
     * Field b = UUID
     * Field c = EntityTypeID
     * Field i = InventoryObject
     * Field d,e,f = X,Y,Z Coordinates
     * Field j,k = Yaw/Pitch
     */

    private final int entityID = EntityID.nextEntityId();
    private final UUID entityUUID = UUID.randomUUID();
    private final PacketContainer mountPacketContainer = new PacketContainer(PacketType.Play.Server.MOUNT);
    private final PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
    private static final BiFunction<PacketContainer, List<WrappedWatchableObject>, Void> metadataFunction;
    
    @Deprecated
    private int entityTypeID;
    private double positionX, positionY, positionZ;
    private byte yaw, pitch;
    
    private Location location;
    private DefaultKey<String> customName = new DefaultKey<String>("");
    
    protected DefaultKey<Boolean> fire = new DefaultKey<Boolean>(false), nameVisible = new DefaultKey<Boolean>(false), isPlayed = new DefaultKey<Boolean>(false);
    protected DefaultKey<Boolean> glowing = new DefaultKey<Boolean>(false), invisible = new DefaultKey<Boolean>(false), gravity = new DefaultKey<Boolean>(false);
    
    private List<Integer> passengerIDs = new ArrayList<>();
    
    static {
    	if(MinecraftVersion.getCurrentVersion().isAtLeast(new MinecraftVersion("1.19.3"))) {
    		metadataFunction = (container, list) -> {
        		final List<com.comphenix.protocol.wrappers.WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
        		list.forEach(entry -> {
        			final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
              		wrappedDataValueList.add(new com.comphenix.protocol.wrappers.WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
        		});
        		container.getDataValueCollectionModifier().write(0, wrappedDataValueList);
				return null;
        	};
    	}else {
    		metadataFunction = (container, list) -> {
        		container.getWatchableCollectionModifier().write(0, list);
				return null;
        	};
    	}
    }
    
    public fEntity(Location loc, EntityType type, int entityID, ObjectID id) {
        super(type, id);
        this.entityTypeID = entityID;
        getHandle().getModifier().writeDefaults();
        getHandle().getIntegers().write(0, this.entityID).write(1, entityTypeID);
        if(FurnitureLib.getVersionInt() > 18) getHandle().getEntityTypeModifier().writeSafely(0, type);
        getHandle().getUUIDs().write(0, this.entityUUID);
        this.mountPacketContainer.getIntegers().write(0, this.entityID);
        setLocation(loc);
    }

    public abstract Entity toRealEntity();
    public abstract boolean isRealEntity();
    public abstract fEntity clone();
    public abstract void copyMetadata(fEntity entity);
    public abstract void setEntity(Entity e);

    public boolean isParticlePlayed() {
        return this.isPlayed.getOrDefault();
    }

    public int getEntityID() {
        return this.entityID;
    }

    public boolean isFire() {
        return this.fire.getOrDefault();
    }

    public fEntity setFire(boolean fire) {
        setBitMask(fire, 0, 0);
        if (!fire) {
            FurnitureLib.getInstance().getLightManager().removeLight(getLocation());
        } else {
            FurnitureLib.getInstance().getLightManager().addLight(getLocation(), 15);
        }
        this.fire.setValue(Boolean.valueOf(fire));
        return this;
    }

    public boolean hasGravity() {
        return this.gravity.getOrDefault();
    }
    
    public fEntity setNameVasibility(boolean b) {
		return this.setNameVisibility(b);
	}

    public boolean isCustomNameVisible() {
        return this.nameVisible.getOrDefault();
    }
    
    public boolean hasCustomName() {
    	if(Objects.isNull(getCustomName())) return false;
    	return getCustomName().isEmpty() == false;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location loc) {
        if (Objects.nonNull(loc)) {
            this.location = loc;
            this.positionX = loc.getX();
            this.positionY = loc.getY();
            this.positionZ = loc.getZ();
            this.yaw = ((byte) (int) (loc.getYaw() * 256.0F / 360.0F));
            this.pitch = ((byte) (int) (loc.getPitch() * 256.0F / 360.0F));
            getHandle().getDoubles().write(0, this.positionX).write(1, this.positionY).write(2, this.positionZ);
            //pitch is unused
            getHandle().getBytes().write(0, this.pitch).write(1, this.yaw);
        }
    }

    public fEntity setGravity(boolean gravity) {
        this.gravity.setValue(Boolean.valueOf(gravity));
        return this;
    }

    public String getCustomName() {
        return this.customName.getOrDefault();
    }

    public fEntity setCustomName(String str) {
        return setName(str);
    }

    public String getName() {
        return getCustomName();
    }

    public fEntity setName(String str) {
        if (str == null) return this;
        if (str.equalsIgnoreCase("")) setNameVisibility(false);
        if (FurnitureLib.isNewVersion()) {
            getWatcher().setObject(new WrappedDataWatcherObject(2, Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromText(str).getHandle()));
        } else {
            getWatcher().setObject(new WrappedDataWatcherObject(2, Registry.get(String.class)), str);
        }

        this.customName.setValue(str);
        return this;
    }

    public List<Integer> getPassenger() {
        return this.passengerIDs;
    }

    public void setPassenger(Entity e) {
        setPassenger(Collections.singletonList(e.getEntityId()));
        if (!FurnitureConfig.getFurnitureConfig().isRotateOnSitEnable()) {
            return;
        }
        if (e.getType().equals(EntityType.PLAYER)) {
            PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
            container.getIntegers().write(0, e.getEntityId());
            container.getBytes().write(0, ((byte) (int) (getLocation().getYaw() * 256.0F / 360.0F)));
            try {
                for (Player p : getObjID().getPlayerList()) {
                    getManager().sendServerPacket(p, container);
                    getManager().sendServerPacket(p, this.mountPacketContainer);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setPassenger(Integer EntityID) {
        setPassenger(Arrays.asList(EntityID));
    }

    public void setPassenger(final List<Integer> entityIDs) {
        if (!FurnitureConfig.getFurnitureConfig().canSitting()) {
            return;
        }
        if (entityIDs == null) {return;}
        this.passengerIDs.addAll(entityIDs);
        int[] passengerID = this.passengerIDs.stream().mapToInt(Integer::intValue).toArray();
        this.mountPacketContainer.getIntegerArrays().write(0, passengerID);
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public boolean isInvisible() {
        return this.invisible.getOrDefault();
    }

    public fEntity setInvisible(boolean invisible) {
        setBitMask(invisible, 0, 5);
        this.invisible.setValue(Boolean.valueOf(invisible));
        return this;
    }

    public Boolean isGlowing() {
        return this.glowing.getOrDefault();
    }

    public fEntity setGlowing(boolean glowing) {
        if (!FurnitureConfig.getFurnitureConfig().isGlowing()) glowing = false;
        setBitMask(glowing, 0, 6);
        this.glowing.setValue(Boolean.valueOf(glowing));
        return this;
    }

    public FurnitureLib getPlugin() {
        return FurnitureLib.getInstance();
    }

    public UUID getUUID() {
        return this.entityUUID;
    }

    @Deprecated
    public void delete() {
        FurnitureLib.getInstance().getFurnitureManager().remove(this);
    }

    public void sendParticle() {
    	Material material = getDestroyMaterial();
        getObjID().getWorld().playEffect(getLocation(), Effect.STEP_SOUND, material);
    }

    public fEntity setNameVisibility(boolean nameVisibility) {
        getWatcher().setObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class)), nameVisibility);
        this.nameVisible.setValue(Boolean.valueOf(nameVisibility));
        return this;
    }

    private void saveLight(Location loc1, Location loc2) {
        if (Bukkit.getPluginManager().isPluginEnabled("LightAPI")) {
            FurnitureLib.getInstance().getLightManager().removeLight(loc1);
            if (loc2 != null) {
                FurnitureLib.getInstance().getLightManager().addLight(loc2, 15);
            }
        }
    }

    public void teleport(Location loc) {
        if (isFire()) saveLight(getLocation(), loc);
        setLocation(loc);
        PacketContainer c = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        c.getIntegers().write(0, getEntityID());
        c.getDoubles().write(0, this.positionX).write(1, this.positionY).write(2, this.positionZ);
        c.getBytes().write(0, this.yaw).write(1, this.pitch);
        for (Player p : getObjID().getPlayerList()) {
            try {
                getManager().sendServerPacket(p, c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void rotate(float yaw, float pitch) {
        Location loc = getLocation();
        loc.setYaw(loc.getYaw() + yaw);
        loc.setPitch(loc.getPitch() + pitch);
        setLocation(loc);
        PacketContainer c = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        c.getIntegers().write(0, getEntityID());
        c.getBytes().write(0, this.yaw).write(1, this.pitch);
        for (Player p : getObjID().getPlayerList()) {
            try {
                getManager().sendServerPacket(p, c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Player player) {
        try {
            getManager().sendServerPacket(player, getHandle());
            this.sendMetadata(player);
            
            if(getPassenger().isEmpty() == false) {
            	   getManager().sendServerPacket(player, this.mountPacketContainer);
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMetadata(Player player) {
        PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        update.getIntegers().write(0, getEntityID());
        
        metadataFunction.apply(update, getWatcher().getWatchableObjects());
        
        try {
            getManager().sendServerPacket(player, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public fEntity setHealth(float health) {
        if (health == 0) {
            return this;
        }
        getWatcher().setObject(new WrappedDataWatcherObject(Type.field.getHealth(), Registry.get(Float.class)), health);
        return this;
    }

    public void send(Player[] player) {
        for (Player p : player) {
            send(p);
        }
    }

    public void send(List<Player> player) {
        for (Player p : player) {
            send(p);
        }
    }

    public void update() {
        for (Player p : getObjID().getPlayerList()) {
            update(p);
        }
    }

    public void update(Player p) {
       if (!getObjID().getPlayerList().contains(p)) {
            return;
       }
       
       try {
    	   sendMetadata(p);
           if(getPassenger().isEmpty() == false) {
           	  getManager().sendServerPacket(p, mountPacketContainer);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill(Player p, boolean b) {
        this.destroy.getIntegerArrays().writeSafely(0, new int[]{getEntityID()});
        if(FurnitureLib.getVersionInt() > 16) {
        	this.destroy.getIntegers().writeSafely(0, getEntityID());
            this.destroy.getIntLists().writeSafely(0, Arrays.asList(getEntityID()));
        }
        try {
            getManager().sendServerPacket(p, destroy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        getObjID().getPlayerListWorld().forEach(p -> {
            kill(p, false);
        });
        this.eject();
    }

    public fEntity setParticleColor(int i) {
        getWatcher().setObject(new WrappedDataWatcherObject(8, Registry.get(Integer.class)), i);
        return this;
    }

    public void eject(Integer i) {
        if (this.passengerIDs == null || this.passengerIDs.isEmpty()) return;
        if (removePassenger(i)) {
            int[] passengerID = this.passengerIDs.stream().mapToInt(Integer::intValue).toArray();
            PacketContainer container = new PacketContainer(PacketType.Play.Server.MOUNT);
            container.getIntegers().write(0, getEntityID());
            container.getIntegerArrays().write(0, passengerID);
            getObjID().getPlayerList().forEach(player -> {
                try {
                    getManager().sendServerPacket(player, container);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public boolean removePassenger(Integer i) {
        if (this.passengerIDs == null || this.passengerIDs.isEmpty()) return false;
        return this.passengerIDs.remove(i);
    }

    public void eject() {
        if (this.passengerIDs == null || this.passengerIDs.isEmpty()) return;
        int[] i = {};
        PacketContainer container = new PacketContainer(PacketType.Play.Server.MOUNT);
        container.getIntegers().write(0, getEntityID());
        container.getIntegerArrays().write(0, i);
        getObjID().getPlayerList().forEach(player -> {
            try {
            	
                getManager().sendServerPacket(player, container);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.passengerIDs.clear();
    }

    @Deprecated
    public void sendParticle(Location loc, int particleID, boolean repeat) {}

    public void loadMetadata(NBTTagCompound metadata) {
        this.setNameVisibility((metadata.getInt("NameVisible") == 1));
        this.setName(metadata.getString("Name"));
        this.setFire((metadata.getInt("Fire") == 1));
        this.setGlowing((metadata.getInt("Glowing") == 1));
        this.setInvisible((metadata.getInt("Invisible") == 1));
    }
    
    public NBTTagCompound getMetaData() {
    	setMetadata("EntityType", this.getEntityType().toString());
        if(!this.customName.isDefault()) setMetadata("Name", this.getName());
        if(!this.fire.isDefault()) setMetadata("Fire", this.isFire());
        if(!this.invisible.isDefault()) setMetadata("Invisible", this.isInvisible());
        if(!this.nameVisible.isDefault()) setMetadata("NameVisible", this.isCustomNameVisible());
        if(!this.glowing.isDefault()) setMetadata("Glowing", this.isGlowing());
        setMetadata(this.getLocation());
        return this.getNBTField();
    }
    
    protected abstract Material getDestroyMaterial();
    
    public boolean haveDestroyMaterial() {
    	final Material material = getDestroyMaterial();
    	return material != null && material.isBlock();
    }
}