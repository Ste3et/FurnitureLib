package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import ru.BeYkeRYkt.LightAPI.LightAPI;
import ru.BeYkeRYkt.LightAPI.LightRegistry;

public class LightManager {

	Boolean enable=false;
	LightRegistry registry;
	Plugin plugin;
	
	public LightManager(Plugin plugin){
		if(Bukkit.getPluginManager().isPluginEnabled("LightAPI")){
			enable=true;
			this.plugin = plugin;
			registry = LightAPI.getRegistry(plugin);
			if(registry.isAutoUpdate()) registry.startAutoUpdate(20);
		}
	}
	
	public void addLight(Location location, Integer size){
		if(!enable){return;}
		registry.createLight(location, size);
		registry.collectChunks(location);
		registry.sendChunkChanges();
	}
	
	public void removeLight(Location location){
		if(!enable){return;}
		registry.deleteLight(location);
		registry.collectChunks(location);
		registry.sendChunkChanges();
	}
}
