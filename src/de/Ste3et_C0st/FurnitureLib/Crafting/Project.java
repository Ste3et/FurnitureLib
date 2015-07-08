package de.Ste3et_C0st.FurnitureLib.Crafting;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Project {

	private String project;
	private CraftingFile file;
	private Plugin plugin;
	
	public String getName(){return project;}
	public Plugin getPlugin(){ return plugin;}
	public CraftingFile getCraftingFile(){return file;}
	
	public Project(String name, CraftingFile file, Plugin plugin){
		this.project = name;
		this.file = file;
		this.plugin = plugin;
	}
	
	public boolean hasPermissions(Player p){
		if(p.hasPermission("Furniture.Player") || p.hasPermission("Furniture.use." + project) || p.isOp()){
			return true;
		}
		return false;
	}
}
