package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public final class FurnitureBlockBreakEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private Block b;
    private ObjectID o;
    private Player p;
    private Location l;
    private boolean cancelled;
    @Override public HandlerList getHandlers() {return handlers; }
    @Deprecated
    @Override public boolean isCancelled() {return cancelled;}
    @Deprecated
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled;}
    public static HandlerList getHandlerList() {return handlers;}
    
    public FurnitureBlockBreakEvent(Player p, Block b, ObjectID o) {
    	if(o.getSQLAction().equals(SQLAction.REMOVE)){return;}
    	this.p = p;
    	this.b = b;
    	this.o = o;
    	this.l = b.getLocation();
    	FurnitureLib.getInstance().getFurnitureManager();
    }
    
    public Block getBlock(){return this.b;}
    public ObjectID getID(){return this.o;}
    public Player getPlayer(){return this.p;}
    public Location getLocation(){return this.l;}
	public boolean canBuild(){return FurnitureLib.getInstance().canBuild(p, o, EventType.BREAK);}
	
	public void remove(){
		o.remove(p);
	}
	
	public void remove(boolean dropItem, boolean deleteEffect){
		o.remove(p, dropItem, deleteEffect);
	}
}