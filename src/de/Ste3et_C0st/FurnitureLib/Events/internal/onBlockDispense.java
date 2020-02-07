package de.Ste3et_C0st.FurnitureLib.Events.internal;

import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;

import java.util.Objects;

public class onBlockDispense implements Listener {

    @EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent e) {
        if (Objects.nonNull(e.getItem())) {
            if (Objects.nonNull(ChunkOnLoad.getProjectByItem(e.getItem()))) e.setCancelled(true);
        }
    }

}
