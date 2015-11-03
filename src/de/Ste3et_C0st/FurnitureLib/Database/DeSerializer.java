package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public class DeSerializer {

	public void Deserialze(String objId,String in){
		try {
			byte[] by = Base64.decodeBase64(in);
			ByteArrayInputStream bin = new ByteArrayInputStream(by);
			NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
			EventType evType = EventType.valueOf(compound.getString("EventType"));
			PublicMode pMode = PublicMode.valueOf(compound.getString("PublicMode")); 
			UUID uuid = uuidFetcher(compound.getString("Owner-UUID"));
			List<UUID> members = membersFetcher(compound.getList("Members"));
			Location startLocation = locationFetcher(compound.getCompound("Location"));
			if(startLocation==null){return;}
			ObjectID obj = new ObjectID(null, null, startLocation);
			obj.setID(objId);
			obj.setEventTypeAccess(evType);
			obj.setPublicMode(pMode);
			obj.setMemberList(members);
			obj.setUUID(uuid);
			obj.setFinish();
			obj.setSQLAction(SQLAction.NOTHING);
			obj.setFromDatabase();
			
			NBTTagCompound armorStands = compound.getCompound("ArmorStands");
			for(Object objectInt : armorStands.c()){
				Integer ArmorID = Integer.parseInt((String) objectInt);
				NBTTagCompound metadata = armorStands.getCompound(ArmorID+"");
				String name = metadata.getString("Name");
				Location loc = locationFetcher(metadata.getCompound("Location"));
				fArmorStand asPacket = FurnitureLib.getInstance().getFurnitureManager().createArmorStand(obj, loc);
				asPacket.setName(name);
				
				NBTTagCompound euler = metadata.getCompound("EulerAngle");
				for(BodyPart part : BodyPart.values()){
					asPacket.setPose(eulerAngleFetcher(euler.getCompound(part.toString())), part);
				}
				
				boolean nameVisible = ItB(metadata.getInt("NameVisible"));
				boolean BasePlate = ItB(metadata.getInt("BasePlate"));
				boolean Small = ItB(metadata.getInt("Small"));
				boolean Fire = ItB(metadata.getInt("Fire"));
				boolean Arms = ItB(metadata.getInt("Arms"));
				boolean Invisible = ItB(metadata.getInt("Invisible"));
				boolean Marker = true;
				if(metadata.hasKey("Marker")){Marker = ItB(metadata.getInt("Marker"));}
				
				NBTTagCompound inventory = metadata.getCompound("Inventory");
				for(int i = 0; i<5; i++){
					String s  = inventory.getString(i+"");
					if(!s.equalsIgnoreCase("NONE")){
						ItemStack is = new CraftItemStack().getItemStack(inventory.getCompound(i+""));
						asPacket.getInventory().setSlot(i, is);
					}
				}
				
				asPacket.setNameVasibility(nameVisible);
				asPacket.setBasePlate(BasePlate);
				asPacket.setSmall(Small);
				asPacket.setFire(Fire);
				asPacket.setArms(Arms);
				asPacket.setInvisible(Invisible);
				asPacket.setArmorID(ArmorID);
				asPacket.setMarker(Marker);
				if(FurnitureLib.getInstance().getFurnitureManager().getLastID()<ArmorID){
					FurnitureLib.getInstance().getFurnitureManager().setLastID(ArmorID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
   private boolean ItB(int i){
	  if(i==1) return true;
	  return false;
	}
	
	private EulerAngle eulerAngleFetcher(NBTTagCompound eularAngle){
		Double X = eularAngle.getDouble("X");
		Double Y = eularAngle.getDouble("Y");
		Double Z = eularAngle.getDouble("Z");
		return new EulerAngle(X, Y, Z);
	}
	
	private Location locationFetcher(NBTTagCompound location){
		Double X = location.getDouble("X");
		Double Y = location.getDouble("Y");
		Double Z = location.getDouble("Z");
		Float Yaw = location.getFloat("Yaw");
		Float Pitch = location.getFloat("Pitch");
		if(!isWorldLoadet(location.getString("World"))){return null;}
		World world = Bukkit.getWorld(location.getString("World"));
		Location loc = new Location(world, X, Y, Z);
		loc.setYaw(Yaw);
		loc.setPitch(Pitch);
		return loc;
	}
	
	public boolean isWorldLoadet(String s){
		boolean loaded = false;
		for(World w: Bukkit.getServer().getWorlds())
		{
		  if(w.getName().equals(s))
		  {
		    loaded = true;
		    break;
		  }
		}
		return loaded;
	}
	
	private UUID uuidFetcher(String s){
		if(s.equalsIgnoreCase("NULL")){return null;}
		try{
			return UUID.fromString(s);
		}catch(Exception e){
			return null;
		}
	}
	
	private List<UUID> membersFetcher(NBTTagList nbtList){
		List<UUID> uuidList = new ArrayList<UUID>();
		if(nbtList==null||nbtList.size()==0){return uuidList;}
		for(int i = 0; i<nbtList.size();i++){
			String string = nbtList.getString(i);
			try{
				uuidList.add(UUID.fromString(string));
			}catch(Exception e){}
		}
		return uuidList;
	}
}
