package de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjektInventory;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class FurnitureBlockPhysikListener extends FurnitureFunctions implements Listener {

	public FurnitureBlockPhysikListener(ObjectID id, ProjektInventory inv) {
		super(id, inv);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onPhysiks(BlockPhysicsEvent e){
		  if(getObjID() == null) return;
		  if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		  if (e.getBlock() == null) return;
		  if (!getObjID().getBlockList().contains(e.getBlock().getLocation())){return;}
		  e.setCancelled(true);
	}
}
