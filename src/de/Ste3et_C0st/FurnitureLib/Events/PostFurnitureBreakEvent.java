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

public final class PostFurnitureBreakEvent extends Event implements Cancellable{
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
    
    public PostFurnitureBreakEvent(Player p, fArmorStand a, ObjectID o, Location l) {
    	if(o.getSQLAction().equals(SQLAction.REMOVE)){return;}
    	this.p = p;
    	this.a = a;
    	this.o = o;
    	this.l = l;
    }
    
    public fArmorStand getArmorStandPacket(){return this.a;}
    public ObjectID getID(){return this.o;}
    public Player getPlayer(){return this.p;}
    public Location getLocation(){return this.l;}
	public boolean canBuild(){
		return FurnitureLib.getInstance().canBuild(p, o, EventType.BREAK);
	}
	
	public void remove(){
		o.remove(p);
	}
	
	public void remove(boolean dropItem, boolean deleteEffect){
		o.remove(p, dropItem, deleteEffect);
	}
	
	public boolean spamBreak(){
		if(FurnitureLib.getInstance().isSpamBreak()){
			if(!FurnitureLib.getInstance().hasPerm(this.p,"furniture.admin") && !FurnitureLib.getInstance().hasPerm(this.p,"furniture.bypass.breakSpam")){
				long current = System.currentTimeMillis();
				if(FurnitureLib.getInstance().getTimeBreak().containsKey(p.getUniqueId())){
					long since = FurnitureLib.getInstance().getTimeBreak().get(p.getUniqueId());
					long newCurrent = current - since;
					long dif = FurnitureLib.getInstance().getBreakTime();
					if(newCurrent < dif){
						String str = FurnitureLib.getInstance().getTimeDif(since, dif, FurnitureLib.getInstance().getTimePattern());
						String msg = FurnitureLib.getInstance().getLangManager().getString("FurnitureToFastBreak");
						msg = msg.replace("#TIME#", str);
						p.sendMessage(msg);
						return false;
					}
				}
				FurnitureLib.getInstance().getTimeBreak().put(p.getUniqueId(), current);
			}
		}
		return true;
	}
}