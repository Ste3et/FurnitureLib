package de.Ste3et_C0st.FurnitureLib.Events.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class onPlayerJoin extends EventLibary implements Listener {
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				getFurnitureMgr().updatePlayerView(player);
			}
		},10);
	}
}
