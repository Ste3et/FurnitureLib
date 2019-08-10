package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fGiant extends fEntity {

	private boolean AI = true;
	private Giant entity = null;
	public static EntityType type = EntityType.GIANT;
	
	public fGiant(Location loc, ObjectID id) {
		super(loc, type, id);
	}
	
	public fGiant moveRelative(int x, int y, int z, float yaw, float pitch, boolean onGround){
		short X = (short) ((getLocation().getX() * 32 - x * 32) * 128);
		short Y = (short) ((getLocation().getY() * 32 - y * 32) * 128);
		short Z = (short) ((getLocation().getZ() * 32 - z * 32) * 128);
		PacketContainer c = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
		c.getIntegers().write(0, getEntityID());
		c.getShorts().write(0, X).write(1, Y).write(2, Z);
		c.getBytes().write(0, (byte) yaw).write(1, (byte) pitch);
		c.getBooleans().write(0, onGround);
		
		Location loc = getLocation();
		loc = loc.add(x, y, z);
		loc.setYaw(yaw);
		loc.setPitch(pitch);
		setLocation(loc);
		
		for(Player p : getObjID().getPlayerList()){
			try {
				getManager().sendServerPacket(p, c);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	public fGiant setNoAI(boolean b){
		setBitMask(b, 11, 0);
		this.AI = b;
		return this;
	}
	
	public fGiant setLeftHanded(boolean b){
		setBitMask(b, 11, 1);
		return this;
	}

	public Giant toRealEntity() {
		if(entity!=null){if(!entity.isDead()){return entity;}}
		entity = (Giant) getWorld().spawnEntity(getLocation(), getEntityType());
		entity.setAI(this.AI);
		return entity;
	}
	
	public boolean isRealEntity(){
		if(entity==null) return false;
		return true;
	}
	
	public void setEntity(Entity entity){
		if(entity instanceof Giant) this.entity = (Giant) entity;
	}
	
	public NBTTagCompound getMetaData(){
		getDefNBT(this);
		return getNBTField();
	}
	
	@Override
	public void loadMetadata(NBTTagCompound metadata) {
		loadDefMetadata(metadata);
	}
}
