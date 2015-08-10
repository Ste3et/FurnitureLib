package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class Project {

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
	private Integer plotLimit = -1;
	private Integer chunkLimit = -1;
	private Integer playerLimit = -1;
	
	public String getName(){return project;}
	public Plugin getPlugin(){ return plugin;}
	public CraftingFile getCraftingFile(){return file;}
	public Class<?> getclass(){return clas;}
	public Integer getWitdh(){return this.witdh;}
	public Integer getHeight(){return this.height;}
	public Integer getLength(){return this.length;}
	
	public Integer getAmountWorld(World w){return limitationWorld.get(w);}
	public Integer getAmountChunk(){return this.chunkLimit;}
	public Integer getAmountPlot(){return this.plotLimit;}
	public Integer getAmountPlayer(){return this.playerLimit;}
	
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
		addDefault("plot");
		addDefault("chunk");
		//addDefault("player");
		this.plotLimit = getDefault("plot");
		this.chunkLimit = getDefault("chunk");
		//this.playerLimit = getDefault("player");
	}
	
	public boolean hasPermissions(Player p){
		if(p.hasPermission("Furniture.Player") || p.hasPermission("Furniture.use." + project) || p.isOp()){
			return true;
		}
		return false;
	}
	
	private void addDefaultWorld(){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig("gobal", "/limitation/");
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
		this.limitationFile.options().copyDefaults(true);
		this.limitationConfig.saveConfig(conf, this.limitationFile, "/limitation/");
	}
	
	private Integer getDefault(String conf){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
		return this.limitationFile.getInt("Projects." + getName());
	}
}
