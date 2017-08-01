package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockManager implements Listener{
	
	public HashSet<Location> locList = new HashSet<Location>();
	private List<Material> activatePhysic = Arrays.asList(Material.TORCH, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON,
			  Material.BED_BLOCK, Material.SIGN, Material.WALL_SIGN, Material.SIGN_POST);
	
	public BlockManager(){}
	
	public void addBlock(Block block) {
		if(block == null || block.getType() == null || block.getType().equals(Material.AIR)) return;
		locList.add(block.getLocation());
		if(activatePhysic.contains(block.getType())){
			Bukkit.getPluginManager().registerEvents(this, FurnitureLib.getInstance());
		}
	}

	public void destroy(HashSet<Location> locList,boolean dropBlock){
		if(locList.isEmpty()){return;}
		for(Location loc : locList){
			if(loc.getBlock()==null||loc.getBlock().isEmpty()||loc.getBlock().getType()==null||loc.getBlock().getType().equals(Material.AIR)){
				continue;
			}
			if(dropBlock){
				loc.getBlock().breakNaturally();
				loc.getBlock().setType(Material.AIR);
			}else{
				loc.getBlock().setType(Material.AIR);
			}
			
			this.locList.remove(loc);
		}
		locList.clear();
	}

	public HashSet<Location> getList() {return locList;}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onPhysiks(BlockPhysicsEvent e){
		  Block b = e.getBlock();
		  if(b == null || locList.isEmpty()) return;
		  if(activatePhysic.contains(b.getType())){
			  if(locList.contains(b.getLocation())) e.setCancelled(true);
		  }
	}
}
