package de.Ste3et_C0st.FurnitureLib.main.Protection;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.intellectualcrafters.plot.api.PlotAPI;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class IPlotSquare {

	PlotAPI api;
	
	@SuppressWarnings("deprecation")
	public IPlotSquare(PluginManager manager){
		api = new PlotAPI(FurnitureLib.getInstance());
	}
	
	public boolean canBuild(Player p, Location loc){
		if(api.getPlotManager(loc.getWorld().getName())==null) return true;
		if(api.getPlot(loc)== null || api.getPlot(loc).isAdded(p.getUniqueId()) || api.getPlot(loc).isOwner(p.getUniqueId())) return true;
		return false;
	}
}
