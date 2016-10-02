package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.ProtectionLib.main.RegionClearEvent;

public class FurnitureRegionClear implements Listener {

	@EventHandler
	private void onPlotClear(RegionClearEvent e){
		Location loc1 = e.getLoc1();
		Location loc2 = e.getLoc2();
		for(ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(checkCuboid(id.getStartLocation(), loc1, loc2)){
				id.remove();
			}
		}
	}
	
	public boolean checkCuboid(Location checkLoc, Location loc1, Location loc2)
	{
	    double x1 = Math.min(loc1.getX(), loc2.getX());
	    double y1 = Math.min(loc1.getY(), loc2.getY());
	    double z1 = Math.min(loc1.getZ(), loc2.getZ());
	    
	    double x2 = Math.min(loc1.getX(), loc1.getX());
	    double y2 = Math.min(loc1.getY(), loc1.getY());
	    double z2 = Math.min(loc1.getZ(), loc1.getZ());
	 
	    double cx = checkLoc.getX();
	    double cy = checkLoc.getY();
	    double cz = checkLoc.getZ();
	    return (cx > x1 && cx < x2 && cy > y1 && cy < y2 && cy > z1 && cz < z2);
	}
	
}
