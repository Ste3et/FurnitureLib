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

public final class PostFurnitureBlockBreakEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private Block b;
    private ObjectID o;
    private Player p;
    private Location l;
    private boolean cancelled;
    @Override public HandlerList getHandlers() {return handlers; }
    @Override public boolean isCancelled() {return cancelled;}
    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled;}
    public static HandlerList getHandlerList() {return handlers;}
    
    public PostFurnitureBlockBreakEvent(Player p, Block b, ObjectID o) {
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