package de.Ste3et_C0st.FurnitureLib.Limitation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;

public class LimitationManager {

	worldLimit wLimit;
	PlotSquaredLimit plotSqLimit;
	PlotMeLimit plotmeLimit;
	PlotzLimit plotzLimit;
	ChunkLimit chunkLimit;
	//PlayerLimiter playerLimit;
	
	public LimitationManager(Plugin plugin){
		wLimit = new worldLimit(plugin);
		if(Bukkit.getPluginManager().isPluginEnabled("PlotSquared")) plotSqLimit = new PlotSquaredLimit(plugin);
		if(Bukkit.getPluginManager().isPluginEnabled("PlotME")) plotmeLimit = new PlotMeLimit(plugin);
		if(Bukkit.getPluginManager().isPluginEnabled("Plotz")) plotzLimit = new PlotzLimit(plugin);
		chunkLimit = new ChunkLimit(plugin);
		//playerLimit = new PlayerLimiter(plugin);
	}
	
	public void add(Location loc, Project pro){
		wLimit.add(loc.getWorld(), pro);
		if(plotSqLimit!=null) plotSqLimit.add(loc, pro);
		if(plotmeLimit!=null) plotmeLimit.add(loc, pro);
		if(plotzLimit!=null) plotzLimit.add(loc, pro);
		chunkLimit.add(loc, pro);
		//playerLimit.add(p, pro);
	}
	
	public void remove(Location loc, Project pro){
		wLimit.remove(loc.getWorld(), pro);
		if(plotSqLimit!=null) plotSqLimit.remove(loc, pro);
		if(plotmeLimit!=null) plotmeLimit.remove(loc, pro);
		if(plotzLimit!=null) plotzLimit.remove(loc, pro);
		chunkLimit.remove(loc, pro);
		//playerLimit.remove(p, pro);
	}

	public void save() {
		wLimit.save();
		if(plotSqLimit!=null) plotSqLimit.save();
		if(plotmeLimit!=null) plotmeLimit.save();
		if(plotzLimit!=null) plotzLimit.save();
		chunkLimit.save();
		//playerLimit.save();
	}

	public boolean canPlace(Location loc, Project pro, Player p) {
		boolean wL = wLimit.canPlace(loc.getWorld(), pro);
		boolean plotSQ = true;
		boolean plotMe = true;
		boolean plotzL = true;
		
		if(plotSqLimit!=null) plotSQ = plotSqLimit.canPlace(loc, pro);
		if(plotmeLimit!=null) plotSQ = plotmeLimit.canPlace(loc, pro);
		if(plotzLimit!=null) plotSQ = plotzLimit.canPlace(loc, pro);
		
		boolean chunkLimi = chunkLimit.canPlace(loc, pro);
		//boolean playerLimi = playerLimit.canPlace(p, pro);
		if(wL&&plotSQ&&plotMe&&plotzL&&chunkLimi){
			return true;
		}
		return false;
	}
}
