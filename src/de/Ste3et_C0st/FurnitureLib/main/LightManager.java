package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import ru.beykerykt.lightapi.chunks.ChunkInfo;
import ru.beykerykt.lightapi.chunks.Chunks;
import ru.beykerykt.lightapi.light.Lights;

public class LightManager {

	Boolean enable=false;
	Plugin plugin;
	
	public LightManager(Plugin plugin){
		if(Bukkit.getPluginManager().isPluginEnabled("LightAPI")){
			enable=true;
			this.plugin = plugin;
		}
	}
	
	public void addLight(Location location, Integer size){
		if(!enable){return;}
		Lights.createLight(location, size, false);
		for (ChunkInfo info : Chunks.collectModifiedChunks(location)) Chunks.sendChunkUpdate(info);
	}
	
	public void removeLight(Location location){
		if(!enable){return;}
		try{
			Lights.deleteLight(location, false);
			for (ChunkInfo info : Chunks.collectModifiedChunks(location)) Chunks.sendChunkUpdate(info);
		}catch(Exception e){e.printStackTrace();}

	}
}
