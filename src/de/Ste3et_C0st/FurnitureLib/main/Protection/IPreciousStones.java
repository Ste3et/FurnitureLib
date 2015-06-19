package de.Ste3et_C0st.FurnitureLib.main.Protection;

import java.util.List;

import net.sacredlabyrinth.Phaed.PreciousStones.FieldFlag;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.vectors.Field;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class IPreciousStones {
	PreciousStones api;
	public IPreciousStones(PluginManager manager){
		api = (PreciousStones) manager.getPlugin("PreciousStones");
	}
	
	public boolean canBuild(Player p, Location l){
		List<Field> fields = PreciousStones.API().getFieldsProtectingArea(FieldFlag.ALL, l);
		if ((fields == null) || (fields.size() == 0)){ return true; }else{
		    Field stones = (Field)fields.get(0);
		    if(stones.isBuyer(p)){
		    	return true;
		    }
		    return false;
		}
	}
}
