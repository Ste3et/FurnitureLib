package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import ru.BeYkeRYkt.LightAPI.LightAPI;

public class LightManager {

	List<Location> locationlist = new ArrayList<Location>();
	Boolean enable=false;
	
	public LightManager(){
		if(Bukkit.getPluginManager().isPluginEnabled("LightAPI")){
			enable=true;
		}
	}
	
	public void addLight(Location location, Integer size){
		if(!enable){return;}
		locationlist.add(location);
		LightAPI.createLight(location, size);
	}
	
	public void removeLight(Location location){
		if(!enable){return;}
		if(!locationlist.contains(location)){return;}
		System.out.println("location found");
		LightAPI.deleteLight(location);
		LightAPI.updateChunks(location);
		locationlist.remove(location);
	}
	
	public void clearAll(){
		if(!enable){return;}
		if(locationlist.isEmpty()){return;}
		for(Location l : locationlist){
			LightAPI.deleteLight(l);
			LightAPI.updateChunks(l);
		}
		locationlist.clear();
	}
}
