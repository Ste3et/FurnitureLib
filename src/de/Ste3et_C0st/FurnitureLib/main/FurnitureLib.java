package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.logging.Logger;

import net.milkbowl.vault.Metrics;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.DeSerializer;
import de.Ste3et_C0st.FurnitureLib.Database.Serializer;
import de.Ste3et_C0st.FurnitureLib.Database.SQLManager;
import de.Ste3et_C0st.FurnitureLib.Database.Serialize;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureEvents;
import de.Ste3et_C0st.FurnitureLib.LimitationManager.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ColorUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.CraftingInv;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Protection.ProtectionManager;

public class FurnitureLib extends JavaPlugin{

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;
	private Connection con;
	private Serialize serialize;
	private ProtectionManager Pmanager;
	private LightManager lightMgr;
	private Boolean useGamemode = true;
	private CraftingInv craftingInv;
	private LanguageManager lmanager;
	private SQLManager sqlManager;
	private LimitationManager limitManager;
	private ColorUtil colorManager;
	private Serializer serializeNew;
	private DeSerializer deSerializerNew;
	private Permission permission = null;
	private PluginManager pluginManager = null;
	private Updater updater;
	
	public LanguageManager getLangManager(){return this.lmanager;}
	public LightManager getLightManager(){return this.lightMgr;}
	public ProtectionManager getPermManager(){return this.Pmanager;}
	public LimitationManager getLimitManager(){return this.limitManager;}
	public ColorUtil getColorManager(){return this.colorManager;}
	public CraftingInv getCraftingInv(){return this.craftingInv;}
	public Serialize getSerialize(){ return this.serialize;}
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public Connection getConnection(){return this.con;}
	public ObjectID getObjectID(String c, String plugin, Location loc){return new ObjectID(c, plugin, loc);}
	public boolean canBuild(Player p, ObjectID id, EventType type){ return Pmanager.canBuild(p, id, type);}
	public Boolean useGamemode() {return useGamemode;}
	public Serializer getSerializer(){return serializeNew;}
	public DeSerializer getDeSerializer(){return deSerializerNew;}
	public PluginManager getPluginManager(){return pluginManager;}
	public boolean hasPerm(Player p, String perm){return permission.has(p, perm);}
	public boolean hasPerm(CommandSender p, String perm){return permission.has(p, perm);}
	public Updater getUpdater(){return updater;}
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		if(!isEnable("ProtocolLib", true)){getLogger().warning("ProtocolLib not found");}
		if(!isEnable("Vault", true)){getLogger().warning("Vault not found");}
		if(!setupPermissions()){getLogger().warning("No Permission System found"); Bukkit.getPluginManager().disablePlugin(this);}
		try{new Metrics(this).start();}catch(Exception e){e.printStackTrace();}
		instance = this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		this.colorManager = new ColorUtil();
		this.serialize = new Serialize();
		this.serializeNew = new Serializer();
		this.deSerializerNew = new DeSerializer();
		this.lightMgr = new LightManager(this);
		this.lmanager = new LanguageManager(instance, getConfig().getString("config.Language"));
		this.pluginManager = getServer().getPluginManager();
		this.useGamemode = getConfig().getBoolean("config.NormalGamemodeRemove");
		this.updater = new Updater();
		getConfig().addDefaults(YamlConfiguration.loadConfiguration(getResource("config.yml")));
		getConfig().options().copyDefaults(true);
		saveConfig();
		new FurnitureEvents(instance, manager);
		getServer().getPluginManager().registerEvents(new ChunkOnLoad(), this);
		getCommand("furniture").setExecutor(new command(this));
		this.Pmanager = new ProtectionManager(instance);
		getLogger().info("==========================================");
		getLogger().info("FurnitureLibary Version: " + this.getDescription().getVersion());
		getLogger().info("Furniture Autor: " + this.getDescription().getAuthors().get(0));
		getLogger().info("Furniture Website: " + this.getDescription().getWebsite());
		getLogger().info("Furniture start load");
		this.sqlManager = new SQLManager(instance);
		this.sqlManager.loadALL();
		getLogger().info("Furniture load finish");
		getLogger().info("==========================================");
		this.craftingInv = new CraftingInv(this);
		this.limitManager = new LimitationManager(this);
		if(getConfig().getBoolean("config.timer.Enable")){
			int time = getConfig().getInt("config.timer.time");
			sqlManager.saveIntervall(time);
		}
		
		for(Player p : Bukkit.getOnlinePlayers()){
			if(p.isOp()){
				getUpdater().sendPlayer(p);
			}
		}
	}
	
	
	
	private boolean setupPermissions()
	   {
	       RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	       if (permissionProvider != null) {
	           permission = permissionProvider.getProvider();
	       }
	       return (permission != null);
	   }
	
	private boolean isEnable(String plugin, boolean shutdown){
		if(plugin.equalsIgnoreCase("Vault")){
			if(!getConfig().getBoolean("config.UseMetrics")) return false;
		}
		boolean b = getServer().getPluginManager().isPluginEnabled(plugin);
		if(!b && shutdown) getServer().getPluginManager().disablePlugin(this);
		return b;
	}
	
	@Override
	public void onDisable(){	
		getLogger().info("==========================================");
		getLogger().info("Furniture shutdown started");
		sqlManager.save();
		sqlManager.stop();
		getLogger().info("ArmorStandPackets saved");
		if(!getFurnitureManager().getObjectList().isEmpty()){
			for(ObjectID obj : getFurnitureManager().getObjectList()){
				for(ArmorStandPacket as : obj.getPacketList()){
					as.destroy();
				}
			}
		}
		
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
	
	public void spawn(Project pro, Location l){
		Class<?> c = pro.getclass();
		ObjectID obj = new ObjectID(pro.getName(), pro.getPlugin().getName(), l);
		if(c==null ){return;}
		Constructor<?> ctor = c.getConstructors()[0];
			try {
			ctor.newInstance(getInstance(), pro.getPlugin(), obj);
		} catch (Exception e) {}
	}
}
