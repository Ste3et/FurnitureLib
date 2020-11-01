package de.Ste3et_C0st.FurnitureLib.Events.internal;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onFurnitureLibDisabled implements Listener {

	private final List<String> instructions;
	
	public onFurnitureLibDisabled(List<String> instructions) {
		this.instructions = instructions;
	}

	@EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if(player.isOnline() && (player.hasPermission("furniture.admin") || player.isOp())) {
        	instructions.stream().forEach(event.getPlayer()::sendMessage);
        }
    }
	
}
