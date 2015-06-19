package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.api.ILocation;
import com.worldcretornica.plotme_core.api.IWorld;
import com.worldcretornica.plotme_core.bukkit.PlotMe_CorePlugin;
import com.worldcretornica.plotme_core.bukkit.api.BukkitLocation;
import com.worldcretornica.plotme_core.bukkit.api.BukkitWorld;

public class IPlotME {

	PlotMeCoreManager api;
	
	public IPlotME(PluginManager manager){
		PlotMe_CorePlugin corePlugin = (PlotMe_CorePlugin) manager.getPlugin("PlotME");
		api = corePlugin.getAPI().getPlotMeCoreManager();
	}
	
	@SuppressWarnings("static-access")
	public boolean canBuild(Player p, Location loc){
		IWorld world = new BukkitWorld(loc.getWorld());
		if(world==null||!api.isPlotWorld(world)) return true;
		ILocation location = new BukkitLocation(loc);
		String plotID = api.getPlotId(location);
		if(plotID==null||plotID.isEmpty()) return true;
		Plot plot = api.getPlotById(plotID, world);
		return plot.isAllowed(p.getUniqueId());
	}
}
