package de.Ste3et_C0st.FurnitureLib.main;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions.FunctionTypes.FunctionType;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Furniture extends FurnitureHelper implements Listener{
	
	public Furniture(ObjectID id){super(id);}
	
	public fArmorStand spawnArmorStand(Location loc){return getManager().createArmorStand(getObjID(), loc);}
	
	public abstract void spawn(Location location);
	
	public boolean runFunction(Player p) {
		List<JsonObject> functions = getObjID().getProjectOBJ().getFunctions();
		if(!functions.isEmpty()) {
			boolean update = false;
			for(JsonObject function : functions) {
				if(function.has("function") && function.has("data")) {
					FunctionType type = FunctionType.valueOf(function.get("function").getAsString().toUpperCase());
					boolean b = type.parse(function.getAsJsonObject("data"), getObjID(), p);
					if(b) update = true;
				}
			}
			if(update) return update;
		}
		
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#SITZ")){
				if(stand.getPassanger().isEmpty()){
					stand.setPassanger(p);
					return true;
				}
			}
		}
		return false;
	}
}
