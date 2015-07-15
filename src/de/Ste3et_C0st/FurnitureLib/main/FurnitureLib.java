package de.Ste3et_C0st.FurnitureLib.main;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.SQLite;
import de.Ste3et_C0st.FurnitureLib.Database.Serialize;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.Limitation.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.CraftingInv;
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
	private Boolean Donate = true;
	private LimitationManager limitationMgr;
	private CraftingInv craftingInv;
	
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
		this.limitationMgr = new LimitationManager(instance);
		this.sql.load();
		try{
			getConfig().addDefaults(YamlConfiguration.loadConfiguration(getResource("config.yml")));
			getConfig().options().copyDefaults(true);
			saveConfig();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if ((getServer().getPluginManager().isPluginEnabled("Vault")) && (getConfig().getBoolean("config.UseMetrics"))) {
		      try
		      {
		        Metrics metrics = new Metrics(this);
		        metrics.start();
		      }
		      catch (IOException localIOException) {}
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
		this.Pmanager = new ProtectionManager(instance);
		getLogger().info("==========================================");
		getLogger().info("furniture load start, it can laggs");
		this.sql.loadAll();
		getLogger().info("furniture load finish");
		getLogger().info("==========================================");
		this.craftingInv = new CraftingInv(this);
	}
	
	@Override
	public void onDisable(){
		if(Donate) getLimitationManager().save();
		getLogger().info("==========================================");
		getLogger().info("furniture start save all ArmorStandPackets into the Database");
		if(!getFurnitureManager().getObjectList().isEmpty()){
			List<ObjectID> objList = new ArrayList<ObjectID>();
			for(ObjectID obj : getFurnitureManager().getUpdateList()){
				if(!getFurnitureManager().getPreLoadetList().contains(obj)){
					if(getFurnitureManager().getUpdateList().contains(obj)){
						sql.delete(obj);
						saveObjToDB(obj);
						objList.add(obj);
					}
				}
			}
			for(ObjectID obj : getFurnitureManager().getObjectList()){
				if(!objList.contains(obj)){
					if(!getFurnitureManager().getPreLoadetList().contains(obj)){
							saveObjToDB(obj);
							objList.add(obj);
					}
				}
			}
			for(ObjectID obj : getFurnitureManager().getRemoveList()){
				sql.delete(obj);
			}
		}else{
			for(ObjectID obj : getFurnitureManager().getRemoveList()){
				sql.delete(obj);
			}
			getLogger().info("ObjectList Empty");
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
	
		public boolean canPlace(Location l, Project pro, Player p){
			for(ObjectID obj : manager.getObjectList()){
				if(obj.getStartLocation().equals(l)){
					p.sendMessage("§aon this Position is already an furniture");
					return false;
				}
			}
			Integer i = (int) l.getY();
			BlockFace b = lUtil.yawToFace(l.getYaw());
			for(int x = 0; x<pro.getWitdh();x++){
				for(int y = 0; y<pro.getHeight();y++){
					for(int z = 0; z<pro.getLength();z++){
						Location loc = lUtil.getRelativ(l, b,(double) z,(double) -x).add(0, y, 0);
						Integer integer = (int) loc.getY();
						if(integer!=i){
							if(loc.getBlock().getType()!=null&&!loc.getBlock().getType().equals(Material.AIR)){
									p.sendMessage("§anot enauth Space");
									return false;
							}
						}

					}
				}
			}
			return true;
		}
		
	public void spawn(Project pro, Location l){
		Class<?> c = pro.getclass();
		if(c==null ){return;}
		Constructor<?> ctor = c.getConstructors()[0];
			try {
			ctor.newInstance(l, FurnitureLib.getInstance(), pro.getName(), pro.getPlugin(), null);
		} catch (Exception e) {}
	}
	
	public CraftingInv getCraftingInv(){return this.craftingInv;}
	public boolean isDonate(){return this.Donate;}
	private void saveObjToDB(ObjectID obj){sql.save(obj);}
	public Serialize getSerialize(){ return this.serialize;}
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public LimitationManager getLimitationManager(){return this.limitationMgr;}
	public Connection getConnection(){return this.con;}
	public ObjectID getObjectID(String c, String plugin, Location loc){return new ObjectID(c, plugin, loc);}
	public boolean canBuild(Player p, Location loc, Material m){
		return Pmanager.canBuild(p, loc, m);
	}
}
