package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

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
	
	public void addLight(final Location location, final Integer size){
		if(!enable){return;}
		if(location==null){return;}
		if(size==null){return;}
		Bukkit.getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run() {
				LightAPI.createLight(location, size, false);
				for(ChunkInfo info: LightAPI.collectChunks(location)){
					LightAPI.updateChunk(info);
				}
			}
		});
	}
	
	public void removeLight(Location location){
		if(!enable){return;}
		try{
			if(location == null) return;
			LightAPI.deleteLight(location, false);
			for(ChunkInfo info: LightAPI.collectChunks(location)){
				LightAPI.updateChunk(info);
			}
		}catch(Exception e){e.printStackTrace();}

	}
}
