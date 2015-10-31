package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.Utilitis.EntityID;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public abstract class fEntity {

	private Location location;
	private fInventory inventory;
	private EntityType type;
	private int Eid;
	private String customName = "";
	private Entity passanger;
	private WrappedDataWatcher watcher;
	private LocationUtil lutil;
	private List<Player> loadetPlayer;
	private ProtocolManager manager;
	private PacketContainer container;
	private boolean fire, nameVisible, visible;

	public fEntity(Location location, EntityType type) {
		this.type = type;
		this.location = location;
		this.Eid = EntityID.nextEntityId();
		this.inventory = new fInventory(this.Eid);
		this.watcher = getDefaultWatcher();
		this.lutil = FurnitureLib.getInstance().getLocationUtil();
		this.loadetPlayer = new ArrayList<Player>();
		this.manager = ProtocolLibrary.getProtocolManager();
		create();
	}
	
	@SuppressWarnings("deprecation")
	private void create(){
		this.container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		container.getIntegers()
		.write(0, getEntityID())
		.write(1, (int) type.getTypeId())
		.write(2, FurnitureLib.getInstance().getLocationUtil().getFixedPoint(this.location.getX()))
		.write(3, FurnitureLib.getInstance().getLocationUtil().getFixedPoint(this.location.getY()))
		.write(4, FurnitureLib.getInstance().getLocationUtil().getFixedPoint(this.location.getZ()));
		container.getBytes()
		.write(0, FurnitureLib.getInstance().getLocationUtil().getCompressedAngle(this.location.getYaw()))
		.write(1, FurnitureLib.getInstance().getLocationUtil().getCompressedAngle(this.location.getPitch()));
	}

	private WrappedDataWatcher getDefaultWatcher() {
		Entity entity = getWorld().spawnEntity(
				new Location(getWorld(), 0, 256, 0), type);
		WrappedDataWatcher watcher = WrappedDataWatcher
				.getEntityWatcher(entity).deepClone();
		entity.remove();
		return watcher;
	}

	public int getEntityID() {
		return this.Eid;
	}

	public boolean isFire() {
		return this.fire;
	}

	public boolean isCustomNameVisible() {
		return this.nameVisible;
	}

	public Location getLocation() {
		return this.location;
	}

	public EntityType getEntityType() {
		return this.type;
	}

	public fInventory getEquipment() {
		return this.inventory;
	}
	
	public fInventory getInventory() {
		return this.inventory;
	}

	public ItemStack getBoots() {
		return this.inventory.getBoots();
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
	
	public void setLeggings(ItemStack is) {
		getInventory().setLeggings(is);
	}
	
	public void setChestPlate(ItemStack is) {
		getInventory().setChestPlate(is);
	}
	
	public void setHelmet(ItemStack is) {
		getInventory().setHelmet(is);
	}
	
	public void setBoots(ItemStack is) {
		getInventory().setBoots(is);
	}
	
	public void setItemInHand(ItemStack is) {
		getInventory().setItemInHand(is);
	}

	public ItemStack getItemInHand() {
		return getInventory().getItemInHand();
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

	public World getWorld() {
		return this.getLocation().getWorld();
	}

	public WrappedDataWatcher getHandle() {
		return this.watcher;
	}

	public ProtocolManager getProtocolManager() {
		return this.manager;
	}

	public PacketContainer getPacketContainer() {
		return this.container;
	}

	public Server getServer() {
		return Bukkit.getServer();
	}
	
	public boolean isVisible(){
		return this.visible;
	}

	public void setNameVasibility(boolean b) {
		this.watcher.setObject(3, (byte) (b ? 1 : 0));
		this.nameVisible = b;
	}

	public void teleport(Location location) {
		this.location = location;
		int x = lutil.getFixedPoint(location.getX());
		int y = lutil.getFixedPoint(location.getY());
		int z = lutil.getFixedPoint(location.getZ());
		byte yaw = lutil.getCompressedAngle(location.getYaw());
		byte pitch = lutil.getCompressedAngle(location.getPitch());
		
		PacketContainer c = new PacketContainer(
				PacketType.Play.Server.ENTITY_TELEPORT);
		c.getIntegers().write(0, getEntityID()).write(1, x).write(2, y).write(3, z);
		c.getBytes().write(0, yaw).write(1, pitch);
		for (Player p : this.loadetPlayer) {
			try {
				manager.sendServerPacket(p, c);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void send(Player p) {
		if (this.loadetPlayer.contains(p)){return;}
		if (this.manager == null){return;}
		if (this.container == null){return;}
		try {
			this.container.getDataWatcherModifier().write(0, watcher);
			this.manager.sendServerPacket(p, container);
			this.loadetPlayer.add(p);
			this.sendInventoryPacket(p);
			if (getPassanger() != null) {
				setPassanger(getPassanger());
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		for(Player p : this.loadetPlayer){
			update(p);
		}
	}

	public void update(Player p) {
		PacketContainer update = new PacketContainer(
				PacketType.Play.Server.ENTITY_METADATA);
		update.getIntegers().write(0, getEntityID());
		update.getWatchableCollectionModifier().write(0,
				watcher.getWatchableObjects());
		try {
			this.manager.sendServerPacket(p, update);
			this.sendInventoryPacket(p);
			if (getPassanger() != null) {
				setPassanger(getPassanger());
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void kill(Player p) {
		if (!this.loadetPlayer.contains(p))
			return;
		PacketContainer destroy = new PacketContainer(
				PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegerArrays().write(0, new int[] { getEntityID() });
		try {
			eject();
			this.manager.sendServerPacket(p, destroy);
			if (this.loadetPlayer.contains(p)) {
				this.loadetPlayer.remove(p);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void kill(){
		PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegerArrays().write(0, new int[] {getEntityID()});
		try {
			 eject();
			 for(Player p : this.loadetPlayer){this.manager.sendServerPacket(p, destroy);}
			 this.loadetPlayer.clear();
		} catch (InvocationTargetException e) {e.printStackTrace();}
	}

	public void setFire(boolean b) {
		byte b0 = this.watcher.getByte(0);
		if (b) {
			b0 = (byte) (b0 | 0x01);
			if (Bukkit.getPluginManager().isPluginEnabled("LightAPI"))
				FurnitureLib.getInstance().getLightManager()
						.addLight(getLocation(), 15);
		} else {
			b0 = (byte) (b0 & 0xFFFFFFFE);
			if (Bukkit.getPluginManager().isPluginEnabled("LightAPI"))
				FurnitureLib.getInstance().getLightManager()
						.removeLight(getLocation());
		}
		this.watcher.setObject(0, Byte.valueOf(b0));
		this.fire = b;
	}

	public void setName(String str) {
		if (str == null) {
			return;
		}
		if (str == "") {
			setNameVasibility(false);
			return;
		}
		this.watcher.setObject(2, str);
		this.customName = str;
	}

	public void setPassanger(Entity e) {
		if(!FurnitureLib.getInstance().canSitting()){return;}
		if (e == null) {return;}
		if (passanger != null) {return;}
		int passangerID = e.getEntityId();
		PacketContainer container = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
		container.getIntegers().write(1, passangerID).write(2, getEntityID());
		try {
			for (Player p : this.loadetPlayer) {
				this.manager.sendServerPacket(p, container);
			}
			this.passanger = e;
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	public void eject() {
		if (passanger == null) {
			return;
		}
		PacketContainer container = new PacketContainer(
				PacketType.Play.Server.ATTACH_ENTITY);
		container.getIntegers().write(1, passanger.getEntityId()).write(2, -1);
		try {
			for (Player p : this.loadetPlayer) {
				this.manager.sendServerPacket(p, container);
			}
			this.passanger = null;
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	public void sendInventoryPacket(final Player player) {
		List<PacketContainer> packets = this.inventory.createPackets();
		if (packets.isEmpty())
			return;
		try {
			for (final PacketContainer packet : packets) {
				if (player == null || packet == null || manager == null) {
					return;
				}
				this.manager.sendServerPacket(player, packet);
				Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(),
						new Runnable() {
							@Override
							public void run() {
								try {
									manager.sendServerPacket(player, packet);
								} catch (Exception e) {
								}
							}
						}, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void remove(){
			this.container = null;
			this.manager = null;
			this.inventory = null;
	}
	
	protected void b(int i, boolean flag)
	{
	   byte b0 = getHandle().getByte(0);
	   if (flag) {
		   getHandle().setObject(0, Byte.valueOf((byte)(b0 | 1 << i)));
	   } else {
		   getHandle().setObject(0, Byte.valueOf((byte)(b0 & (1 << i ^ 0xFFFFFFFF))));
	   }
	}
	
	public void setInvisible(boolean b) {
		b(5, b);
		this.visible = b;
	}
}