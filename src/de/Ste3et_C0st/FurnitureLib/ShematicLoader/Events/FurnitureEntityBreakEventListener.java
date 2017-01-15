package de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjektInventory;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class FurnitureEntityBreakEventListener extends FurnitureFunctions implements Listener {
	
	public FurnitureEntityBreakEventListener(ObjectID id, ProjektInventory inv) {
		super(id, inv);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFurnitureBreak(FurnitureBreakEvent e) {
		if(getObjID()==null){return;}
		if(e.getID()==null) return;
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(!e.getID().equals(getObjID())) return;
		if(!e.canBuild()){return;}
		ProjectBreakEvent event = new ProjectBreakEvent(e.getPlayer(), e.getID());
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()){
			checkItems();
			e.remove();
		}
	}
}
