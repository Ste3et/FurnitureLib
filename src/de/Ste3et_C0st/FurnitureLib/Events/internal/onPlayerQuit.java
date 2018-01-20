package de.Ste3et_C0st.FurnitureLib.Events.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class onPlayerQuit extends EventLibary implements Listener{
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		getFurnitureMgr().getSendList().remove(player.getUniqueId());
		getFurnitureMgr().removeFurniture(player);
	}
}
