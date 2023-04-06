package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ChatInputHandler implements Listener{

	private final Player player;
	private final Predicate<String> inputFilter;
	private final Consumer<ReturnValue> supplier;
	private final Task task;
	
	public ChatInputHandler(Player player, Predicate<String> inputFilter, Consumer<ReturnValue> supplier, Consumer<Player> openSupplier, Duration timeDuration) {
		this.player = player;
		this.inputFilter = inputFilter;
		this.supplier = supplier;
		this.task = SchedularHelper.runLater(() -> {
			this.stop();
			supplier.accept(ReturnValue.of(ReturnState.TIMEOUT));
		}, (int) (timeDuration.toMillis() * 1000 * 20), false);
		Bukkit.getPluginManager().registerEvents(this, FurnitureLib.getInstance());
		openSupplier.accept(player);
	}
	
	private void stop() {
		if(Objects.nonNull(task)) {
			this.task.cancel();
		}
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void AsyncPlayerChatEvent(final org.bukkit.event.player.AsyncPlayerChatEvent event) {
		if(Objects.isNull(this.player)) return;
		if(event.getPlayer().equals(this.player)) {
			supplier.accept(inputFilter.test(event.getMessage()) ? ReturnValue.of(ReturnState.SUCCESS, event.getMessage()) : ReturnValue.of(ReturnState.WRONG, event.getMessage()));
			this.stop();
			event.setCancelled(true);
			event.getRecipients().clear();
		}
	}
	
	public static class ReturnValue {
		private final ReturnState state;
		private final String string;
		
		public ReturnValue(ReturnState returnState, String string) {
			this.state = returnState;
			this.string = string;
		}

		public ReturnState getState() {
			return state;
		}

		public String getInput() {
			return string;
		}
		
		public static ReturnValue of(ReturnState state) {
			return of(state, new String());
		}
		
		public static ReturnValue of(ReturnState state, String string) {
			return new ReturnValue(state, string);
		}
	}
	
	public enum ReturnState{
		TIMEOUT, SUCCESS, WRONG;
	}
	
}
