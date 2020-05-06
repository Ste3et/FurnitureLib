package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;

import de.Ste3et_C0st.FurnitureLib.Events.PaperEvents;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class BlockManager implements Listener {

    public HashSet<Location> locList = new HashSet<>();
    private List<Material> activatePhysic = Collections.singletonList(Material.TORCH);
    private Listener listener = null;
    
    public void addBlock(Block block) {
        if (block == null || block.getType() == null || block.getType().equals(Material.AIR)) return;
        locList.add(block.getLocation());
        if (Objects.isNull(listener) && activatePhysic.contains(block.getType())) {
        	try {
        		Class<?> clazz = Class.forName("com.destroystokyo.paper.event.block.BlockDestroyEvent");
        		if(Objects.isNull(clazz)) {
        			this.listener = this;
        		}else {
        			this.listener = new PaperEvents();
        		}
        	}catch (ClassNotFoundException e) {
        		this.listener = this;
			}catch (Exception ex) {
				ex.printStackTrace();
			}finally {
				if(Objects.nonNull(this.listener)) {
					Bukkit.getPluginManager().registerEvents(this.listener, FurnitureLib.getInstance());
				}
			}
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPhysics(BlockPhysicsEvent e) {
        if(!e.isCancelled()) {
        	boolean contains = locList.contains(e.getBlock().getLocation());
            if(contains) {
            	e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent e) {
    	if(!e.isCancelled()) {
    		boolean contains = locList.contains(e.getBlock().getLocation());
    		if(contains) {
    			e.setCancelled(true);
    		}
    	}
    }
    
    public boolean contains(Location location) {
    	return Objects.nonNull(getPresetLocation(location));
    }
    
    public Location getPresetLocation(Location location) {
    	Predicate<Location> predicate = entry -> 
    		entry.getWorld().getName().equals(location.getWorld().getName()) && 
    			location.getBlockX() == entry.getBlockX() && 
    			location.getBlockY() == entry.getBlockY() && 
    			location.getBlockZ() == entry.getBlockZ();
    	return this.locList.stream().filter(predicate).findFirst().orElse(null);
    }
}