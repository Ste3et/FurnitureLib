package de.Ste3et_C0st.FurnitureLib.main.Protection;

import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class IPreciousStones {
	PreciousStones api;
	public IPreciousStones(PluginManager manager){
		api = (PreciousStones) manager.getPlugin("PreciousStones");
	}
	
	public boolean canBuild(Player p, Location l){
		return PreciousStones.API().canPlace(p, l);
	}
}
