package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class physicsEvent implements Listener {
	
	public physicsEvent() {
		System.out.println("FurnitureLib did use the BlockPhysicsEvent to prevent item Duplication");
		System.out.println("If you use PaperSpigot there is a better solution BlockDestroyEvent");
		System.out.println("If you switch to PaperSpigot that can improve your Server Performance a lot!");
		System.out.println("https://papermc.io/");
	}

	@EventHandler(priority = EventPriority.LOW)
    public void onPhysics(BlockPhysicsEvent e) {
        if(!e.isCancelled()) {
            if(FurnitureLib.getInstance().getBlockManager().contains(e.getBlock().getLocation())) e.setCancelled(true);
        }
    }
	
}
