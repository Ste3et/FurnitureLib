package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class LanguageManager{

	private String lang;
	private Plugin plugin;
	private HashMap<String, String> hash = new HashMap<String, String>();
	
	private HashMap<String, List<String>> invHashList = new HashMap<String, List<String>>();
	private HashMap<String, Material> invMatList = new HashMap<String, Material>();
	private HashMap<String, String> invStringList = new HashMap<String, String>();
	private HashMap<String, Short> invShortList = new HashMap<String, Short>();
	private static LanguageManager instance;
	
	config c;
	FileConfiguration file;
	
	public LanguageManager(Plugin plugin, String lang){
		instance = this;
		//this.lang = lang;
		this.lang = lang;
		this.plugin = plugin;
		addDefault();
		addDefaultInv();
	}
	
	private void addDefault(){
		try{
			if(this.lang == null || this.lang.isEmpty()) lang = "EN_en";
			String s = "";
			
			if(plugin.getResource("language/" + lang + ".yml") != null){
				s = lang;
			}else{
				s = "EN_en";
			}
			
			c = new config(plugin);
			file = c.getConfig(lang, "/lang/");
			file.addDefaults(YamlConfiguration.loadConfiguration(FurnitureLib.getInstance().loadStream("language/" + s + ".yml")));
			file.options().copyDefaults(true);
			file.options().copyHeader(true);
			c.saveConfig(lang, file, "/lang/");
			
			file.getConfigurationSection("").getKeys(true).stream().forEach(key -> {
				if(key.startsWith(".")) key = key.replaceFirst(".", "");
				if(file.isString(key)) {
					String value = file.getString(key);
					hash.put(key.toLowerCase(), value);
				}else if(file.isList(key)) {
					String value = "";
					List<String> stringList = file.getStringList(key);
					int end = (stringList.size() - 1);
					for(String a : stringList) {
						if(stringList.indexOf(a) != end) {
							value+=a+"\n";
						}else {
							value+=a;
						}
					}
					hash.put(key.toLowerCase(), value);
				}else {
					hash.put(key.toLowerCase(), key.toLowerCase() + " is Missing");
				}
			});
		}catch(NullPointerException ex){
			ex.printStackTrace();
			return;
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void addDefault(String a, Configuration defaults){
		c = new config(plugin);
		file = c.getConfig(lang + a, "/lang/");
		file.addDefaults(defaults);
		file.options().copyDefaults(true);
		c.saveConfig(lang + a, file, "/lang/");
		for(String str : file.getConfigurationSection("message").getKeys(false)){
			String string = file.getString("message"+"."+str);
			hash.put(str, string);
		}
	}
	
	private void addDefaultInv(){
		c = new config(plugin);
		file = c.getConfig("inventoryManage", "");
		if(file==null) return;
		file.addDefaults(YamlConfiguration.loadConfiguration(FurnitureLib.getInstance().loadStream("manageInv.yml")));
		file.options().copyDefaults(true);
		c.saveConfig("inventoryManage", file, "");
		for(String str : file.getConfigurationSection("inv.mode").getKeys(false)){
			invHashList.put(str, file.getStringList("inv.mode." + str + ".Text"));
			invMatList.put(str, Material.valueOf(file.getString("inv.mode." + str + ".Material").toUpperCase()));
			invStringList.put(str, file.getString("inv.mode." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.mode." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.event").getKeys(false)){
			invMatList.put(str, Material.valueOf(file.getString("inv.event." + str + ".Material").toUpperCase()));
			invStringList.put(str, file.getString("inv.event." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.event." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.player").getKeys(false)){
			invMatList.put(str, Material.valueOf(file.getString("inv.player." + str + ".Material").toUpperCase()));
			invStringList.put(str, file.getString("inv.player." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.player." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.controller").getKeys(false)){
			invMatList.put(str, Material.valueOf(file.getString("inv.controller." + str + ".Material").toUpperCase()));
			invStringList.put(str, file.getString("inv.controller." + str + ".String"));
			invShortList.put(str,(short) file.getInt("inv.controller." + str + ".SubID"));
		}
		for(String str : file.getConfigurationSection("inv.admin").getKeys(false)){
			invMatList.put(str, Material.valueOf(file.getString("inv.admin." + str + ".Material").toUpperCase()));
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
		a = a.toLowerCase();
		if(hash.isEmpty()) return "§cHash is empty";
		if(!hash.containsKey(a)) return "§ckey not found";
		String b = hash.get(a);
		return ChatColor.translateAlternateColorCodes('&', b);
	}
	
	public String getString(String key, StringTranslater ... stringTranslaters) {
		String a = getString(key);
		if(stringTranslaters != null) {
			for(StringTranslater trans : stringTranslaters) {
				if(trans.getkey() != null && trans.getValue() != null) {
					a = a.replaceAll(trans.getkey(), trans.getValue());
				}
			}
		}
		return a;
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
	
	public static LanguageManager getInstance() {
		return instance;
	}
	
	public void addText(YamlConfiguration configuration) {
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(new File("plugin/FurnitureLib/lang/" + this.lang + ".yml"));
		conf.addDefaults(configuration);
		conf.options().copyDefaults(true);
		try {
			conf.save(new File("plugin/FurnitureLib/lang/" + this.lang + ".yml"));
			addDefault();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
