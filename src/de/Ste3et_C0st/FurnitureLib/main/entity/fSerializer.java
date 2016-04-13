package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class fSerializer extends fProtocol{

	private NBTTagCompound metadata = new NBTTagCompound();
	private HashMap<Integer, WrappedDataWatcherObject> objMap = new HashMap<Integer, WrappedDataWatcherObject>();
	public fSerializer(World w, EntityType type, ObjectID id) {super(w, type, id);}
	
	public void setObject(WrappedDataWatcher watcher, Object o, int index){
		WrappedDataWatcherObject wdwo = getDefWatcher(watcher,index, o);
		watcher.setObject(wdwo, o);
		objMap.put(index, wdwo);
	}

	public Object getObject(WrappedDataWatcher watcher,Object o, int index){
		WrappedDataWatcherObject wdwo = getDefWatcher(watcher,index, o);
		return watcher.getObject(wdwo);
	}
	
	private WrappedDataWatcherObject getDefWatcher(WrappedDataWatcher watcher, int index, Object o){
		WrappedDataWatcherObject wdwo = null;
		if(objMap.containsKey(index)){wdwo = objMap.get(index);}else{wdwo = new WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(o.getClass()));objMap.put(index, wdwo);watcher.setObject(wdwo, o);}
		return wdwo;
	}
	
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
		setMetadata("Arms", stand.hasArms());
		setMetadata("BasePlate", stand.hasBasePlate());
		setMetadata("Fire", stand.isFire());
		setMetadata("Invisible", stand.isVisible());
		setMetadata("Small", stand.isSmall());
		setMetadata("NameVisible", stand.isCustomNameVisible());
		setMetadata("Marker", stand.isMarker());
		setMetadata("Glowing", stand.isGlowing());
		setMetadata(stand.getLocation());
		setMetadata(stand.getInventory());
		setMetadata(stand);
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
		for(Object o : new Vector3f().b()){
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
	
	public String toString(fArmorStand stand){
		NBTTagCompound nbt = getMetaData(stand);
		return Base64.encodeBase64String(getByte(nbt));
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
