package de.Ste3et_C0st.FurnitureLib.Database;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import org.bukkit.Location;
import org.bukkit.World;

import com.migcomponents.migbase64.Base64;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class DeSerializer {
	
	public int purged = 0;
	
	public static ObjectID Deserialize(String objId, String md5, SQLAction action, World world) {
		byte[] binary = Base64.decode(md5);
		return Deserialize(objId, binary, action, world);
	}
	
	public static ObjectID Deserialize(String objId, byte[] binary, SQLAction action, World world) {
		try(ByteArrayInputStream bin = new ByteArrayInputStream(binary)) {
			NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
			return Objects.isNull(compound) ? null : Deserialize(objId, compound, action, world);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ObjectID Deserialize(String objId, NBTTagCompound compound, SQLAction action, World world) {
		if(Objects.isNull(compound)) {return null;}
		ObjectID obj = new ObjectID(objId);
		Location startLocation = locationFetcher(compound.getCompound("Location"), world);
		if(Objects.isNull(startLocation)){
			obj.setSQLAction(SQLAction.REMOVE);
			FurnitureLib.getInstance().getFurnitureManager().addObjectID(obj);
			return null;
		}
		
		obj.setStartLocation(startLocation);
		
		if(compound.hasKeyOfType("EventType", 8)) {
			EventType evType = EventType.valueOf(compound.getString("EventType"));
			obj.setEventTypeAccess(evType);
		}
		
		if(compound.hasKeyOfType("PublicMode", 8)) {
			PublicMode pMode = PublicMode.valueOf(compound.getString("PublicMode"));
			obj.setPublicMode(pMode);
		}
		
		if(compound.hasKeyOfType("Members", 9)) {
			HashSet<UUID> members = membersFetcher(compound.getList("Members"));
			obj.setMemberList(members);
		}
		
		if(compound.hasKeyOfType("Owner-UUID", 8)) {
			UUID uuid = uuidFetcher(compound.getString("Owner-UUID"));
			obj.setUUID(uuid);
		}
		
		obj.setFinish();
		obj.setSQLAction((action != null && action.equals(SQLAction.SAVE)) ? SQLAction.SAVE : SQLAction.NOTHING);
		obj.setFromDatabase(true);
		
		
		if(compound.hasKey("entities") || compound.hasKey("entitys")) {
			NBTTagCompound armorStands = compound.hasKey("entitys") ? compound.getCompound("entitys") : compound.getCompound("entities");
			armorStands.c().stream().forEach(packet -> {
				NBTTagCompound metadata = armorStands.getCompound((String) packet);
				Location loc = locationFetcher(metadata.getCompound("Location"), world);
				FurnitureManager.getInstance().createFromType(metadata.getString("EntityType"), loc, obj).loadMetadata(metadata);
			});
		}else if(!FurnitureLib.isNewVersion() && compound.hasKey("ArmorStands")) {
			NBTTagCompound armorStands = compound.getCompound("ArmorStands");
			armorStands.c().stream().forEach(packet -> {
				NBTTagCompound metadata = armorStands.getCompound((String) packet);
				Location loc = locationFetcher(metadata.getCompound("Location"), world);
				FurnitureManager.getInstance().createFromType("armor_stand", loc, obj).loadMetadata(metadata);
			});
		}else {
			NBTTagCompound armorStands = Converter.convertPacketItemStack(compound.getCompound("ArmorStands"));
			armorStands.c().stream().forEach(packet -> {
				NBTTagCompound metadata = armorStands.getCompound((String) packet);
				Location loc = locationFetcher(metadata.getCompound("Location"), world);
				FurnitureManager.getInstance().createFromType("armor_stand", loc, obj).loadMetadata(metadata);
			});
			obj.setSQLAction(SQLAction.UPDATE);
		}
		
		return obj;
	}
	
	public static void autoPurge(int purgeTime) {
		FurnitureManager.getInstance().getObjectList().stream().filter(entry -> Objects.nonNull(entry.getUUID())).forEach(entry -> {
			Optional<DiceOfflinePlayer> player = FurnitureLib.getInstance().getPlayerCache().getPlayer(entry.getUUID());
			if(player.isPresent()) {
				long lastSeen = player.get().getLastSeen();
				if(lastSeen > 0) {
					if(FurnitureLib.getInstance().isAfterDate(lastSeen, purgeTime)) {
						if(FurnitureConfig.getFurnitureConfig().isPurgeRemove()) {
							FurnitureManager.getInstance().remove(entry);
						}else {
							entry.setSQLAction(SQLAction.REMOVE);
						}
					}
				}
			}
		});
	}

    public static Location locationFetcher(NBTTagCompound location, World world) {
        double X = location.getDouble("X");
        double Y = location.getDouble("Y");
        double Z = location.getDouble("Z");
        float Yaw = location.getFloat("Yaw");
        float Pitch = location.getFloat("Pitch");
        return new Location(world, X, Y, Z, Yaw, Pitch);
    }

    public static UUID uuidFetcher(String s) {
        if (s.equalsIgnoreCase("NULL")) {
            return null;
        }
        try {
            return UUID.fromString(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static HashSet<UUID> membersFetcher(NBTTagList nbtList) {
        HashSet<UUID> uuidList = new HashSet<UUID>();
        if (nbtList == null || nbtList.size() == 0) {
            return uuidList;
        }
        for (int i = 0; i < nbtList.size(); i++) {
            String string = nbtList.getString(i).replaceAll("\"", "");
            try {
                uuidList.add(UUID.fromString(string));
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        return uuidList;
    }
}
