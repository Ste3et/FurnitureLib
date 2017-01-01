package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public abstract class ProjectSettings {

	private HashMap<String, List<String>> metaDatas = new HashMap<String, List<String>>();
	
	public void clear(){metaDatas.clear();}
	public void remove(String key){if(metaDatas.containsKey(key)) metaDatas.remove(key);}
	public List<String> getMetadatas(String key){if(metaDatas.containsKey(key)){return metaDatas.get(key);}return null;}
	
	public void addMetadata(String key, String value){
		List<String> metadatas = new ArrayList<String>();
		if(metaDatas.containsKey(key)) metadatas = this.metaDatas.get(key);
		metadatas.add(value);
		metaDatas.put(key, metadatas);
	}
	
	public void saveMetadata(String name){
		config c = new config(FurnitureLib.getInstance());
		if(!name.endsWith(".yml")) name += ".yml";
		File f = new File("plugins/FurnitureLib/Crafting/", name);
		if(f.exists()){
			FileConfiguration config = c.getConfig(name, "/Crafting/");
			name.replace(".yml", "");
			String header = getHeader(config, name);
			if(!metaDatas.isEmpty()){
				System.out.println("name, metadata saving");
				for(String key : metaDatas.keySet()){
					config.set(header + ".metadata." + key, metaDatas.get(key));
				}
				c.saveConfig(name, config, "/Crafting/");
			}
		}
	}
	
	public void loadConfig(String name){
		config c = new config(FurnitureLib.getInstance());
		if(!name.endsWith(".yml")) name += ".yml";
		File f = new File("/Crafting/", name);
		if(f.exists()){
			FileConfiguration config = c.getConfig(name, "/Crafting/");
			name.replace(".yml", "");
			String header = getHeader(config, name);
			if(config.isSet(header + ".metadata")){
				for(String s : config.getConfigurationSection(header + ".metadata").getKeys(false)){
					this.metaDatas.put(s, config.getStringList(header + ".metadata." + s));
				}
			}
		}
	}
	
	public String getHeader(FileConfiguration file, String name){
		try{
			return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];
		}catch(ArrayIndexOutOfBoundsException ex){
			return name;
		}
	}
}
