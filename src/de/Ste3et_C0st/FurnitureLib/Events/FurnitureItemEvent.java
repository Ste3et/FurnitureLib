package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;

public final class FurnitureItemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private ItemStack is;
    private Project pro;
    private ObjectID obj;
    private Player p;
    private Location l;
    private boolean cancelled;
    
    public FurnitureItemEvent(Player p, ItemStack is, Project pro, Location l) {
    	this.p = p;
    	this.pro = pro;
    	this.is = is;
    	this.l = l;
    	this.obj = new ObjectID(pro.getName(), pro.getPlugin().getName(), l.clone().add(0, 1, 0));
    	this.obj.setUUID(p.getUniqueId());
    }
    
    public ItemStack getItemStack(){return this.is;}
    public Project getProject(){return this.pro;}
    public Player getPlayer(){return this.p;}
    public Location getLocation(){return this.l;}
	public HandlerList getHandlers() {return handlers;}
	public static HandlerList getHandlerList() {return handlers;}
	public ObjectID getObjID(){return this.obj;}
	
	public boolean canBuild(){
		if(!FurnitureLib.getInstance().canPlace(l, pro, p)){return false;}
		if(p.isOp()) return true;
		if(!pro.hasPermissions(p)){return false;}
		if(!FurnitureLib.getInstance().getLimitManager().canPlace(p, obj)){
			FurnitureLib.getInstance().getLangManager().getString("LimitReached");
			return false;
		}
		if(FurnitureLib.getInstance().getPermManager().canBuild(p, l, EventType.PLACE)){
			FurnitureLib.getInstance().getLimitManager().sendAuncer(p, obj);
			return true;
		}
		return false;
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
	
	public void finish(){
		this.obj.setFinish();
		FurnitureLateSpawnEvent lateSpawn = new FurnitureLateSpawnEvent(p, obj, pro, l);
		Bukkit.getPluginManager().callEvent(lateSpawn);
	}
}