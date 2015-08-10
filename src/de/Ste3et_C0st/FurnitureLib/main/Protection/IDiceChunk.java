package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import de.Ste3et_C0st.DiceChunk.DiceChunk;
import de.Ste3et_C0st.DiceChunk.persistantData.Friend;
import de.Ste3et_C0st.DiceChunk.persistantData.OwnedLand;

public class IDiceChunk {
	DiceChunk api;
	public IDiceChunk(PluginManager manager){
		api = (DiceChunk) manager.getPlugin("DiceChunk");
	}
	
	public boolean check(Player p, Location l){
		OwnedLand ow = OwnedLand.getApplicableLand(l);
		if(ow==null){return true;}
		if(ow.getOwnerUUID().equals(p.getUniqueId())){return true;}
		if(ow.getFriends().contains(Friend.friendFromPlayer(p))){return true;}
		return false;
	}
}
