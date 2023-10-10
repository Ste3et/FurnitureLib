package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;

public abstract class Limitation {
	
	protected HashMap<Project, Integer> amountMap = new HashMap<Project, Integer>();
	protected HashMap<String, Integer> worldMap = new HashMap<String, Integer>();
	
	public abstract int getAmount(Predicate<ObjectID> projectAmount);
	public abstract boolean canPlace(Location location, Project project, Player player);
	public abstract void writeConfig();
	public abstract void updateConfig(Project project);
	public abstract Predicate<ObjectID> buildFilter(Location location, Project project, Player player);
	
	private final LimitationType type;
	private boolean activate = false;
	
	public Limitation(Type.LimitationType type) {
		this.type = type;
	}
	
	public Type.LimitationType getEnum(){
		return this.type;
	};
	
	protected File getFile() {
		final File file = new File(LimitationManager.getLimitationFolder(), getEnum().name().toLowerCase() + ".yml");
		return file;
	}
	
	public YamlConfiguration loadYaml() {
		return this.loadYaml("");
	}
	
	public YamlConfiguration loadYaml(final List<String> headerList) {
		final String headerString = String.join("\n", headerList.toArray(new String[headerList.size()]));
		return this.loadYaml(headerString);
	}
	
	@SuppressWarnings("deprecation")
	public YamlConfiguration loadYaml(String headerString) {
		final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(getFile());
		configuration.options().copyDefaults(true);
		configuration.options().copyHeader(true);
		configuration.options().header(headerString);
		return configuration;
	}
	
	protected void writeGlobal(YamlConfiguration configuration, String headerString) {
		if(configuration.contains(headerString + ".total") == false) {
			configuration.addDefault(headerString + ".total.enable", false);
			configuration.addDefault(headerString + ".total.amount", -1);
			configuration.addDefault(headerString + ".total.global", false);
		}
	}
	
    protected void ioProjectLimit(String headerString, Project project, YamlConfiguration configuration) {
    	if(Objects.isNull(project)) return;
    	if(LimitationType.WORLD != getEnum()) {
    		configuration.addDefault(headerString + ".projects." + project.getName(), getEnum() == Type.LimitationType.PLAYER ? 10 : -1);
    		
        	if(configuration.getBoolean(headerString + ".total.enable", false)) {
    			this.setLimit(project, configuration.getInt(headerString + ".total.amount", -1));
    			FurnitureConfig.getFurnitureConfig().getLimitManager().setGlobal(configuration.getBoolean(headerString + ".total.global", false));
    		}else {
    			this.setLimit(project, configuration.getInt(headerString + ".projects." + project.getName(), getEnum() == Type.LimitationType.PLAYER ? 10 : -1));
    		}
    	}else {
    		Bukkit.getWorlds().stream().forEach(entry -> {
    			configuration.addDefault(headerString + ".worlds." + entry.getName(), -1);
    			if(configuration.getBoolean(headerString + ".total.enable", false)) {
    				this.setWorldLimit(entry.getName(), configuration.getInt(headerString + ".total.amount", -1));
    			}else {
    				this.setWorldLimit(entry.getName(), configuration.getInt(headerString + ".worlds." + entry.getName(), -1));
    			}
    			//System.out.println(headerString + ".worlds." + entry.getName() + "/" + configuration.getInt(headerString + ".worlds." + entry.getName(), -1));
    		});
    		
    		if(configuration.getBoolean(headerString + ".total.enable", false)) {
    			FurnitureConfig.getFurnitureConfig().getLimitManager().setGlobal(configuration.getBoolean(headerString + ".total.global", false));
    		}
    	}
    }
    
    protected void save(YamlConfiguration configuration, File file) {
    	try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public Optional<LimitationInforamtion> buildInforamtion(Player player, Location location, Project project) {
    	return Optional.of(new LimitationInforamtion(getEnum().name().toLowerCase(), getLimit(project, location), getAmount(buildFilter(location, project, player))));
    }
    
	public int getLimit(Project project, Location location) {
		if(getEnum() == LimitationType.WORLD) {
			final String world = location.getWorld().getName();
			return worldMap.getOrDefault(world, -1);
		}
		return amountMap.getOrDefault(project, -1);
	}
	
	protected void setLimit(Project project, int amount) {
		this.amountMap.put(project, amount);
	}
	
	protected void setWorldLimit(final String world, int amount) {
		this.worldMap.put(world, amount);
	}
	
	public boolean isGlobal() {
		return FurnitureConfig.getFurnitureConfig().getLimitGlobal() > 0;
	}
	
	public int getGlobalAmount() {
		return FurnitureConfig.getFurnitureConfig().getLimitGlobal();
	}
	
	public void reload() {
		this.amountMap.clear();
		this.writeConfig();
	}
	
	public boolean isActivate() {
		return activate;
	}
	
	public void setActivate(boolean activate) {
		this.activate = activate;
	}
}
