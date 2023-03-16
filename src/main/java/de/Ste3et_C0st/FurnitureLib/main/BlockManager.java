package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

import de.Ste3et_C0st.FurnitureLib.Listener.PaperEvents;
import de.Ste3et_C0st.FurnitureLib.Listener.physicsEvent;

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
        	try {
        		Class<?> clazz = Class.forName("com.destroystokyo.paper.event.block.BlockDestroyEvent");
        		if(Objects.isNull(clazz)) {
        			listener.add(new physicsEvent());
        		}else {
        			isPaper = true;
        			listener.add(new PaperEvents());
        		}
        	}catch (ClassNotFoundException e) {
        		listener.add(new physicsEvent());
			}catch (Exception ex) {
				ex.printStackTrace();
			}
        	listener.add(this);
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

        locList.stream().filter(loc -> loc.getBlock() != null && !loc.getBlock().getType().equals(Material.AIR)).forEach(
                loc -> {
                    if (dropBlock) {
                        loc.getBlock().breakNaturally();
                    } else {
                        loc.getBlock().setType(Material.AIR, false);
                    }
                    this.locList.remove(loc);
                }
        );

        locList.clear();
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