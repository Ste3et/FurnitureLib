package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.engine.EngineMain;
import com.massivecraft.massivecore.ps.PS;

public class IFactions {

	
	public IFactions(PluginManager manager){}
	
	public boolean canBuild(Player p, Location l){
		return EngineMain.canPlayerBuildAt(p, PS.valueOf(l.getBlock()), true);
	}
}
