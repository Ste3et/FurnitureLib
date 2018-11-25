package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public abstract class fSerializer extends fProtocol{

	private NBTTagCompound metadata = new NBTTagCompound();
	public fSerializer(World w, EntityType type, ObjectID id) {super(w, type, id);}
	public NBTTagCompound getNBTField() {return this.metadata;}
	
	public void setMetadata(String field, String value){metadata.setString(field, value);}
	public void setMetadata(String field, Boolean value){setMetadata(field, value ? 1 : 0);}
	public void setMetadata(String field, Integer value){metadata.setInt(field, value);}
	public void setMetadata(String field, Byte value){metadata.setByte(field, value);}
	public void setMetadata(String field, Double value){metadata.setDouble(field, value);}
	public void setMetadata(String field, Float value){metadata.setFloat(field, value);}
	public void setMetadata(String field, Long value){metadata.setLong(field, value);}
	public void setMetadata(String field, Short value){metadata.setShort(field, value);}
	public void setMetadata(String field, byte[] value){metadata.setByteArray(field, value);}
	public void setMetadata(String field, int[] value){metadata.setIntArray(field, value);}
	public void setMetadata(Location value){set("Location", getFromLocation(value));}
	public void setMetadata(fArmorStand value){set("EulerAngle", getEulerAngle(value));}
	public void setMetadata(fInventory inventory){set("Inventory", getFromInventory(inventory));}
	public void set(String field, NBTTagCompound value){metadata.set(field, value);}
	
	public abstract NBTTagCompound getMetaData();
	
	public void getDefNBT(fEntity entity){
		setMetadata("EntityType", entity.getEntityType().toString());
		setMetadata("Name", entity.getName());
		setMetadata("Fire", entity.isFire());
		setMetadata("Invisible", entity.isInvisible());
		setMetadata("NameVisible", entity.isCustomNameVisible());
		setMetadata("Glowing", entity.isGlowing());
		setMetadata(entity.getLocation());
		setMetadata(entity.getInventory());
	}
	
	private NBTTagCompound getFromLocation(Location loc){
		NBTTagCompound location = new NBTTagCompound();
		location.setDouble("X", loc.getX());
		location.setDouble("Y", loc.getY());
		location.setDouble("Z", loc.getZ());
		location.setFloat("Yaw", loc.getYaw());
		location.setFloat("Pitch", loc.getPitch());
		location.setString("World", loc.getWorld().getUID().toString());
		return location;
	}
	
	public NBTTagCompound getEulerAngle(fArmorStand packet){
		NBTTagCompound eulerAngle = new NBTTagCompound();
		for(BodyPart part : BodyPart.values()){
			EulerAngle angle = packet.getPose(part);
			NBTTagCompound partAngle = new NBTTagCompound();
			partAngle.setDouble("X", angle.getX());
			partAngle.setDouble("Y", angle.getY());
			partAngle.setDouble("Z", angle.getZ());
			eulerAngle.set(part.toString(), partAngle);
		}
		return eulerAngle;
	}
	
	private NBTTagCompound getFromInventory(fInventory fInventory){
		NBTTagCompound inventory = new NBTTagCompound();
		for(Object o : EnumWrappers.ItemSlot.values()){
			ItemStack is = fInventory.getSlot(o.toString());
			if(is==null||is.getType().equals(Material.AIR)){inventory.setString(o.toString()+"", "NONE");continue;}
			try {
				inventory.set(o.toString()+"", new CraftItemStack().getNBTTag(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return inventory;
	}
	
	public void setBitMask(boolean flag, int field, int i) {
		byte b0 = (byte) 0;
		if(getWatcher().hasIndex(field)) {
			b0 = (byte) getWatcher().getObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)));
		}
		if (flag) {
			getWatcher().setObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)), (byte) (b0 | 1 << i));
		} else {
			getWatcher().setObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)), Byte.valueOf((byte) (b0 & (1 << i ^ 0xFFFFFFFF))));
		}
	}
	
	public String toString(fArmorStand stand){
		return Base64.getEncoder().encodeToString(getByte(getNBTField()));
	}
	
	public byte[] getByte(NBTTagCompound compound){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			NBTCompressedStreamTools.write(compound, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
		return out.toByteArray();
	}
}
