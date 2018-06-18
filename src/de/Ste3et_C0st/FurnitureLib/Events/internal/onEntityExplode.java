package de.Ste3et_C0st.FurnitureLib.Events.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class onEntityExplode implements Listener{
	@EventHandler
	public void explode(EntityExplodeEvent e){
		List<Block> blockList = new ArrayList<Block>(e.blockList());
		HashSet<Location> furnitureBlocks = FurnitureLib.getInstance().getBlockManager().getList();
		blockList.stream().forEach(block -> {
			if(furnitureBlocks.contains(block.getLocation())) e.blockList().remove(block);
		});
	}
}
