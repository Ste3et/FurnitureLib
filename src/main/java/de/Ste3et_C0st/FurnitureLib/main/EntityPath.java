package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Location;

public class EntityPath {

	private final Location startLocation, endLocation;
	private Location actuallyLocation;
	
	public EntityPath(Location endLocation, ObjectID objectID) {
		this.startLocation = objectID.getStartLocation();
		this.endLocation = endLocation;
		this.actuallyLocation = startLocation;
	}
	
}
