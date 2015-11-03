package de.Ste3et_C0st.FurnitureLib.Events;

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
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;

public class FurnitureEvents {

	public FurnitureEvents(FurnitureLib instance, final FurnitureManager manager){
		ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        	Integer PacketID = event.getPacket().getIntegers().read(0);
                            if(manager.isArmorStand(PacketID)){
                            	event.setCancelled(true);
                            	fArmorStand asPacket = manager.getfArmorStandByID(PacketID);
                            	if(asPacket==null){return;}
                            	ObjectID objID = manager.getObjectIDByID(PacketID);
                            	if(objID==null){return;}
                            	if(objID.getSQLAction().equals(SQLAction.REMOVE)){return;}
                            	
                            	Location loc = asPacket.getLocation();
                            	Player p = event.getPlayer();
                            	EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            	
                            	if(loc==null){return;}
                            	if(p==null){return;}
								final Player player = p;
								final fArmorStand packet = asPacket;
								final ObjectID objectID = objID;
								final Location location = loc;
                            	switch (action) {
								case ATTACK:
									if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
									Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
										@Override
										public void run() {
											FurnitureLib.getInstance().getPluginManager().callEvent(new FurnitureBreakEvent(player, packet, objectID, location));
										}
									});
									break;
								case INTERACT_AT:
									if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
									Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
									@Override
										public void run() {
											FurnitureLib.getInstance().getPluginManager().callEvent(new FurnitureClickEvent(player, packet, objectID, location));
										}
									});
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
                        	if(event.getPacket().getSpecificModifier(boolean.class).read(1)){
                        		final Player p = event.getPlayer();
                        		for(ObjectID obj : manager.getObjectList()){
                        			if(obj.isInRange(p)){
                                		for(final fArmorStand packet : obj.getPacketList()){
                                			if(packet.getPassanger()!=null){
                                				if(packet.getPassanger().equals(p)){
                                					event.setCancelled(true);
                                					Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
														@Override
														public void run() {
															packet.eject();
														}
													});
                                    				
                                				}
                                			}
                                		}
                        			}
                        		}
                        	}
                        }
                    }
        });
	}
}
