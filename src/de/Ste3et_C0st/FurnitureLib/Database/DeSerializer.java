package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.wrappers.EnumWrappers;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.WorldPool;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fCreeper;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fPig;

public class DeSerializer {
	
	public int armorStands = 0;
	public int purged = 0;
	public FurnitureLib lib = FurnitureLib.getInstance();
	public WorldPool pool = FurnitureLib.getInstance().getWorldPool();
	private Object[] enumItemSlots = EnumWrappers.ItemSlot.values();
	
	public void Deserialze(String objId,String in, SQLAction action, boolean autoPurge){
		try {
			byte[] by = Base64.decodeBase64(in);
			ByteArrayInputStream bin = new ByteArrayInputStream(by);
			NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
			System.out.println(compound.toString());
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
			if(action!=null&&action.equals(SQLAction.SAVE)){obj.setSQLAction(SQLAction.SAVE);}else{obj.setSQLAction(SQLAction.NOTHING);}
			obj.setFromDatabase();
			if(autoPurge){if(FurnitureLib.getInstance().checkPurge(obj, uuid)){purged++;return;}}
			NBTTagCompound armorStands = compound.getCompound("ArmorStands");
			for(Object o : armorStands.c()){
				Integer ArmorID = Integer.parseInt((String) o);
				NBTTagCompound metadata = armorStands.getCompound(ArmorID+"");
				EntityType type = EntityType.ARMOR_STAND;
				if(metadata.hasKey("EntityType")){type = EntityType.valueOf(metadata.getString("EntityType"));}
				
				switch (type) {
				case ARMOR_STAND:loadArmorStandMetadata(metadata, ArmorID, obj);break;
				case PIG:loadPigMetadata(metadata, ArmorID, obj);break;
				case CREEPER:loadCreeperMetadata(metadata, ArmorID, obj);break;
				default:break;
				}
				if(FurnitureLib.getInstance().getFurnitureManager().getLastID()<ArmorID){FurnitureLib.getInstance().getFurnitureManager().setLastID(ArmorID);}
				this.armorStands++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadCreeperMetadata(NBTTagCompound metadata, Integer ArmorID, ObjectID obj){
		Location loc = locationFetcher(metadata.getCompound("Location"));
		fCreeper packet = FurnitureLib.getInstance().getFurnitureManager().createCreeper(obj, loc);
		loadDefMetadata(metadata, packet);
		boolean i = (metadata.getInt("Ignite")==1), f = (metadata.getInt("Charged")==1);
		packet.setArmorID(ArmorID).setIgnited(i).setCharged(f);
	}
	
	private void loadPigMetadata(NBTTagCompound metadata, Integer ArmorID, ObjectID obj){
		Location loc = locationFetcher(metadata.getCompound("Location"));
		fPig packet = FurnitureLib.getInstance().getFurnitureManager().createPig(obj, loc);
		loadDefMetadata(metadata, packet);
		boolean s = (metadata.getInt("Saddle")==1);
		packet.setArmorID(ArmorID).setSaddle(s);
	}
	
	private void loadArmorStandMetadata(NBTTagCompound metadata, Integer ArmorID, ObjectID obj){
		Location loc = locationFetcher(metadata.getCompound("Location"));
		fArmorStand packet = FurnitureLib.getInstance().getFurnitureManager().createArmorStand(obj, loc);
		loadDefMetadata(metadata, packet);
		NBTTagCompound euler = metadata.getCompound("EulerAngle");
		for(BodyPart part : BodyPart.values()){
			packet.setPose(eulerAngleFetcher(euler.getCompound(part.toString())), part);
		}
		boolean b = (metadata.getInt("BasePlate")==1),s = (metadata.getInt("Small")==1),a = (metadata.getInt("Arms")==1),m = (metadata.getInt("Marker")==1),grav = false;
		if(metadata.hasKey("Gravity")){
			grav = metadata.getInt("Gravity")==1;
		}	
		packet.setBasePlate(b).setSmall(s).setMarker(m).setArms(a).setArmorID(ArmorID).setGravity(grav);
	}
	
	private void loadDefMetadata(NBTTagCompound metadata, fEntity entity){
			String name = metadata.getString("Name");
			boolean n = (metadata.getInt("NameVisible")==1);
			boolean f = (metadata.getInt("Fire")==1),i = (metadata.getInt("Invisible")==1);
			boolean g = (metadata.getInt("Glowing")==1);
			NBTTagCompound inventory = metadata.getCompound("Inventory");
			for(Object object : enumItemSlots){
				if(!inventory.getString(object.toString()).equalsIgnoreCase("NONE")){
					ItemStack is = new CraftItemStack().getItemStack(inventory.getCompound(object.toString()+""));
					if(is!=null){
						entity.getInventory().setSlot(object.toString(), is);
					}
				}
			}
			entity.setNameVasibility(n).setName(name).setFire(f).setGlowing(g).setInvisible(i);
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
