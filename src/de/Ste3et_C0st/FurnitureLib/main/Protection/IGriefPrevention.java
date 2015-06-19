package de.Ste3et_C0st.FurnitureLib.main.Protection;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class IGriefPrevention {
	GriefPrevention api;
	
	public IGriefPrevention(PluginManager manager){
		api = (GriefPrevention) manager.getPlugin("GriefPrevention");
	}
	
	public boolean canBuild(Player p, Location l){
		if(!api.claimsEnabledForWorld(l.getWorld())){return true;}
		if(GriefPrevention.instance.dataStore.getPlayerData(p.getUniqueId()).ignoreClaims){return true;}
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(l, false, null);
		if(claim==null || claim.ownerID == null){return true;}
		if(claim.ownerID.equals(p.getUniqueId())){return true;}
		return false;
	}
}
