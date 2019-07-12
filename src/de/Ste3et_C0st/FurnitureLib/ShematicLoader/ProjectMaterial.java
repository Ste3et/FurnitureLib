package de.Ste3et_C0st.FurnitureLib.ShematicLoader;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class ProjectMaterial {

	private String str;
	private Location loc;
	private BlockData data;
	
	public ProjectMaterial(String str, Location loc, BlockData data) {
		this.str = str;
		this.loc = loc;
		this.data = data;
	}
	
	public String getKey() {
		return this.str;
	}
	
	public Location getLocation() {
		return this.loc;
	}
	
	public Material getMaterial() {
		return this.data.getMaterial();
	}
	
	public BlockData getData() {
		return this.data;
	}
}
