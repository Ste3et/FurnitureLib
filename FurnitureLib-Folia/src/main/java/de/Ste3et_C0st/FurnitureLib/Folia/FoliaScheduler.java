package de.Ste3et_C0st.FurnitureLib.Folia;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import de.Ste3et_C0st.FurnitureLib.Utilitis.SchedularInterface;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Task;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaScheduler implements SchedularInterface{

    static Task wrapFolia(final ScheduledTask scheduledTask) {
        return new Task(scheduledTask, task -> ((ScheduledTask) task).cancel());
    }
	
    @Override
	public Task newTask(final Runnable runnable, boolean sync) {
		if(sync) {
			return wrapFolia(Bukkit.getGlobalRegionScheduler().run(FurnitureLib.getInstance(), task -> runnable.run()));
		}else {
			return wrapFolia(Bukkit.getAsyncScheduler().runNow(FurnitureLib.getInstance(), task -> runnable.run()));
		}
	};
	
	@Override
	public Task newTimer(final Runnable runnable, int ticks, int delayed, boolean sync) {
		if(sync) {
			return wrapFolia(Bukkit.getGlobalRegionScheduler().runAtFixedRate(FurnitureLib.getInstance(), task -> runnable.run(), delayed, ticks));
		}else {
			ticks = ticks > 0 ? ticks : 1;
			delayed = delayed > 0 ? delayed : 1;
			return wrapFolia(Bukkit.getAsyncScheduler().runAtFixedRate(FurnitureLib.getInstance(), task -> runnable.run(), delayed * 50, ticks * 50, TimeUnit.MILLISECONDS));
		}
	};
	
	@Override
	public Task newLater(final Runnable runnable, int ticks, boolean sync) {
		if(sync) {
			return new Task(Bukkit.getGlobalRegionScheduler().runDelayed(FurnitureLib.getInstance(), task -> runnable.run(), ticks), task -> ((ScheduledTask) task).cancel());
		}else {
			ticks = ticks > 0 ? 1 : ticks;
			return wrapFolia(Bukkit.getAsyncScheduler().runDelayed(FurnitureLib.getInstance(), task -> runnable.run(), ticks * 50, TimeUnit.MILLISECONDS));
		}
	}

	@Override
	public Task newRegionTask(Runnable runbable, Location location, boolean sync) {
		return wrapFolia(Bukkit.getRegionScheduler().run(FurnitureLib.getInstance(), location, task -> runbable.run()));
	}

}
