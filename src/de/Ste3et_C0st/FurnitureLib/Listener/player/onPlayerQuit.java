package de.Ste3et_C0st.FurnitureLib.Listener.player;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.Ste3et_C0st.FurnitureLib.Listener.EventLibrary;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class onPlayerQuit extends EventLibrary implements Listener {
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
        getFurnitureMgr().removeFurniture(player);
        Optional<DiceOfflinePlayer> offlinePlayer = FurnitureLib.getInstance().getPlayerCache().getPlayer(player.getUniqueId());
        if(offlinePlayer.isPresent()) {
			offlinePlayer.get().update(player);
        }
        
    }
}
