package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class FurniturePlugin extends JavaPlugin {

	private boolean hasModelsLoadet = false;
	
	public FurniturePlugin(JavaPlugin plugin) {
		FurnitureLib.getInstance().addFurnitureAddon(this);
		registerFurnitureProject();
		Bukkit.getScheduler().runTaskLater(plugin, () -> {loadModels();}, 5);
	}

	public abstract void registerFurnitureProject();
	
	private void applyFunction() {
		
	}
	
	public void loadModels() {
		FurnitureManager.getInstance().getPluginObjects(this).stream().forEach(obj -> {
			FurnitureLib.getInstance().spawn(obj.getProjectOBJ(), obj);
		});
		hasModelsLoadet = true;
	};
	
	public boolean isModelsLoadet() {
		return this.hasModelsLoadet;
	}
	
}
