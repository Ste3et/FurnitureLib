package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class SchedularHelper implements SchedularInterface{
	
	private static Optional<SchedularInterface> folia = Optional.empty();
	
	static {
		if(FurnitureLib.isFolia()) {
			try {
				Class<?> foliaSchedulerClazz = Class.forName("de.Ste3et_C0st.FurnitureLib.Folia.FoliaScheduler");
				folia = Optional.ofNullable((SchedularInterface) foliaSchedulerClazz.newInstance());
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}else {
			folia = Optional.empty();
		}
	}
	
	@Override
	public Task newTask(final Runnable runnable, boolean sync) {
		if(FurnitureLib.isFolia()) {
			if(folia.isPresent()) {
				return folia.get().newTask(runnable, sync);
			}
			return null;
		}else {
			if(sync) {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), runnable));
			}else {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), runnable));
			}
		}
	}
	
	@Override
	public Task newTimer(final Runnable runnable, int ticks, int delayed, boolean sync) {
		if(FurnitureLib.isFolia()) {
			if(folia.isPresent()) {
				return folia.get().newTimer(runnable, ticks, delayed, sync);
			}
			return null;
		}else {
			if(sync) {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskTimer(FurnitureLib.getInstance(), runnable, delayed, ticks));
			}else {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskTimerAsynchronously(FurnitureLib.getInstance(), runnable, delayed, ticks));
			}
		}
	}
	
	@Override
	public Task newLater(final Runnable runnable, int ticks, boolean sync) {
		if(FurnitureLib.isFolia()) {
			if(folia.isPresent()) {
				return folia.get().newLater(runnable, ticks, sync);
			}
			return null;
		}else {
			if(sync) {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), runnable, ticks));
			}else {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskLaterAsynchronously(FurnitureLib.getInstance(), runnable, ticks));
			}
		}
	}
	
	@Override
	public Task newRegionTask(Runnable runbable, Location location, boolean sync) {
		if(FurnitureLib.isFolia()) {
			if(folia.isPresent()) {
				return folia.get().newRegionTask(runbable, location, sync);
			}
			return null;
		}else {
			return Task.wrapBukkitTask(Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), runbable));
		}
	}
	
	public static Task runAsync(final Runnable runnable) {
		return new SchedularHelper().newTask(runnable, false);
	}
	
	public static Task runTask(final Runnable runnable, boolean sync) {
		return new SchedularHelper().newTask(runnable, sync);
	}
	
	public static Task runTimer(final Runnable runnable, int ticks, int delayed, boolean sync) {
		return new SchedularHelper().newTimer(runnable, ticks, delayed, sync);
	}
	
	public static Task runLater(final Runnable runnable, int ticks, boolean sync) {
		return new SchedularHelper().newLater(runnable, ticks, sync);
	}
	
	public static Task regionTask(Runnable runbable, Location location, boolean sync) {
		return new SchedularHelper().newRegionTask(runbable, location, sync);
	}
}
