package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class IResidence {
	Residence api;
	
	public IResidence(PluginManager manager){
		api = (Residence) manager.getPlugin("Residence");
	}
	
	public boolean canBuild(Player p, Location l){
	ClaimedResidence residence = Residence.getResidenceManager().getByLoc(l);
	if(residence==null||residence.getOwner().equalsIgnoreCase(p.getName())||residence.getOwner().equalsIgnoreCase(p.getUniqueId().toString())){
		return true;
	}
	return false;
	}
}
