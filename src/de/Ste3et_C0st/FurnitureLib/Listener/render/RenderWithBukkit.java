package de.Ste3et_C0st.FurnitureLib.Listener.render;

import org.bukkit.Bukkit;
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
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (player.getHealth() <= 0.0D) return;
		int xFrom = e.getFrom().getBlockX() >> 4, xTo = e.getTo().getBlockX() >> 4;
		int zFrom = e.getFrom().getBlockZ() >> 4, zTo = e.getTo().getBlockZ() >> 4;
		if ((xFrom != xTo) || (zFrom != zTo)) {
			getFurnitureManager().updatePlayerViewWithRange(player);
		}
	}
	
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> getFurnitureManager().updatePlayerViewWithRange(player), 5);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();      
        Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> getFurnitureManager().updatePlayerViewWithRange(player), 5);
    }
    
 	@EventHandler
     public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
         final Player player = event.getPlayer();  
         Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> getFurnitureManager().updatePlayerViewWithRange(player), 5);
     }
	
}
