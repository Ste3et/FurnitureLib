package de.Ste3et_C0st.FurnitureLib.main.LightAPI;

import org.bukkit.Location;

import ru.beykerykt.minecraft.lightapi.common.LightAPI;
import ru.beykerykt.minecraft.lightapi.common.api.engine.LightFlag;

public class LightAPIv5 implements iLightAPI{

	@Override
	public boolean deleteLight(Location location) {
		int lightLevel = location.getBlock().getLightFromBlocks();
		if(lightLevel > 0) {
			LightAPI.get().setLightLevel(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0, LightFlag.BLOCK_LIGHTING);
			return true;
		}
		return false;
	}

	@Override
	public boolean createLight(Location location, int lightLevel) {
		return LightAPI.get().setLightLevel(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), lightLevel, LightFlag.BLOCK_LIGHTING) > 0;
	}
}
