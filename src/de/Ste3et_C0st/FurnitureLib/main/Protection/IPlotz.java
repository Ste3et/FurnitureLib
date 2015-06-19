package de.Ste3et_C0st.FurnitureLib.main.Protection;

import me.kyle.plotz.api.Plotz;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class IPlotz {

	Plotz plotz;
	
	public IPlotz(PluginManager manager){
		plotz = (Plotz) manager.getPlugin("Plotz");
	}
	
	@SuppressWarnings("static-access")
	public boolean canBuild(Player p, Location loc){
		if(plotz.getPlotByLocation(loc)==null) return true;
		if(plotz.getPlotByLocation(loc).isOwner(p)) return true;
		if(plotz.getPlotByLocation(loc).isAllowed(p)) return true;
		if(plotz.getPlotByLocation(loc).isAdmin(p)) return true;
		return false;
	}
}
