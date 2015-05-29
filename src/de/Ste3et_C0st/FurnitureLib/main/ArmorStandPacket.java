package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class ArmorStandPacket{

	private Integer ID;
	private ObjectID objID;
	private Location location;
	private BodyPart part;
	private EulerAngle angle;
	private boolean mini, invisible, arms, basePlate, graviti, customname, fire;
	private WrappedDataWatcher watcher;
	private PacketContainer container;
	private ArmorStandInventory inventory;
	private String name = "";
	private ProtocolManager manager;
	private List<Player> loadedPlayers = new ArrayList<Player>();
	
	public Location getLocation(){return this.location;}
	public PacketContainer getContainer(){return this.container;}
	public BodyPart getBodyPart(){return this.part;}
	public EulerAngle getAngle(){return this.angle;}
	public String getName(){return this.name;}
	public ObjectID getObjectId(){return this.objID;}
	public ArmorStandInventory getInventory() {return this.inventory;}
	public void setNameVasibility(boolean b){this.watcher.setObject(3, (byte)(b?1:0));this.customname=b;}
	public int getEntityId() {return this.ID;}
	public boolean isFire(){return this.fire;}
	public boolean isNameVisible(){return this.customname;}
	public boolean isInvisible(){return this.invisible;}
	public boolean isMini(){return this.mini;}
	public boolean hasArms(){return this.arms;}
	public boolean hasBasePlate(){return this.basePlate;}
	public boolean hasGraviti(){return this.graviti;}
	public boolean isInRange(Player player) {return getLocation().getWorld() == player.getLocation().getWorld() && (getLocation().distance(player.getLocation()) <= 48D);}
	private int getFixedPoint(Double d){return (int) (d*32D);}
	private byte getCompressedAngle(float value) {return (byte)(int)(value * 256.0F / 360.0F);}
	
	public ArmorStandPacket(Location l, int ID, ObjectID id){
		this.location = l;
		this.watcher = getDefaultWatcher(l.getWorld(), EntityType.ARMOR_STAND);
		this.ID = ID;
		this.manager = ProtocolLibrary.getProtocolManager();
		this.objID = id;
		create();
	}
	
	public void setInventory(ArmorStandInventory inv) {
		this.inventory = inv;
		if (this.inventory == null)
		this.inventory = new ArmorStandInventory();
	}

	public WrappedDataWatcher getDefaultWatcher(World world, EntityType type) {
        Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), type);
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
        entity.remove();
        return watcher;
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
		this.container = container;
		this.inventory = new ArmorStandInventory();
	}
	
	public void setYaw(Player player, double yaw) {
		PacketContainer packet = this.manager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		packet.getIntegers().write(0, this.getEntityId());
		packet.getBytes().write(0, getCompressedAngle((float) yaw));
		player.sendMessage(getCompressedAngle(getLocation().getYaw()) + "");
		try {
			this.manager.sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public void setGrafiti(boolean b) {
		byte b0 = this.watcher.getByte(10);

		if (b)
			b0 = (byte)(b0 | 0x02);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFD);
		}
		
		this.watcher.setObject(10, Byte.valueOf(b0));
		this.graviti = b;
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
	
	public void setFire(boolean b){
		byte b0 = this.watcher.getByte(0);
		if (b)
			b0 = (byte)(b0 | 0x01);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		
		this.watcher.setObject(0, Byte.valueOf(b0));
		this.fire = b;
	}
	
	public void setPose(EulerAngle angle, BodyPart part){
		if(angle==null){return;}
		if(part==null){return;}
		this.part = part;
		this.angle = angle;
		try {
			Class<?> Vector3f = Class.forName("net.minecraft.server." + FurnitureLib.getInstance().getBukkitVersion() + ".Vector3f");
			Constructor<?> ctor = Vector3f.getConstructors()[0];
			ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ());
			switch (part) {
			case HEAD:
				this.watcher.setObject(11, ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
				break;
			case BODY:
				this.watcher.setObject(12, ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
				break;
			case LEFT_ARM:
				this.watcher.setObject(13, ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
				break;
			case RIGHT_ARM:
				this.watcher.setObject(14, ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
				break;
			case LEFT_LEG:
				this.watcher.setObject(15, ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
				break;
			case RIGHT_LEG:
				this.watcher.setObject(16, ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
				break;
			default:return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setName(String str){
		if(str==null){return;}
		this.watcher.setObject(2, str);
		this.name = str;
	}
	
	public void sendInventoryPacket(Player player) {
		List<PacketContainer> packets = this.inventory.createPackets(this.getEntityId());
		if (packets.isEmpty()) return;
		
		try {
			for (PacketContainer packet : packets){
				if(packet.getItemModifier().read(0)!=null){
					this.manager.sendServerPacket(player, packet);
					ItemStack is = packet.getItemModifier().read(0);
					if(is.hasItemMeta()){
						Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
							@Override
							public void run() {
								try {
									manager.sendServerPacket(player, packet);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}, 2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Player p){
		PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		update.getIntegers().write(0, getEntityId());
		update.getWatchableCollectionModifier().write(0, this.watcher.getWatchableObjects());
		 try
         {
                this.manager.sendServerPacket(p, update);
                this.sendInventoryPacket(p);
                this.setYaw(p, getLocation().getYaw());
         }
         catch (InvocationTargetException e){e.printStackTrace();}
	}
	
	public void update(){
		PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		update.getIntegers().write(0, getEntityId());
		update.getWatchableCollectionModifier().write(0, this.watcher.getWatchableObjects());
		 try
         {
			 for(Player p : this.loadedPlayers){
	                this.manager.sendServerPacket(p, update);
	                this.sendInventoryPacket(p);
	                this.setYaw(p, getLocation().getYaw());
			 }
         }catch (InvocationTargetException e){e.printStackTrace();}	 
	}
	
	public void destroy(Player p){
		if (!this.loadedPlayers.contains(p)) return;
		PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegerArrays().write(0, new int[] {this.ID});
		try {
			this.manager.sendServerPacket(p, destroy);
			if(this.loadedPlayers.contains(p)){this.loadedPlayers.remove(p);}
		} catch (InvocationTargetException e) {e.printStackTrace();}
	}
	
	public void send(Player p){
		if (this.loadedPlayers.contains(p)) return;
		if (this.manager == null) return;
		if (this.container == null) return;
		try {
			this.container.getDataWatcherModifier().write(0, this.watcher);
            this.manager.sendServerPacket(p, container.deepClone());
            this.loadedPlayers.add(p);
            this.sendInventoryPacket(p);
            this.setYaw(p, getLocation().getYaw());
        } catch (InvocationTargetException e) { e.printStackTrace();}
	}
}