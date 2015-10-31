package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagString;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fInventory;

public class Serializer {

	public String SerializeObjectID(ObjectID obj){
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("EventType", obj.getEventType().toString());
		compound.setString("PublicMode", obj.getPublicMode().toString());
		compound.setString("Owner-UUID", getOwnerUUID(obj));
		compound.set("Members",getMemberList(obj));
		compound.set("Location",getFromLocation(obj.getStartLocation()));
		
		NBTTagCompound armorStands = new NBTTagCompound();
		for(fArmorStand packet : obj.getPacketList()){
			NBTTagCompound metadata = new NBTTagCompound();
			metadata.setString("Name", packet.getName());
			metadata.set("Location",getFromLocation(packet.getLocation()));
			metadata.set("Inventory",getFromInventory(packet.getInventory()));
			metadata.set("EulerAngle",getEulerAngle(packet));
			metadata.setInt("Arms", BtI(packet.hasArms()));
			metadata.setInt("BasePlate", BtI(packet.hasBasePlate()));
			metadata.setInt("Fire", BtI(packet.isFire()));
			metadata.setInt("Invisible", BtI(packet.isVisible()));
			metadata.setInt("Small", BtI(packet.isSmall()));
			metadata.setInt("NameVisible", BtI(packet.isCustomNameVisible()));
			metadata.setInt("Marker", BtI(packet.isMarker()));
			armorStands.set(packet.getArmorID()+"", metadata);
		}
		compound.set("ArmorStands", armorStands);
		return Base64.encodeBase64String(armorStandtoBytes(compound));
	}
	
	private byte[] armorStandtoBytes(NBTTagCompound compound) {
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
	
	private String getOwnerUUID(ObjectID obj){
		String s = "NULL";
		if(obj.getUUID()!=null){
			try{
				s = obj.getUUID().toString();
			}catch(Exception e){}
			
		}
		return s;
	}
	
	private int BtI(boolean b){
		if(b) return 1;
		return 0;
	}
	
	private NBTTagList getMemberList(ObjectID obj){
		NBTTagList memberList = new NBTTagList();
		for(UUID uuid : obj.getMemberList()){NBTTagString string = new NBTTagString(uuid.toString());memberList.add(string);}
		return memberList;
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
}