package de.Ste3et_C0st.FurnitureLib.main.Protection;

import me.kyle.plotz.api.Plotz;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class IPlotz {

	
	public IPlotz(PluginManager manager){}
	
	public boolean canBuild(Player p, Location loc){
		if(Plotz.getPlotByLocation(loc)==null) return true;
		if(Plotz.getPlotByLocation(loc).isOwner(p)) return true;
		if(Plotz.getPlotByLocation(loc).isAllowed(p)) return true;
		if(Plotz.getPlotByLocation(loc).isAdmin(p)) return true;
		return false;
	}
}
