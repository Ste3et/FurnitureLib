package de.Ste3et_C0st.FurnitureLib.main.Protection;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.bukkit.BukkitUtil;

public class IPlotSquare {
	PluginManager manager;
	
	public IPlotSquare(PluginManager manager){this.manager= manager;}
	
	public boolean canBuild(Player p, Location loc){
		if(!PS.get().isPlotWorld(loc.getWorld().getName())){return true;}
		com.intellectualcrafters.plot.object.Location ploc = BukkitUtil.getLocation(loc);
		Plot plot = MainUtil.getPlot(ploc);
		if(plot==null){return false;}
		if(plot.isOwner(p.getUniqueId())){return true;}
		if(plot.isAdded(p.getUniqueId())){return true;}
		return false;
	}
}
