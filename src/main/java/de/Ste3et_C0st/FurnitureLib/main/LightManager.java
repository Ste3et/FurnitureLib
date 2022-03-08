package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.main.LightAPI.LightAPIv3;
import de.Ste3et_C0st.FurnitureLib.main.LightAPI.LightAPIv5;
import de.Ste3et_C0st.FurnitureLib.main.LightAPI.iLightAPI;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class LightManager {

    private Plugin plugin = null;
    private iLightAPI lightApi;
    
    public LightManager(Plugin plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("LightAPI")) {
            if (Bukkit.getPluginManager().getPlugin("LightAPI").getDescription().getVersion().startsWith("3")) {
                this.plugin = plugin;
                this.lightApi = new LightAPIv3();
            } else if(Bukkit.getPluginManager().getPlugin("LightAPI").getDescription().getVersion().startsWith("5")) {
            	 this.plugin = plugin;
                 this.lightApi = new LightAPIv5();
            } else {
                FurnitureLib.getInstance().getLogger().warning("You use a old version of LightAPI this is not supported: " + Bukkit.getPluginManager().getPlugin("LightAPI").getDescription().getVersion());
            }
        }
    }

    public synchronized void addLight(final Location location, final Integer size) {
    	if (Objects.isNull(plugin)) {
            return;
        }
        if (Objects.isNull(location)) {
            return;
        }
        if (Objects.isNull(size)) {
            return;
        }
        this.lightApi.createLight(location, size);
    }

    public synchronized void removeLight(Location location) {
        if (Objects.isNull(plugin)) {
            return;
        }
        try {
            if (Objects.isNull(location)) return; 
            this.lightApi.deleteLight(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public iLightAPI getLightAPI() {
    	return this.lightApi;
    }
}
