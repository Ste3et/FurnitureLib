package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import ru.beykerykt.lightapi.LightAPI;

public class LightManager {

	Boolean enable=false;
	Plugin plugin;
	
	public LightManager(Plugin plugin){
		if(Bukkit.getPluginManager().isPluginEnabled("LightAPI")){
			if(Bukkit.getPluginManager().getPlugin("LightAPI").getDescription().getVersion().startsWith("3")){
				enable=true;
				this.plugin = plugin;
			}else{
				FurnitureLib.getInstance().getLogger().warning("You use a old version of LightAPI this is not supportet: " + Bukkit.getPluginManager().getPlugin("LightAPI").getDescription().getVersion());
			}
		}
	}
	
	public void addLight(Location location, Integer size){
		if(!enable){return;}
		LightAPI.createLight(location, size, false);
		LightAPI.updateChunks(location, location.getWorld().getPlayers());
	}
	
	public void removeLight(Location location){
		if(!enable){return;}
		try{
			LightAPI.deleteLight(location, false);
			LightAPI.updateChunks(location, location.getWorld().getPlayers());
		}catch(Exception e){e.printStackTrace();}

	}
}
