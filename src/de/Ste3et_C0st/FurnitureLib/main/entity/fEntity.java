package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntityID;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class fEntity extends fSerializer {

    /*
     * Field a = EntityID
     * Field b = UUID
     * Field c = EntityTypeID
     * Field i = InventoryObject
     * Field d,e,f = X,Y,Z Coordinates
     * Field j,k = Yaw/Pitch
     */

    public World world;
    private int a;
    private UUID b = UUID.randomUUID();
    private int c;
    private double d, e, f;
    private byte j, k;
    private fInventory i;
    private Location l;
    private String customName = "";
    private boolean fire = false, nameVisible = false, isPlayed = false, glowing = false, invisible = false, gravity = false;
    private List<Integer> passengerIDs = new ArrayList<>();

    public fEntity(Location loc, EntityType type, int entityID, ObjectID id) {
        super(type, id);
        this.a = EntityID.nextEntityId();
        this.c = entityID;
        this.i = new fInventory(this.a);
        getHandle().getModifier().writeDefaults();
        getHandle().getIntegers().write(0, a).write(1, c);
        getHandle().getUUIDs().write(0, b);
        setLocation(loc);
    }

    public abstract Entity toRealEntity();

    public abstract boolean isRealEntity();

    public abstract void setEntity(Entity e);

    public boolean isParticlePlayed() {
        return this.isPlayed;
    }

    public int getEntityID() {
        return this.a;
    }

    public boolean isFire() {
        return this.fire;
    }

    public fEntity setFire(boolean b) {
        setBitMask(b, 0, 0);
        if (!b) {
            FurnitureLib.getInstance().getLightManager().removeLight(getLocation());
        } else {
            FurnitureLib.getInstance().getLightManager().addLight(getLocation(), 15);
        }
        this.fire = b;
        return this;
    }

    public boolean hasGravity() {
        return this.gravity;
    }

    public boolean isCustomNameVisible() {
        return this.nameVisible;
    }

    public Location getLocation() {
        return this.l;
    }

    public void setLocation(Location loc) {
        if (Objects.nonNull(loc)) {
            this.l = loc;
            this.world = l.getWorld();
            this.d = loc.getX();
            this.e = loc.getY();
            this.f = loc.getZ();
            this.j = ((byte) (int) (loc.getYaw() * 256.0F / 360.0F));
            this.k = ((byte) (int) (loc.getPitch() * 256.0F / 360.0F));
            getHandle().getDoubles().write(0, d).write(1, e).write(2, f);
            getHandle().getBytes().write(0, j).write(1, k);
        }
    }

    public fInventory getEquipment() {
        return this.i;
    }

    public fInventory getInventory() {
        return this.i;
    }

    public fEntity setInventory(fInventory inv) {
        this.i = inv;
        return this;
    }

    public ItemStack getBoots() {
        return getInventory().getBoots();
    }

    public fEntity setBoots(ItemStack is) {
        getInventory().setBoots(is);
        return this;
    }

    public ItemStack getHelmet() {
        return getInventory().getHelmet();
    }

    public fEntity setHelmet(ItemStack is) {
        getInventory().setHelmet(is);
        return this;
    }

    public ItemStack getChestPlate() {
        return getInventory().getChestPlate();
    }

    public fEntity setChestPlate(ItemStack is) {
        getInventory().setChestPlate(is);
        return this;
    }

    public ItemStack getLeggings() {
        return getInventory().getLeggings();
    }

    public fEntity setLeggings(ItemStack is) {
        getInventory().setLeggings(is);
        return this;
    }

    public fEntity setGravity(boolean b) {
        this.gravity = b;
        return this;
    }

    public ItemStack getItemInMainHand() {
        return getInventory().getItemInMainHand();
    }

    public fEntity setItemInMainHand(ItemStack is) {
        getInventory().setItemInMainHand(is);
        return this;
    }

    public ItemStack getItemInOffHand() {
        return getInventory().getItemInOffHand();
    }

    public fEntity setItemInOffHand(ItemStack is) {
        getInventory().setItemInOffHand(is);
        return this;
    }

    public String getCustomName() {
        return this.customName;
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

        this.customName = str;
        return this;
    }

    public List<Integer> getPassenger() {
        return this.passengerIDs;
    }

    public void setPassenger(Entity e) {
        setPassenger(Collections.singletonList(e.getEntityId()));
        if (!FurnitureLib.getInstance().isRotateOnSitEnable()) {
            return;
        }
        if (e.getType().equals(EntityType.PLAYER)) {
            PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
            container.getIntegers().write(0, e.getEntityId());
            container.getBytes().write(0, ((byte) (int) (getLocation().getYaw() * 256.0F / 360.0F)));
            try {
                for (Player p : getObjID().getPlayerList()) {
                    getManager().sendServerPacket(p, container);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    ///tp -534 69 13328
    public void setPassenger(Integer EntityID) {
        setPassenger(Arrays.asList(EntityID));
    }

    public void setPassenger(final List<Integer> entityIDs) {
        if (!FurnitureLib.getInstance().canSitting()) {
            return;
        }
        if (entityIDs == null) {
            return;
        }
        int[] passengerID = entityIDs.stream().mapToInt(Integer::intValue).toArray();
        PacketContainer container = new PacketContainer(PacketType.Play.Server.MOUNT);
        container.getIntegers().write(0, getEntityID());
        container.getIntegerArrays().write(0, passengerID);
        getObjID().getPlayerList().forEach(player -> {
            try {
                getManager().sendServerPacket(player, container);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        entityIDs.stream().filter(passenger -> !this.passengerIDs.contains(passenger)).forEach(this.passengerIDs::add);
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public fEntity setInvisible(boolean b) {
        setBitMask(b, 0, 5);
        this.invisible = b;
        return this;
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    public fEntity setGlowing(boolean b) {
        if (!FurnitureLib.getInstance().isGlowing()) b = false;
        setBitMask(b, 0, 6);
        this.glowing = b;
        return this;
    }

    public FurnitureLib getPlugin() {
        return FurnitureLib.getInstance();
    }

    public UUID getUUID() {
        return this.b;
    }

    public void delete() {
        FurnitureLib.getInstance().getFurnitureManager().remove(this);
    }

    public void sendParticle() {
        getObjID().getWorld().playEffect(getLocation(), Effect.STEP_SOUND, getHelmet().getType());
    }

    public World getWorld() {
        return this.world;
    }

    public fEntity setNameVisibility(boolean b) {
        getWatcher().setObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class)), b);
        this.nameVisible = b;
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
        c.getDoubles().write(0, this.d).write(1, this.e).write(2, this.f);
        c.getBytes().write(0, this.j).write(1, this.k);
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
        c.getBytes().write(0, this.j).write(1, this.k);
        for (Player p : getObjID().getPlayerList()) {
            try {
                getManager().sendServerPacket(p, c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Player player) {
        //getHandle().getDataWatcherModifier().write(0, getWatcher());
        try {
            getManager().sendServerPacket(player, getHandle());
            sendInventoryPacket(player);
            if (Objects.nonNull(getPassenger())) setPassenger(getPassenger());
            sendMetadata(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMetadata(Player player) {
        PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        update.getIntegers().write(0, getEntityID());
        update.getWatchableCollectionModifier().write(0, getWatcher().getWatchableObjects());
        try {
            getManager().sendServerPacket(player, update);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* NEED BACKWARDS 1.13(7) -> 1.14(8) */
    public fEntity setHealth(float health) {
        if (health == 0) {
            return this;
        }
        getWatcher().setObject(new WrappedDataWatcherObject(Type.field.getHealth(), Registry.get(Float.class)), health);
        return this;
    }
	
	@Deprecated
	public List<Integer> getPassanger() {
		return this.getPassenger();
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

	public boolean isInvisible(){
		return this.invisible;
	}
	
	public boolean isGlowing(){
		return this.glowing;
	}
	
	public FurnitureLib getPlugin(){
		return FurnitureLib.getInstance();
	}
	
	public UUID getUUID() {
		return this.b;
	}
	
	public void delete(){
		FurnitureLib.getInstance().getFurnitureManager().remove(this);
	}
	
	public void sendParticle() {
		getObjID().getWorld().playEffect(getLocation(), Effect.STEP_SOUND, getHelmet().getType());
	}
	
	public World getWorld(){return this.world;}
	
	
	public fEntity setInvisible(boolean b) {
		setBitMask(b, 0, 5);
		this.invisible = b;
		return this;
	}
	
	public fEntity setGlowing(boolean b) {
		if(!FurnitureLib.getInstance().isGlowing()) b = false;
		setBitMask(b, 0, 6);
		this.glowing = b;
		return this;
	}
	
	public fEntity setInventory(fInventory inv) {
		this.i = inv;return this;
	}
	
	@Deprecated
	public fEntity setNameVasibility(boolean b) {
		return this.setNameVisibility(b);
	}
	
	public fEntity setNameVisibility(boolean b) {
		getWatcher().setObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class)), b);
		this.nameVisible = b;return this;
	}

    public void update(Player p) {
        if (!getObjID().getPlayerList().contains(p)) {
            return;
        }
        PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        update.getIntegers().write(0, getEntityID());
        update.getWatchableCollectionModifier().write(0, getWatcher().getWatchableObjects());
        try {
            getManager().sendServerPacket(p, update);
            this.sendInventoryPacket(p);
            if (getPassenger() != null) {
                setPassenger(getPassenger());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill(Player p, boolean b) {
        if (!getObjID().getPlayerList().contains(p)) return;
        PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntegerArrays().write(0, new int[]{getEntityID()});
        try {
            eject();
            getManager().sendServerPacket(p, destroy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        getObjID().getPlayerList().forEach(p -> {
            kill(p, false);
        });
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
                } catch (InvocationTargetException e) {
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
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        this.passengerIDs.clear();
    }

    public void sendInventoryPacket(final Player player) {
        List<PacketContainer> packets = this.i.createPackets();
        if (packets.isEmpty())
            return;
        try {
            for (final PacketContainer packet : packets) {
                if (player == null || packet == null || getManager() == null) {
                    return;
                }
                getManager().sendServerPacket(player, packet);
                Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(),
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    getManager().sendServerPacket(player, packet);
                                } catch (Exception e) {
                                }
                            }
                        }, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendParticle(Location loc, int particleID, boolean repeat) {
//		Particle particle = Particle.getById(particleID);
//	    PacketContainer container = new PacketContainer(PacketType.Play.Server.WORLD_PARTICLES);
//	    container.getParticles().write(0, particle);
//	    container.getBooleans().write(0, Boolean.valueOf(true));
//	    container.getFloat().write(0, Float.valueOf((float)loc.getX()));
//	    container.getFloat().write(1, Float.valueOf((float)loc.getY()));
//	    container.getFloat().write(2, Float.valueOf((float)loc.getZ()));
//
//	    if(repeat){
//	    	final PacketContainer packet = container.deepClone();
//	    	isPlayed = true;
//	    	new BukkitRunnable() {
//				@Override
//				public void run() {
//					if(isKilled){isPlayed = false;cancel();return;}
//					for (Player p : getObjID().getPlayerList()) {
//						try {
//							getManager().sendServerPacket(p, packet);
//						} catch (Exception e) {e.printStackTrace();}
//					}
//				}
//			}.runTaskTimer(FurnitureLib.getInstance(), 0L, 10L);
//	    }else{
//	    	if(isKilled) return;
//		    for (Player p : getObjID().getPlayerList()) {
//				try {
//					getManager().sendServerPacket(p, container);
//				} catch (Exception e) {e.printStackTrace();}
//			}
//	    }
    }

    public void loadDefMetadata(NBTTagCompound metadata) {
        String name = metadata.getString("Name");
        boolean n = (metadata.getInt("NameVisible") == 1);
        boolean f = (metadata.getInt("Fire") == 1), i = (metadata.getInt("Invisible") == 1);
        boolean g = (metadata.getInt("Glowing") == 1);
        NBTTagCompound inventory = metadata.getCompound("Inventory");
        for (Object object : EnumWrappers.ItemSlot.values()) {
            if (!inventory.getString(object.toString()).equalsIgnoreCase("NONE")) {
                ItemStack is = new CraftItemStack().getItemStack(inventory.getCompound(object.toString() + ""));
                if (is != null) {
                    this.getInventory().setSlot(object.toString(), is);
                }
            }
        }
        this.setNameVisibility(n);
        this.setName(name);
        this.setFire(f);
        this.setGlowing(g);
        this.setInvisible(i);
    }

    public abstract void loadMetadata(NBTTagCompound metadata);

    public abstract fEntity clone();
}