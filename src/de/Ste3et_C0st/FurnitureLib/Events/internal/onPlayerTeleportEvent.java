package de.Ste3et_C0st.FurnitureLib.Events.internal;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class onPlayerTeleportEvent extends EventLibrary implements Listener {
	
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPayerTeleport(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (FurnitureLib.getInstance() == null) return;
        //Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> getFurnitureMgr().updatePlayerView(player), 5);
    }
    
}
