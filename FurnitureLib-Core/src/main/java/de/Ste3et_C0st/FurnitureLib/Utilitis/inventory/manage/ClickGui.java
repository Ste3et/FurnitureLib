package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ItemStackBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SchedularHelper;

public class ClickGui implements Listener {

	private Inventory inv;
	private Player p;
	private Plugin plugin;
	private final int maxSize;
	private CallbackGUIClose<ClickGui> callbackClose;
	protected CallbackGUI callbackClick;
	private HashSet<BukkitTask> task = new HashSet<BukkitTask>();
	private ClickedInventory clickedInv = ClickedInventory.TOP;
	
	private boolean canClickBottomInventory = false;
	
	private HashMap<Integer, GuiButton> guiButtons = new HashMap<Integer, GuiButton>();
	
	public static enum ClickedInventory{
		TOP,
		BOTTOM
	}
	
	private static int getDefaultSize(InventoryType type) {
		return type == InventoryType.CHEST ? 54 : type.getDefaultSize();
	}
	
	private static int getMaxSize(InventoryType type, int size) {
		int defaultSize = getDefaultSize(type);
		int returnSize = InventoryType.CHEST == type ? size > defaultSize ? defaultSize : size : defaultSize;
		return returnSize;
	}
	
	public ClickGui(int size, String name,InventoryType type, Player p, Plugin plugin) {
		this.maxSize = getMaxSize(type, size);
		if(InventoryType.CHEST == type) {
			this.inv = Bukkit.createInventory(null, maxSize, name != null ? ChatColor.translateAlternateColorCodes('&', name) : "§c");
		}else {
			this.inv = Bukkit.createInventory(null, type, name != null ? ChatColor.translateAlternateColorCodes('&', name) : "§c");
		}
		this.p = p;
		this.plugin = plugin;
	}
	
	public ClickGui(int size, String name, Player p, Plugin plugin) {
		this(size, name, InventoryType.CHEST, p, plugin);
	}
	
	public Inventory getInventory() {
		return this.inv;
	}
	
	public ClickGui addItemStack(int slot, ItemStackBuilder builder) {
		return addItemStack(slot, builder.build());
	}
	
	public ClickGui addItemStack(int slot, ItemStack stack) {
		if(slot < this.getSize()) this.inv.setItem(slot, stack);
		return this;
	}
	
	public ClickGui addButton(int slot, GuiButton button) {
		if(slot < this.getSize()) this.guiButtons.put(slot, button);
		return this.addItemStack(slot, button.getItem());
	}
	
	public ClickGui removeStack(int i) {
		if(i < this.getSize()) getInventory().clear(i);
		return this;
	}
	
	public boolean canClickBottomInventory() {
		return this.canClickBottomInventory;
	}
	
	public void setClickBottomInventory(boolean bool) {
		this.canClickBottomInventory = bool;
	}
	
	public int getSize() {
		return this.inv.getSize();
	}
	
	public static ItemStack getFillerStack() {
		return ItemStackBuilder.of(Material.BLACK_STAINED_GLASS_PANE).setName("§c").build();
	}
	
	public void open(Player p) {
		if(p==null) return;
		SchedularHelper.runTask(() -> {
			p.openInventory(getInventory());
			Bukkit.getPluginManager().registerEvents(this, this.plugin);
		}, true);
	}
	
	public void close() {
		if(p==null) return;
		p.closeInventory();
	}
	
	public Player getPlayer() {
		return this.p;
	}
	
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(p == null) return;
		if(plugin == null) return;
		if(this.inv == null) return;
		if(e.getInventory() == null) return;
		if(!this.inv.equals(e.getInventory())) return;
		HandlerList.unregisterAll(this);
		if(this.callbackClose!=null) this.callbackClose.onResult(this);
		task.stream().filter(t-> t != null && !t.isCancelled()).forEach(task -> {
			task.cancel();
		});
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(p == null) return;
		if(plugin == null) return;
		if(this.inv == null) return;
		if(e.getInventory() == null) return;
		if(!this.inv.equals(e.getInventory())) return;
		if(e.getCurrentItem() == null) return;
		
		if((e.getRawSlot() + 1) > e.getView().getTopInventory().getSize()) {
			this.clickedInv = ClickedInventory.BOTTOM;
			e.setCancelled(this.canClickBottomInventory == false);
		}else {
			this.clickedInv = ClickedInventory.TOP;
			e.setCancelled(true);
		}
		
		if(this.applyEvent(e)) {
			if(this.callbackClick != null) {
				this.callbackClick.setInventoryAction(e.getAction());
				this.callbackClick.setClickedInventory(this.clickedInv);
				this.callbackClick.setInventory(this.clickedInv == ClickedInventory.TOP ? e.getView().getTopInventory() : e.getView().getBottomInventory());
				this.callbackClick.onResult(e.getCurrentItem(), e.getRawSlot());
			}
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
	
	public void onClose(CallbackGUIClose<ClickGui> gui) {
		this.callbackClose = gui;
	}
	
	protected boolean applyEvent(InventoryClickEvent onClickAction) {
		if(Objects.nonNull(onClickAction.getCurrentItem()) && onClickAction.getCurrentItem().getType() != Material.AIR) {
			ItemStack stack = onClickAction.getCurrentItem();
			GuiButton button = this.guiButtons.values().stream().filter(entry -> entry.isStack(stack)).findFirst().orElse(null);
			if(Objects.nonNull(button)) {
				button.callAction(onClickAction);
				if(onClickAction.isCancelled()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void fillComplete() {
		fillComplete(getFillerStack());
	}
	
	public void fillComplete(ItemStack Stack) {
		for(int slot = 0; slot < this.getSize(); slot++) {
			this.addItemStack(slot, Stack);
		}
	}
	
	public void clearSegment(int start, int end) {
		for(int slot = start; slot < end; slot++) {
			removeItem(slot);
		}
	}
	
	public void removeItem(int slot) {
		if(slot < this.getSize())  this.addItemStack(slot, new ItemStack(Material.AIR));
		if(this.guiButtons.containsKey(slot)) this.guiButtons.remove(slot);
	}
	
	public ClickGui clearArea(int xStart, int yStart, int xEnd, int yEnd) {
		return setArea(xStart, yStart, xEnd, yEnd, new ItemStack(Material.AIR));
	}
	
	public ClickGui setArea(int xStart, int yStart, int xEnd, int yEnd, ItemStack stack) {
		xEnd++;
		yEnd++;
		Rectangle area = new Rectangle(xStart, yStart, xEnd - xStart, yEnd - yStart);
		int row = 0;
		int cell = 0;
		
		for(int slot = 0; slot < this.getSize(); slot++) {
			if(slot > 0 && (slot) % 9 == 0) {
				row++;
				cell = 0;
			}else {
				cell++;
			}
			
			if(area.contains(row, cell)) addItemStack(slot, stack);
		}
		return this;
	}
	
	public int getLastSlot() {
		return this.inv.getSize() - 1;
	}
	
	public void setBackButton(ClickGui clickGui) {
		this.setBackButton(getSize() - 1, ItemStackBuilder.of(Material.ARROW).setName("back"), clickGui);
	}

    public void setBackButton(int slot,ItemStackBuilder builder, ClickGui clickGui) {
    	this.addButton(slot, new GuiButton(builder, event -> {
    		clickGui.open(p);
    		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PUT, 1f, .8f);
    	}));
    }
    
    public void setBackButtonLegacy(int slot,ItemStackBuilder builder, String command) {
    	this.addButton(slot, new GuiButton(builder, event -> {
    		p.chat(command);
    		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PUT, 1f, .8f);
    	}));
    }
    
    public void setBackButtonLegacy(int slot, String command) {
    	this.addButton(slot, new GuiButton(ItemStackBuilder.of(Material.ARROW).setName("back"), event -> {
    		p.chat(command);
    		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PUT, 1f, .8f);
    	}));
    }
}
