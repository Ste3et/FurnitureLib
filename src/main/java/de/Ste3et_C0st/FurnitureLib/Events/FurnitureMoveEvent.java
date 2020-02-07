package de.Ste3et_C0st.FurnitureLib.Events;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EntityMoving;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FurnitureMoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private fArmorStand a;
    private ObjectID o;
    private Player p;
    private EntityMoving m;
    private boolean cancelled;

    public FurnitureMoveEvent(Player p, fArmorStand a, ObjectID o, EntityMoving m) {
        if (p == null || a == null || o == null || m == null) {
            return;
        }
        if (o.getSQLAction().equals(SQLAction.REMOVE)) {
            return;
        }
        this.p = p;
        this.a = a;
        this.o = o;
        this.m = m;
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

    public fArmorStand getfArmorStand() {
        return this.a;
    }

    public ObjectID getID() {
        return this.o;
    }

    public Player getPlayer() {
        return this.p;
    }

    public EntityMoving getEntityMoving() {
        return this.m;
    }

    public boolean canBuild() {
        return FurnitureLib.getInstance().canBuild(p, o, EventType.INTERACT);
    }
}
