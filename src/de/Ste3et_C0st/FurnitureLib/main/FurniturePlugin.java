package de.Ste3et_C0st.FurnitureLib.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class FurniturePlugin {
	
	private Plugin pluginInstance;
	
	public abstract void registerProjects();
	public abstract void applyPluginFunctions();
	public abstract void onFurnitureLateSpawn(ObjectID obj);
	
	public FurniturePlugin(Plugin pluginInstance) {
		this.pluginInstance = pluginInstance;
	}
	
	public Plugin getPlugin() {
		return this.pluginInstance;
	}
	
	public InputStream getResource(String filename) throws NullPointerException {
		return Objects.nonNull(getPlugin()) ? getPlugin().getResource(filename) : null;
	}
	
	private BufferedReader readRessource(String str){
		if(!str.startsWith("/")) str = "/" + str;
		InputStream stream = getPlugin().getClass().getResourceAsStream(str);
		try {
			return new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public YamlConfiguration saveRessource(String sourceName, String fileName) {
		File folder = new File("plugins/FurnitureLib/plugin");
		File file = new File(folder,  fileName + ".yml");
		if(!folder.exists()) folder.mkdirs();
		try {
			YamlConfiguration conf = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
			BufferedReader reader = readRessource(sourceName);
			if(Objects.nonNull(reader)) conf.addDefaults(YamlConfiguration.loadConfiguration(reader));
			conf.options().copyDefaults(true);
			conf.save(file);
			return conf;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void register() {
		if(!FurnitureLib.getFurniturePlugins().contains(this)) FurnitureLib.registerPlugin(this);
	}
	
}