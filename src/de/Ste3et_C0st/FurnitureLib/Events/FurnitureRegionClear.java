package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ZoneVector;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.ProtectionLib.events.RegionClearEvent;

public class FurnitureRegionClear implements Listener {

	public FurnitureRegionClear() {
		Bukkit.getPluginManager().registerEvents(this, FurnitureLib.getInstance());
	}
	

	@EventHandler
	private void onPlotClear(RegionClearEvent e){
		Vector v1 = e.getLoc1().toVector();
		Vector v2 = e.getLoc2().toVector();
		
		if(v1.getBlockY() == v2.getBlockY()){
			v1 = v1.setY(0);
			v2 = v2.setY(256);
		}
		
	    ZoneVector min = new ZoneVector(Math.min(v1.getBlockX(), v2.getBlockX()), Math.min(v1.getBlockY(), v2.getBlockY()), Math.min(v1.getBlockZ(), v2.getBlockZ()));
	    ZoneVector max = new ZoneVector(Math.max(v1.getBlockX(), v2.getBlockX()), Math.max(v1.getBlockY(), v2.getBlockY()), Math.max(v1.getBlockZ(), v2.getBlockZ()));
		
		for(ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			ZoneVector curr = new ZoneVector(id.getStartLocation());
			if(curr.isInAABB(min, max)){
				if(e.isClear() || id.getUUID() == null){
					id.remove();
				}else{
					id.setUUID(id.getUUID());
					id.getMemberList().clear();
					id.setSQLAction(SQLAction.UPDATE);
				}
			}
		}
	}
}
