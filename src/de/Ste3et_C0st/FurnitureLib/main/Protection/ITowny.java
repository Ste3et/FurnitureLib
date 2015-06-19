package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.palmergames.bukkit.towny.Towny;

public class ITowny {
	Towny api;
	public ITowny(PluginManager pm){
		api = (Towny) pm.getPlugin("Towny");
	}
	
	public boolean canBuild(Player p, Location l){
			if (TownyPermission.has(p, TownyPermission.PROTECTION_BYPASS)) return true;
			return TownyUtils.isPlotOwner(p, new Location[] { l });
	}
}
