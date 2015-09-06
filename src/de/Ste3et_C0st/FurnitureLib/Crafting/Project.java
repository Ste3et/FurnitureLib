package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class Project{
	private String project;
	private CraftingFile file;
	private Plugin plugin;
	private Class<?> clas;
	private Integer witdh = 0;
	private Integer height = 0;
	private Integer length = 0;
	private config limitationConfig;
	private FileConfiguration limitationFile;
	private HashMap<World, Integer> limitationWorld = new HashMap<World, Integer>();
	private Integer chunkLimit = -1;
	private Integer playerLimit = -1;
	
	public String getName(){return project;}
	public Plugin getPlugin(){ return plugin;}
	public CraftingFile getCraftingFile(){return file;}
	public void setCraftingFile(CraftingFile file){this.file = file;}
	public Class<?> getclass(){return clas;}
	public Integer getWitdh(){return this.witdh;}
	public Integer getHeight(){return this.height;}
	public Integer getLength(){return this.length;}
	
	public Integer getAmountWorld(World w){if(limitationWorld.containsKey(w)){return limitationWorld.get(w);}else{return -1;}}
	public Integer getAmountChunk(){return this.chunkLimit;}
	public Integer getAmountPlayer(){return this.playerLimit;}
	public Integer hasPermissionsAmount(Player p){
		int i = -1;
		if(!permissionList.isEmpty()){
			for(String s : permissionList.keySet()){
				if(FurnitureLib.getInstance().hasPerm(p,s)){
					int j = permissionList.get(s);
					if(j>i){i = permissionList.get(s);}
				}
			}
		}
		return i;
	}
	public HashMap<String, Integer> permissionList = new HashMap<String, Integer>();
	
	public void setSize(Integer witdh, Integer height, Integer length){
		this.witdh = witdh;
		this.height = height;
		this.length = length;
	}
	
	public Project(String name, CraftingFile file, Plugin plugin, Class<?> clas){
		this.project = name;
		this.file = file;
		this.plugin = plugin;
		this.clas = clas;
		FurnitureLib.getInstance().getFurnitureManager().addProject(this);
		addDefaultWorld();
		addDefault("chunk");
		addDefault("player");
		this.chunkLimit = getDefault("chunk");
		this.playerLimit = getDefault("player");
	}
	
	public boolean hasPermissions(Player p){
		if(FurnitureLib.getInstance().hasPerm(p,"Furniture.Player") || FurnitureLib.getInstance().hasPerm(p,"Furniture.place." + project) || p.isOp() || FurnitureLib.getInstance().hasPerm(p,"Furniture.admin")){
			return true;
		}
		p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
		return false;
	}
	
	private void addDefaultWorld(){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig("world", "/limitation/");
		for(World w : Bukkit.getWorlds()){
			this.limitationFile.addDefault("Projects." + w.getName() + "." + getName(), -1);
		}
		this.limitationFile.options().copyDefaults(true);
		this.limitationConfig.saveConfig("world", this.limitationFile, "/limitation/");
		getAmountWorld();
	}
	
	private void getAmountWorld(){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig("world", "/limitation/");
		for(World w : Bukkit.getWorlds()){
			Integer i = this.limitationFile.getInt("Projects." + w.getName() + "." + getName());
			limitationWorld.put(w, i);
		}
	}

	private void addDefault(String conf){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
		this.limitationFile.addDefault("Projects." + getName(), -1);
		if(conf.equalsIgnoreCase("player")){
			if(!this.limitationFile.isSet("Projects." + getName())){
				this.limitationFile.addDefault("PermissionsLimit.test." + getName(), 10);
			}
		}
		this.limitationFile.options().copyDefaults(true);
		this.limitationConfig.saveConfig(conf, this.limitationFile, "/limitation/");
	}
	
	private Integer getDefault(String conf){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
		if(conf.equalsIgnoreCase("player")){
			if(this.limitationFile.isSet("PermissionsLimit")){
				if(this.limitationFile.isConfigurationSection("PermissionsLimit")){
					for(String s : this.limitationFile.getConfigurationSection("PermissionsLimit").getKeys(false)){
						if(this.limitationFile.isSet("PermissionsLimit." + s + "." + getName())){
							String permission = "furniture.limit." + s;
							Integer i = this.limitationFile.getInt("PermissionsLimit." + s + "." + getName());
							permissionList.put(permission, i);
						}
					}
				}
			}
		}
		return this.limitationFile.getInt("Projects." + getName());
	}
}
