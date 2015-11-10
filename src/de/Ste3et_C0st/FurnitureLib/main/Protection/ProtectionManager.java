package de.Ste3et_C0st.FurnitureLib.main.Protection;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.ProtectionLib.main.ProtectionLib;

public class ProtectionManager {

	Plugin plugin, FP;
	PluginManager manager;
	FurnitureLib lib;
	HashMap<Player, EventType> playerList = new HashMap<Player, EventType>();
	
	public ProtectionManager(Plugin plugin){
		this.lib = FurnitureLib.getInstance();
		this.plugin = plugin;
		this.manager = Bukkit.getPluginManager();
		if(Bukkit.getPluginManager().isPluginEnabled("ProtectionLib")){
			this.FP = Bukkit.getPluginManager().getPlugin("ProtectionLib");
		}
	}
	
	public boolean isSolid(Material m, int subID, PlaceableSide side){if(!checkPlaceable(m, subID, side)){return false;}{return m.isSolid();}}
	
	private boolean checkPlaceable(Material m, int subID, PlaceableSide side){
	    switch (m) {
			case WOOD_STAIRS: 
			if(subID>=4) return true;
			return false;
			case COBBLESTONE_STAIRS:
			if(subID>=4) return true;
			return false;
			case BRICK_STAIRS:
			if(subID>=4) return true;
			return false;
			case SMOOTH_STAIRS:
			if(subID>=4) return true;
			return false;
			case QUARTZ_STAIRS:
			if(subID>=4) return true;
			return false;
			case NETHER_BRICK_STAIRS:
			if(subID>=4) return true;
			return false;
			case SANDSTONE_STAIRS:
			if(subID>=4) return true;
			return false;
			case SPRUCE_WOOD_STAIRS:
			if(subID>=4) return true;
			return false;
			case BIRCH_WOOD_STAIRS:
			if(subID>=4) return true;
			return false;
			case JUNGLE_WOOD_STAIRS:
			if(subID>=4) return true;
			return false;
			case ACACIA_STAIRS:
			if(subID>=4) return true;
			return false;
			case DARK_OAK_STAIRS:
			if(subID>=4) return true;
			return false;
			case RED_SANDSTONE_STAIRS:
			if(subID>=4) return true;
			return false;
			case WOOD_STEP:
			if(subID>=8) return true;
			return false;
			case STEP:
			if(subID>=8) return true; 
			return false;
			case STONE_SLAB2:
			if(subID>=8) return true; 
			return false;
			case SOIL: return false;
			case ICE: return false;
			case GLOWSTONE: return false;
			case TNT: return false;
			case PISTON_BASE: return false;
			case STONE_PLATE: return false;
			case WOOD_PLATE: return false;
			case IRON_PLATE: return false;
			case GOLD_PLATE: return false;
			case IRON_BARDING: return false;
			case STATIONARY_WATER: if(side.equals(PlaceableSide.WATER)){return true;}
			case AIR: return true;
			case SNOW: 
			if(subID==7) return true;
			return false;
			default: return true;
		}
	}
	
	public boolean canBuild(Player p, Location loc){
		if(FP==null){return true;}
		ProtectionLib fp = (ProtectionLib) this.FP;
		return fp.canBuild(loc, p);
	}
	
	public Boolean isOwner(Player p, Location loc) {
		if(FP==null){return null;}
		ProtectionLib fp = (ProtectionLib) this.FP;
		return fp.isOwner(loc, p);
	}
	
	public boolean canBuild(Player p, ObjectID id, EventType type){
		if(p.isOp() || FurnitureLib.getInstance().hasPerm(p,"furniture.bypass.protection") || FurnitureLib.getInstance().hasPerm(p,"furniture.admin")){return true;}
		PublicMode publicMode = id.getPublicMode();
		UUID userID = p.getUniqueId();
		UUID ownerID = id.getUUID();
		if(ownerID!=null&&userID.equals(ownerID)){return true;}
		
		Boolean b = false;
		if(publicMode.equals(PublicMode.PRIVATE)){
			if(ownerID==null){b=false;}
			if(ownerID!=null&&userID!=null&&!ownerID.equals(userID)){b=false;}
		}else if(publicMode.equals(PublicMode.MEMBERS)){
			if(id.getMemberList().isEmpty()){b=false;}
			if(id.getMemberList().contains(userID)){
				b = isEventType(id, type);
			}
		}else if(publicMode.equals(PublicMode.PUBLIC)){
			b = isEventType(id, type);
		}
		if(!b){p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));}
		return b;
	}
	
	private boolean isEventType(ObjectID objID, EventType type){
		if(objID.getEventType().equals(type)||objID.getEventType().equals(EventType.BREAK_INTERACT)){
			return true;
		}
		return false;
	}
}
