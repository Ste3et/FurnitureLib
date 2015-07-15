package de.Ste3et_C0st.FurnitureLib.Limitation;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class worldLimit {
	
	config c;
	FileConfiguration file;
	Plugin plugin;
	HashMap<World, HashMap<Project, Integer>> playerHash = new HashMap<World, HashMap<Project, Integer>>();
	
	public worldLimit(Plugin plugin){
		this.plugin = plugin;
	}
	
	public void add(World w, Project pro){
		if(!playerHash.containsKey(w)) playerHash.put(w, insertWorld(w));
		HashMap<Project, Integer> hash = playerHash.get(w);
		Integer i = 0;
		if(hash.containsKey(pro)) i = hash.get(pro);
		i++;
		hash.put(pro, i);
		playerHash.put(w, hash);
	}
	
	public void remove(World w, Project pro){
		if(!playerHash.containsKey(w)) return;
		HashMap<Project, Integer> hash = playerHash.get(w);
		Integer i = hash.get(pro);
		if(i==null||i<=0) return;
		i--;
		hash.put(pro, i);
		playerHash.put(w, hash);
	}
	
	public boolean canPlace(World w, Project pro){
			if(pro==null) return true;
			if(w==null) return true;
			if(pro.getAmountWorld(w)==null) return true;
			if(pro.getAmountWorld(w)==-1){return true;}
			if(pro.getAmountWorld(w)==0){return false;}
			if(getAmount(w, pro)>pro.getAmountWorld(w)){ return false;}
			return true;
	}
	
	private Integer getAmount(World w, Project pro){
		Integer i = 0;
		if(!playerHash.containsKey(w)) return i;
		if(!playerHash.get(w).containsKey(pro)) return i;
		return playerHash.get(w).get(pro);
	}
	
	public void save(){
		for(World w : Bukkit.getWorlds()){
			resetWorld(w);
			c = new config(plugin);
			file = c.getConfig(w.getName(), "/limitation/Worlds/");
			HashMap<Project, Integer> map = playerHash.get(w);
			if(map==null||map.isEmpty()) return;
			for(Project pro : map.keySet()){
				file.set("Project." + pro.getName(), map.get(pro));
			}
			c.saveConfig(w.getName(), file, "/limitation/Worlds/");
		}
	}
	
	private void resetWorld(World w){
			c = new config(plugin);
			file = c.getConfig(w.getName(), "/limitation/Worlds/");
			file.set("Project", null);
			c.saveConfig(w.getName(), file, "/limitation/Worlds/");
	}
	
	private HashMap<Project, Integer> insertWorld(World w){
		if(playerHash.containsKey(w)){return playerHash.get(w);}
		HashMap<Project, Integer> map = new HashMap<Project, Integer>();
		c = new config(plugin);
		file = c.getConfig(w.getName(), "/limitation/Worlds/");
		if(file!=null){
			if(!file.isSet("Project")) return map;
			for(String project : file.getConfigurationSection("Project").getKeys(false)){
				if(FurnitureLib.getInstance().getFurnitureManager().getProject(project)!=null){
					Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(project);
					Integer i = file.getInt("Project." + pro.getName());
					map.put(pro, i);
				}
			}
		}
		return map;
	}
}
