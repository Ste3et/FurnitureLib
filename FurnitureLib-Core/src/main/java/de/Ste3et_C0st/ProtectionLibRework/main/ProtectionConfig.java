package de.Ste3et_C0st.ProtectionLibRework.main;

import java.util.HashMap;
import java.util.Objects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.ProtectionLib.main.ConfigType;
import de.Ste3et_C0st.ProtectionLib.main.ProtectionLib;
import de.Ste3et_C0st.ProtectionLib.main.protectionObj;

public abstract class ProtectionConfig extends protectionObj {
	
	private HashMap<String, ConfigType<Boolean>> configSet = new HashMap<String, ConfigType<Boolean>>();
	
	public ProtectionConfig(Plugin plugin) {
		super(plugin);
		this.initConfig();
		this.addDefault();
	}
	
	public void addDefault() {
		this.configSet.entrySet().forEach(entry -> {
			this.addDefault(entry.getKey(), entry.getValue().getObject());
		});
	}
	
	public void loadConfig() {
		this.configSet.entrySet().forEach(entry -> {
			String path = "config." + getPlugin().getName() + "." + entry.getKey();
			entry.getValue().setObject(ProtectionLib.getInstance().getConfig().getBoolean(path));
		});
	}
	
	public abstract void initConfig();
	
	public void update() {
		super.update();
		this.loadConfig();
	}
	
	public void addDefault(String path, Boolean object) {
		this.configSet.put(path, new ConfigType<Boolean>(path, object));
		ProtectionLib.getInstance().getConfig().addDefault("config." + getPlugin().getName() + "." + path, object);
	}
	
	public ConfigType<Boolean> getObjectRaw(String string){
		return configSet.getOrDefault(string, null);
	}
	
	public Boolean getObject(String string){
		return Objects.nonNull(getObjectRaw(string)) ? getObjectRaw(string).getObject() : false;
	}
	
}
