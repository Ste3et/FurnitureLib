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
import de.Ste3et_C0st.FurnitureLib.main.Type.EntityMoving;
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
                            	if(asPacket==null){System.out.println("error1");return;}
                            	ObjectID objID = manager.getObjectIDByID(PacketID);
                            	if(objID==null){System.out.println("error2");return;}
                            	if(objID.getSQLAction().equals(SQLAction.REMOVE)){return;}
                            	if(objID!=null){if(objID.isPrivate()){return;}}
                            	Location loc = asPacket.getLocation();
                            	Player p = event.getPlayer();
                            	EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            	
                            	if(loc==null){System.out.println("error3");return;}
                            	if(p==null){System.out.println("error4");return;}
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
											FurnitureBreakEvent event = new FurnitureBreakEvent(player, packet, objectID, location);
											Bukkit.getServer().getPluginManager().callEvent(event);
										}
									});
									break;
								case INTERACT_AT:
									if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
									if(p.getGameMode().equals(GameMode.CREATIVE)&&!FurnitureLib.getInstance().creativeInteract()){
										if(!FurnitureLib.getInstance().hasPerm(p, "furniture.bypass.creative.interact")){
											return;
										}
									}
									Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
									@Override
										public void run() {
											FurnitureClickEvent event = new FurnitureClickEvent(player, packet, objectID, location);
											Bukkit.getServer().getPluginManager().callEvent(event);		
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
                        	final Player p = event.getPlayer();
                        	float a = event.getPacket().getFloat().read(0);
                    		float b = event.getPacket().getFloat().read(1);
                    		boolean c = event.getPacket().getBooleans().read(0);
                    		boolean d = event.getPacket().getBooleans().read(1);
                    		EntityMoving moving = null;
                    		if(a>0){moving = EntityMoving.LEFT;}
                    		if(a<0){moving = EntityMoving.RIGHT;}
                    		if(b>0){moving = EntityMoving.FORWARD;}
                    		if(b<0){moving = EntityMoving.BACKWARD;}
                        	if(c){moving = EntityMoving.JUMPING;}
                        	if(d){moving = EntityMoving.SNEEKING;}
                        	for(final ObjectID obj : manager.getObjectList()){
                        		if(obj.isInRange(p)){
                        			for(final fArmorStand packet : obj.getPacketList()){
                        				if(packet.getPassanger()!=null){
                        					event.setCancelled(true);
                        					final EntityMoving action = moving;
                        					Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
												@Override
												public void run() {
													if(action==null) return;
													if(action.equals(EntityMoving.SNEEKING)){packet.eject();return;}
													else{
														FurnitureMoveEvent event = new FurnitureMoveEvent(p, packet, obj, action);
														Bukkit.getServer().getPluginManager().callEvent(event);
													}
												}
                        					});
                        				}
                        			}
                        		}
                        	}
                        }
                    }
        });
	}
}
