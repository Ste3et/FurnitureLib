package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Command.TabCompleterHandler;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Command.disabledCommand;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.DeSerializer;
import de.Ste3et_C0st.FurnitureLib.Database.SQLManager;
import de.Ste3et_C0st.FurnitureLib.Database.Serializer;
import de.Ste3et_C0st.FurnitureLib.LimitationManager.Limitation;
import de.Ste3et_C0st.FurnitureLib.Listener.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Listener.FurnitureProtocolListener;
import de.Ste3et_C0st.FurnitureLib.Listener.onBlockDispense;
import de.Ste3et_C0st.FurnitureLib.Listener.onCrafting;
import de.Ste3et_C0st.FurnitureLib.Listener.onEntityExplode;
import de.Ste3et_C0st.FurnitureLib.Listener.player.onFurnitureLibDisabled;
import de.Ste3et_C0st.FurnitureLib.Listener.player.onPlayerDeath;
import de.Ste3et_C0st.FurnitureLib.Listener.player.onPlayerJoin;
import de.Ste3et_C0st.FurnitureLib.Listener.player.onPlayerQuit;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.ProjectManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.*;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.OfflinePlayerCache;
import de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.InventoryManager;
import de.Ste3et_C0st.FurnitureLib.async.listener.onChunkChange;
import de.Ste3et_C0st.FurnitureLib.main.Protection.ProtectionManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.*;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class FurnitureLib extends JavaPlugin {

    private static FurnitureLib instance;
    private static List<FurniturePlugin> furniturePlugins = Lists.newArrayList();
    private static int versionInt = 0;
    private static boolean enableDebug = false;
    private static int debugLevel = 0;
    
    private static Boolean newVersion = null;
    public boolean enabled = true;
    public HashMap<Project, Long> deleteMap = new HashMap<>();
    public HashMap<UUID, Long> timeStampPlace = new HashMap<>();
    public HashMap<UUID, Long> timeStampBreak = new HashMap<>();
    
    private LocationUtil lUtil;
    private FurnitureManager manager;
    private ProtectionManager Pmanager;
    private LightManager lightMgr;
    private HashMap<String, List<String>> permissionKit = new HashMap<>();

    private CraftingInv craftingInv;
    private SQLManager sqlManager;
    private ColorUtil colorManager;
    private Serializer serializeNew;
    private DeSerializer deSerializerNew;
    private Updater updater;
    private BlockManager bmanager;
    private InventoryManager inventoryManager;
    private ProtocolFields field = ProtocolFields.Spigot110;
    
    private ProjectManager pManager;
    private PermissionHandler permissionHandler;
    private Material defMaterial = Material.valueOf(isNewVersion() ? "COW_SPAWN_EGG" : "MONSTER_EGG");
    private OfflinePlayerCache cache;
    private boolean enabledPlugin = false;
    private FurnitureConfig furnitureConfig;
    private FurnitureProtocolListener furnitureProtocolListener;
    private FloodgateManager floodgateManager = null;
    private ServerFunction serverFunction = null;
    private static boolean folia = false, paper = false;
    
    static {
    	 versionInt = Integer.parseInt(getBukkitVersion());
    	 newVersion = versionInt > 12;
         folia = containsClass("io.papermc.paper.threadedregions.RegionizedServer");
         paper = containsClass("com.destroystokyo.paper.event.block.BlockDestroyEvent");
    }
    
    private static boolean containsClass(String string) {
    	try {
	       	Class<?> foliaClass = Class.forName(string);
	       	return Objects.nonNull(foliaClass);
        } catch (ClassNotFoundException e) {
       	    return false;
        }
    }
    
    public static boolean isFolia() {
        return folia;
    }
    
    public static boolean isPaper() {
    	return paper;
    }
    
    public static String getBukkitVersion() {
        return Bukkit.getServer().getBukkitVersion().replace(".", ",").replace("-", ",").split(",")[1];
    }

    public static FurnitureLib getInstance() {
        return instance;
    }

    public static List<FurniturePlugin> getFurniturePlugins() {
        return furniturePlugins;
    }

    public static void registerPlugin(FurniturePlugin plugin) {
        furniturePlugins.add(plugin);
        plugin.registerProjects();
        plugin.applyPluginFunctions();
    }

    public static void debug(String str) {
        debug(str, 0);
    }
    
    public static void debug(String str, int level) {
        if (enableDebug || level > debugLevel) FurnitureLib.getInstance().getLogger().log(Level.INFO, str);
    }
    
    public static void debug(List<String> str, int level) {
    	str.forEach(entry -> debug(entry, level));
    }

    public static int getVersionInt() {
        return versionInt;
    }

    public static boolean isNewVersion() {
    	return newVersion;
    }

    public LightManager getLightManager() {
        return this.lightMgr;
    }

    public ProtectionManager getPermManager() {
        return this.Pmanager;
    }
    
    public InventoryManager getInventoryManager() {
    	return this.inventoryManager;
    }

    public ColorUtil getColorManager() {
        return this.colorManager;
    }

    public CraftingInv getCraftingInv() {
        return this.craftingInv;
    }

    public String getTimeDif(long input, long dif, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(dif - (System.currentTimeMillis() - input)));
    }

    public Updater getUpdater() {
        return updater;
    }

    public Material getDefaultSpawnMaterial() {
        return this.defMaterial;
    }

    public LocationUtil getLocationUtil() {
        return this.lUtil;
    }

    public FurnitureManager getFurnitureManager() {
        return this.manager;
    }

    public ObjectID getObjectID(String c, String plugin, Location loc) {
        return new ObjectID(c, plugin, loc);
    }

    public Serializer getSerializer() {
        return serializeNew;
    }

    public DeSerializer getDeSerializer() {
        return deSerializerNew;
    }

    public PluginManager getPluginManager() {
        return this.getServer().getPluginManager();
    }

    public BlockManager getBlockManager() {
        return this.bmanager;
    }

    public SQLManager getSQLManager() {
        return this.sqlManager;
    }

    public ProtocolFields getField() {
        return this.field;
    }

    public HashMap<String, List<String>> getPermissionList() {
        return this.permissionKit;
    }

    public ProjectManager getProjectManager() {
        return this.pManager;
    }

    public HashMap<UUID, Long> getTimePlace() {
        return this.timeStampPlace;
    }

    public HashMap<UUID, Long> getTimeBreak() {
        return this.timeStampBreak;
    }

    public PermissionHandler getPermission() {
        return this.permissionHandler;
    }

    public void send(final String s) {
    	getLogger().info(s);
    }

    public boolean canBuild(Player p, ObjectID id, EventType type, boolean sendMessage) {
        return Pmanager.canBuild(p, id, type, sendMessage);
    }

    public boolean canBuild(Player p, ObjectID id, EventType type) {
        return this.canBuild(p, id, type, true);
    }
    
    public boolean isAutoFileUpdater() {
        return this.furnitureConfig.isAutoFileUpdater();
    }

    public boolean checkPurge(ObjectID obj, UUID uuid) {
        return checkPurge(obj, uuid, this.furnitureConfig.getPurgeTime());
    }

    public boolean checkPurge(ObjectID obj, OfflinePlayer player) {
        return checkPurge(obj, player.getUniqueId());
    }

    public boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isBoolean(String s) {
        try {
            Boolean.parseBoolean(s.toLowerCase());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public boolean isAfterDate(long time, int purgeTime) {
        return System.currentTimeMillis() - (time + this.furnitureConfig.getPurgeTimeDays()) > 0;
    }

    @Override
    public void onEnable() {
    	instance = this;
    	
    	this.furnitureConfig = new FurnitureConfig(instance);
    	this.furnitureConfig.initLanguage();
        if (getVersionInt() < 12 || getVersionInt() > 21) {
            this.disableFurnitureLib(Arrays.asList("<red>FurnitureLib only works on Spigot 1.12 - 1.21.10"));
            return;
        }

        if (!getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.disableFurnitureLib(Arrays.asList(
            		"<red>FurnitureLib <gray>can't be enabled",
            		"<gray>Please <red>install <gray>the right <red>Protocollib version",
            		"<click:open_url:'https://www.spigotmc.org/resources/protocollib.1997/'><red>https://www.spigotmc.org/resources/protocollib.1997/</click>",
            		"<red>FurnitureLib is temporarily <dark_red>disabled"
            		));
            send("==========================================");
            return;
        }
		
		if(this.getPluginManager().isPluginEnabled("Floodgate")) this.floodgateManager = new FloodgateManager();
		this.enabledPlugin = true;
		this.field = ProtocolFields.getField(getServer().getBukkitVersion());
		this.lUtil = new LocationUtil();
		this.loadServerFunctions();
		this.manager = new FurnitureManager();
		this.furnitureConfig.loadPluginConfig();
		this.updater = new Updater();
		this.colorManager = new ColorUtil();
		this.serializeNew = new Serializer();
		this.deSerializerNew = new DeSerializer();
		this.lightMgr = new LightManager(this);
		this.pManager = new ProjectManager();
		this.permissionHandler = new PermissionHandler();
		this.Pmanager = new ProtectionManager(instance);
		this.cache = new OfflinePlayerCache();
		this.furnitureConfig.getLangManager().sendConsoleMessage("==========================================");
		this.furnitureConfig.getLangManager().sendConsoleMessage("FurnitureLib Version: <yellow>" + this.getDescription().getVersion());
		this.furnitureConfig.getLangManager().sendConsoleMessage("Furniture Author: <gold>Ste3et_C0st");
		this.furnitureConfig.getLangManager().sendConsoleMessage("Furniture Website: <yellow>" + this.getDescription().getWebsite());
		this.furnitureConfig.getLangManager().sendConsoleMessage("Furniture find ProtectionLib: " + (isEnable("ProtectionLib", false) ? "<green>true" : "<red>false"));
		this.bmanager = new BlockManager();
		this.craftingInv = new CraftingInv(this);
		this.loadPermissionKit();
		autoConverter.modelConverter(getServer().getConsoleSender());
		
		this.sqlManager = new SQLManager(instance);
		this.sqlManager.saveInterval(FurnitureConfig.getFurnitureConfig().getSaveIntervall());
		this.inventoryManager = new InventoryManager();
		autoConverter.databaseConverter(getServer().getConsoleSender(), this.furnitureConfig.getDatabaseString());
		this.pManager.loadProjectFiles();
		
		this.getFurnitureManager().getObjectList().stream().forEach(ObjectID::registerBlocks);
		this.registerEvents();
		
		if (this.furnitureConfig.isAutoPurge()) {
			DeSerializer.autoPurge(this.furnitureConfig.getPurgeTime());
		}
		this.furnitureConfig.getLangManager().sendConsoleMessage("§2Furniture load finish :)");
		this.furnitureConfig.getLangManager().sendConsoleMessage("==========================================");
		Bukkit.getOnlinePlayers().stream().filter(p -> p != null && p.isOp()).forEach(p -> getUpdater().sendPlayer(p));
		PluginCommand c = getCommand("furniture");
		c.setExecutor(new command(this));
		c.setTabCompleter(new TabCompleterHandler());
    }
    
    private void loadServerFunctions() {
    	try {
    		if(isPaper()) {
        		this.serverFunction = (ServerFunction) Class.forName("de.Ste3et_C0st.FurnitureLib.Paper.PaperFunctions").newInstance();
        	}else if(isFolia()) {
        		this.serverFunction = (ServerFunction) Class.forName("de.Ste3et_C0st.FurnitureLib.Folia.FoliaFunctions").newInstance();
        	}
    	}catch (Exception excpetion) {
    		excpetion.printStackTrace();
    	}
    	if(this.serverFunction == null) this.serverFunction = new SpigotFunctions();
    	this.serverFunction.onEnable();
    }
    
    private void registerEvents() {
    	getPluginManager().registerEvents(new onPlayerJoin(), getInstance());
    	getPluginManager().registerEvents(new onCrafting(), getInstance());
    	getPluginManager().registerEvents(new onBlockDispense(), getInstance());
    	getPluginManager().registerEvents(new onEntityExplode(), getInstance());
    	getPluginManager().registerEvents(new onPlayerDeath(), getInstance());
    	getPluginManager().registerEvents(new onPlayerQuit(), getInstance());
    	getPluginManager().registerEvents(new ChunkOnLoad(), getInstance());
    	getPluginManager().registerEvents(new onChunkChange(), getInstance());
    	this.furnitureProtocolListener = new FurnitureProtocolListener();
    }
    
    private void disableFurnitureLib(List<String> instructions) {
    	this.furnitureConfig.getLangManager().sendConsoleMessage(instructions.toArray(new String[instructions.size()]));
    	this.enabled = false;
    	this.getCommand("furniture").setExecutor(new disabledCommand(this, instructions));
		Bukkit.getPluginManager().registerEvents(new onFurnitureLibDisabled(instructions), this);
    }
    
    public boolean isEnabledPlugin() {
    	return this.enabledPlugin;
    }
    
    public void reloadPluginConfig() {
        this.reloadConfig();
        this.getFurnitureConfig().getLimitManager().getTypes().forEach(Limitation::reload);
        this.furnitureConfig.initLanguage();
        this.furnitureConfig.loadPluginConfig();
        FurnitureManager.getInstance().getProjects().forEach(Project::loadDefaults);
    }

    public BufferedReader loadStream(String internUri) {
        if (!internUri.startsWith("/"))
        	internUri = "/" + internUri;
        InputStream stream = getInstance().getClass().getResourceAsStream(internUri);
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private void loadPermissionKit() {
    	try {
    		final File permissionFile = new File(getDataFolder(), "permissionKit.yml");
    		final YamlConfiguration permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
    		if(Objects.isNull(permissionConfig)) return;
    		permissionConfig.addDefaults(YamlConfiguration.loadConfiguration(loadStream("permissionKit.yml")));
    		permissionConfig.options().copyDefaults(true);
    		permissionConfig.options().copyHeader(true);
    		permissionConfig.save(permissionFile);
    		if (permissionConfig.contains("kit") == false) return;
    		if (permissionConfig.isConfigurationSection("kit")) {
    			permissionConfig.getConfigurationSection("kit").getKeys(false).forEach(letter -> {
                    String header = letter;
                    if (permissionConfig.isSet("kit." + header)) {
                        List<String> projectList = new ArrayList<String>();
                        if (permissionConfig.getStringList("kit." + header) != null) {
                            projectList = permissionConfig.getStringList("kit." + header);
                        }
                        permissionKit.put(header, projectList);
                    }
                });
            }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void saveIgnore() {
        try {
        	final File ignoredFile = new File(this.getDataFolder(), "ignoredPlayers.yml");
        	final YamlConfiguration configuration = new YamlConfiguration();
        	final List<String> ignoreList = new ArrayList<>();
        	this.getFurnitureManager().getIgnoreList().stream().map(UUID::toString).forEach(ignoreList::add);
            configuration.set("ignoreList", ignoreList);
            configuration.save(ignoredFile);
        }catch (Exception e) {
			e.printStackTrace();
		}
    }

    public boolean checkPurge(ObjectID obj, UUID uuid, int purgeTime) {
    	Optional<DiceOfflinePlayer> optional = getPlayerCache().getPlayer(uuid);
    	if(optional.isPresent()) {
    		long lastSeen = optional.get().getLastSeen();
    		if (!isAfterDate(lastSeen, purgeTime))
    		    return false;
    		if (this.furnitureConfig.isPurgeRemove()) {
    		   getFurnitureManager().remove(obj);
    		   return false;
    		}
    		obj.setSQLAction(SQLAction.REMOVE);
    		return true;
    	}
        return false;
    }

    public void registerPluginFurnitures(Plugin plugin) {
    	if(!enabledPlugin) return;
        manager.getObjectList().stream().filter(obj -> obj != null && obj.getPlugin() != null).forEach(obj -> {
            if (!obj.getSQLAction().equals(SQLAction.REMOVE)) {
                if (obj.getPlugin().equalsIgnoreCase(plugin.getName())) {
                    spawn(obj.getProjectOBJ(), obj);
                }
            }
        });
    }

    private boolean isEnable(String plugin, boolean shutdown) {
        boolean b = getServer().getPluginManager().isPluginEnabled(plugin);
        if (!b && shutdown)
        	this.disableFurnitureLib(Arrays.asList("ProtocolLib is missing please install ProtocolLib","You can it download at: §6§lhttps://www.spigotmc.org/resources/protocollib.1997/"));
        return b;
    }

    @Override
    public void onDisable() {
        getLogger().info("==========================================");
        getLogger().info("Furniture shutdown started");
        this.furnitureConfig.getLangManager().close();
        if(Objects.nonNull(sqlManager)) {
        	if (!getConfig().getBoolean("config.timer.Enable")) {
                this.sqlManager = new SQLManager(this);
            }
            sqlManager.save();
            sqlManager.stop();
        }
        instance = null;
        if(Objects.nonNull(getFurnitureManager())) {
        	if (!getFurnitureManager().getObjectList().isEmpty()) {
                for (ObjectID obj : getFurnitureManager().getObjectList()) {
                    for (fEntity as : obj.getPacketList()) {
                        as.kill();
                    }
                }
            }
        	this.saveIgnore();
        }
        getLogger().info("==========================================");
    }

    public ObjectID spawn(Project pro, Location l) {
        ObjectID objectID = pro.createObjectID(l);
        spawn(pro, objectID);
        return objectID;
    }
    
    public static boolean useDebugMode() {
    	return enableDebug;
    }

    public void spawn(Project pro, ObjectID obj) {
        if (pro == null || obj == null) return;
        obj.getProjectOBJ().getModelschematic().spawn(obj);
        pro.applyFunction(obj);
        obj.setFinish();
    }
    
    public void spawnWithAnimation(Project pro, ObjectID objectID) {
    	if (pro == null || objectID == null) return;
        objectID.getProjectOBJ().getModelschematic().spawnWithAnimation(objectID);
        pro.applyFunction(objectID);
        objectID.setFinish();
    }
    
    public FloodgateManager getFloodgateManager() {
    	return this.floodgateManager;
    }
    
    public OfflinePlayerCache getPlayerCache() {
    	return this.cache;
    }
    
    public static void setDebug(boolean bool) {
    	enableDebug = bool;
    }
    
    public FurnitureConfig getFurnitureConfig() {
    	return this.furnitureConfig;
    }
    
    public static boolean getVersion(MinecraftVersion minecraftVersion) {
    	return minecraftVersion.atOrAbove();
    }
    
    public static boolean isVersionOrAbove(String string) {
    	return FurnitureLib.getVersion(new MinecraftVersion(string));
    }

	public ServerFunction getServerFunction() {
		return this.serverFunction;
	}
	
	public static String getPacketVersion() {
		return "";
	}
}