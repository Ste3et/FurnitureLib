package de.Ste3et_C0st.FurnitureLib.main;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class FurnitureLib extends JavaPlugin {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;

	@Override
	public void onEnable(){
		instance = this;
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
	}
	
	@Override
	public void onDisable(){
		
	}
	
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public ObjectID getObjectID(Class<?> c){return new ObjectID(c);}
}
