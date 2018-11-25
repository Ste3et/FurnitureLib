package de.Ste3et_C0st.FurnitureLib.ShematicLoader;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;

public class ProjectMaterial {

	private Material mat;
	private BlockFace face = BlockFace.NORTH;
	private boolean rootable = false;
	private Inventory inv = null;
	
	public ProjectMaterial(Material mat){
		this.mat = mat;
	}
	
	public ProjectMaterial(Material mat, BlockFace face){
		this.mat = mat;
		this.face = face;
		this.rootable = true;
	}
	
	public BlockFace getBlockFace(){return this.face;}
	public Material getMaterial(){return this.mat;}
	public Inventory getInventory(){return this.inv;}
	public boolean isDirectional(){return this.rootable;}
	public void setBlockFace(BlockFace face) {this.face = face;this.rootable = true;}
	public void setInventory(Inventory inv) {this.inv = inv;}
}
