package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.InvocationTargetException;

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
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class ArmorStandPacket{

	private Integer ID;
	private Location location;
	private BodyPart part;
	private EulerAngle angle;
	private boolean mini, invisible, arms, basePlate, graviti;
	private WrappedDataWatcher watcher;
	private PacketContainer container;
	private PacketContainer inventory;
	
	public Location getLocation(){return this.location;}
	public PacketContainer getContainer(){return this.container;}
	public BodyPart getBodyPart(){return this.part;}
	public EulerAngle getAngle(){return this.angle;}
	public ItemStack getItemStack(){return this.inventory.getItemModifier().read(0);}
	public void setNameVasibility(boolean b){this.watcher.setObject(3, (byte)(b?1:0));}
	public void setSlot(short Slot){this.inventory.getIntegers().write(1, (int) Slot);}
	public void giveItem(ItemStack is){this.inventory.getItemModifier().write(0, is);}
	public int getID() {return this.ID;}
	public boolean isInvisible(){return this.invisible;}
	public boolean isMini(){return this.mini;}
	public boolean hasArms(){return this.arms;}
	public boolean hasBasePlate(){return this.basePlate;}
	public boolean hasGraviti(){return this.graviti;}
	private int getFixedPoint(Double d){return (int) (d*32D);}
	
	public ArmorStandPacket(Location l, int ID){
		this.location = l;
		this.watcher = getDefaultWatcher(l.getWorld(), EntityType.ARMOR_STAND);
		this.ID = ID;
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
	}
	
	public void send(Player p){
		try {
			this.container.getDataWatcherModifier().write(0, this.watcher);
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);
            ProtocolLibrary.getProtocolManager().sendServerPacket(p, inventory);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
	}
}