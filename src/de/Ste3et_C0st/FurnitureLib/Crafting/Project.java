package de.Ste3et_C0st.FurnitureLib.Crafting;

import org.bukkit.entity.Player;

public class Project {

	private String project;
	private CraftingFile file;
	
	public String getName(){return project;}
	public CraftingFile getCraftingFile(){return file;}
	
	public Project(String name, CraftingFile file){
		this.project = name;
		this.file = file;
	}
	
	public boolean hasPermissions(Player p){
		if(p.hasPermission("Furniture.Player") || p.hasPermission("Furniture.use." + project) || p.isOp()){
			return true;
		}
		return false;
	}
}
