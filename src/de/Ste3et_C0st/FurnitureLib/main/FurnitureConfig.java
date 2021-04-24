package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.Ste3et_C0st.FurnitureLib.LimitationManager.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.Listener.render.RenderEventHandler;
import de.Ste3et_C0st.FurnitureLib.Listener.render.RenderWithBukkit;
import de.Ste3et_C0st.FurnitureLib.Listener.render.RenderWithProtocols;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Metrics;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;

public class FurnitureConfig {

	private LanguageManager lmanager;
	
    private boolean useGamemode = true, canSit = true, update = true, useParticle = true, useRegionMemberAccess = false,
            autoPurge = false, removePurge = false, creativeInteract = true, creativePlace = true, glowing = true,
            spamBreak = true, spamPlace = true, rotateOnSit = true, useSSL = false, sync = true, packetRenderMethode = false;
    
    private int purgeTime = 30, viewDistance = 100, limitGlobal = -1;
    private long purgeTimeMS = 0, spamBreakTime = 5000, spamPlaceTime = 5000;
    private List<String> ignoredWorlds = new ArrayList<String>();
    private final FurnitureLib instance;
    private String timePattern = "mm:ss:SSS";
    private PublicMode mode;
    private EventType type;
    private LimitationManager limitManager;
    private static FurnitureConfig furnitureConfig = null;
    private RenderEventHandler renderEventHandler;
    private static final int BSTATS_ID = 454;
    
    public FurnitureConfig(FurnitureLib instance) {
    	this.instance = instance;
    	furnitureConfig = this;
    	getConfig().addDefaults(YamlConfiguration.loadConfiguration(instance.loadStream("config.yml")));
		getConfig().options().copyDefaults(true);
		getConfig().options().copyHeader(true);
		instance.saveConfig();
		this.initStaticConfigs();
    }
    
    private void initStaticConfigs() {
    	this.useSSL = getConfig().getBoolean("config.Database.useSSL");
		this.sync = getConfig().getBoolean("config.sync", true);
		if (getConfig().getBoolean("config.UseMetrics"))
			new Metrics(instance, BSTATS_ID);
    }
    
    public void loadPluginConfig() {
        FurnitureLib.setDebug(getConfig().getBoolean("config.debugMode", false));
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
        this.packetRenderMethode = getConfig().getBoolean("config.packetRenderMethode");
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
        this.limitManager = new LimitationManager(instance, LimitationType.valueOf(limitConfig.toUpperCase()));
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
        debug("Config->PlaceMode.Access:" + type.name);
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
    	return this.ignoredWorlds;
    }
    
    public boolean isWorldIgnored(String worldName) {
    	return this.ignoredWorlds.contains(worldName.toLowerCase());
    }
    
    public Boolean useGamemode() {
        return this.useGamemode;
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
        config c = new config(instance);
        FileConfiguration configuration = c.getConfig("ignoredPlayers", "");
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
    
}
