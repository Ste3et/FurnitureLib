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
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

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
                            	if(asPacket==null){System.out.println("DEBUG#1");return;}
                            	ObjectID objID = manager.getObjectIDByID(PacketID);
                            	if(objID==null){System.out.println("DEBUG#2");return;}
                            	if(objID.getSQLAction().equals(SQLAction.REMOVE)){System.out.println("DEBUG#3");return;}
                            	
                            	Location loc = asPacket.getLocation();
                            	Player p = event.getPlayer();
                            	EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            	
                            	if(loc==null){System.out.println("DEBUG#4");return;}
                            	if(p==null){System.out.println("DEBUG#5");return;}
								final Player player = p;
								final ArmorStandPacket packet = asPacket;
								final ObjectID objectID = objID;
								final Location location = loc;
                            	switch (action) {
								case ATTACK:
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
