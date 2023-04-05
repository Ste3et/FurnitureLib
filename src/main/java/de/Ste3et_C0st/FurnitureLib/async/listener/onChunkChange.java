package de.Ste3et_C0st.FurnitureLib.async.listener;

import de.Ste3et_C0st.FurnitureLib.async.WorldData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

import java.util.Optional;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class onChunkChange implements Listener {

    private FurnitureManager manager = FurnitureManager.getInstance();

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!FurnitureConfig.getFurnitureConfig().isSync()) {
        	final World world = e.getWorld();
        	final Optional<WorldData> optWorldData = manager.getAsyncWorldFiles().stream().filter(entry -> entry.getWorldName().equalsIgnoreCase(world.getName())).findFirst();
        	final int chunkX = e.getChunk().getX(), chunkZ = e.getChunk().getZ();
        	
            optWorldData.ifPresent(worldData -> {
            	worldData.getChunk(chunkX, chunkZ).ifPresent(chunk -> {
            		if(chunk.isLoaded() == false) chunk.load(world);
            	});
            });
        }
    }
}
