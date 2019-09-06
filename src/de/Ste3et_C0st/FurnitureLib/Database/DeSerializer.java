package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class DeSerializer {
	
	public AtomicInteger armorStands = new AtomicInteger(0);
	public int purged = 0;
	
	@SuppressWarnings("unchecked")
	public ObjectID Deserialize(String objId,String in, SQLAction action, String world){
		ObjectID obj = new ObjectID(null, null, null);
		obj.setID(objId);
		byte[] by = Base64.getDecoder().decode(in);
		try(ByteArrayInputStream bin = new ByteArrayInputStream(by)) {
			NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
			if(Objects.isNull(compound)) {return null;}
			EventType evType = EventType.valueOf(compound.getString("EventType"));
			PublicMode pMode = PublicMode.valueOf(compound.getString("PublicMode")); 
			UUID uuid = uuidFetcher(compound.getString("Owner-UUID"));
			HashSet<UUID> members = membersFetcher(compound.getList("Members"));
			Location startLocation = locationFetcher(compound.getCompound("Location"));
			if(Objects.isNull(startLocation)){
				obj.setSQLAction(SQLAction.REMOVE);
				FurnitureLib.getInstance().getFurnitureManager().addObjectID(obj);
				return null;
			}
			obj.setStartLocation(startLocation);
			obj.setEventTypeAccess(evType);
			obj.setPublicMode(pMode);
			obj.setMemberList(members);
			obj.setUUID(uuid);
			obj.setFinish();
			obj.setSQLAction((action != null && action.equals(SQLAction.SAVE)) ? SQLAction.SAVE : SQLAction.NOTHING);
			obj.setFromDatabase(true);

			NBTTagCompound armorStands = compound.getCompound("entitys");
			
			armorStands.c().stream().filter(Objects::nonNull).forEach(packet -> {
				NBTTagCompound metadata = armorStands.getCompound((String) packet);
				Location loc = locationFetcher(metadata.getCompound("Location"));
				FurnitureManager.getInstance().createFromType(metadata.getString("EntityType"), loc, obj).loadMetadata(metadata);
			});
			this.armorStands.addAndGet(armorStands.c().size());
			if(world == null || world.equals("null")) obj.setSQLAction(SQLAction.UPDATE);
			//if(autoPurge){if(FurnitureLib.getInstance().checkPurge(obj, uuid)){purged++;}} <-- why is this here ?
			return obj;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Location locationFetcher(NBTTagCompound location){
		Double X = location.getDouble("X");
		Double Y = location.getDouble("Y");
		Double Z = location.getDouble("Z");
		Float Yaw = location.getFloat("Yaw");
		Float Pitch = location.getFloat("Pitch");
		String str = location.getString("World");
		World world = null;
		try{
		    UUID uuid = UUID.fromString(str);
		    world = Bukkit.getWorld(uuid);
		} catch (IllegalArgumentException exception){
			world = Bukkit.getWorld(str);
		}
		if(world == null){FurnitureLib.getInstance().getLogger().info("The world: " + location.getString("World") + " deos not exist.");return null;}
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
	
	public boolean isWorldLoadet(UUID uuid){
		boolean loaded = false;
		for(World w: Bukkit.getServer().getWorlds())
		{
		  if(w.getUID().equals(uuid))
		  {
		    loaded = true;
		    break;
		  }
		}
		return loaded;
	}
	
	public static UUID uuidFetcher(String s){
		if(s.equalsIgnoreCase("NULL")){return null;}
		try{
			return UUID.fromString(s);
		}catch(Exception e){
			return null;
		}
	}
	
	private HashSet<UUID> membersFetcher(NBTTagList nbtList){
		HashSet<UUID> uuidList = new HashSet<UUID>();
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
