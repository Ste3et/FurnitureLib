package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;

public class DeSerializer {

	public void Deserialze(String objId,String s){
		try {
			NbtCompound compound = NbtTextSerializer.DEFAULT.deserializeCompound(s);
			EventType evType = EventType.valueOf(compound.getString("EventType"));
			PublicMode pMode = PublicMode.valueOf(compound.getString("PublicMode")); 
			UUID uuid = uuidFetcher(compound.getString("Owner-UUID"));
			List<UUID> members = membersFetcher(compound.getList("Members"));
			Location startLocation = locationFetcher(compound.getCompound("Location"));
			
			ObjectID obj = new ObjectID(null, null, startLocation);
			obj.setID(objId);
			obj.setEventTypeAccess(evType);
			obj.setPublicMode(pMode);
			obj.setMemberList(members);
			obj.setUUID(uuid);
			obj.setFinish();
			
			NbtCompound armorStands = compound.getCompound("ArmorStands");
			for(String id : armorStands.getKeys()){
				Integer ArmorID = Integer.parseInt(id);
				NbtCompound metadata = armorStands.getCompound(id);
				String name = metadata.getString("Name");
				Location loc = locationFetcher(metadata.getCompound("Location"));
				ArmorStandPacket asPacket = FurnitureLib.getInstance().getFurnitureManager().createArmorStand(obj, loc);
				asPacket.setName(name);
				
				NbtCompound euler = metadata.getCompound("EulerAngle");
				for(BodyPart part : BodyPart.values()){
					asPacket.setPose(eulerAngleFetcher(euler.getCompound(part.getName())), part);
				}
				
				boolean nameVisible = ItB(metadata.getInteger("NameVisible"));
				boolean BasePlate = ItB(metadata.getInteger("BasePlate"));
				boolean Small = ItB(metadata.getInteger("Small"));
				boolean Fire = ItB(metadata.getInteger("Fire"));
				boolean Arms = ItB(metadata.getInteger("Arms"));
				boolean Marker = ItB(metadata.getInteger("Marker"));
				boolean Invisible = ItB(metadata.getInteger("Invisible"));
				boolean Gravity = ItB(metadata.getInteger("Gravity"));
				
				NbtCompound inventory = metadata.getCompound("Inventory");
				for(int i = 0; i<5; i++){
					String slot = inventory.getString(i+"");
					ItemStack is = fromBase64(slot);
					asPacket.getInventory().setSlot(i, is);
				}
				
				asPacket.setNameVasibility(nameVisible);
				asPacket.setBasePlate(BasePlate);
				asPacket.setSmall(Small);
				asPacket.setFire(Fire);
				asPacket.setArms(Arms);
				asPacket.setMarker(Marker);
				asPacket.setInvisible(Invisible);
				asPacket.setGravity(Gravity);
				asPacket.setArmorID(ArmorID);
				if(FurnitureLib.getInstance().getFurnitureManager().getLastID()<ArmorID){
					FurnitureLib.getInstance().getFurnitureManager().setLastID(ArmorID);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
   private boolean ItB(int i){
	  if(i==1) return true;
	  return false;
	}
	
	private EulerAngle eulerAngleFetcher(NbtCompound eularAngle){
		Double X = eularAngle.getDouble("X");
		Double Y = eularAngle.getDouble("Y");
		Double Z = eularAngle.getDouble("Z");
		return new EulerAngle(X, Y, Z);
	}
	
	private Location locationFetcher(NbtCompound location){
		Double X = location.getDouble("X");
		Double Y = location.getDouble("Y");
		Double Z = location.getDouble("Z");
		Float Yaw = location.getFloat("Yaw");
		Float Pitch = location.getFloat("Pitch");
		World world = Bukkit.getWorld(location.getString("World"));
		Location loc = new Location(world, X, Y, Z);
		loc.setYaw(Yaw);
		loc.setPitch(Pitch);
		return loc;
	}
	
	private UUID uuidFetcher(String s){
		if(s.equalsIgnoreCase("NULL")){return null;}
		return UUID.fromString(s);
	}
	
	private List<UUID> membersFetcher(NbtList<Object> nbtList){
		List<UUID> uuidList = new ArrayList<UUID>();
		if(nbtList==null||nbtList.size()==0){return uuidList;}
		for(Object s : nbtList){
			String string = (String) s;
			uuidList.add(UUID.fromString(string));
		}
		return uuidList;
	}
}
