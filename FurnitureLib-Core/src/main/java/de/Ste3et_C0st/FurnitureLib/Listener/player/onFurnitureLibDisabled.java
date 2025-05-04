package de.Ste3et_C0st.FurnitureLib.Listener.player;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class onFurnitureLibDisabled implements Listener {

	private final List<String> instructions;
	
	public onFurnitureLibDisabled(List<String> instructions) {
		this.instructions = instructions;
	}

	@EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if(player.isOnline() && (player.hasPermission("furniture.admin") || player.isOp())) {
        	instructions.stream().map(MiniMessage.miniMessage()::deserialize).forEach(message -> LanguageManager.sendChatMessage(player, message));
        }
    }
	
}
