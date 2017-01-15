package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Location;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagString;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fCreeper;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fPig;

public class Serializer {

	public String SerializeObjectID(ObjectID obj){
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("EventType", obj.getEventType().toString());
		compound.setString("PublicMode", obj.getPublicMode().toString());
		compound.setString("Owner-UUID", getOwnerUUID(obj));
		compound.set("Members",getMemberList(obj));
		compound.set("Location",getFromLocation(obj.getStartLocation()));
		compound.setInt("ArmorStands", obj.getPacketList().size());
		NBTTagCompound armorStands = new NBTTagCompound();
		for(fEntity packet : obj.getPacketList()){
			if(packet instanceof fArmorStand){
				fArmorStand stand = (fArmorStand) packet;
				armorStands.set(stand.getArmorID()+"", stand.getMetadata());
			}else if(packet instanceof fPig){
				fPig stand = (fPig) packet;
				armorStands.set(stand.getArmorID()+"", stand.getMetadata());
			}else if(packet instanceof fCreeper){
				fCreeper stand = (fCreeper) packet;
				armorStands.set(stand.getArmorID()+"", stand.getMetadata());
			}
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
	
	private NBTTagList getMemberList(ObjectID obj){
		NBTTagList memberList = new NBTTagList();
		for(UUID uuid : obj.getMemberList()){NBTTagString string = new NBTTagString(uuid.toString());memberList.add(string);}
		return memberList;
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
}