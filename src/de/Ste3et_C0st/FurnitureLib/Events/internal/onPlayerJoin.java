package de.Ste3et_C0st.FurnitureLib.Events.internal;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;

public class onPlayerJoin extends EventLibrary implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> {
        	if(player.isOnline()) {
        		getFurnitureMgr().sendAllInView(player);
        		Optional<DiceOfflinePlayer> offlinePlayer = FurnitureLib.getInstance().getPlayerCache().getPlayer(player.getUniqueId());
        		if(offlinePlayer.isPresent()) {
        			offlinePlayer.get().update(player);
        		}else {
        			FurnitureLib.getInstance().getPlayerCache().addPlayer(player);
        		}
        	}
        }, 5);
    }
}
