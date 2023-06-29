package de.Ste3et_C0st.FurnitureLib.Paper;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelHandler;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class PaperEvents implements Listener {
	
	static {
		ModelHandler.MODELBUILD_FILTER = block -> block.getType().isSolid() && block.isReplaceable() == false;
	}
	
	public PaperEvents(){
		FurnitureLib.getInstance().send("FurnitureLib use FurnitureLib-Paper module");
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockDestroy(BlockDestroyEvent e) {
		if(!e.isCancelled()) {
			Location loc = e.getBlock().getLocation();
			if(FurnitureLib.getInstance().getBlockManager().contains(loc)) e.setCancelled(true);
		}
	}

}
