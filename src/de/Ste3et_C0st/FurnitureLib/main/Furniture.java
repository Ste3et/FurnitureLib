package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;

public abstract class Furniture implements Listener {

	Location loc;
	BlockFace b;
	World w;
	ObjectID obj;
	FurnitureManager manager;
	FurnitureLib lib;
	LocationUtil lutil;
	Plugin plugin;
	
	public Furniture(FurnitureLib lib, Plugin plugin, ObjectID id){}
	
	public abstract void spawn(Location location);
	public abstract void onFurnitureBreak(FurnitureBreakEvent e);
	public abstract void onFurnitureClick(FurnitureClickEvent e);
}
