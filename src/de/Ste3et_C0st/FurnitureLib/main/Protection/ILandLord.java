package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.jcdesimp.landlord.Landlord;
import com.jcdesimp.landlord.persistantData.Friend;
import com.jcdesimp.landlord.persistantData.OwnedLand;

public class ILandLord {
	Landlord api;
	public ILandLord(PluginManager manager){
		api = (Landlord) manager.getPlugin("Landlord");
	}
	
	public boolean check(Player p, Location l){
		OwnedLand ow = OwnedLand.getApplicableLand(l);
		if(ow==null){return true;}
		if(ow.getOwnerUUID().equals(p.getUniqueId())){return true;}
		if(ow.getFriends().contains(Friend.friendFromPlayer(p))){return true;}
		return false;
	}
}
