package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.function.Consumer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Task {
        
		final Object wrapped;
		final Consumer<Object> canceller;
	
		public Task(Object wrapped, Consumer<Object> canceller) {
			this.wrapped = wrapped;
			this.canceller = canceller;
		}
	
		public void cancel() {
            this.canceller.accept(this.wrapped);
        }

        static Task wrapBukkit(final BukkitRunnable runnable) {
            return new Task(runnable, task -> ((BukkitRunnable) task).cancel());
        }
        
        static Task wrapBukkitTask(final BukkitTask runnable) {
            return new Task(runnable, task -> runnable.cancel());
        }
}