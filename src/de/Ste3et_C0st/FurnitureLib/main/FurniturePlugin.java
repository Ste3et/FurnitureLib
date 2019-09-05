package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class FurniturePlugin implements FurniturePluginInterface{

	private boolean hasModelsLoadet = false;
	private JavaPlugin plugin;
	
	public FurniturePlugin(JavaPlugin plugin) {
		this.plugin = plugin;
		FurnitureLib.getInstance().addFurnitureAddon(this);
		registerFurnitureProject();
		Bukkit.getScheduler().runTaskLater(plugin, () -> {loadModels();}, 5);
	}
	
	public void loadModels() {
		FurnitureLib.debug("Load Models from Plugin: " + plugin.getName());
		FurnitureManager.getInstance().getPluginProjects(this).forEach(pro -> {
			pro.applyFunction();
			pro.getObjects().forEach(obj -> {
				FurnitureLib.getInstance().spawn(pro, obj);
			});
		});
		hasModelsLoadet = true;
	};
	
	public boolean isModelsLoadet() {
		return this.hasModelsLoadet;
	}

	@Override
	public void registerFurnitureProject() {}
	
	public String getName() {
		return this.plugin.getName();
	}
	
	public Plugin getPlugin() {
		return this.plugin;
	}
}
