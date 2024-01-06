package de.Ste3et_C0st.FurnitureLib.main;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.zaxxer.hikari.HikariConfig;

import de.Ste3et_C0st.FurnitureLib.LimitationManager.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.Listener.render.RenderEventHandler;
import de.Ste3et_C0st.FurnitureLib.Listener.render.RenderWithBukkit;
import de.Ste3et_C0st.FurnitureLib.Listener.render.RenderWithProtocols;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Metrics;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.StorageType;

public class FurnitureConfig {

	private LanguageManager lmanager;
	
    private boolean useGamemode = true, canSit = true, update = true, useParticle = true, useRegionMemberAccess = false,
            autoPurge = false, removePurge = false, creativeInteract = true, creativePlace = true, glowing = true,
            spamBreak = true, spamPlace = true, rotateOnSit = true, useSSL = false, sync = true, packetRenderMethode = false,
            hideBedrockPlayers = true, whitelist = false, autoFileUpdater = false, importCheck = false, autoSaveConsoleMessage = true;
    
    private StorageType storageType = StorageType.LEGACY;
    private int purgeTime = 30, viewDistance = 100, limitGlobal = -1, saveIntervall;
    private long purgeTimeMS = 0, spamBreakTime = 5000, spamPlaceTime = 5000;
    private List<String> worlds = new ArrayList<String>();
    private final FurnitureLib instance;
    private String timePattern = "mm:ss:SSS", databaseTableString = "FurnitureLib_Objects";
    private PublicMode mode;
    private EventType type;
    private LimitationManager limitManager;
    private static FurnitureConfig furnitureConfig = null;
    private RenderEventHandler renderEventHandler;
    private static final int BSTATS_ID = 454;
    private DataBaseType databaseType = DataBaseType.SQLite;
    private BiConsumer<CommandSender,String> databaseOutput = (sender, message) -> sender.sendMessage(message);
    
    public FurnitureConfig(FurnitureLib instance) {
    	this.instance = instance;
    	furnitureConfig = this;
    	FurnitureLib.getInstance().saveDefaultConfig();
		this.initStaticConfigs();
    }
    
    private void initStaticConfigs() {
    	if(isNewVersion()) {
    		//storage
    		this.updateConfig();
    		final String key = "storage-options.";
    		this.storageType = StorageType.valueOf(getConfig().getString(key + "storage-methode", "LEGACY").toUpperCase());
    		this.autoFileUpdater = getConfig().getBoolean(key + "auto_convert", false);
    		this.databaseTableString = getConfig().getString(key + "database-table-old", "FurnitureLib_Objects");
    		this.importCheck = getConfig().getBoolean(key + "importCheck", false);
    		this.saveIntervall = getConfig().getInt("storage-options.auto-save-interval", 300);
    		this.sync = getConfig().getBoolean("sync", true);
    	}else {
    		this.useSSL = getConfig().getBoolean("config.Database.useSSL");
    		this.sync = getConfig().getBoolean("config.sync", true);
    		this.databaseTableString = getConfig().getString("config.fileConverter.database_table", "FurnitureLib_Objects");
    		this.autoFileUpdater = getConfig().getBoolean("config.fileConverter.auto_mode", false);
    		this.importCheck = getConfig().getBoolean("config.Database.importCheck", false);
    		this.saveIntervall = getConfig().getInt("config.timer.time", 300);
    		if (getConfig().getBoolean("config.UseMetrics"))
    			new Metrics(instance, BSTATS_ID);
    	}
    }
    
    public HikariConfig loadDatabaseAsset() {
    	final HikariConfig config = new HikariConfig();
    	config.getDataSourceProperties().put("dataSource.logWriter", new PrintWriter(System.out));
    	if(isNewVersion()) {
    		final String key = "storage-options.";
    		if (getConfig().getString(key + "storage-type").equalsIgnoreCase("SQLite")) {
                String database = getConfig().getString(key + "database");
                config.setJdbcUrl("jdbc:sqlite:plugins/FurnitureLib/" + database + ".db");
                config.setDriverClassName("org.sqlite.JDBC");
                config.setPoolName("FurnitureLib");
                config.setConnectionTestQuery("SELECT 1");
                config.setMaximumPoolSize(10);
                
                this.databaseType = DataBaseType.SQLite;
            } else if (getConfig().getString(key + "storage-type").equalsIgnoreCase("Mysql")) {
            	String database = getConfig().getString(key + "database");
                String user = getConfig().getString(key + "user");
                String password = getConfig().getString(key + "password");
                String port = getConfig().getString(key + "port", "3306");
                String host = getConfig().getString(key + "adress");
                boolean allowPublicKeyRetrieval = getConfig().getBoolean(key + "connection-properties.allowPublicKeyRetrieval", false);
                boolean useSSL = getConfig().getBoolean(key + "connection-properties.useSSL", true);
                config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL + "&allowPublicKeyRetrieval=" + allowPublicKeyRetrieval);
                config.setUsername(user);
                config.setPassword(password);
                config.setPoolName("FurnitureLib");
                config.setMaximumPoolSize(getConfig().getInt(key + "database-pool-settings.maximum-pool-size", 10));
                config.setIdleTimeout(getConfig().getInt(key + "database-pool-settings.idleTimeout", 10));
                config.setMinimumIdle(getConfig().getInt(key + "database-pool-settings.minimum-idle", 10));
                config.setMaxLifetime(getConfig().getInt(key + "database-pool-settings.maximum-lifetime", 10));
                config.setConnectionTimeout(getConfig().getInt(key + "database-pool-settings.connection-timeout", 10));
                this.databaseType = DataBaseType.MySQL;
            }
    	}else {
    		if (getConfig().getString("config.Database.type").equalsIgnoreCase("SQLite")) {
                String database = getConfig().getString("config.Database.database");
                config.setJdbcUrl("jdbc:sqlite:plugins/FurnitureLib/" + database + ".db");
                config.setDriverClassName("org.sqlite.JDBC");
                config.setPoolName("FurnitureLib");
                config.setConnectionTestQuery("SELECT 1");
                config.setMaximumPoolSize(10);
                config.setIdleTimeout(15);
                this.databaseType = DataBaseType.SQLite;
            } else if (getConfig().getString("config.Database.type").equalsIgnoreCase("Mysql")) {
                String database = getConfig().getString("config.Database.database");
                String user = getConfig().getString("config.Database.user");
                String password = getConfig().getString("config.Database.password");
                String port = getConfig().getString("config.Database.port", "3306");
                String host = getConfig().getString("config.Database.host");
                boolean allowPublicKeyRetrieval = getConfig().getBoolean("config.Database.allowPublicKeyRetrieval", false);
                boolean useSSL = getConfig().getBoolean("config.Database.useSSL", true);
                config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL + "&allowPublicKeyRetrieval=" + allowPublicKeyRetrieval);
                config.setUsername(user);
                config.setPassword(password);
                config.setPoolName("FurnitureLib");
                config.setMaximumPoolSize(10);
                config.setIdleTimeout(15);
                config.setMinimumIdle(10);
                config.setMaxLifetime(1800000);
                config.setConnectionTimeout(5000);
                this.databaseType = DataBaseType.MySQL;
            }
    	}
    	return config;
    }
    
    public void loadPluginConfig() {
        if(isNewVersion()) {
        	this.loadNewConfig();
        }else {
        	this.loadLegacyConfig();
        }
    }
    
    private boolean isNewVersion() {
    	if(getConfig().contains("config-version") == false) {
    		return false;
    	}
    	return getConfig().getInt("config-version", 1) > 1;
    }
    
    public void initLanguage() {
    	if(isNewVersion()) {
    		this.lmanager = new LanguageManager(instance, getConfig().getString("general-options.language"));
    	}else {
    		this.lmanager = new LanguageManager(instance, getConfig().getString("config.Language"));
    	}
    }
    
    private void loadNewConfig() {
    	//general Config
    	FurnitureLib.setDebug(getConfig().getBoolean("general-options.debugMode", false));
        this.canSit = !getConfig().getBoolean("general-options.DisableSitting");
        this.useParticle = getConfig().getBoolean("general-options.useParticles");
        this.glowing = getConfig().getBoolean("general-options.glowing");
        this.rotateOnSit = getConfig().getBoolean("general-options.rotateOnSit");
        this.update = getConfig().getBoolean("general-options.checkForUpdate");//
        this.autoSaveConsoleMessage = getConfig().getBoolean("storage-options.auto-save-console-message", true);
        
        if(this.autoSaveConsoleMessage) {
        	databaseOutput = (sender, message) -> sender.sendMessage(message);
        }else {
        	databaseOutput = (sender, message) -> {};
        }
        
        //creative config
        this.useGamemode = !getConfig().getBoolean("creative-options.removeItems");
        this.creativeInteract = getConfig().getBoolean("creative-options.interact-with-models");
        this.creativePlace = getConfig().getBoolean("creative-options.place-models");
        
        //purge config
        this.purgeTime = getConfig().getInt("purge-options.time");
        this.autoPurge = getConfig().getBoolean("purge-options.enable");
        this.purgeTimeMS = TimeUnit.DAYS.toMillis(purgeTime);
        this.removePurge = getConfig().getBoolean("purge-options.removePurge");
        
        //world config
        this.viewDistance = (Bukkit.getViewDistance());
        this.worlds = getConfig().getStringList("world-options.worlds");
        this.packetRenderMethode = getConfig().getBoolean("world-options.packetRenderMethode");
        this.viewDistance = getConfig().getInt("world-options.viewRange", 10);
        this.whitelist = getConfig().getBoolean("world-options.whitelist", false);
        
        ObjectID.setRange(this.viewDistance);
        
        //hooked Plugins
        this.useRegionMemberAccess = getConfig().getBoolean("hook.ProtectionLib.RegeionMemberAccess");
        this.hideBedrockPlayers = getConfig().getBoolean("hook.Floodgate.hideModelsForBedrockClients", true);
        
        this.spamBreak = getConfig().getBoolean("antispam-options.break-delay.enable");
        this.spamPlace = getConfig().getBoolean("antispam-options.place-delay.enable");
        this.spamBreakTime = getConfig().getLong("antispam-options.break-delay.time");
        this.spamPlaceTime = getConfig().getLong("antispam-options.place-delay.time");
        this.timePattern = getConfig().getString("antispam-options.timeDisplay");
        
        //limit config
        String limitConfig = getConfig().getString("limit-options.limitConfig", "PLAYER").toUpperCase();
        List<LimitationType> type = new ArrayList<LimitationType>();
        if(limitConfig.contains(",")) {
        	String[] arrays = limitConfig.split(",");
        	for(String str : arrays) {
        		try {
        			type.add(LimitationType.valueOf(str.toUpperCase()));
        		}catch (Exception e) {
        			e.printStackTrace();
				}
        	}
        }else {
        	type.add(LimitationType.valueOf(limitConfig));
        }
        this.limitManager = new LimitationManager(instance, type.toArray(new LimitationType[type.size()]));
        this.limitGlobal = getConfig().getInt("limit-options.limitGlobal", -1);
        
        //protection config 
        this.type = EventType.valueOf(getConfig().getString("model-protection.action", "INTERACT").toUpperCase());
        this.mode = PublicMode.valueOf(getConfig().getString("model-protection.place-models-in-mode", "PRIVATE").toUpperCase());
        
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
        debug("Config->PlaceMode.Access:" + this.type.name);
        debug("Config->PlaceMode.Mode:" + this.mode.name);
        debug("Config->update:" + update);
        
        this.registerRenderEvents();
    }
    
    private void updateConfig() {
    	try (InputStream stream = FurnitureLib.getInstance().getClass().getResourceAsStream("/config.yml");InputStreamReader reader = new InputStreamReader(stream);) {
    		YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
    		FileConfiguration furnitureConfig = getConfig();
    		if(furnitureConfig.getInt("config-version", 2) != defaults.getInt("config-version", 3)) {
    			boolean change = false;
                for (String defaultKey : defaults.getKeys(true)) {
                    if (!furnitureConfig.contains(defaultKey, true)) {
                    	furnitureConfig.set(defaultKey, defaults.get(defaultKey));
                        change = true;
                    }
                    
                }
                if (change) {
                	furnitureConfig.set("config-version", defaults.getInt("config-version"));
                	FurnitureLib.getInstance().saveConfig();
                }
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    private void loadLegacyConfig() {
    	FurnitureLib.setDebug(getConfig().getBoolean("config.debugMode", false));
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
        this.worlds = getConfig().getStringList("config.ignoredWorlds");
        this.packetRenderMethode = getConfig().getBoolean("config.packetRenderMethode");
        this.viewDistance = getConfig().getInt("config.viewRange", 10);
        this.hideBedrockPlayers = getConfig().getBoolean("config.autoHideBedrockPlayers", true);
        ObjectID.setRange(this.viewDistance);
        this.glowing = getConfig().getBoolean("config.glowing");
        this.spamBreak = getConfig().getBoolean("config.spamBlock.Break.Enable");
        this.spamPlace = getConfig().getBoolean("config.spamBlock.Place.Enable");
        this.spamBreakTime = getConfig().getLong("config.spamBlock.Break.time");
        this.spamPlaceTime = getConfig().getLong("config.spamBlock.Place.time");
        this.timePattern = getConfig().getString("config.spamBlock.timeDisplay");
        this.rotateOnSit = getConfig().getBoolean("config.rotateOnSit");
        String limitConfig = getConfig().getString("limit-options.limitConfig", "PLAYER").toUpperCase();
        List<LimitationType> type = new ArrayList<LimitationType>();
        if(limitConfig.contains(",")) {
        	String[] arrays = limitConfig.split(",");
        	for(String str : arrays) {
        		try {
        			type.add(LimitationType.valueOf(str.toUpperCase()));
        		}catch (Exception e) {
        			e.printStackTrace();
				}
        	}
        }else {
        	type.add(LimitationType.valueOf(limitConfig));
        }
        this.limitManager = new LimitationManager(instance, type.toArray(new LimitationType[type.size()]));
        this.limitGlobal = getConfig().getInt("config.limit.limitGlobal", -1);
        this.type = EventType.valueOf(getConfig().getString("config.PlaceMode.Access", "INTERACT"));
        this.mode = PublicMode.valueOf(getConfig().getString("config.PlaceMode.Mode", "PRIVATE"));
        this.update = getConfig().getBoolean("config.CheckUpdate");
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
        debug("Config->PlaceMode.Access:" + this.type.name);
        debug("Config->PlaceMode.Mode:" + mode.name);
        debug("Config->update:" + update);
        this.registerRenderEvents();
    }
    
    private void registerRenderEvents() {
    	if(Objects.nonNull(this.renderEventHandler)) this.renderEventHandler.remove();
    	this.renderEventHandler = this.packetRenderMethode ? new RenderWithProtocols() : new RenderWithBukkit();
    	this.renderEventHandler.register();
    }
    
    public List<String> getIgnoredWorldList(){
    	return this.worlds;
    }
    
    public boolean isWorldIgnored(String worldName) {
    	boolean contains = this.worlds.contains(worldName.toLowerCase());
    	return this.whitelist ? !contains : contains;
    }
    
    public boolean useGamemode() {
        return this.useGamemode;
    }
    
    public boolean shouldAutoSaveConsoleMessage() {
    	return this.autoSaveConsoleMessage;
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
    
    public boolean useSSL() {
        return this.useSSL;
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
    
    public LanguageManager getLangManager() {
        return this.lmanager;
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
    
    private void debug(String text) {
    	FurnitureLib.debug(text);
    }
    
    public boolean isSync() {
        return this.sync;
    }
    
    private FileConfiguration getConfig() {
    	return FurnitureLib.getInstance().getConfig();
    }
    
    public String getTimePattern() {
        return this.timePattern;
    }
    
    private void loadIgnore() {
    	final File file = new File(FurnitureLib.getInstance().getDataFolder(), "ignoredPlayers.yml");
    	final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (configuration.isSet("ignoreList")) {
            configuration.getStringList("ignoreList").forEach(letter -> {
            	instance.getFurnitureManager().getIgnoreList().add(UUID.fromString(letter));
            });
        }
    }
    
    public long getPurgeTimeDays() {
    	return this.purgeTimeMS;
    }
    
    public EventType getDefaultEventType() {
        return this.type;
    }

    public PublicMode getDefaultPublicType() {
        return this.mode;
    }
    
    public static FurnitureConfig getFurnitureConfig() {
    	return furnitureConfig;
    }
    
    public LimitationManager getLimitManager() {
        return this.limitManager;
    }
    
    public boolean isRenderPacketMethode() {
    	return this.packetRenderMethode;
    }

	public boolean isHideBedrockPlayers() {
		return hideBedrockPlayers;
	}

	public boolean isAutoFileUpdater() {
		return this.autoFileUpdater;
	}

	public String getDatabaseString() {
		return databaseTableString;
	}

	public DataBaseType getDatabaseType() {
		return databaseType;
	}

	public boolean isImportCheck() {
		return importCheck;
	}
	
	public void setFileUpdater(boolean bool) {
		this.autoFileUpdater = bool;
		if(isNewVersion()) {
			getConfig().set("storage-options.auto_convert", bool);
		}else {
			getConfig().set("config.fileConverter.auto_mode", bool);
		}
	}

	public int getSaveIntervall() {
		return this.saveIntervall;
	}
	
	public void sendDatabaseLog(CommandSender sender, String string){
		this.sendDatabaseLog(null, string, false);
	}
	
	public void sendDatabaseLog(CommandSender sender, String string, boolean force){
		if(force) {
			sender.sendMessage(string);
		}else {
			this.databaseOutput.accept(sender, string);
		}
	}
    
}
