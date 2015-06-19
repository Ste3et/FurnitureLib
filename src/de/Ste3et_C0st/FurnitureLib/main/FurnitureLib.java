package de.Ste3et_C0st.FurnitureLib.main;

import java.sql.Connection;
import java.util.logging.Logger;

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

import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Database.SQLite;
import de.Ste3et_C0st.FurnitureLib.Database.Serialize;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Protection.ProtectionManager;

public class FurnitureLib extends JavaPlugin{

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;
	private Connection con;
	private SQLite sql;
	private Serialize serialize;
	private ProtectionManager Pmanager;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		instance = this;
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		this.serialize = new Serialize();
		this.sql = new SQLite(this);
		this.sql.load();
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
		getCommand("furniture").setExecutor(new command(manager, this));
		this.sql.loadALL();
		this.Pmanager = new ProtectionManager(instance);
		getFurnitureManager().sendAll();
	}
	
	@Override
	public void onDisable(){
		if(!getFurnitureManager().getAsList().isEmpty()){
			for(ArmorStandPacket as : getFurnitureManager().getAsList()){
				as.destroy();
			}
		}
	}
	
	  public Boolean isDouble(String s){
		  try{
			  Double.parseDouble(s);
			  return true;
		  }catch(NumberFormatException e){
			  return false;
		  }
	  }
	  
	  public Boolean isBoolean(String s){
		  try {
			  s = s.toLowerCase();
			  Boolean.parseBoolean(s);
			  return true;
		} catch (Exception e) {
			return false;
		}
	  }
	  
	  public Boolean isInt(String s){
		  try{
			  Integer.parseInt(s);
			  return true;
		  }catch(NumberFormatException e){
			  return false;
		  }
	  }
	
	public void saveObjToDB(ObjectID obj){sql.save(obj);}
	public void removeObjFromDB(ObjectID obj){this.sql.delete(obj);}
	public void updateObjInDB(ObjectID obj){this.sql.delete(obj);this.sql.save(obj);}
	public Serialize getSerialize(){ return this.serialize;}
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public Connection getConnection(){return this.con;}
	public ObjectID getObjectID(String c, String plugin){return new ObjectID(c, plugin);}
	public boolean canBuild(Player p, Location loc){return Pmanager.canBuild(p, loc);}
}
