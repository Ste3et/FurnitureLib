package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R2.Vector3f;

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
import com.comphenix.protocol.reflect.StructureModifier;
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
	private PacketContainer inventory;
	private String name = "";
	private ProtocolManager manager;
	private List<Player> loadedPlayers = new ArrayList<Player>();
	
	public Location getLocation(){return this.location;}
	public PacketContainer getContainer(){return this.container;}
	public BodyPart getBodyPart(){return this.part;}
	public EulerAngle getAngle(){return this.angle;}
	public ItemStack getItemStack(){return this.inventory.getItemModifier().read(0);}
	public String getName(){return this.name;}
	public ObjectID getObjectID(){return this.objID;}
	public void setNameVasibility(boolean b){this.watcher.setObject(3, (byte)(b?1:0));this.customname=b;}
	public void setSlot(short Slot){this.inventory.getIntegers().write(1, (int) Slot);}
	public void giveItem(ItemStack is){this.inventory.getItemModifier().write(0, is);}
	public int getID() {return this.ID;}
	public boolean isFire(){return this.fire;}
	public boolean isNameVisible(){return this.customname;}
	public boolean isInvisible(){return this.invisible;}
	public boolean isMini(){return this.mini;}
	public boolean hasArms(){return this.arms;}
	public boolean hasBasePlate(){return this.basePlate;}
	public boolean hasGraviti(){return this.graviti;}
	private int getFixedPoint(Double d){return (int) (d*32D);}
	
	public ArmorStandPacket(Location l, int ID, ObjectID id){
		this.location = l;
		this.watcher = getDefaultWatcher(l.getWorld(), EntityType.ARMOR_STAND);
		this.ID = ID;
		this.manager = ProtocolLibrary.getProtocolManager();
		this.objID = id;
		create();
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
		StructureModifier<Integer> integers = container.getIntegers();
		integers.write(0, this.ID);
		integers.write(1, (int) EntityType.ARMOR_STAND.getTypeId());
		integers.write(2, getFixedPoint(this.location.getX()));
		integers.write(3, getFixedPoint(this.location.getY()));
		integers.write(4, getFixedPoint(this.location.getZ()));
		integers.write(5, (int) ((this.location.getYaw() * 256.0F) / 360.0F));
		integers.write(6, (int) ((this.location.getPitch() * 256.0F) / 360.0F));
		integers.write(7, 0);
		this.container = container;
		
		PacketContainer entityInventory = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
		integers = entityInventory.getIntegers();
		integers.write(0, this.ID);
		this.inventory = entityInventory;
	}
	
	public void delete(){
		this.container = null;
		this.manager = null;
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
		Vector3f vector = new Vector3f((float) angle.getX(), (float) angle.getY(), (float) angle.getZ());
		switch (part) {
		case HEAD:
			this.watcher.setObject(11, vector);
			break;
		case BODY:
			this.watcher.setObject(12, vector);
			break;
		case LEFT_ARM:
			this.watcher.setObject(13, vector);
			break;
		case RIGHT_ARM:
			this.watcher.setObject(14, vector);
			break;
		case LEFT_LEG:
			this.watcher.setObject(15, vector);
			break;
		case RIGHT_LEG:
			this.watcher.setObject(16, vector);
			break;
		default:return;
		}
	}
	
	public void setName(String str){
		if(str==null){return;}
		this.watcher.setObject(2, str);
		this.name = str;
	}
	
	public void update(Player p){
		PacketContainer update = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		update.getIntegers().write(0, getID());
		update.getWatchableCollectionModifier().write(0, this.watcher.getWatchableObjects());
		 try
         {
                this.manager.sendServerPacket(p, update);
                this.manager.sendServerPacket(p, inventory.deepClone());
         }
         catch ( InvocationTargetException e )
         {
                 e.printStackTrace();
         }
		 
		 
	}
	
	public void destroy(Player p){
		if (!this.loadedPlayers.contains(p)) return;
		PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		destroy.getIntegerArrays().write(0, new int[] {this.ID});
		try {
			this.manager.sendServerPacket(p, destroy);
			if(this.loadedPlayers.contains(p)){this.loadedPlayers.remove(p);}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Player p){
		if (this.loadedPlayers.contains(p)) return;
		if (this.manager == null) return;
		if (this.container == null) return;
		try {
			this.container.getDataWatcherModifier().write(0, this.watcher);
            this.manager.sendServerPacket(p, container.deepClone());
            this.manager.sendServerPacket(p, inventory.deepClone());
            this.loadedPlayers.add(p);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
	}
	
	public boolean isInRange(Player player) {
		return getLocation().getWorld() == player.getLocation().getWorld() && (getLocation().distance(player.getLocation()) <= 48D);
	}
}