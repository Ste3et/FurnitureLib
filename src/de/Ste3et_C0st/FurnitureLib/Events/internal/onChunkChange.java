package de.Ste3et_C0st.FurnitureLib.Events.internal;

import de.Ste3et_C0st.FurnitureLib.main.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class onChunkChange implements Listener {

    private FurnitureManager manager = FurnitureManager.getInstance();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo().getBlock().getLocation().equals(e.getFrom().getBlock().getLocation())) return;
        Player player = e.getPlayer();
        if (player.getHealth() <= 0.0D) return;
        Chunk oldChunk = e.getFrom().getChunk();
        Chunk newChunk = e.getTo().getChunk();

        if (!oldChunk.equals(newChunk)) {
            if (FurnitureLib.getInstance().isSync()) {
                manager.updatePlayerView(player);
            } else {
                manager.updatePlayerView(player);
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!FurnitureLib.getInstance().isSync()) {
            ChunkData data = manager.getChunkDataList().stream().findFirst().filter(c -> c.equals(e.getChunk())).orElse(new ChunkData(e.getChunk()));
            if (!manager.getChunkDataList().contains(data)) {
                manager.getChunkDataList().add(data);
                if (!data.isLoaded()) data.load();
            }
        }
    }
}
