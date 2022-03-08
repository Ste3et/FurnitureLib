package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import org.bukkit.Location;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;

public abstract class LimitationType {

	public abstract int getAmount(Location location, Project project);
	public abstract int getMaxCount(Project project);
	public abstract boolean canPlace(Location location, Project project);
	
	public abstract void init();
}
