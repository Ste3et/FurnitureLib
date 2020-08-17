package de.Ste3et_C0st.FurnitureLib.main.LightAPI;

import org.bukkit.Location;

import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;

public class LightAPIv5 implements iLightAPI{

	@Override
	public boolean deleteLight(Location location) {
		int lightLevel = location.getBlock().getLightFromBlocks();
		boolean bool = LightAPI.deleteLight(location, LightType.BLOCK, false);
		if(bool) {
        	LightAPI.collectChunks(location, LightType.BLOCK , lightLevel).forEach(entry -> LightAPI.updateChunk(entry, LightType.BLOCK));
        }
		return false;
	}

	@Override
	public boolean createLight(Location location, int lightLevel) {
		boolean bool = LightAPI.createLight(location, LightType.BLOCK, lightLevel, false);
        if(bool) {
        	LightAPI.collectChunks(location, LightType.BLOCK , lightLevel).forEach(entry -> LightAPI.updateChunk(entry, LightType.BLOCK));
        }
		return bool;
	}

	
	
}
