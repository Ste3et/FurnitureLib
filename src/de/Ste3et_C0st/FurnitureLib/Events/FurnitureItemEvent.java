package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;

public final class FurnitureItemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private ItemStack is;
    private Project pro;
    private Player p;
    private Location l;
    private boolean cancelled;
    
    public FurnitureItemEvent(Player p, ItemStack is, Project pro, Location l) {
    	this.p = p;
    	this.pro = pro;
    	this.is = is;
    	this.l = l;
    }
    
    public ItemStack getItemStack(){return this.is;}
    public Project getProject(){return this.pro;}
    public Player getPlayer(){return this.p;}
    public Location getLocation(){return this.l;}
	public HandlerList getHandlers() {return handlers;}
	public static HandlerList getHandlerList() {return handlers;}
	
	public boolean canBuild(){
		if(!FurnitureLib.getInstance().canPlace(l, pro, p)){return false;}
		if(p.isOp()) return true;
		if(!p.hasPermission("furniture.item." + pro.getName()) && !p.hasPermission("furniture.place." + pro.getName()) && !p.hasPermission("furniture.player")){
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
			return false;}
		if(!FurnitureLib.getInstance().getLimitationManager().canPlace(l, pro, p)){
			if(p.hasPermission("furniture.baypass.limitation") || p.hasPermission("furniture.admin")) return true;
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("LimitReached"));
			return false;
		}
		return FurnitureLib.getInstance().canBuild(p, l, EventType.PLACE);
	}
	public boolean isCancelled() {return cancelled;}
	public void setCancelled(boolean arg0) {cancelled = arg0;}
	
	public void removeItem(){
		Boolean useGameMode = FurnitureLib.getInstance().useGamemode();
		if(useGameMode&&p.getGameMode().equals(GameMode.CREATIVE)){return;}
		Integer slot = p.getInventory().getHeldItemSlot();
		ItemStack itemStack = is.clone();
		itemStack.setAmount(itemStack.getAmount()-1);
		p.getInventory().setItem(slot, itemStack);
		p.updateInventory();
	}
}