package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Command.TabCompleterHandler;
import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.DeSerializer;
import de.Ste3et_C0st.FurnitureLib.Database.SQLManager;
import de.Ste3et_C0st.FurnitureLib.Database.Serializer;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureEvents;
import de.Ste3et_C0st.FurnitureLib.Events.internal.*;
import de.Ste3et_C0st.FurnitureLib.LimitationManager.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.ProjectManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.*;
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
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FurnitureLib extends JavaPlugin {

    private static FurnitureLib instance;
    private static List<FurniturePlugin> furniturePlugins = new ArrayList<>();
    private static int versionInt = 0;
    private static boolean enableDebug = false;
    private static int debugLevel = 0;
    /**
     * Check if the plugin is 1.13 or higher
     *
     * @return
     */

    private static Boolean newVersion = null;
    public boolean autoFileUpdater = true;
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
    private boolean useGamemode = true, canSit = true, update = true, useParticle = true, useRegionMemberAccess = false,
            autoPurge = false, removePurge = false, creativeInteract = true, creativePlace = true, glowing = true,
            spamBreak = true, spamPlace = true, rotateOnSit = true, useSSL = false;
    private CraftingInv craftingInv;
    private LanguageManager lmanager;
    private SQLManager sqlManager;
    private LimitationManager limitManager;
    private ColorUtil colorManager;
    private Serializer serializeNew;
    private DeSerializer deSerializerNew;
    private Updater updater;
    private BlockManager bmanager;
    private InventoryManager inventoryManager;
    private PublicMode mode;
    private EventType type;
    private ProtocolFields field = ProtocolFields.Spigot110;
    private ProjectManager pManager;
    private PermissionHandler permissionHandler;
    private String timePattern = "mm:ss:SSS";
    private int purgeTime = 30, viewDistance = 100, limitGlobal = -1;
    private long purgeTimeMS = 0, spamBreakTime = 5000, spamPlaceTime = 5000;
    private Material defMaterial = Material.valueOf(isNewVersion() ? "COW_SPAWN_EGG" : "MONSTER_EGG");
    private boolean sync = true;
    private static final int BSTATS_ID = 454;
    private List<String> ignoredWorlds = new ArrayList<String>();

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
        if (enableDebug || level < debugLevel) System.out.println(str);
    }

    public static int getVersionInt() {
    	
        if (versionInt == 0) {
            String bukkitVersion = getBukkitVersion();
            int version = 0;
            if (bukkitVersion.contains("_")) {
                String versionString = bukkitVersion.split("_")[1];
                version = Integer.parseInt(versionString);
            }
            versionInt = version;
        }
        return versionInt;
    }

    public static boolean isNewVersion() {
        if (Objects.isNull(newVersion)) {
            newVersion = getVersionInt() > 12;
            return newVersion;
        } else {
            return newVersion;
        }
    }

    public LanguageManager getLangManager() {
        return this.lmanager;
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

    public LimitationManager getLimitManager() {
        return this.limitManager;
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

    public Boolean useGamemode() {
        return this.useGamemode;
    }

    public Serializer getSerializer() {
        return serializeNew;
    }

    public DeSerializer getDeSerializer() {
        return deSerializerNew;
    }

    public EventType getDefaultEventType() {
        return this.type;
    }

    public PublicMode getDefaultPublicType() {
        return this.mode;
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

    public String getTimePattern() {
        return this.timePattern;
    }

    public PermissionHandler getPermission() {
        return this.permissionHandler;
    }

    public int getPurgeTime() {
        return this.purgeTime;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public int getLimitGlobal() {
        return this.limitGlobal;
    }

    public long getBreakTime() {
        return this.spamBreakTime;
    }

    public long getPlaceTime() {
        return this.spamPlaceTime;
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

    public boolean canSitting() {
        return this.canSit;
    }

    public boolean creativeInteract() {
        return this.creativeInteract;
    }

    public boolean creativePlace() {
        return this.creativePlace;
    }

    public boolean haveRegionMemberAccess() {
        return this.useRegionMemberAccess;
    }

    public boolean isAutoFileUpdater() {
        return this.autoFileUpdater;
    }

    public boolean useSSL() {
        return this.useSSL;
    }

    public boolean checkPurge(ObjectID obj, UUID uuid) {
        return checkPurge(obj, uuid, this.purgeTime);
    }

    public boolean checkPurge(ObjectID obj, OfflinePlayer player) {
        return checkPurge(obj, player.getUniqueId());
    }

    public boolean isSync() {
        return this.sync;
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    public boolean isAutoPurge() {
        return this.autoPurge;
    }

    public boolean isPurgeRemove() {
        return this.removePurge;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public boolean isParticleEnable() {
        return this.useParticle;
    }

    public boolean isSpamPlace() {
        return this.spamPlace;
    }

    public boolean isSpamBreak() {
        return this.spamBreak;
    }

    public boolean isRotateOnSitEnable() {
        return this.rotateOnSit;
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
        return System.currentTimeMillis() - (time + purgeTimeMS) > 0;
    }

    private boolean isRightProtocollib(String s) {
        return s.startsWith("4");
    }

    @Override
    public void onEnable() {
        if (getVersionInt() < 9 || getVersionInt() > 16) {
            send("§cFurnitureLib only works on Spigot 1.9 - 1.15");
            getPluginManager().disablePlugin(this);
            return;
        }

        if (!getPluginManager().isPluginEnabled("ProtocolLib")) {
        	send("§cFurnitureLib §7can't be enabled please §cinstall §7the right §e§nProtocollib version");
            send("§5Download it here: §l§9http://ci.dmulloy2.net/job/ProtocolLib%20Gradle/lastStableBuild/");
            send("§c§4FurnitureLib will be disabled");
            getPluginManager().disablePlugin(this);
        } else {
            instance = this;
            getConfig().addDefaults(YamlConfiguration.loadConfiguration(loadStream("config.yml")));
            getConfig().options().copyDefaults(true);
            getConfig().options().copyHeader(true);
            saveConfig();
            field = ProtocolFields.getField(getServer().getBukkitVersion());
            this.lUtil = new LocationUtil();
            this.manager = new FurnitureManager();
            this.colorManager = new ColorUtil();
            this.serializeNew = new Serializer();
            this.deSerializerNew = new DeSerializer();
            this.lightMgr = new LightManager(this);
            this.useSSL = getConfig().getBoolean("config.Database.useSSL");
            this.pManager = new ProjectManager();
            this.permissionHandler = new PermissionHandler();
            this.Pmanager = new ProtectionManager(instance);
            this.sync = getConfig().getBoolean("config.sync", true);

            send("==========================================");
            send("FurnitureLibrary Version: §e" + this.getDescription().getVersion());
            send("Furniture Author: §6" + this.getDescription().getAuthors().get(0));
            send("Furniture Website: §e" + this.getDescription().getWebsite());
            send("FurnitureLib load for Minecraft: 1." + getVersionInt());
            String s = getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
            if (getBukkitVersion().startsWith("v1_14")) {
                send("§5Info: §eFor Spigot 1.14.x you need §6ProtocolLib 4.5.0 Build #8 §eor above");
                send("§5Download it here: §l§9http://ci.dmulloy2.net/job/ProtocolLib%20Gradle/lastStableBuild/");
                send("§5Otherwise you will receive: §cNoClassDefFoundError: org/apache/commons/lang3/Validate");
            }else if (getBukkitVersion().startsWith("v1_15_2") && !s.equals("4.5.1")) {
            	send("§5Info: §eFor Spigot 1.15.2 you need §6ProtocolLib 4.5.1 §eor above");
                send("§5Download it here: §l§9https://www.spigotmc.org/resources/protocollib.1997/");
                send("§5Otherwise you will receive: §cNo field with type java.util.Map exists in class EnumProtocol.");
            }
            boolean protocollib = isRightProtocollib(s);
            if (protocollib) {
                send("Furniture start load");
                boolean b = isEnable("ProtectionLib", false);
                send("Furniture find ProtectionLib: §e" + Boolean.toString(b));
                this.bmanager = new BlockManager();
                this.craftingInv = new CraftingInv(this);
                this.loadPermissionKit();
                this.autoFileUpdater = getConfig().getBoolean("config.autoFileUpdater");
                autoConverter.modelConverter(getServer().getConsoleSender());
                loadPluginConfig();
                if (getConfig().getBoolean("config.UseMetrics")) new Metrics(this, BSTATS_ID);
                //ModelFileLoading #1
                this.pManager.loadProjectFiles();
                this.sqlManager = new SQLManager(instance);
                this.sqlManager.initialize();
                this.inventoryManager = new InventoryManager();
                autoConverter.databaseConverter(getServer().getConsoleSender());
                new FurnitureEvents(instance, manager);
                getServer().getPluginManager().registerEvents(new onCrafting(), getInstance());
                getServer().getPluginManager().registerEvents(new onBlockDispense(), getInstance());
                getServer().getPluginManager().registerEvents(new onEntityExplode(), getInstance());
                getServer().getPluginManager().registerEvents(new onPlayerChangeWorld(), getInstance());
                getServer().getPluginManager().registerEvents(new onPlayerDeath(), getInstance());
                getServer().getPluginManager().registerEvents(new onPlayerJoin(), getInstance());
                getServer().getPluginManager().registerEvents(new onPlayerQuit(), getInstance());
                getServer().getPluginManager().registerEvents(new onPlayerRespawn(), getInstance());
                getServer().getPluginManager().registerEvents(new onPlayerTeleportEvent(), getInstance());
                getServer().getPluginManager().registerEvents(new ChunkOnLoad(), getInstance());
                getServer().getPluginManager().registerEvents(new onChunkChange(), getInstance());
                
                if(this.autoPurge) {
                	DeSerializer.autoPurge(purgeTime);
                }
                
                send("§2Furniture load finish :)");
                if (getConfig().getBoolean("config.timer.Enable")) {
                    int time = getConfig().getInt("config.timer.time");
                    sqlManager.saveInterval(time);
                }
                
                send("==========================================");
                Bukkit.getOnlinePlayers().stream().filter(p -> p != null && p.isOp())
                        .forEach(p -> getUpdater().sendPlayer(p));
                PluginCommand c = getCommand("furniture");
                c.setExecutor(new command(this));
                c.setTabCompleter(new TabCompleterHandler());
            } else {
                send("Furniture Lib doesn't find the correct ProtocolLib");
                send("Please Install Protocollib §c4.x");
                send("You can it download at: §6§lhttps://www.spigotmc.org/resources/protocollib.1997/");
                send("==========================================");
                getPluginManager().disablePlugin(this);
            }
        }
    }

    private void loadPluginConfig() {
        enableDebug = getConfig().getBoolean("config.debugMode", false);
        this.lmanager = new LanguageManager(instance, getConfig().getString("config.Language"));
        this.useGamemode = !getConfig().getBoolean("config.Creative.RemoveItems");
        this.creativeInteract = getConfig().getBoolean("config.Creative.Interact");
        this.creativePlace = getConfig().getBoolean("config.Creative.Place");
        this.useRegionMemberAccess = getConfig().getBoolean("config.ProtectionLib.RegeionMemberAccess");
        this.canSit = !getConfig().getBoolean("config.DisableSitting");
        this.useParticle = getConfig().getBoolean("config.useParticles");
        this.purgeTime = getConfig().getInt("config.Purge.time");
        this.autoPurge = getConfig().getBoolean("config.Purge.autoPurge");
        this.purgeTimeMS = TimeUnit.DAYS.toMillis(purgeTime);
        this.removePurge = getConfig().getBoolean("config.Purge.removePurge");
        this.viewDistance = (Bukkit.getViewDistance());
        this.ignoredWorlds = getConfig().getStringList("config.ignoredWorlds");
        if (getConfig().getInt("config.viewRange") < this.viewDistance) {
            this.viewDistance = getConfig().getInt("config.viewRange", 10);
        }
        
        ObjectID.setRange(this.viewDistance);

        this.glowing = getConfig().getBoolean("config.glowing");
        this.spamBreak = getConfig().getBoolean("config.spamBlock.Break.Enable");
        this.spamPlace = getConfig().getBoolean("config.spamBlock.Place.Enable");
        this.spamBreakTime = getConfig().getLong("config.spamBlock.Break.time");
        this.spamPlaceTime = getConfig().getLong("config.spamBlock.Place.time");
        this.timePattern = getConfig().getString("config.spamBlock.timeDisplay");
        this.rotateOnSit = getConfig().getBoolean("config.rotateOnSit");
        String limitConfig = getConfig().getString("config.limit.limitConfig", "PLAYER");
        this.limitManager = new LimitationManager(this, LimitationType.valueOf(limitConfig.toUpperCase()));
        this.limitGlobal = getConfig().getInt("config.limit.limitGlobal", -1);
        this.type = EventType.valueOf(getConfig().getString("config.PlaceMode.Access", "INTERACT"));
        this.mode = PublicMode.valueOf(getConfig().getString("config.PlaceMode.Mode", "PRIVATE"));
        this.update = getConfig().getBoolean("config.CheckUpdate");
        this.updater = new Updater();
        this.loadIgnore();

        debug("Config->useGamemode:" + useGamemode);
        debug("Config->creativeInteract:" + creativeInteract);
        debug("Config->creativePlace:" + creativePlace);
        debug("Config->useRegionMemberAccess:" + useRegionMemberAccess);
        debug("Config->canSit:" + canSit);
        debug("Config->useParticle:" + useParticle);
        debug("Config->purgeTime" + purgeTime);
        debug("Config->autoPurge:" + autoPurge);
        debug("Config->removePurge:" + removePurge);
        debug("Config->viewDistance:" + viewDistance);
        debug("Config->glowing:" + glowing);
        debug("Config->spamBreak:" + spamBreak);
        debug("Config->spamPlace:" + spamPlace);
        debug("Config->spamBreakTime:" + spamBreakTime);
        debug("Config->spamPlaceTime:" + spamPlaceTime);
        debug("Config->timePattern:" + timePattern);
        debug("Config->rotateOnSit:" + rotateOnSit);
        debug("Config->limitConfig:" + limitConfig);
        debug("Config->limitGlobal:" + limitGlobal);
        debug("Config->PlaceMode.Access:" + type.name);
        debug("Config->PlaceMode.Mode:" + mode.name);
        debug("Config->update:" + update);

    }

    public void reloadPluginConfig() {
        enableDebug = true;
        this.reloadConfig();
        this.loadPluginConfig();

        FurnitureManager.getInstance().getProjects().forEach(Project::loadDefaults);
    }

    private void loadIgnore() {
        config c = new config(getInstance());
        FileConfiguration configuration = c.getConfig("ignoredPlayers", "");
        if (configuration.isSet("ignoreList")) {
            configuration.getStringList("ignoreList").forEach(letter -> {
                getFurnitureManager().getIgnoreList().add(UUID.fromString(letter));
            });
        }
    }

    public BufferedReader loadStream(String str) {
        if (!str.startsWith("/"))
            str = "/" + str;
        InputStream stream = getInstance().getClass().getResourceAsStream(str);
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
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (!player.hasPlayedBefore())
            return false;
        long time = player.getLastPlayed();
        if (!isAfterDate(time, purgeTime))
            return false;
        if (removePurge) {
            getFurnitureManager().remove(obj);
            return false;
        }
        obj.setSQLAction(SQLAction.REMOVE);
        return true;
    }

    public void registerPluginFurnitures(Plugin plugin) {
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
            getServer().getPluginManager().disablePlugin(this);
        return b;
    }

    @Override
    public void onDisable() {
        getLogger().info("==========================================");
        getLogger().info("Furniture shutdown started");
        if(Objects.nonNull(sqlManager)) {
        	if (!getConfig().getBoolean("config.timer.Enable")) {
                this.sqlManager = new SQLManager(this);
                this.sqlManager.initialize();
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
        Class<?> c = pro.getFunctionClazz();
        if (c == null) {
            return null;
        }
        ObjectID objectID = new ObjectID(pro.getName(), pro.getPlugin().getName(), l);
        spawn(pro, objectID);
        return objectID;
    }
    
    public static boolean useDebugMode() {
    	return enableDebug;
    }

    public void spawn(Project pro, ObjectID obj) {
        if (pro == null)
            return;
        if (pro.getClass() == null)
            return;
        if (obj == null)
            return;
        Class<?> c = pro.getFunctionClazz();
        if (Objects.isNull(c)) {
            return;
        }
        try {
            obj.getProjectOBJ().getModelschematic().spawn(obj);
            Object o = c.getConstructor(ObjectID.class).newInstance(obj);
            if (obj.getFunctionObject() == null) {
                obj.setFunctionObject(o);
                //Method m = o.getClass().getMethod("spawn", Location.class);
            }
            obj.setFinish();
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getIgnoredWorldList(){
    	return this.ignoredWorlds;
    }
    
    public boolean isWorldIgnored(String worldName) {
    	return this.ignoredWorlds.contains(worldName.toLowerCase());
    }
}