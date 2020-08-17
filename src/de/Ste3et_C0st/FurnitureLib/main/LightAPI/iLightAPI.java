package de.Ste3et_C0st.FurnitureLib.main.LightAPI;

import org.bukkit.Location;

public interface iLightAPI {

	public boolean deleteLight(Location location);
	public boolean createLight(Location location, int lightLevel);
	
}
