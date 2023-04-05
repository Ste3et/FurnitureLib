package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class SchedularHelper {
	
	public static void runAsync(final Runnable runnable) {
		runTask(runnable, false);
	}
	
	public static void runTask(final Runnable runnable, boolean sync) {
		if(FurnitureLib.isFolia()) {
			if(sync) {
				Bukkit.getGlobalRegionScheduler().run(FurnitureLib.getInstance(), task -> runnable.run());
			}else {
				Bukkit.getAsyncScheduler().runNow(FurnitureLib.getInstance(), task -> runnable.run());
			}
		}else {
			if(sync) {
				Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), task -> runnable.run());
			}else {
				Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), task -> runnable.run());
			}
		}
	}
	
	public static Task runTimer(final Runnable runnable, int ticks, int delayed, boolean sync) {
		if(FurnitureLib.isFolia()) {
			if(sync) {
				return Task.wrapFolia(Bukkit.getGlobalRegionScheduler().runAtFixedRate(FurnitureLib.getInstance(), task -> runnable.run(), delayed, ticks));
			}else {
				ticks = ticks > 0 ? ticks : 1;
				delayed = delayed > 0 ? delayed : 1;
				return Task.wrapFolia(Bukkit.getAsyncScheduler().runAtFixedRate(FurnitureLib.getInstance(), task -> runnable.run(), delayed * 50, ticks * 50, TimeUnit.MILLISECONDS));
			}
		}else {
			if(sync) {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskTimer(FurnitureLib.getInstance(), () -> runnable.run(), delayed, ticks));
			}else {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskTimerAsynchronously(FurnitureLib.getInstance(), () -> runnable.run(), delayed, ticks));
			}
		}
	}
	
	public static Task runLater(final Runnable runnable, int ticks, boolean sync) {
		if(FurnitureLib.isFolia()) {
			if(sync) {
				return Task.wrapFolia(Bukkit.getGlobalRegionScheduler().runDelayed(FurnitureLib.getInstance(), task -> runnable.run(), ticks));
			}else {
				ticks = ticks > 0 ? 1 : ticks;
				return Task.wrapFolia(Bukkit.getAsyncScheduler().runDelayed(FurnitureLib.getInstance(), task -> runnable.run(), ticks * 50, TimeUnit.MILLISECONDS));
			}
		}else {
			if(sync) {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> runnable.run(), ticks));
			}else {
				return Task.wrapBukkitTask(Bukkit.getScheduler().runTaskLaterAsynchronously(FurnitureLib.getInstance(), () -> runnable.run(), ticks));
			}
		}
	}
	
    public static <T> CompletableFuture<T> runOnPlayerScheduler(final Player player,final Supplier<T> supplier) {
        if (FurnitureLib.isFolia()) {
            if (Bukkit.isOwnedByCurrentRegion(player)) {
                return CompletableFuture.completedFuture(supplier.get());
            } else {
                return CompletableFuture.supplyAsync(supplier, runPlayerScheduler(player));
            }
        } else {
            if (Bukkit.isPrimaryThread()) {
                return CompletableFuture.completedFuture(supplier.get());
            } else {
                return CompletableFuture.supplyAsync(supplier, Bukkit.getScheduler().getMainThreadExecutor(FurnitureLib.getInstance()));
            }
        }
    }
    
    private static Executor runPlayerScheduler(final Player player) {
        return command -> player.getScheduler().run(FurnitureLib.getInstance(), task -> command.run(), null);
    }
}
