package de.Ste3et_C0st.FurnitureLib.main;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

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
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Client.ENTITY_ACTION) {
			@SuppressWarnings("deprecation")
			@Override
			public void onPacketReceiving(PacketEvent event){
				if(event.getPacket() == null){return;}
				PacketContainer container = event.getPacket();
				StructureModifier<Integer> integers = container.getIntegers();
				if(!integers.read(1).equals((int) EntityType.ARMOR_STAND.getTypeId())){return;}
				if(!FurnitureLib.getInstance().getFurnitureManager().isArmorStand(integers.read(0))){return;}
				Player p = event.getPlayer();
				ArmorStandPacket packet = FurnitureLib.getInstance().getFurnitureManager().getArmorStandPacketByID(integers.read(0));
				ObjectID id = FurnitureLib.getInstance().getFurnitureManager().getObjectIDByID(integers.read(0));
				Location loc = packet.getLocation();
				FurnitureClickEvent cevent = new FurnitureClickEvent(p, packet, id, loc);
				this.plugin.getServer().getPluginManager().callEvent(cevent);
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
