package de.Ste3et_C0st.FurnitureLib.main.Protection;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;

public class ProtectionManager {

	Plugin plugin;
	PluginManager manager;
	FurnitureLib lib;
	HashMap<Player, EventType> playerList = new HashMap<Player, EventType>();
	
	public ProtectionManager(Plugin plugin){
		this.lib = FurnitureLib.getInstance();
		this.plugin = plugin;
		this.manager = Bukkit.getPluginManager();
	}
	
	private boolean isSolid(Material m, int subID){if(!checkPlaceable(m, subID)) return false;return m.isSolid();}
	
	private boolean checkPlaceable(Material m, int subID){
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
			case SNOW: 
			if(subID==7) return true;
			return false;
			default: return true;
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public boolean canBuild(Player p, Location loc, EventType type){
		Block b = loc.getBlock();
		BlockFace face = lib.getLocationUtil().yawToFace(p.getLocation().getYaw()).getOppositeFace();
		if(b!=null&&!isSolid(loc.getBlock().getType(), loc.getBlock().getData())) return false;
		ItemStack is = p.getItemInHand();
		if(b==null){Bukkit.broadcastMessage("block=null");return true;}
		if(is==null){Bukkit.broadcastMessage("is=null");return true;}
		if(face==null){Bukkit.broadcastMessage("face=null");return true;}
		if(type==null){Bukkit.broadcastMessage("type=null");return true;}
		
		switch(type){
		case BREAK: 
			BlockBreakEvent event = new BlockBreakEvent(b,p);
			Bukkit.getPluginManager().callEvent(event);
			return !event.isCancelled();
		case PLACE:
			BlockPlaceEvent ev = new BlockPlaceEvent(b, b.getState(), b, p.getItemInHand(), p, true);
			Bukkit.getPluginManager().callEvent(ev);
			return !ev.isCancelled();
		case INTERACT:
			ev = new BlockPlaceEvent(b, b.getState(), b, p.getItemInHand(), p, true);
			Bukkit.getPluginManager().callEvent(ev);
			return !ev.isCancelled();
		default: return true;
		}
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
