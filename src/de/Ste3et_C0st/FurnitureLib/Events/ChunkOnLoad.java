package de.Ste3et_C0st.FurnitureLib.Events;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ChunkOnLoad implements Listener{
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		if(FurnitureLib.getInstance().getFurnitureManager().getArmorStandPacketsFromChunk().containsKey(e.getChunk())){
			HashMap<Chunk, List<ArmorStandPacket>> asList = FurnitureLib.getInstance().getFurnitureManager().getArmorStandPacketsFromChunk();
			for(Chunk c : asList.keySet()){
				if(c.equals(e.getChunk())){
					List<ArmorStandPacket> aSList = asList.get(e.getChunk());
					for(Player p : e.getWorld().getPlayers()){
						for(ArmorStandPacket as : aSList){
							as.send(p);
						}
					}
				}
			}
		}
	}
}
