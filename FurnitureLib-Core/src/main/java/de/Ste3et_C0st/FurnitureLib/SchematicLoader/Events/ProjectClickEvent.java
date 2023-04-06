package de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ProjectClickEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private ObjectID o;
    private Player p;
    private Location l;
    private boolean cancelled;

    public ProjectClickEvent(Player p, ObjectID o) {
        if (o.getSQLAction().equals(SQLAction.REMOVE)) {
            return;
        }
        this.p = p;
        this.o = o;
        this.l = o.getStartLocation();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ObjectID getID() {
        return this.o;
    }

    public Player getPlayer() {
        return this.p;
    }

    public Location getLocation() {
        return this.l;
    }

    public boolean canBuild() {
        return FurnitureLib.getInstance().canBuild(p, o, EventType.INTERACT);
    }
}