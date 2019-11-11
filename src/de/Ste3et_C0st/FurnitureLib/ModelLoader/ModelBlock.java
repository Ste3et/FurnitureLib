package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class ModelBlock {

	protected ModelVector vector;
	
	public ModelBlock(ModelVector vector) {
		this.vector = vector;
	}
	
	public ModelBlock(YamlConfiguration yamlConfiguration, String key) {}
	
	public abstract Material getMaterial();
	public abstract void place(Location loc);
	public abstract void place(Location loc, BlockFace face);
	
	public ModelVector getVector() {
		return this.vector;
	}
}
