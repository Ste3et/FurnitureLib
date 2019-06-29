package de.Ste3et_C0st.FurnitureLib.Events.internal;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import de.Ste3et_C0st.FurnitureLib.main.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public class onChunkChange implements Listener{

	private FurnitureManager manager = FurnitureManager.getInstance();
	
	//private HashSet<ChunkData> data = new HashSet<ChunkData>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
	 	if (e.getTo().getBlock().getLocation().equals(e.getFrom().getBlock().getLocation())) return;
	 	Player player = e.getPlayer();
	 	if (player.getHealth() <= 0.0D) return;
	 	Chunk oldChunk = e.getFrom().getChunk();
	 	Chunk newChunk = e.getTo().getChunk();
	 	if (!oldChunk.equals(newChunk)) manager.updatePlayerView(player);
//	 	if (!oldChunk.equals(newChunk)) {
//	 		FurnitureLib.debug("Furniture.PlayerMoveEvent:");
//	 		ChunkData data = this.data.stream().filter(d -> d.equals(newChunk) == true).findFirst().orElse(new ChunkData(newChunk));
//	 		if(!this.data.contains(data)) {
//	 			FurnitureLib.debug("Furniture.PlayerMoveEvent: ChunkData not found");
//	 			data.load();
//	 			this.data.add(data);
//	 		}
//	 	}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		if(!FurnitureLib.getInstance().isSync()) {
			ChunkData data = manager.getChunkDataList().stream().findFirst().filter(c -> c.equals(e.getChunk())).orElse(new ChunkData(e.getChunk()));
			if(!data.isLoadet()) data.load();
			if(!manager.getChunkDataList().contains(data)) manager.getChunkDataList().add(data);
		}
	}
}
