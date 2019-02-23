package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import ru.beykerykt.lightapi.LightAPI;

public class LightManager {

	private Plugin plugin = null;
	
	public LightManager(Plugin plugin){
		if(Bukkit.getPluginManager().isPluginEnabled("LightAPI")){
			if(Bukkit.getPluginManager().getPlugin("LightAPI").getDescription().getVersion().startsWith("3")){
				this.plugin = plugin;
			}else{
				FurnitureLib.getInstance().getLogger().warning("You use a old version of LightAPI this is not supportet: " + Bukkit.getPluginManager().getPlugin("LightAPI").getDescription().getVersion());
			}
		}
	}
	
	public void addLight(final Location location, final Integer size){
		if(this.plugin == null){return;}
		if(location==null){return;}
		if(size==null){return;}
		Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), () -> {
			LightAPI.createLight(location, size, false);
			LightAPI.collectChunks(location).stream().forEach(info -> LightAPI.updateChunk(info));
		});
	}
	
	public void removeLight(Location location){
		if(this.plugin == null){return;}
		try{
			if(location == null) return;
			LightAPI.deleteLight(location, false);
			LightAPI.collectChunks(location).stream().forEach(info -> LightAPI.updateChunk(info));
		}catch(Exception e){e.printStackTrace();}

	}
}
