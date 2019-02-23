package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Furniture extends FurnitureHelper implements Listener{
	
	public Furniture(ObjectID id){super(id);}
	
	public fArmorStand spawnArmorStand(Location loc){return getManager().createArmorStand(getObjID(), loc);}
	
	public abstract void spawn(Location location);
	
	public void runFunction(Player p) {
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#SITZ")){
				if(stand.getPassanger().isEmpty()){
					stand.setPassanger(p);
					return;
				}else{
					stand.eject(p.getEntityId());
				}
			}
		}
	}
}
