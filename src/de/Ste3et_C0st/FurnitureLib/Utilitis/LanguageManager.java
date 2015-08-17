package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class LanguageManager{

	String lang;
	Plugin plugin;
	HashMap<String, String> hash = new HashMap<String, String>();
	
	HashMap<String, List<String>> invHashList = new HashMap<String, List<String>>();
	HashMap<String, Material> invMatList = new HashMap<String, Material>();
	HashMap<String, String> invStringList = new HashMap<String, String>();
	HashMap<String, Short> invShortList = new HashMap<String, Short>();
	
	config c;
	FileConfiguration file;
	
	public LanguageManager(Plugin plugin, String lang){
		this.lang = lang;
		this.plugin = plugin;
		addDefault();
		addDefaultInv();
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
	
	@SuppressWarnings("deprecation")
	private void addDefaultInv(){
		c = new config(plugin);
		file = c.getConfig("manageInv", "");
		file.addDefaults(YamlConfiguration.loadConfiguration(plugin.getResource("manageInv.yml")));
		file.options().copyDefaults(true);
		c.saveConfig("manageInv", file, "");
		if(file==null) return;
		for(String str : file.getConfigurationSection("inv.mode").getKeys(false)){
			invHashList.put(str, file.getStringList("inv.mode." + str + ".Text"));
			invMatList.put(str, Material.getMaterial(file.getInt("inv.mode." + str + ".Material")));
			invStringList.put(str, file.getString("inv.mode." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.mode." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.event").getKeys(false)){
			invMatList.put(str, Material.getMaterial(file.getInt("inv.event." + str + ".Material")));
			invStringList.put(str, file.getString("inv.event." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.event." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.player").getKeys(false)){
			invMatList.put(str, Material.getMaterial(file.getInt("inv.player." + str + ".Material")));
			invStringList.put(str, file.getString("inv.player." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.player." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.controller").getKeys(false)){
			invMatList.put(str, Material.getMaterial(file.getInt("inv.controller." + str + ".Material")));
			invStringList.put(str, file.getString("inv.controller." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.controller." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.admin").getKeys(false)){
			invMatList.put(str, Material.getMaterial(file.getInt("inv.admin." + str + ".Material")));
			invStringList.put(str, file.getString("inv.admin." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.admin." + str + ".SubID"));
			invHashList.put(str, file.getStringList("inv.admin." + str + ".Text"));
		}
		invStringList.put("manageInvName", file.getString("inv.manageInvName"));
		invStringList.put("playerAddInvName", file.getString("inv.playerAddInvName"));
		invStringList.put("playerRemoveInvName", file.getString("inv.playerRemoveInvName"));
		invStringList.put("playerSetInvName", file.getString("inv.playerSetInvName"));
	}
	
	
	public String getString(String a){
		String b = hash.get(a);
		return ChatColor.translateAlternateColorCodes('&', b);
	}

	public List<String> getStringList(String a) {
		if(!invHashList.containsKey(a)){return null;}
		List<String> b = invHashList.get(a);
		Integer i = 0;
		for(String str : b){
			b.set(i, ChatColor.translateAlternateColorCodes('&', str));
			i++;
		}
		return b;
	}
	
	public String getName(String a){
		String b = invStringList.get(a);
		return ChatColor.translateAlternateColorCodes('&', b);
	}
	
	public Short getShort(String a){
		return invShortList.get(a);
	}

	public Material getMaterial(String a){
		return invMatList.get(a);
	}
}
