package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import de.Ste3et_C0st.FurnitureLib.Listener.physicsEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SchedularHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class BlockManager implements Listener {

    public HashSet<Location> locList = new HashSet<>();
    private List<Listener> listener = new ArrayList<Listener>();
    private boolean isPaper = false;
    
    public void addBlock(Block block) {
        if (block == null || block.getType() == null) return;
        locList.add(block.getLocation());
    }
    
    public BlockManager() {
    	this.registerBlockEvents();
    }
    
    public void registerBlockEvents() {
    	if (listener.isEmpty()) {
    		if(FurnitureLib.isPaper()) {
    			isPaper = true;
    			try {
    				Class<?> clazz = Class.forName("de.Ste3et_C0st.FurnitureLib.Paper.PaperEvents");
    				listener.add((Listener) clazz.newInstance());
    			}catch (Exception e) {
					e.printStackTrace();
				}
    		}else {
    			listener.add(new physicsEvent());
    		}
        	
        	this.listener.add(this);
        	this.listener.forEach(handler -> Bukkit.getPluginManager().registerEvents(handler, FurnitureLib.getInstance()));
        }
    }

    public void addBlock(Location loc) {
        if (loc == null) return;
        locList.add(loc);
    }

    public void destroy(HashSet<Location> locList, boolean dropBlock) {
        if (locList.isEmpty()) {
            return;
        }
        
        if(FurnitureLib.isFolia()) {
        	final Location startLocation = locList.stream().findFirst().get();
        	SchedularHelper.regionTask(() -> {
        		locList.stream().filter(loc -> loc.getBlock() != null && loc.getBlock().getType() != Material.AIR).forEach(
                        loc -> {
                            if (dropBlock) {
                                loc.getBlock().breakNaturally();
                            } else {
                                loc.getBlock().setType(Material.AIR, false);
                            }
                        }
                );
        	}, startLocation, true);
        }else {
        	locList.stream().filter(loc -> loc.getBlock() != null && loc.getBlock().getType() != Material.AIR).forEach(
                    loc -> {
                        if (dropBlock) {
                            loc.getBlock().breakNaturally();
                        } else {
                            loc.getBlock().setType(Material.AIR, false);
                        }
                    }
            );
        }
        
        this.locList.removeAll(locList);
    }

    public HashSet<Location> getList() {
        return locList;
    }

    

    @EventHandler(priority = EventPriority.LOW)
    public void onWaterFlow(BlockFromToEvent e) {
    	if(!e.isCancelled()) {
    		if(locList.contains(e.getBlock().getLocation())) e.setCancelled(true);
    		if(locList.contains(e.getToBlock().getLocation())) e.setCancelled(true);
    	}
    }
    
    public boolean contains(Location location) {
    	return Objects.nonNull(getPresetLocation(location));
    }
    
    public boolean contains(Block block) {
    	return block != null && this.contains(block.getLocation());
    }
    
    public Location getPresetLocation(Location location) {
    	Predicate<Location> predicate = entry -> entry.getWorld().getName().equals(location.getWorld().getName()) && 
    	location.getBlockX() == entry.getBlockX() && 
    	location.getBlockY() == entry.getBlockY() && 
    	location.getBlockZ() == entry.getBlockZ();
    	return this.locList.stream().filter(predicate).findFirst().orElse(null);
    }

	public boolean isPaper() {
		return isPaper;
	}
}