package de.Ste3et_C0st.FurnitureLib.main.LightAPI;

import org.bukkit.Location;

import ru.beykerykt.lightapi.LightAPI;

public class LightAPIv3 implements iLightAPI{

	@Override
	public boolean deleteLight(Location location) {
		boolean bool = LightAPI.deleteLight(location, false);
        if(bool) LightAPI.collectChunks(location).forEach(LightAPI::updateChunk);
		return bool;
	}

	@Override
	public boolean createLight(Location location, int lightLevel) {
		boolean bool = LightAPI.createLight(location, lightLevel, false);
		if(bool) LightAPI.collectChunks(location).forEach(LightAPI::updateChunk);
		return bool;
	}

}
