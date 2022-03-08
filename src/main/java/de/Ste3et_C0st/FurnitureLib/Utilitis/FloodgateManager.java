package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.UUID;

import org.geysermc.floodgate.api.FloodgateApi;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;

public class FloodgateManager {
	
	public boolean isBedrockPlayer(UUID uuid) {
		if(FurnitureConfig.getFurnitureConfig().isHideBedrockPlayers() == false) return false;
		return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
	}
	
}
