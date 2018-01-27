package de.Ste3et_C0st.FurnitureLib.Events.internal;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public class onChunkChange implements Listener{

	private FurnitureManager manager = FurnitureLib.getInstance().getFurnitureManager();
	
	@EventHandler
	 public void onPlayerMove(PlayerMoveEvent e){
	 		if (e.getTo().getBlock().getLocation().equals(e.getFrom().getBlock().getLocation())) return;
	 		Player player = e.getPlayer();
	 		if (player.getHealth() <= 0.0D) return;
	 		Chunk oldChunk = e.getFrom().getChunk();
	 	    Chunk newChunk = e.getTo().getChunk();
	 		if (!oldChunk.equals(newChunk)) manager.updatePlayerView(player);
	 }
	
}
