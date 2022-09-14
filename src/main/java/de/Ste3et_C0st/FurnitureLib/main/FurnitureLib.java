package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Command.TabCompleterHandler;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Command.disabledCommand;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.DeSerializer;
import de.Ste3et_C0st.FurnitureLib.Database.SQLManager;
import de.Ste3et_C0st.FurnitureLib.Database.Serializer;
import de.Ste3et_C0st.FurnitureLib.Listener.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Listener.FurnitureProtocolListener;
import de.Ste3et_C0st.FurnitureLib.Listener.onBlockDispense;
import de.Ste3et_C0st.FurnitureLib.Listener.onChunkChange;
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
import de.Ste3et_C0st.FurnitureLib.main.Protection.ProtectionManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.*;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FurnitureLib extends JavaPlugin {

    private static FurnitureLib instance;
    private static List<FurniturePlugin> furniturePlugins = new ArrayList<>();
    private static int versionInt = 0;
    private static boolean enableDebug = false;
    private static int debugLevel = 0;

    private static Boolean newVersion = null;
    public boolean enabled = true;
    public HashMap<Project, Long> deleteMap = new HashMap<>();
    public HashMap<UUID, Long> timeStampPlace = new HashMap<>();
    public HashMap<UUID, Long> timeStampBreak = new HashMap<>();
    
    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger("Minecraft");
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
    
    static {
    	 String bukkitVersion = getBukkitVersion();
         if (bukkitVersion.contains("_")) {
             String versionString = bukkitVersion.split("_")[1];
             versionInt = Integer.parseInt(versionString);
             newVersion = versionInt > 12;
         }
    }
    
    public static String getBukkitVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
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

    public void send(String s) {
        getServer().getConsoleSender().sendMessage(s);
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

    private boolean isRightProtocollib(String s) {
        return s.startsWith("4");
    }

    @Override
    public void onEnable() {
    	instance = this;
        if (getVersionInt() < 9 || getVersionInt() > 19) {
            this.disableFurnitureLib(Arrays.asList("§cFurnitureLib only works on Spigot 1.9 - 1.19"));
            return;
        }

        if (!getPluginManager().isPluginEnabled("ProtocolLib")) {
            this.disableFurnitureLib(Arrays.asList(
            		"§cFurnitureLib §7can't be enabled",
            		"§7Please §cinstall §7the right §e§nProtocollib version",
            		"§5Download it here: §l§9https://www.spigotmc.org/resources/protocollib.1997/",
            		"§c§4FurnitureLib is temporarily disabled"
            		));
            send("==========================================");
            return;
        }
        
        int protocolLibVersion = getProcotoLlibVersion(getPluginManager().getPlugin("ProtocolLib"));
        
		if (getBukkitVersion().startsWith("v1_14")) {
			send("§5Info: §eFor Spigot 1.14.x you need §6ProtocolLib 4.5.0 Build #8 §eor above");
			send("§5Download it here: §l§9https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/");
			send("§5Otherwise you will receive: §cNoClassDefFoundError: org/apache/commons/lang3/Validate");
		} else if (getBukkitVersion().startsWith("v1_15_2") && protocolLibVersion < 46) {
			send("§5Info: §eFor Spigot 1.15.2 you need §6ProtocolLib 4.5.1 §eor above");
			send("§5Download it here: §l§9https://www.spigotmc.org/resources/protocollib.1997/");
			send("§5Otherwise you will receive: §cNo field with type java.util.Map exists in class EnumProtocol.");
		} else if (getVersionInt() > 15) {
			if (getVersionInt() > 16) {
				try {
					Class<?> clazz = Class.forName("com.comphenix.protocol.events.PacketContainer");
					Method method = clazz.getMethod("getEnumEntityUseActions");
					if(Objects.isNull(method)) {
						getLogger().warning("[FurnitureLib] getEnumEntityUseActions didn't exist");
						this.disableFurnitureLib(Arrays.asList(
								"§5Info: §eFor Spigot 1.17.x you need §6ProtocolLib 4.7.0 Build #511 §eor above",
								"§5Download it here: §l§9https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/",
								"§7FurnitureLib will stop working!"));
						return;
					}
				}catch (Exception e) {
					this.disableFurnitureLib(Arrays.asList(
							"§5Info: §eFor Spigot 1.17.x you need §6ProtocolLib 4.7.0 Build #511 §eor above",
							"§5Download it here: §l§9https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/",
							"§7FurnitureLib will stop working!"));
					return;
				}
			}
			
			if(getVersionInt() == 18) {
				if(protocolLibVersion < 48) {
					this.disableFurnitureLib(Arrays.asList(
							"§5Info: §eFor Spigot 1.18.x you need §6ProtocolLib 4.8.0 §eor above",
							"§5Download it here: §l§9https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/",
							"§7FurnitureLib will stop working!"));
					return;
				}
			}
			
			try {
				Class.forName("com.comphenix.protocol.wrappers.Pair");
			} catch (ClassNotFoundException ex) {
				this.disableFurnitureLib(Arrays.asList(
						"§5Info: §eFor Spigot 1.16.x you need §6ProtocolLib 4.6.0 Build #472 §eor above",
						"§5Download it here: §l§9https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/",
						"§7FurnitureLib will stop working!"));
				return;
			}
		}
		
		if(getVersionInt() < 13) {
			debug("FurnitureLib >> Please Update your Server to a newer environment (1.13 or higher)");
			debug("FurnitureLib >> Maybye your Server Version " + getBukkitVersion() + " can't be supported in the future!");
		}
        
		if(protocolLibVersion < 40) {
			List<String> instructions = Arrays.asList("Furniture Lib doesn't find the correct ProtocolLib",
					"Please Install Protocollib §c4.x",
					"You can it download at: §6§lhttps://www.spigotmc.org/resources/protocollib.1997/");
			this.disableFurnitureLib(instructions);
			send("==========================================");
			return;
		}
		
		if(this.getPluginManager().isPluginEnabled("Floodgate")) {
			this.floodgateManager = new FloodgateManager();
		}
		
		this.furnitureConfig = new FurnitureConfig(instance);
		this.updater = new Updater();
		this.enabledPlugin = true;
		field = ProtocolFields.getField(getServer().getBukkitVersion());
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		this.colorManager = new ColorUtil();
		this.serializeNew = new Serializer();
		this.deSerializerNew = new DeSerializer();
		this.lightMgr = new LightManager(this);
		
		this.pManager = new ProjectManager();
		this.permissionHandler = new PermissionHandler();
		this.Pmanager = new ProtectionManager(instance);
		this.cache = new OfflinePlayerCache();
		send("==========================================");
		send("FurnitureLibrary Version: §e" + this.getDescription().getVersion());
		send("Furniture Author: §6" + this.getDescription().getAuthors().get(0));
		send("Furniture Website: §e" + this.getDescription().getWebsite());
		send("FurnitureLib load for Minecraft: 1." + getVersionInt());
		send("Furniture start load");
		boolean b = isEnable("ProtectionLib", false);
		send("Furniture find ProtectionLib: §e" + Boolean.toString(b));
		this.bmanager = new BlockManager();
		this.craftingInv = new CraftingInv(this);
		this.loadPermissionKit();
		
		autoConverter.modelConverter(getServer().getConsoleSender());
		this.furnitureConfig.loadPluginConfig();
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

		send("§2Furniture load finish :)");
		send("==========================================");
		Bukkit.getOnlinePlayers().stream().filter(p -> p != null && p.isOp()).forEach(p -> getUpdater().sendPlayer(p));
		PluginCommand c = getCommand("furniture");
		c.setExecutor(new command(this));
		c.setTabCompleter(new TabCompleterHandler());
		
    }
    
    private int getProcotoLlibVersion(Plugin plugin) {
    	String version = plugin.getDescription().getVersion();
    	int maxLength = 3;
    	String subString = version.substring(0, maxLength > version.length() ? version.length() : maxLength).replaceAll("[^0-9]", "");
    	return Integer.parseInt(subString);
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
    	FurnitureLib.debug(instructions, 10);
    	this.enabled = false;
    	PluginCommand pluginCommand = getCommand("furniture");
    	pluginCommand.setExecutor(new disabledCommand(this, instructions));
		Bukkit.getPluginManager().registerEvents(new onFurnitureLibDisabled(instructions), this);
    }
    
    public boolean isEnabledPlugin() {
    	return this.enabledPlugin;
    }
    
    public void reloadPluginConfig() {
        enableDebug = true;
        this.reloadConfig();
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
        config c = new config(this);
        FileConfiguration file = c.getConfig("permissionKit.yml", "");
        if (file == null)
            return;
        file.addDefaults(YamlConfiguration.loadConfiguration(loadStream("permissionKit.yml")));
        file.options().copyDefaults(true);
        file.options().copyHeader(true);
        c.saveConfig("permissionKit.yml", file, "");
        if (file.contains("kit")) {
            if (file.isSet("kit")) {
                if (file.isConfigurationSection("kit")) {
                    file.getConfigurationSection("kit").getKeys(false).forEach(letter -> {
                        String header = letter;
                        if (file.isSet("kit." + header)) {
                            List<String> projectList = new ArrayList<String>();
                            if (file.getStringList("kit." + header) != null) {
                                projectList = file.getStringList("kit." + header);
                            }
                            permissionKit.put(header, projectList);
                        }
                    });
                }
            }
        }
    }

    private void saveIgnore() {
        List<String> ignoreList = new ArrayList<>();
        for (UUID uuid : getFurnitureManager().getIgnoreList())
            ignoreList.add(uuid.toString());
        config c = new config(this);
        FileConfiguration configuration = c.getConfig("ignoredPlayers", "");
        configuration.set("ignoreList", ignoreList);
        c.saveConfig("ignoredPlayers", configuration, "");
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
        if (pro == null) return;
        if (obj == null) return;
        try {
            obj.getProjectOBJ().getModelschematic().spawn(obj);
            pro.applyFunction(obj);
            obj.setFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void spawnWithAnimation(Project pro, ObjectID objectID) {
    	if (pro == null) return;
        if (objectID == null) return;
        try {
        	objectID.getProjectOBJ().getModelschematic().spawn(objectID);
            pro.applyFunction(objectID);
            objectID.setFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}