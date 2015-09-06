package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.Utilitis.EntityID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class ArmorStandPacket{
	private int ID;
	private int ArmorID;
	private ObjectID objID;
	private Location location;
	private HashMap<BodyPart, EulerAngle> angle = new HashMap<Type.BodyPart, EulerAngle>();
	private boolean mini, invisible, arms, basePlate, gravity, customname, fire, marker;
	private WrappedDataWatcher watcher;
	private PacketContainer container;
	private ArmorStandInventory inventory;
	private String name = "";
	private ProtocolManager manager;
	private List<Player> loadedPlayers = new ArrayList<Player>();
	private Entity pessanger;

	public Location getLocation(){return this.location;}
	public EulerAngle getAngle(BodyPart part){if(!angle.containsKey(part)){return part.getDefAngle();}return angle.get(part);}
	public String getName(){return this.name;}
	public Entity getPessanger(){return this.pessanger;}
	public ObjectID getObjectId(){return this.objID;}
	public ArmorStandInventory getInventory() {return this.inventory;}
	public void setNameVasibility(boolean b){this.watcher.setObject(3, (byte)(b?1:0));this.customname=b;}
	public int getEntityId() {return this.ID;}
	public int getArmorID(){return this.ArmorID;}
	public boolean isFire(){return this.fire;}
	public boolean isNameVisible(){return this.customname;}
	public boolean isInvisible(){return this.invisible;}
	public boolean isMini(){return this.mini;}
	public boolean hasArms(){return this.arms;}
	public boolean hasBasePlate(){return this.basePlate;}
	public boolean hasGravity(){return this.gravity;}
	public boolean hasMarker(){return this.marker;}
	public void setArmorID(int i){this.ArmorID = i;}
	private int getFixedPoint(Double d){return (int) (d*32D);}
	private int getCompressedAngle(float value) {return (int)(value * 256.0F / 360.0F);}
	public void setID(int Int) {this.ArmorID=Int;}
	public void setInventory(ArmorStandInventory inv) {this.inventory = inv;}
	
	public ArmorStandPacket(Location l, ObjectID id, int i){
		try{
			this.location = l;
			this.watcher = getDefaultWatcher(l.getWorld(), EntityType.ARMOR_STAND);
			this.ID = EntityID.nextEntityId();
			this.ArmorID = i;
			this.manager = ProtocolLibrary.getProtocolManager();
			this.objID = id;
			this.inventory = new ArmorStandInventory(getEntityId());
			create();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setPessanger(Entity e){
		PacketContainer container = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
		container.getIntegers()
		.write(1, e.getEntityId())
		.write(2, getEntityId());
		try {
			for(Player p : loadedPlayers){
				this.manager.sendServerPacket(p, container);
			}
			this.pessanger = e;
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

	private WrappedDataWatcher getDefaultWatcher(World world, EntityType type) {
        Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), type);
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
        entity.remove();
        return watcher;
    }
	
	public void teleport(Location loc){
		this.location = loc;
		PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
		container.getIntegers()
		.write(0, getEntityId())
		.write(1, getFixedPoint(loc.getX()))
		.write(2, getFixedPoint(loc.getY()))
		.write(3, getFixedPoint(loc.getZ()));
		
		for(Player p : loadedPlayers){
			try {
				this.manager.sendServerPacket(p, container);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void create(){
		PacketContainer container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		container.getIntegers()
		.write(0, this.ID)
		.write(1, (int) EntityType.ARMOR_STAND.getTypeId())
		.write(2, getFixedPoint(this.location.getX()))
		.write(3, getFixedPoint(this.location.getY()))
		.write(4, getFixedPoint(this.location.getZ()))
		.write(5, 0)
		.write(6, 0)
		.write(7, 0);
		container.getBytes()
		.write(0, (byte) getCompressedAngle(this.location.getYaw()))
		.write(1, (byte) getCompressedAngle(this.location.getPitch()));
		this.container = container;
	}
	
	public void delete(){
		this.container = null;
		this.manager = null;
		this.inventory = null;
		FurnitureLib.getInstance().getFurnitureManager().remove(this);
	}
	
	public void setSmall(boolean b){
		byte b0 = this.watcher.getByte(10);

		if (b)
			b0 = (byte)(b0 | 0x1);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		this.watcher.setObject(10, Byte.valueOf(b0));
		this.mini = b;
	}

	public void setArms(boolean b) {
		byte b0 = this.watcher.getByte(10);

		if (b)
			b0 = (byte)(b0 | 0x4);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFB);
		}
		this.watcher.setObject(10, Byte.valueOf(b0));
		this.arms = b;
	}

	public void setGravity(boolean b) {
		byte b0 = this.watcher.getByte(10);

		if (b)
			b0 = (byte)(b0 | 0x02);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFD);
		}
		this.watcher.setObject(10, Byte.valueOf(b0));
		this.gravity = b;
	}

	public void setBasePlate(boolean b) {
		byte b0 = this.watcher.getByte(10);

		if (b)
			b0 = (byte)(b0 | 0x08);
		else {
			b0 = (byte)(b0 & 0xFFFFFFF7);
		}
		this.watcher.setObject(10, Byte.valueOf(b0));
		this.basePlate = b;
	}

	public void setInvisible(boolean b) {
		byte b0 = this.watcher.getByte(0);

		if (b)
			b0 = (byte)(b0 | 0x20);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFB);
		}
		this.watcher.setObject(0, Byte.valueOf(b0));
		this.invisible = b;
	}
	
	public void setMarker(boolean b){
		byte b0 = this.watcher.getByte(0);

		if (b)
			b0 = (byte)(b0 | 0x16);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		this.watcher.setObject(0, Byte.valueOf(b0));
		this.marker = b;
	}

	public void setFire(boolean b){
		byte b0 = this.watcher.getByte(0);
		if (b){
			b0 = (byte)(b0 | 0x01);
			if(Bukkit.getPluginManager().isPluginEnabled("LightAPI")) FurnitureLib.getInstance().getLightManager().addLight(getLocation(), 15);
		}else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
			if(Bukkit.getPluginManager().isPluginEnabled("LightAPI")) FurnitureLib.getInstance().getLightManager().removeLight(getLocation());
		}
		this.watcher.setObject(0, Byte.valueOf(b0));
		this.fire = b;
	}

	public void setPose(EulerAngle angle, BodyPart part){
		if(angle==null){return;}
		if(part==null){return;}
		this.angle.put(part, angle);
		angle = FurnitureLib.getInstance().getLocationUtil().Radtodegress(angle);
		try {
			Class<?> Vector3f = Class.forName("net.minecraft.server." + FurnitureLib.getInstance().getBukkitVersion() + ".Vector3f");
			Constructor<?> ctor = Vector3f.getConstructors()[0];
			ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ());
			this.watcher.setObject(part.getField(), ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setName(String str){
		if(str==null){return;}
		if(str==""){setNameVasibility(false);return;}
		this.watcher.setObject(2, str);
		this.name = str;
	}
	
	public void sendInventoryPacket(final Player player) {
		List<PacketContainer> packets = this.inventory.createPackets();
		if (packets.isEmpty()) return;
		try {
			for (final PacketContainer packet : packets){
					if(player==null||packet==null||manager==null){return;}
					this.manager.sendServerPacket(player, packet);
					Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
					@Override
						public void run() {
							try {
								manager.sendServerPacket(player, packet);
							} catch (Exception e) {}
						}
					}, 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Player p){
		PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		update.getIntegers().write(0, getEntityId());
		update.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
		 try
         {
                this.manager.sendServerPacket(p, update);
                this.sendInventoryPacket(p);
                if(getPessanger()!=null){setPessanger(getPessanger());}
         }
         catch (InvocationTargetException e){e.printStackTrace();}
	}
	
	public void update(){
		PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		update.getIntegers().write(0, getEntityId());
		update.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
		 try
         {
			 for(Player p : this.loadedPlayers){
	                this.manager.sendServerPacket(p, update);
	                this.sendInventoryPacket(p);
			 }
			 if(getPessanger()!=null){setPessanger(getPessanger());}
         }catch (InvocationTargetException e){e.printStackTrace();}	 
	}
	
	public void destroy(Player p){
		if (!this.loadedPlayers.contains(p)) return;
		PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegerArrays().write(0, new int[] {this.ID});
		try {
			unleash();
			this.manager.sendServerPacket(p, destroy);
			if(this.loadedPlayers.contains(p)){this.loadedPlayers.remove(p);}
		} catch (InvocationTargetException e) {e.printStackTrace();}
	}
	
	public void destroy(){
		PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegerArrays().write(0, new int[] {this.ID});
		try {
			unleash();
			 for(Player p : this.loadedPlayers){this.manager.sendServerPacket(p, destroy);}
			 this.loadedPlayers.clear();
		} catch (InvocationTargetException e) {e.printStackTrace();}
	}
	
	public void send(Player p){
		if (this.loadedPlayers.contains(p)) return;
		if (this.manager == null) return;
		if (this.container == null) return;
		try {
			this.container.getDataWatcherModifier().write(0, watcher);
            this.manager.sendServerPacket(p, container);
            this.loadedPlayers.add(p);
            this.sendInventoryPacket(p);
            if(getPessanger()!=null){setPessanger(getPessanger());}
        } catch (InvocationTargetException e) { e.printStackTrace();}
	}
	
	public void unleash() {
		if(pessanger==null) return;
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.ATTACH_ENTITY);
		packet.getIntegers()
		.write(0, 0)
		.write(1, pessanger.getEntityId())
		.write(2, -1);
		try {
			for(Player p : loadedPlayers){if(p.isOnline()){manager.sendServerPacket(p, packet);}}
			this.pessanger = null;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}