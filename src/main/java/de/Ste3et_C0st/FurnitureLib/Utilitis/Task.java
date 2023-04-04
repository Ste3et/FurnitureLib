package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.function.Consumer;
import org.bukkit.scheduler.BukkitTask;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

//jmp
public record Task(Object wrapped, Consumer<Object> canceller) {
    void cancel() {
        this.canceller.accept(this.wrapped);
    }

    static Task wrapBukkit(final BukkitTask runnable) {
        return new Task(runnable, task -> ((BukkitTask) task).cancel());
    }

    static Task wrapFolia(final ScheduledTask scheduledTask) {
        return new Task(scheduledTask, task -> ((ScheduledTask) task).cancel());
    }
}
