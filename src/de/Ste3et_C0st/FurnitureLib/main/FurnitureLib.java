package de.Ste3et_C0st.FurnitureLib.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import de.Ste3et_C0st.FurnitureLib.Database.Save;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;

public class FurnitureLib extends JavaPlugin{

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;
	private Connection con;
	private Save save;
	private Database sql;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		instance = this;
		createDatabase();
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		this.save = new Save(this);
		try{
			getConfig().addDefaults(YamlConfiguration.loadConfiguration(getResource("config.yml")));
			getConfig().options().copyDefaults(true);
			saveConfig();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        	Integer PacketID = event.getPacket().getIntegers().read(0);
                            if(getFurnitureManager().isArmorStand(PacketID)){
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
		
		ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
                        	if(event.getPacket().getSpecificModifier(boolean.class).read(1)){
                        		Player p = event.getPlayer();
                        		for(ArmorStandPacket packet : getFurnitureManager().getAsList()){
                        			if(packet.getPessanger()!=null){
                        				if(packet.getPessanger().equals(p)){
                            				packet.unleash();
                        				}
                        			}
                        		}
                        	}
                        }
                    }
        });
		
		getServer().getPluginManager().registerEvents(new ChunkOnLoad(), this);
		
	}
	
	private void createDatabase(){
		sql = new SQLite(
				Logger.getLogger("Minecraft"),
				"[FurnitureLib]", 
				getDataFolder().getAbsolutePath(), 
				"Furniture", 
				".sqlite");
		if (!sql.isOpen()) {
		    sql.open();
		}
	}
	
	public Database getDB(){return this.sql;}
	
	@Override
	public void onDisable(){
		if(!getFurnitureManager().getAsList().isEmpty()){
			for(ArmorStandPacket as : getFurnitureManager().getAsList()){
				as.destroy();
			}
		}
	}
	
	public void save(ArmorStandPacket asPacket){
		try {
			this.save.saveAsPacket(asPacket);
		} catch (SQLException e) {
			getLogger().warning("AsPacket cannot saving");
			getLogger().warning("===============================================================");
			e.printStackTrace();
			getLogger().warning("===============================================================");
		}
	}
	
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public Connection getConnection(){return this.con;}
	public ObjectID getObjectID(Class<?> c){return new ObjectID(c);}
	public Save getSaveManager(){return save;}
	
}
