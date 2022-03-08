package de.Ste3et_C0st.FurnitureLib.Listener.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import de.Ste3et_C0st.FurnitureLib.Listener.EventLibrary;

public class onPlayerDeath extends EventLibrary implements Listener {
    
	@EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        getFurnitureManager().removeFurniture(player);
    }
	
}
