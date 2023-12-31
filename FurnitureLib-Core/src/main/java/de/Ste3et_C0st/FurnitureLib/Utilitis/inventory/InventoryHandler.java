package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ItemStackBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackGUI;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackGUIClose;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.md_5.bungee.api.ChatColor;

public abstract class InventoryHandler implements Listener{

	private Player player = null;
	protected final Inventory inventory;
	protected int page = 1;
	private ClickedInventory clickedInv = ClickedInventory.TOP;
	private HashSet<BukkitTask> task = new HashSet<BukkitTask>();
	private CallbackGUIClose callbackClose;
	private CallbackGUI callbackClick;
	
	public InventoryHandler(Player player, int slotSize, String inventoryName){
		this.player = player;
		this.inventory = Bukkit.createInventory(null, slotSize, Objects.isNull(inventoryName) ? "§c" : ChatColor.translateAlternateColorCodes('&', inventoryName));
	}

	public static enum ClickedInventory{
		TOP,
		BOTTOM
	}
	
	public Player getPlayer() {
		return player;
	}

	public static LanguageManager getLangManager() {
		return LanguageManager.getInstance();
	}
	
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public abstract void setButtons();
	public abstract void setContent();
	
	protected Player getPlayerFromUUID(UUID uuid){
		Player player = Bukkit.getPlayer(uuid);
		return Objects.isNull(player) ? null : player;
	}
	
	public InventoryHandler addItemStack(int slot, ItemStackBuilder builder) {
		return addItemStack(slot, builder.build());
	}
	
	public InventoryHandler addItemStack(int slot, ItemStack stack) {
		getInventory().setItem(slot, stack);
		return this;
	}
	
	public InventoryHandler removeStack(int slot) {
		getInventory().clear(slot);
		return this;
	}
	
	public void open(Player p) {
		if(Objects.isNull(this.player)) return;
		this.player.openInventory(this.inventory);
		Bukkit.getPluginManager().registerEvents(this, FurnitureLib.getInstance());
	}
	
	public void close() {
		if(Objects.isNull(this.player)) return;
		this.player.closeInventory();
	}
	
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(Objects.isNull(this.player)) return;
		if(Objects.isNull(this.inventory)) return;
		if(e.getInventory() == null) return;
		if(!this.inventory.equals(e.getInventory())) return;
		HandlerList.unregisterAll(this);
		if(this.callbackClose!=null) this.callbackClose.onResult(this);
		task.stream().filter(t-> t != null && !t.isCancelled()).forEach(task -> {
			task.cancel();
		});
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(Objects.isNull(this.player)) return;
		if(Objects.isNull(this.inventory)) return;
		if(e.getInventory() == null) return;
		if(!this.inventory.equals(e.getInventory())) return;
		e.setCancelled(true);
		if(e.getCurrentItem() == null) return;
		if(e.getRawSlot() > e.getView().getTopInventory().getSize()) {
			this.clickedInv = ClickedInventory.BOTTOM;
		}else {
			this.clickedInv = ClickedInventory.TOP;
		}
		if(this.callbackClick != null) {
			this.callbackClick.setInventoryAction(e.getAction());
			this.callbackClick.setClickedInventory(this.clickedInv);
			this.callbackClick.setInventory(this.clickedInv == ClickedInventory.TOP ? e.getView().getTopInventory() : e.getView().getBottomInventory());
			this.callbackClick.onResult(e.getCurrentItem(), e.getRawSlot());
		}
	}
	
	public void addTask(BukkitTask ... task) {
		for(BukkitTask t : task) {
			if(t == null) continue;
			this.task.add(t);
		}
	}
	
	public void onClick(CallbackGUI click) {
		this.callbackClick = click;
	}
	
	public void onClose(CallbackGUIClose gui) {
		this.callbackClose = gui;
	}
}
