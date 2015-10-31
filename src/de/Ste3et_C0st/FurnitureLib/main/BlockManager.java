package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

public class BlockManager{
	public List<Location> locList = new ArrayList<Location>();
	public BlockManager(){}
	
	public void addLocation(Location location) {
		locList.add(location);
	}
	
	public void destroy(List<Location> locList,boolean dropBlock){
		if(locList.isEmpty()){return;}
		for(Location loc : locList){
			if(loc.getBlock()==null||loc.getBlock().isEmpty()||loc.getBlock().getType()==null||loc.getBlock().getType().equals(Material.AIR)){
				continue;
			}
			if(dropBlock){
				loc.getBlock().breakNaturally();
				loc.getBlock().setType(Material.AIR);
			}else{
				loc.getBlock().setType(Material.AIR);
			}
			
			this.locList.remove(loc);
		}
		locList.clear();
	}

	public List<Location> getList() {return locList;}
}
