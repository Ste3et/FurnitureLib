package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import net.milkbowl.vault.Metrics;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.Ste3et_C0st.FurnitureLib.Command.TabCompleterHandler;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.DeSerializer;
import de.Ste3et_C0st.FurnitureLib.Database.Serializer;
import de.Ste3et_C0st.FurnitureLib.Database.SQLManager;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureEvents;
import de.Ste3et_C0st.FurnitureLib.LimitationManager.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ColorUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.CraftingInv;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.Protection.ProtectionManager;

public class FurnitureLib extends JavaPlugin{

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;
	private Connection con;
	private ProtectionManager Pmanager;
	private LightManager lightMgr;
	private boolean useGamemode = true, canSit = true, update = true, useParticle = true, useRegionMemberAccess = false, autoPurge = false, removePurge = false;
	private CraftingInv craftingInv;
	private LanguageManager lmanager;
	private SQLManager sqlManager;
	private LimitationManager limitManager;
	private ColorUtil colorManager;
	private Serializer serializeNew;
	private DeSerializer deSerializerNew;
	private Permission permission = null;
	private Updater updater;
	private BlockManager bmanager;
	private PublicMode mode;
	private EventType type;
	private WorldPool wPool;
	private int purgeTime = 30;

	public LanguageManager getLangManager(){return this.lmanager;}
	public LightManager getLightManager(){return this.lightMgr;}
	public ProtectionManager getPermManager(){return this.Pmanager;}
	public LimitationManager getLimitManager(){return this.limitManager;}
	public ColorUtil getColorManager(){return this.colorManager;}
	public CraftingInv getCraftingInv(){return this.craftingInv;}
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public Connection getConnection(){return this.con;}
	public ObjectID getObjectID(String c, String plugin, Location loc){return new ObjectID(c, plugin, loc);}
	public Boolean useGamemode() {return useGamemode;}
	public Serializer getSerializer(){return serializeNew;}
	public DeSerializer getDeSerializer(){return deSerializerNew;}
	public EventType getDefaultEventType(){return this.type;}
	public PublicMode getDefaultPublicType(){return this.mode;}
	public PluginManager getPluginManager(){return this.getServer().getPluginManager();}
	public BlockManager getBlockManager() {return bmanager;}
	public SQLManager getSQLManager(){return this.sqlManager;}
	public WorldPool getWorldPool(){return this.wPool;}
	public int getPurgeTime(){return this.purgeTime;}
	public static FurnitureLib getInstance(){return instance;}
	public boolean isAutoPurge(){return this.autoPurge;}
	public boolean isPurgeRemove(){return this.removePurge;}
	public boolean canBuild(Player p, ObjectID id, EventType type){ return Pmanager.canBuild(p, id, type);}
	public boolean isUpdate(){return this.update;}
	public boolean isParticleEnable(){return this.useParticle;}
	public boolean hasPerm(Player p, String perm){return permission.has(p, perm);}
	public boolean hasPerm(CommandSender p, String perm){return permission.has(p, perm);}
	public boolean canSitting(){return this.canSit;}
	public boolean haveRegionMemberAccess(){return this.useRegionMemberAccess;}
	public boolean isDouble(String s){try{Double.parseDouble(s);}catch(NumberFormatException e){return false;}return true;}
	public boolean isBoolean(String s){try {Boolean.parseBoolean(s.toLowerCase());}catch (Exception e) {return false;}return true;}
	public boolean isInt(String s){try{Integer.parseInt(s);}catch(NumberFormatException e){return false;}return true;}
	
	
	public Updater getUpdater(){return updater;}
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		if(!isEnable("ProtocolLib", true)){getLogger().warning("ProtocolLib not found");}
		if(!isEnable("Vault", true)){getLogger().warning("Vault not found");}
		if(!setupPermissions()){getLogger().warning("No Permission System found"); Bukkit.getPluginManager().disablePlugin(this);}
		try{new Metrics(this).start();}catch(Exception e){e.printStackTrace();}
		instance = this;
		getConfig().addDefaults(YamlConfiguration.loadConfiguration(getResource("config.yml")));
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		this.colorManager = new ColorUtil();
		this.serializeNew = new Serializer();
		this.deSerializerNew = new DeSerializer();
		this.lightMgr = new LightManager(this);
		this.lmanager = new LanguageManager(instance, getConfig().getString("config.Language"));
		this.useGamemode = getConfig().getBoolean("config.NormalGamemodeRemove");
		this.useRegionMemberAccess = getConfig().getBoolean("config.ProtectionLib.RegeionMemberAccess");
		this.canSit = !getConfig().getBoolean("config.DisableSitting");
		this.useParticle = getConfig().getBoolean("config.useParticles");
		this.purgeTime = getConfig().getInt("config.Purge.time");
		this.autoPurge = getConfig().getBoolean("config.Purge.autoPurge");
		this.removePurge = getConfig().getBoolean("config.Purge.removePurge");
		this.updater = new Updater();
		this.wPool = new WorldPool();
		this.wPool.loadWorlds();
		new FurnitureEvents(instance, manager);
		getServer().getPluginManager().registerEvents(new ChunkOnLoad(), this);
		PluginCommand c = getCommand("furniture");
		c.setExecutor(new command(this));
		c.setTabCompleter(new TabCompleterHandler(this));
		this.Pmanager = new ProtectionManager(instance);
		getLogger().info("==========================================");
		getLogger().info("FurnitureLibary Version: " + this.getDescription().getVersion());
		getLogger().info("Furniture Autor: " + this.getDescription().getAuthors().get(0));
		getLogger().info("Furniture Website: " + this.getDescription().getWebsite());
		getLogger().info("Furniture start load");
		Boolean b = isEnable("ProtectionLib", false);
		getLogger().info("Furniture find ProtectionLib: " + b.toString());
		createDefaultWatchers();
		this.sqlManager = new SQLManager(instance);
		this.sqlManager.initialize();
		this.sqlManager.loadALL();
		getLogger().info("Furniture load finish");
		getLogger().info("==========================================");
		this.craftingInv = new CraftingInv(this);
		this.limitManager = new LimitationManager(this);
		this.update = getConfig().getBoolean("config.CheckUpdate");
		this.bmanager = new BlockManager();
		PublicMode mode = PublicMode.valueOf(getConfig().getString("config.PlaceMode.Mode"));
		EventType type = EventType.valueOf(getConfig().getString("config.PlaceMode.Access"));
		if(mode!=null){this.mode = mode;}else{this.mode = PublicMode.PRIVATE;}
		if(type!=null){this.type = type;}else{this.type = EventType.INTERACT;}
		if(getConfig().getBoolean("config.timer.Enable")){int time = getConfig().getInt("config.timer.time");sqlManager.saveIntervall(time);}
		for(Player p : Bukkit.getOnlinePlayers()){if(p.isOp()){getUpdater().sendPlayer(p);}}
	}
	
	public boolean checkPurge(ObjectID obj, UUID uuid, int purgeTime){
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if(!player.hasPlayedBefore()) return false;
		long time = player.getLastPlayed();
		if(!isAfterDate(time, purgeTime))return false;
		if(removePurge){getFurnitureManager().remove(obj);return false;}
		obj.setSQLAction(SQLAction.REMOVE);
		return true;
	}
	
	public boolean isAfterDate(long time, int purgeTime){
		if(System.currentTimeMillis() - (time+(86400000*purgeTime)) >0){return true;}
		return false;
	}
	
	public boolean checkPurge(ObjectID obj, UUID uuid){
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if(!player.hasPlayedBefore()) return false;
		long time = player.getLastPlayed();
		if(!isAfterDate(time, purgeTime))return false;
		if(removePurge){getFurnitureManager().remove(obj);return false;}
		obj.setSQLAction(SQLAction.REMOVE);
		return true;
	}
	
	public boolean checkPurge(ObjectID obj, OfflinePlayer player){
		if(!player.hasPlayedBefore()) return false;
		long time = player.getLastPlayed();
		if(!isAfterDate(time, purgeTime))return false;
		if(removePurge){getFurnitureManager().remove(obj);return false;}
		obj.setSQLAction(SQLAction.REMOVE);
		return true;
	}
	
	private void createDefaultWatchers(){
		for(World w : Bukkit.getWorlds()){
			if(w!=null){
				getFurnitureManager().getDefaultWatcher(w, EntityType.ARMOR_STAND);
			}
		}
	}
	
	public void registerPluginFurnitures(Plugin plugin){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : manager.getObjectList()){
			if(obj==null) continue;
			if(objList.contains(obj)) continue;
			if(!objList.contains(obj)) objList.add(obj);
			if(obj.getPlugin()==null) continue;
			if(obj.getSQLAction().equals(SQLAction.REMOVE)) continue;
			if(obj.getPlugin().equalsIgnoreCase(plugin.getName())){
				spawn(obj.getProjectOBJ(), obj);
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
				for(fArmorStand as : obj.getPacketList()){
					as.kill();
				}
			}
		}
		getLogger().info("==========================================");
	}
	
	public void spawn(Project pro, Location l){
		Class<?> c = pro.getclass();
		ObjectID obj = new ObjectID(pro.getName(), pro.getPlugin().getName(), l);
		if(c==null ){return;}
		Constructor<?> ctor = c.getConstructors()[0];
			try {
			ctor.newInstance(obj);
			obj.setFinish();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void spawn(Project pro, ObjectID obj){
		Class<?> c = pro.getclass();
		if(c==null ){return;}
		Constructor<?> ctor = c.getConstructors()[0];
			try {
			ctor.newInstance(obj);
		} catch (Exception e) {e.printStackTrace();}
	}
}
