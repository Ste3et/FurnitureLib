package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class IWorldGuard {
	WorldGuardPlugin plugin;
	
	public IWorldGuard(PluginManager manager){
		this.plugin = (WorldGuardPlugin) manager.getPlugin("WorldGuard");
	}
	
	public boolean canBuild(Player p, Location loc){
		return plugin.canBuild(p, loc);
	}
}
