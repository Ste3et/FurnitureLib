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

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class FurnitureEvents {

	public FurnitureEvents(FurnitureLib instance, final FurnitureManager manager){
		ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        	Integer PacketID = event.getPacket().getIntegers().read(0);
                            if(manager.isArmorStand(PacketID)){
                            	event.setCancelled(true);
                            	ArmorStandPacket asPacket = manager.getArmorStandPacketByID(PacketID);
                            	ObjectID objID = manager.getObjectIDByID(PacketID);
                            	Location loc = asPacket.getLocation();
                            	Player p = event.getPlayer();
                            	EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            	if(objID==null) return;
                            	if(loc==null) return;
                            	if(p==null) return;
                            	if(action==null) return;
                            	switch (action) {
								case ATTACK:
									if(p.getGameMode().equals(GameMode.ADVENTURE)) return;
									if(p.getGameMode().equals(GameMode.SPECTATOR)) return;
									final Player player = p;
									final ArmorStandPacket packet = asPacket;
									final ObjectID objectID = objID;
									final Location location = loc;
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										@Override
										public void run() {
											FurnitureBreakEvent bEvent = new FurnitureBreakEvent(player, packet, objectID, location);
											Bukkit.getServer().getPluginManager().callEvent(bEvent);
										}
									});
									break;
								case INTERACT_AT:
									if(p.getGameMode().equals(GameMode.SPECTATOR)) return;
									final Player player2 = p;
									final ArmorStandPacket packet2 = asPacket;
									final ObjectID objectID2 = objID;
									final Location location2 = loc;
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										@Override
										public void run() {
											FurnitureClickEvent cEvent = new FurnitureClickEvent(player2, packet2, objectID2, location2);
											Bukkit.getServer().getPluginManager().callEvent(cEvent);
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
                new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                        	if(event.getPacket().getSpecificModifier(boolean.class).read(1)){
                        		Player p = event.getPlayer();
                        		for(ObjectID obj : manager.getObjectList()){
                        			if(obj.isInRange(p)){
                                		for(ArmorStandPacket packet : obj.getPacketList()){
                                			if(packet.getPessanger()!=null){
                                				if(packet.getPessanger().equals(p)){
                                    				packet.unleash();
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
