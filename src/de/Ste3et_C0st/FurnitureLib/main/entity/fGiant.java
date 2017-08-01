package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fGiant extends fEntity {

	private int armorstandID;
	public fGiant setObjID(ObjectID objID) {setObjectID(objID);return this;}
	public fGiant setArmorID(int i){this.armorstandID = i;return this;}
	public int getArmorID(){return this.armorstandID;}
	public Project getProject(){return this.pro;}
	private Project pro;
	
	public fGiant(Location loc, ObjectID id) {
		super(loc, EntityType.GIANT, id);
		this.armorstandID = FurnitureLib.getInstance().getFurnitureManager().getLastID();
		this.setObjID(id);
		this.pro = id.getProjectOBJ();
	}

	@Override
	public NBTTagCompound getMetadata() {
		return getMetaData(this);
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
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), getField().getBitMask());
		if (b)
			b0 = (byte)(b0 | 0x1);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		setObject(getWatcher(), Byte.valueOf(b0),getField().getBitMask());
		return this;
	}
	
	public fGiant setLeftHanded(boolean b){
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), getField().getBitMask());
		if (b)
			b0 = (byte)(b0 | 0x2);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		setObject(getWatcher(), Byte.valueOf(b0),getField().getBitMask());
		return this;
	}

}
