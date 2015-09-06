package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public final class FurnitureClickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private ArmorStandPacket a;
    private ObjectID o;
    private Player p;
    private Location l;
    private boolean cancelled;
    
    public FurnitureClickEvent(Player p, ArmorStandPacket a, ObjectID o, Location l) {
    	if(p==null||a==null||o==null||l==null){return;}
    	if(o.getSQLAction().equals(SQLAction.REMOVE)){return;}
    	this.p = p;
    	this.a = a;
    	this.o = o;
    	this.l = l;
    }
    
    public ArmorStandPacket getArmorStandPacket(){return this.a;}
    public ObjectID getID(){return this.o;}
    public Player getPlayer(){return this.p;}
    public Location getLocation(){return this.l;}
	public HandlerList getHandlers() {return handlers;}
	public static HandlerList getHandlerList() {return handlers;}
	public boolean canBuild(){return FurnitureLib.getInstance().canBuild(p, o, EventType.INTERACT);}
	public boolean isCancelled() {return cancelled;}
	public void setCancelled(boolean arg0) {cancelled = arg0;}
}