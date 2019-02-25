package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public final class FurnitureLateSpawnEvent extends Event implements Cancellable {
	
    private static final HandlerList handlers = new HandlerList();
    private ObjectID objID;
    private Project pro;
    private Player p;
    private Location l;
    private boolean cancelled;
    @Override public HandlerList getHandlers() {return handlers; }
    @Override public boolean isCancelled() {return cancelled;}
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled;}
    public static HandlerList getHandlerList() {return handlers;}
    
    public FurnitureLateSpawnEvent(Player p, ObjectID objID, Project pro, Location l) {
    	this.p = p;
    	this.pro = pro;
    	this.objID = objID;
    	this.l = l;
    }
    
    public ObjectID getID(){return this.objID;}
    public Project getProject(){return this.pro;}
    public Player getPlayer(){return this.p;}
    public Location getLocation(){return this.l;}
}