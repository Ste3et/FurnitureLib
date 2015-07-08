package de.Ste3et_C0st.FurnitureLib.main;

import java.sql.Connection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import de.Ste3et_C0st.FurnitureLib.main.Bed.sleepAnimation;
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
	private LightManager lightMgr;
	private sleepAnimation animation;
	
	public LightManager getLightManager(){return this.lightMgr;}
	public sleepAnimation getSleepManager(){return this.animation;}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		instance = this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		this.serialize = new Serialize();
		this.sql = new SQLite(this);
		this.lightMgr = new LightManager();
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
		
		getLogger().info("==========================================");
		getLogger().info("furniture load start, it can laggs");
		this.sql.loadALL();
		getLogger().info("furniture load finish");
		getLogger().info("==========================================");
		
		this.Pmanager = new ProtectionManager(instance);
		getFurnitureManager().sendAll();
	}
	
	@Override
	public void onDisable(){
		getLogger().info("==========================================");
		getLogger().info("furniture start save all ArmorStandPackets into the Database");
		if(!getFurnitureManager().getObjectList().isEmpty()){
			for(ObjectID obj : getFurnitureManager().getObjectList()){
				if(!getFurnitureManager().getRemoveList().contains(obj)){
					saveObjToDB(obj);
					continue;
				}else if(getFurnitureManager().getUpdateList().contains(obj)){
					removeObjToDB(obj);
					saveObjToDB(obj);
					continue;
				}else if(getFurnitureManager().getRemoveList().contains(obj)){
					removeObjToDB(obj);
					continue;
				}
			}
		}
		getLogger().info("ArmorStandPackets saved");
		getLogger().info("ArmorStands delete from all worlds startet");
		if(!getFurnitureManager().getAsList().isEmpty()){
			for(ArmorStandPacket as : getFurnitureManager().getAsList()){
				as.destroy();
			}
		}
		getLogger().info("all ArmorStands removed");
		getLogger().info("==========================================");
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
	
	private void saveObjToDB(ObjectID obj){sql.save(obj);}
	private void removeObjToDB(ObjectID obj){sql.delete(obj);}
	public Serialize getSerialize(){ return this.serialize;}
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public Connection getConnection(){return this.con;}
	public ObjectID getObjectID(String c, String plugin, Location loc){return new ObjectID(c, plugin, loc);}
	public boolean canBuild(Player p, Location loc, Material m){return Pmanager.canBuild(p, loc, m);}
}
