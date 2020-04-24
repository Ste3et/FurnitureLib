package de.Ste3et_C0st.FurnitureLib.Events.internal;

import de.Ste3et_C0st.FurnitureLib.Utilitis.DoubleKey;
import de.Ste3et_C0st.FurnitureLib.main.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class onChunkChange implements Listener {

    private FurnitureManager manager = FurnitureManager.getInstance();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        
        if (player.getHealth() <= 0.0D) return;
        
        DoubleKey<Integer> oldChunk = new DoubleKey<Integer>(e.getFrom().getBlockX() >> 4, e.getFrom().getBlockZ() >> 4);
        DoubleKey<Integer> newChunk = new DoubleKey<Integer>(e.getTo().getBlockX() >> 4, e.getTo().getBlockZ() >> 4);

        if (!oldChunk.equals(newChunk)) manager.updatePlayerView(player);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!FurnitureLib.getInstance().isSync()) {
        	World world = e.getWorld();
            ChunkData data = manager.getChunkDataList().stream().findFirst().filter(c -> c.equals(e.getChunk())).orElse(new ChunkData(e.getChunk()));
            if (!manager.getChunkDataList().contains(data)) {
                manager.getChunkDataList().add(data);
                if (!data.isLoaded()) data.load(world);
            }
        }
    }
}
