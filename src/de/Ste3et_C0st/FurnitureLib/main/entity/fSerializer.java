package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class fSerializer {
	
	Serializer intSerializer, byteSerializer, objSerializer, stringSerializer;
	NBTTagCompound metadata;
	
	
	public fSerializer(){
		intSerializer = WrappedDataWatcher.Registry.get(Integer.class);
		byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
		objSerializer = WrappedDataWatcher.Registry.get(Object.class);
		stringSerializer = WrappedDataWatcher.Registry.get(String.class);
		metadata = new NBTTagCompound();
	}
	
	public void setObject(WrappedDataWatcher watcher, Byte byt, int index){watcher.setObject(new WrappedDataWatcherObject(index, byteSerializer), byt);}
	public void setObject(WrappedDataWatcher watcher, Object obj, int index){watcher.setObject(new WrappedDataWatcherObject(index, objSerializer), obj);}
	public void setObject(WrappedDataWatcher watcher, Integer integer, int index){watcher.setObject(new WrappedDataWatcherObject(index, intSerializer), integer);}
	public void setObject(WrappedDataWatcher watcher, String str, int index){watcher.setObject(new WrappedDataWatcherObject(index, stringSerializer), str);}
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
	
	public NBTTagCompound getMetaData(fArmorStand stand){
		setMetadata("Name", stand.getName());
		setMetadata(stand.getLocation());
		setMetadata(stand.getInventory());
		set("EulerAngle", getEulerAngle(stand));
		setMetadata("Arms", stand.hasArms());
		setMetadata("BasePlate", stand.hasBasePlate());
		setMetadata("Fire", stand.isFire());
		setMetadata("Invisible", stand.isVisible());
		setMetadata("Small", stand.isSmall());
		setMetadata("NameVisible", stand.isCustomNameVisible());
		setMetadata("Marker", stand.isMarker());
		setMetadata("Glowing", stand.isGlowing());
		return metadata;
	}
	
	private NBTTagCompound getFromLocation(Location loc){
		NBTTagCompound location = new NBTTagCompound();
		location.setDouble("X", loc.getX());
		location.setDouble("Y", loc.getY());
		location.setDouble("Z", loc.getZ());
		location.setFloat("Yaw", loc.getYaw());
		location.setFloat("Pitch", loc.getPitch());
		location.setString("World", loc.getWorld().getName());
		return location;
	}
	
	private NBTTagCompound getEulerAngle(fArmorStand packet){
		NBTTagCompound eulerAngle = new NBTTagCompound();
		for(BodyPart part : BodyPart.getList()){
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
		for(int i = 0; i<5; i++){
			ItemStack is = fInventory.getSlot(i);
			if(is==null||is.getType().equals(Material.AIR)){inventory.setString(i+"", "NONE");continue;}
			try {
				inventory.set(i+"", new CraftItemStack().getNBTTag(is));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return inventory;
	}
}
