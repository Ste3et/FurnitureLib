package de.Ste3et_C0st.FurnitureLib.Limitation;

import java.util.HashMap;

import org.bukkit.World;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;

public class LimitationObject {
	Integer x;
	Integer z;
	World w;
	HashMap<Project, Integer> projectHash = new HashMap<Project, Integer>();
	
	public Integer getX(){return this.x;}
	public Integer getZ(){return this.z;}
	public HashMap<Project, Integer> getHash(){return this.projectHash;}
	public World getWorld(){return this.w;}
	
	public LimitationObject(Integer x, Integer z, World w){
		this.x = x;
		this.z = z;
		this.w = w;
	}
	
	public void set(Project pro, Integer i){
		this.projectHash.put(pro, i);
	}
	
	public void add(Project pro){
		Integer i = 0;
		if(this.projectHash.containsKey(pro)){i = this.projectHash.get(pro);}
		i++;
		this.projectHash.put(pro, i);
	}
	
	public void remove(Project pro){
		Integer i = 0;
		if(this.projectHash.containsKey(pro)){i = this.projectHash.get(pro);}
		i--;
		if(i<0){return;}
		this.projectHash.put(pro, i);
	}
	
	public Integer getInteger(Project pro){
		Integer i = 0;
		if(this.projectHash.containsKey(pro)){i = this.projectHash.get(pro);}
		return i;
	}
}
