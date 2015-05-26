package de.Ste3et_C0st.FurnitureLib.main;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;

public class FurnitureLib extends JavaPlugin {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;

	@Override
	public void onEnable(){
		instance = this;
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		getServer().getPluginManager().registerEvents(new ChunkOnLoad(), this);
		ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        	Integer PacketID = event.getPacket().getIntegers().read(0);
                            if(getFurnitureManager().isFromPlugin(PacketID)){
                            	ArmorStandPacket asPacket = getFurnitureManager().getArmorStandPacketByID(PacketID);
                            	ObjectID objID = getFurnitureManager().getObjectIDByID(PacketID);
                            	Location loc = asPacket.getLocation();
                            	Player p = event.getPlayer();
                            	EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            	switch (action) {
								case ATTACK:
									FurnitureBreakEvent bEvent = new FurnitureBreakEvent(p, asPacket, objID, loc);
									Bukkit.getServer().getPluginManager().callEvent(bEvent);
									break;
								case INTERACT_AT:
									FurnitureClickEvent cEvent = new FurnitureClickEvent(p, asPacket, objID, loc);
									Bukkit.getServer().getPluginManager().callEvent(cEvent);
									break;
								default: break;
								}
                            }
                        }
                    }
        });
	}
	
	@Override
	public void onDisable(){
		
	}
	
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public ObjectID getObjectID(Class<?> c){return new ObjectID(c);}
	
	
}
