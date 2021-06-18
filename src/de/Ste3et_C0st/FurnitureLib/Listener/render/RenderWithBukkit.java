package de.Ste3et_C0st.FurnitureLib.Listener.render;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class RenderWithBukkit extends RenderEventHandler implements Listener{
	
	@Override
	public void register() {
		Bukkit.getPluginManager().registerEvents(this, FurnitureLib.getInstance());
	}

	@Override
	public void remove() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getHealth() <= 0.0D) return;
		
		Location fromLocation = event.getFrom();
		Location toLocation = event.getTo();
		
		int xFrom = fromLocation.getBlockX() >> 4, xTo = toLocation.getBlockX() >> 4;
		int zFrom = fromLocation.getBlockZ() >> 4, zTo = toLocation.getBlockZ() >> 4;
		if ((xFrom != xTo) || (zFrom != zTo)) {
			getFurnitureManager().updatePlayerViewWithRange(player, toLocation);
		}
	}
	
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> getFurnitureManager().updatePlayerViewWithRange(player, event.getRespawnLocation()), 5);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> getFurnitureManager().updatePlayerViewWithRange(player,  event.getTo()), 5);
    }
    
 	@EventHandler
     public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
         final Player player = event.getPlayer();
         Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> getFurnitureManager().updatePlayerViewWithRange(player, player.getLocation()), 5);
     }
	
}
