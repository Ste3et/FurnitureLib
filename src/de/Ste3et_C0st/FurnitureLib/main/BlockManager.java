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
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockManager implements Listener{
	
	public HashSet<Location> locList = new HashSet<Location>();
	private List<Material> activatePhysic = Arrays.asList(Material.TORCH);
	private boolean isActive = false;
	public BlockManager(){}
	
	public void addBlock(Block block) {
		if(block == null || block.getType() == null || block.getType().equals(Material.AIR)) return;
		locList.add(block.getLocation());
		if(!isActive && activatePhysic.contains(block.getType())){
			Bukkit.getPluginManager().registerEvents(this, FurnitureLib.getInstance());
			isActive = true;
		}
	}

	public void destroy(HashSet<Location> locList,boolean dropBlock){
		if(locList.isEmpty()){return;}

		locList.stream().filter(loc -> loc.getBlock() != null && !loc.getBlock().getType().equals(Material.AIR)).forEach(
				loc -> {
					if(dropBlock) {
						loc.getBlock().breakNaturally();
					}else {
						loc.getBlock().setType(Material.AIR, false);
					}
					this.locList.remove(loc);
				}
		);
		
		locList.clear();
	}

	public HashSet<Location> getList() {return locList;}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPhysics(BlockPhysicsEvent e){
		  Block b = e.getBlock();
		  e.setCancelled(locList.contains(b.getLocation()));
	}
	
	@EventHandler
	public void onWaterFlow(BlockFromToEvent e){
		e.setCancelled(locList.contains(e.getBlock().getLocation()));
	}
}
