package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntityID;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract class fEntity extends fSerializer{

	private int a;
	private UUID b = UUID.randomUUID();
	private int c;
	private double d;
	private double e;
	private double f;
	private byte j;
	private byte k;
	private fInventory i;
	private Location l;
	private String customName = "";
	private Entity passanger;
	private boolean fire = false, nameVisible = false, visible = true, isKilled = false, isPlayed = false, glowing = false, invisible = false;

	@SuppressWarnings("deprecation")
	public fEntity(Location loc, EntityType type, ObjectID id) {
		super(loc.getWorld(), type, id);
		this.a = EntityID.nextEntityId();
		this.c = (int) type.getTypeId();
		this.i = new fInventory(this.a);
		setLocation(loc);
		getHandle().getIntegers().write(0, a).write(1, c);
		getHandle().getSpecificModifier(UUID.class).write(0, b);
		getHandle().getDoubles().write(0, d).write(1, e).write(2, f);
		getHandle().getBytes().write(0, j).write(1, k);
	}
	
	public boolean isParticlePlayed(){
		return this.isPlayed;
	}

	public int getEntityID() {
		return this.a;
	}

	public boolean isFire() {
		return this.fire;
	}

	public boolean isCustomNameVisible() {
		return this.nameVisible;
	}

	public Location getLocation() {
		return this.l;
	}

	public fInventory getEquipment() {
		return this.i;
	}
	
	public fInventory getInventory() {
		return this.i;
	}

	public ItemStack getBoots() {
		return getInventory().getBoots();
	}

	public ItemStack getHelmet() {
		return getInventory().getHelmet();
	}

	public ItemStack getChestPlate() {
		return getInventory().getChestPlate();
	}

	public ItemStack getLeggings() {
		return getInventory().getLeggings();
	}
	
	public fEntity setLeggings(ItemStack is) {
		getInventory().setLeggings(is);return this;
	}
	
	public fEntity setChestPlate(ItemStack is) {
		getInventory().setChestPlate(is);return this;
	}
	
	public fEntity setHelmet(ItemStack is) {
		getInventory().setHelmet(is);return this;
	}
	
	public fEntity setBoots(ItemStack is) {
		getInventory().setBoots(is);return this;
	}
	
	public fEntity setItemInMainHand(ItemStack is){
		getInventory().setItemInMainHand(is);return this;
	}
	
	public fEntity setItemInOffHand(ItemStack is){
		getInventory().setItemInOffHand(is);return this;
	}
	
	@Deprecated
	public void setItemInHand(ItemStack is) {
		getInventory().setItemInHand(is);
	}

	@Deprecated
	public ItemStack getItemInHand() {
		return getInventory().getItemInHand();
	}
	
	public ItemStack getItemInMainHand(){
		return getInventory().getItemInMainHand();
	}
	
	public ItemStack getItemInOffHand(){
		return getInventory().getItemInOffHand();
	}

	public String getCustomName() {
		return this.customName;
	}

	public String getName() {
		return getCustomName();
	}

	public Entity getPassanger() {
		return this.passanger;
	}

	public Server getServer() {
		return Bukkit.getServer();
	}
	
	@Deprecated
	public boolean isVisible(){
		return this.visible;
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
	
	public fEntity setInvisible(boolean b) {
		b(5, b);
		this.invisible = b;
		return this;
	}
	
	public fEntity setGlowing(boolean b) {
		b(6, b);
		this.glowing = b;return this;
	}
	
	public fEntity setInventory(fInventory inv) {
		this.i = inv;return this;
	}
	
	public fEntity setNameVasibility(boolean b) {
		setObject(getWatcher(), b, 3);
		this.nameVisible = b;return this;
	}

	private void saveLight(Location loc1, Location loc2){
		if (Bukkit.getPluginManager().isPluginEnabled("LightAPI")){
			FurnitureLib.getInstance().getLightManager().removeLight(loc1);
			if(loc2!=null){
				FurnitureLib.getInstance().getLightManager().addLight(loc2, 15);	
			}
		}
	}
	
	public void teleport(Location loc) {
		if(isFire())saveLight(getLocation(), loc);
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
	
	public void send(Player player) {
		
		if (getManager() == null){return;}
		if (getHandle() == null){return;}
		try {
			getHandle().getDataWatcherModifier().write(0, getWatcher());
			getManager().sendServerPacket(player, getHandle());
			sendInventoryPacket(player);
			if (getPassanger() != null) {
				setPassanger(getPassanger());
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
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
		for(Player p : getObjID().getPlayerList()){
			update(p);
		}
	}

	public void update(Player p) {
		if (!getObjID().getPlayerList().contains(p)){return;}
		PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		update.getIntegers().write(0, getEntityID());
		update.getWatchableCollectionModifier().write(0,getWatcher().getWatchableObjects());
		try {
			getManager().sendServerPacket(p, update);
			this.sendInventoryPacket(p);
			if (getPassanger() != null) {
				setPassanger(getPassanger());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void kill(Player p, boolean b) {
		if (!getObjID().getPlayerList().contains(p))return;
		PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegerArrays().write(0, new int[] {getEntityID()});
		try {
			eject();
			getManager().sendServerPacket(p, destroy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void kill(){
		 for(Player p : getObjID().getPlayerList()){kill(p, false);}
	}

	public fEntity setFire(boolean b) {
		b(0, b);
		if(!b){FurnitureLib.getInstance().getLightManager().addLight(getLocation(), 15);}else{FurnitureLib.getInstance().getLightManager().removeLight(getLocation());}
		this.fire = b;return this;
	}

	public fEntity setName(String str) {
		if (str == null) {
			return this;
		}
		if (str.equalsIgnoreCase("")) {
			setNameVasibility(false);
			return this;
		}
		setObject(getWatcher(), str, 2);
		this.customName = str;return this;
	}
	
	public void setPassanger(Entity e) {
		if(!FurnitureLib.getInstance().canSitting()){return;}
		if (e == null) {return;}
		if (passanger != null) {return;}
		int[] passangerID = {e.getEntityId()};
		PacketContainer container = new PacketContainer(PacketType.Play.Server.MOUNT);
		container.getIntegers().write(0, getEntityID());
		container.getIntegerArrays().write(0, passangerID);
		try {
			this.passanger = e;
			for (Player p : getObjID().getPlayerList()){
				getManager().sendServerPacket(p, container);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
	}
	
	public void setPassanger(final List<Integer> entityIDs){
		if(!FurnitureLib.getInstance().canSitting()){return;}
		if (entityIDs == null) {return;}
		int[] passangerID = new int[entityIDs.size()];
		for(int i = 0; i<entityIDs.size();i++){passangerID[i] = entityIDs.get(i);}
		PacketContainer container = new PacketContainer(PacketType.Play.Server.MOUNT);
		container.getIntegers().write(0, getEntityID());
		container.getIntegerArrays().write(0, passangerID);
		try {
			for (Player p : getObjID().getPlayerList()){
				getManager().sendServerPacket(p, container);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void eject() {
		if (passanger == null) {
			return;
		}
		int[] i = {};
		PacketContainer container = new PacketContainer(PacketType.Play.Server.MOUNT);
		container.getIntegers().write(0, getEntityID());
		container.getIntegerArrays().write(0, i);
		try {
			for (Player p : getObjID().getPlayerList()) {getManager().sendServerPacket(p, container);}
			this.passanger = null;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
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
									getManager().sendServerPacket(player,packet);
								} catch (Exception e) {
								}
							}
						}, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendParticle(Location loc, int particleID, boolean repeat)
	{
		Particle particle = Particle.getById(particleID);
	    PacketContainer container = new PacketContainer(PacketType.Play.Server.WORLD_PARTICLES);
	    container.getParticles().write(0, particle);
	    container.getBooleans().write(0, Boolean.valueOf(true));
	    container.getFloat().write(0, Float.valueOf((float)loc.getX()));
	    container.getFloat().write(1, Float.valueOf((float)loc.getY()));
	    container.getFloat().write(2, Float.valueOf((float)loc.getZ()));
	    
	    if(repeat){
	    	final PacketContainer packet = container.deepClone();
	    	isPlayed = true;
	    	new BukkitRunnable() {
				@Override
				public void run() {
					if(isKilled){isPlayed = false;cancel();return;}
					for (Player p : getObjID().getPlayerList()) {
						try {
							getManager().sendServerPacket(p, packet);
						} catch (Exception e) {e.printStackTrace();}
					}
				}
			}.runTaskTimer(FurnitureLib.getInstance(), 0L, 10L);
	    }else{
	    	if(isKilled) return;
		    for (Player p : getObjID().getPlayerList()) {
				try {
					getManager().sendServerPacket(p, container);
				} catch (Exception e) {e.printStackTrace();}
			}
	    }
    }
	
	protected void b(int i, boolean flag) {
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), 0);
		if (flag) {
			setObject(getWatcher(), Byte.valueOf((byte) (b0 | 1 << i)), 0);
		} else {
			setObject(getWatcher(),
					Byte.valueOf((byte) (b0 & (1 << i ^ 0xFFFFFFFF))), 0);
		}
	}
	
	public void setLocation(Location loc){
		this.l = loc;
		this.d = loc.getX();
		this.e = loc.getY();
		this.f = loc.getZ();
		this.j = ((byte) (int) (loc.getYaw() * 256.0F / 360.0F));
		this.k = ((byte) (int) (loc.getPitch() * 256.0F / 360.0F));
	}
	
	public void delete(){
		FurnitureLib.getInstance().getFurnitureManager().remove(this);
	}
	
	public abstract NBTTagCompound getMetadata();
	
}