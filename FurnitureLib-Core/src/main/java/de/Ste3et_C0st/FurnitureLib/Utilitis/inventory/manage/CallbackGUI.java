package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage.ClickGui.ClickedInventory;


public abstract class CallbackGUI{
	private ClickedInventory inventory = ClickedInventory.TOP;
	private Inventory inv = null;
	private InventoryAction action = null;
	public abstract void onResult(ItemStack stack, Integer slot);
	
	public ClickedInventory getInventoryPos() {return this.inventory;}
	public Inventory getInventory() {return this.inv;}
	public InventoryAction getAction() {return this.action;}
	public void setInventory(Inventory inv) {this.inv = inv;}
	public void setClickedInventory(ClickedInventory clickedInv) {this.inventory = clickedInv;}
	public void setInventoryAction(InventoryAction action) {this.action = action;}
}