package de.Ste3et_C0st.FurnitureLib.Events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EntityMoving;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.ProjectBreakEvent;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.ProjectClickEvent;

public class FurnitureEvents {

	public FurnitureEvents(FurnitureLib instance, final FurnitureManager manager){
		ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        	Integer PacketID = event.getPacket().getIntegers().read(0);
                            if(manager.isArmorStand(PacketID)){
                            	event.setCancelled(true);
                            	fEntity asPacket = manager.getfArmorStandByID(PacketID);
                            	if(asPacket==null){return;}
                            	ObjectID objID = manager.getObjectIDByID(PacketID);
                            	if(objID==null){return;}
                            	if(objID.getSQLAction().equals(SQLAction.REMOVE)){return;}
                            	if(objID.isPrivate()){return;}
                            	Location loc = asPacket.getLocation();
                            	Player p = event.getPlayer();
                            	EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            	if(loc==null){return;}
                            	if(p==null){return;}
								switch (action) {
								case ATTACK:
									if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
									if(!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())){
										Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
											ProjectBreakEvent projectBreakEvent = new ProjectBreakEvent(p, objID);
											Bukkit.getPluginManager().callEvent(projectBreakEvent);
											if(!projectBreakEvent.isCancelled()) {
												objID.callFunction("onBreak", p);
											}
										});
									}else {
										event.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleEvent"));
									}
									break;
								case INTERACT_AT:
									if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
									if(!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())){
										Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
											ProjectClickEvent projectBreakEvent = new ProjectClickEvent(p, objID);
											Bukkit.getPluginManager().callEvent(projectBreakEvent);
											if(!projectBreakEvent.isCancelled()) {
												objID.callFunction("onClick", p);
											}
										});
									}else {
										event.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleEvent"));
									}
									break;
								default: break;
								}
                            }
                        }
                    }
        });
		
		ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(instance, ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                        	final Player p = event.getPlayer();
                    		EntityMoving moving = event.getPacket().getBooleans().read(1) ? EntityMoving.SNEEKING : null;
                    		if(moving != null && moving.equals(EntityMoving.SNEEKING)) {
                    			List<fEntity> e = FurnitureManager.getInstance().getArmorStandFromPassanger(p);
                    			if(e != null && !e.isEmpty()) {
                    				fEntity f = e.stream().findFirst().get();
                    				if(f!=null) {
                    					f.eject();
                    				}
                    			}
                    		}
                        }
                    }
        });
	}
}
