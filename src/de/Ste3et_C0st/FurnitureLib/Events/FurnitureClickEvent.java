package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;

public final class FurnitureClickEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private fArmorStand a;
    private ObjectID o;
    private Player p;
    private Location l;
    private boolean cancelled;
    @Override public HandlerList getHandlers() {return handlers; }
    @Override public boolean isCancelled() {return cancelled;}
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled;}
    public static HandlerList getHandlerList() {return handlers;}
    
    public FurnitureClickEvent(Player p, fArmorStand a, ObjectID o, Location l) {
    	if(o.getSQLAction().equals(SQLAction.REMOVE)){return;}
    	this.p = p;
    	this.a = a;
    	this.o = o;
    	this.l = l;
    }
    
    public fArmorStand getfArmorStand(){return this.a;}
    public ObjectID getID(){return this.o;}
    public Player getPlayer(){return this.p;}
    public Location getLocation(){return this.l;}
	public boolean canBuild(){return FurnitureLib.getInstance().canBuild(p, o, EventType.INTERACT);}
}