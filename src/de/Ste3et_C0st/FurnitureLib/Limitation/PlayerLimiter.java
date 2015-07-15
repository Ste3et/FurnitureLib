package de.Ste3et_C0st.FurnitureLib.Limitation;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class PlayerLimiter {
	
	config c;
	FileConfiguration file;
	Plugin plugin;
	HashMap<UUID, HashMap<Project, Integer>> playerHash = new HashMap<UUID, HashMap<Project, Integer>>();
	
	public PlayerLimiter(Plugin plugin){
		this.plugin = plugin;
	}
	
	public void add(Player p, Project pro){
		if(!playerHash.containsKey(p.getUniqueId())) playerHash.put(p.getUniqueId(), insertWorld(p.getUniqueId()));
		HashMap<Project, Integer> hash = playerHash.get(p.getUniqueId());
		Integer i = 0;
		if(hash.containsKey(pro)) i = hash.get(pro);
		i++;
		hash.put(pro, i);
		playerHash.put(p.getUniqueId(), hash);
	}
	
	public void remove(Player p, Project pro){
		if(!playerHash.containsKey(p.getUniqueId())) return;
		HashMap<Project, Integer> hash = playerHash.get(p.getUniqueId());
		Integer i = hash.get(pro);
		if(i==null||i<=0) return;
		i--;
		hash.put(pro, i);
		playerHash.put(p.getUniqueId(), hash);
	}
	
	public boolean canPlace(Player p, Project pro){
			if(pro==null) return true;
			if(p==null) return true;
			if(pro.getAmountPlayer()==null) return true;
			if(pro.getAmountPlayer()==-1){return true;}
			if(pro.getAmountPlayer()==0){return false;}
			if(getAmount(p, pro)>pro.getAmountPlayer()){ return false;}
			return true;
	}
	
	private Integer getAmount(Player p, Project pro){
		Integer i = 0;
		if(!playerHash.containsKey(p.getUniqueId())) return i;
		if(!playerHash.get(p.getUniqueId()).containsKey(pro)) return i;
		return playerHash.get(p.getUniqueId()).get(pro);
	}
	
	public void save(){
		for(UUID uuid : playerHash.keySet()){
			resetWorld(uuid);
			c = new config(plugin);
			file = c.getConfig(uuid.toString(), "/limitation/players/");
			HashMap<Project, Integer> map = playerHash.get(uuid);
			if(map==null||map.isEmpty()) return;
			for(Project pro : map.keySet()){
				file.set("Project." + pro.getName(), map.get(pro));
			}
			c.saveConfig(uuid.toString(), file, "/limitation/players/");
		}
	}
	
	private void resetWorld(UUID uuid){
			c = new config(plugin);
			file = c.getConfig(uuid.toString(), "/limitation/players/");
			file.set("Project", null);
			c.saveConfig(uuid.toString(), file, "/limitation/players/");
	}
	
	private HashMap<Project, Integer> insertWorld(UUID uuid){
		if(playerHash.containsKey(uuid.toString())){return playerHash.get(uuid.toString());}
		HashMap<Project, Integer> map = new HashMap<Project, Integer>();
		c = new config(plugin);
		file = c.getConfig(uuid.toString(), "/limitation/players/");
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
