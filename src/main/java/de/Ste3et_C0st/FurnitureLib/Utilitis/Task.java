package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.function.Consumer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public record Task(Object wrapped, Consumer<Object> canceller) {
        
		public void cancel() {
            this.canceller.accept(this.wrapped);
        }

        static Task wrapBukkit(final BukkitRunnable runnable) {
            return new Task(runnable, task -> ((BukkitRunnable) task).cancel());
        }
        
        static Task wrapBukkitTask(final BukkitTask runnable) {
            return new Task(runnable, task -> runnable.cancel());
        }

        static Task wrapFolia(final ScheduledTask scheduledTask) {
            return new Task(scheduledTask, task -> ((ScheduledTask) task).cancel());
        }
}