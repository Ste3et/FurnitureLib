package de.Ste3et_C0st.FurnitureLib.main;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldPool {

	private HashMap<String, World> worldList = new HashMap<String, World>();
	
	public World getWorld(String worldName){
		World world = null;
		if(worldList.containsKey(worldName)) world = worldList.get(worldName);
		return world;
	}
	
	public void loadWorlds(){
		if(!worldList.isEmpty()){
			for(World world : Bukkit.getServer().getWorlds()){
				worldList.put(world.getName(), world);
			}
		}
	}
	
	public boolean isExist(String worldName){
		return worldList.containsKey(worldName);
	}
}
