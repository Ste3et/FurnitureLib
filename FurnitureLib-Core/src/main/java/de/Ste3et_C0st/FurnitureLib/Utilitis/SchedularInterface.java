package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.Location;

public interface SchedularInterface {

	public Task newTask(final Runnable runnable, boolean sync);
	public Task newTimer(final Runnable runnable, int ticks, int delayed, boolean sync);
	public Task newLater(final Runnable runnable, int ticks, boolean sync);
	public Task newRegionTask(final Runnable runbable, Location location, boolean sync);
	
}
