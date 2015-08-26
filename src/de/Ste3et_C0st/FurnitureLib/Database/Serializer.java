package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.io.NbtBinarySerializer;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandInventory;
import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class Serializer {

	public String SerializeObjectID(ObjectID obj){
		NbtCompound compound = NbtFactory.ofCompound("Object");
		compound.put("EventType", obj.getEventType().toString());
		compound.put("PublicMode", obj.getPublicMode().toString());
		compound.put("Owner-UUID", getOwnerUUID(obj));
		compound.put("Members",getMemberList(obj));
		compound.put(getFromLocation(obj.getStartLocation()));
		
		NbtCompound armorStands = NbtFactory.ofCompound("ArmorStands");
		for(ArmorStandPacket packet : obj.getPacketList()){
			NbtCompound metadata = NbtFactory.ofCompound(packet.getArmorID()+"");
			metadata.put("Name", packet.getName());
			metadata.put(getFromLocation(packet.getLocation()));
			metadata.put(getFromInventory(packet.getInventory()));
			metadata.put(getEulerAngle(packet));
			metadata.put("Arms", BtI(packet.hasArms()));
			metadata.put("BasePlate", BtI(packet.hasBasePlate()));
			metadata.put("Gravity", BtI(packet.hasGravity()));
			metadata.put("Marker", BtI(packet.hasMarker()));
			metadata.put("Fire", BtI(packet.isFire()));
			metadata.put("Invisible", BtI(packet.isInvisible()));
			metadata.put("Small", BtI(packet.isMini()));
			metadata.put("NameVisible", BtI(packet.isNameVisible()));
			armorStands.put(metadata);
		}
		compound.put(armorStands); 
		String string = NbtTextSerializer.DEFAULT.serialize(compound);
		return string;
	}
	
	private String getOwnerUUID(ObjectID obj){
		String s = "NULL";
		if(obj.getUUID()!=null) s = obj.getUUID().toString();
		return s;
	}
	
	private int BtI(boolean b){
		if(b) return 1;
		return 0;
	}
	
	public NbtList<String> getMemberList(ObjectID obj){
		List<String> memberList = new ArrayList<String>();
		for(UUID uuid : obj.getMemberList()){memberList.add(uuid.toString());}
		NbtList<String> members = NbtFactory.ofList("Members", memberList);
		return members;
	}
	 
	public NbtCompound getEulerAngle(ArmorStandPacket packet){
		NbtCompound eulerAngle = NbtFactory.ofCompound("EulerAngle");
		for(BodyPart part : BodyPart.getList()){
			EulerAngle angle = packet.getAngle(part);
			NbtCompound partAngle = NbtFactory.ofCompound(part.getName());
			partAngle.put("X", angle.getX());
			partAngle.put("Y", angle.getY());
			partAngle.put("Z", angle.getZ());
			eulerAngle.put(partAngle);
		}
		return eulerAngle;
	}
	
	public NbtCompound getFromLocation(Location loc){
		NbtCompound location = NbtFactory.ofCompound("Location");
		location.put("X", loc.getX());
		location.put("Y", loc.getY());
		location.put("Z", loc.getZ());
		location.put("Yaw", loc.getYaw());
		location.put("Pitch", loc.getPitch());
		location.put("World", loc.getWorld().getName());
		return location;
	}
	
	public NbtCompound getFromInventory(ArmorStandInventory inv){
		NbtCompound inventory = NbtFactory.ofCompound("Inventory");
		for(int i = 0; i<5; i++){
			ItemStack is = inv.getSlot(i);
			if(is==null||is.getType().equals(Material.AIR)){inventory.put(i+"", "NONE");}
			inventory.put(i+"", toBase64(is));
		}
		return inventory;
	}
	
	  public String toBase64(ItemStack is){
		  try {
	  		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			if(is==null) is=new ItemStack(Material.AIR);
			dataOutput.writeObject(is);
			dataOutput.close();
	        return Base64Coder.encodeLines(outputStream.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	  }

	  public ItemStack fromBase64(String s){
			try {
	            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
	            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
	            ItemStack is = (ItemStack) dataInput.readObject();
	            dataInput.close();
	            return is;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			return null;
		}
	
}