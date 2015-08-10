package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class LanguageManager {

	String lang;
	Plugin plugin;
	HashMap<String, String> hash = new HashMap<String, String>();
	config c;
	FileConfiguration file;
	
	public LanguageManager(Plugin plugin, String lang){
		this.lang = lang;
		this.plugin = plugin;
		addDefault();
	}
	
	@SuppressWarnings("deprecation")
	private void addDefault(){
		String s = "";
		switch(lang){
			case "EN_en": s = "EN_en"; break;
			case "DE_de": s = "DE_de"; break;
			default: s = "EN_en";
		}
		c = new config(plugin);
		file = c.getConfig(lang, "/lang/");
		file.addDefaults(YamlConfiguration.loadConfiguration(plugin.getResource("language/" + s + ".yml")));
		file.options().copyDefaults(true);
		c.saveConfig(lang, file, "/lang/");
		if(file==null) return;
		for(String str : file.getConfigurationSection("message").getKeys(false)){
			String string = file.getString("message"+"."+str);
			hash.put(str, string);
		}
	}
	
	public String getString(String a){
		String b = hash.get(a);
		return ChatColor.translateAlternateColorCodes('&', b);
	}
	
	
}
