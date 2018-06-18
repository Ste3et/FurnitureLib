package de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjektInventory;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class FurnitureEntityClickEventListener extends FurnitureFunctions implements Listener{
	
	public FurnitureEntityClickEventListener(ObjectID objID, ProjektInventory inv){
		super(objID, inv);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFurnitureClick(FurnitureClickEvent e) {
		if(getObjID()==null){return;}
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(e.getID()==null) return;
		if(!e.getID().equals(getObjID())) return;
		if(!e.canBuild()){return;}
		ProjectClickEvent event = new ProjectClickEvent(e.getPlayer(), e.getID());
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			runFunction(e.getPlayer());
			toggleLight(true);
		}
	}
}