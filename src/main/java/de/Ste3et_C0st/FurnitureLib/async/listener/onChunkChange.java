package de.Ste3et_C0st.FurnitureLib.async.listener;

import de.Ste3et_C0st.FurnitureLib.async.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class onChunkChange implements Listener {

    private FurnitureManager manager = FurnitureManager.getInstance();

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!FurnitureConfig.getFurnitureConfig().isSync()) {
        	World world = e.getWorld();
            ChunkData data = manager.getChunkDataList().stream().findFirst().filter(c -> c.equals(e.getChunk())).orElse(new ChunkData(e.getChunk()));
            if (!manager.getChunkDataList().contains(data)) {
                manager.getChunkDataList().add(data);
                if (!data.isLoaded()) data.load(world);
            }
        }
    }
}
