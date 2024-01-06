package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;

public abstract class PermissionKitLimit extends Limitation{

	private static final String KEY = "Permission";
	private static final String headString = KEY + "Kit";
	private static final String PERMISSION = "furniture.limit.";
	
	private HashMap<String, HashMap<Project, OptionalInt>> kitMap = new HashMap<>();
	protected HashMap<String, HashMap<String, OptionalInt>> worldMap = new HashMap<>();
	
	private Supplier<String> permissionConsumer = () -> PERMISSION + getEnum().name().toLowerCase() + ".<kitName>";
	
	public PermissionKitLimit(LimitationType type) {
		super(type);
	}
	
	public YamlConfiguration loadYaml(List<String> headerList) {
		if(headerList.isEmpty() == false) {
			List<String> headerConfig = new ArrayList<>();
			headerConfig.addAll(headerList);
			headerConfig.addAll(Arrays.asList(
					"----------------------------------------------",
					"These Limitation type support permission Kits!",
					"PermissionKit:",
					"	<kitName>:",
					(getEnum() == LimitationType.WORLD ? "		world: -1" : "		chair: -1"),
					"		total:",
					"			enable: false",
					"			amount: -1",
					"----------------------------------------------",
					"the permission for these kit is " + permissionConsumer.get()
			));
			headerList = headerConfig;
		}
		return super.loadYaml(headerList);
	}
	
	@Override
	protected void ioProjectLimit(String headerString, Project project, YamlConfiguration configuration) {
		if(configuration.isConfigurationSection(headString)) {
			configuration.getConfigurationSection(headString).getKeys(false).stream().forEach(kitName -> {
				final String kit = headString + "." + kitName;
				if(LimitationType.WORLD != getEnum()) {
					final HashMap<Project, OptionalInt> kitMap = this.kitMap.getOrDefault(kitName, new HashMap<>());
					OptionalInt totalInt = loadTotal(configuration, kit);
					kitMap.put(project, totalInt.isPresent() ? totalInt : OptionalInt.of(configuration.getInt(kit + "." + project.getName(), -2)));
					this.kitMap.put(kitName, kitMap);
				}else {
					final HashMap<String, OptionalInt> worldKitMap = this.worldMap.getOrDefault(kitName, new HashMap<>());
					OptionalInt worldOptionalInt = loadTotal(configuration, kit);
					if(worldOptionalInt.isPresent() == true) {
						Bukkit.getWorlds().stream().forEach(world -> {
							worldKitMap.put(world.getName(), OptionalInt.of(worldOptionalInt.getAsInt()));
						});
					}else {
						Bukkit.getWorlds().stream().forEach(world -> {
							int limitWorld = configuration.getInt(kit + "." + world.getName(), -2);
							worldKitMap.put(world.getName(), limitWorld > -2 ? OptionalInt.of(limitWorld) : OptionalInt.empty());
						});
					}
					this.worldMap.put(kitName, worldKitMap);
				}
			});
		}
		
		super.ioProjectLimit(headerString, project, configuration);
	}
	
	private OptionalInt loadTotal(YamlConfiguration configuration, String key) {
		if(configuration.getBoolean(key + ".total.enable", false)) {
			return OptionalInt.of(configuration.getInt(key + ".total.amount", -1));
		}
		return OptionalInt.empty();
	}
	
	public OptionalInt getKitLimit(Project project, Location location, Player player) {
		final AtomicInteger returnValue = new AtomicInteger(-2);
		getKitPermissionSet(player, kitMap.keySet()).stream().forEach(kitName -> {
			HashMap<Project, OptionalInt> projectMap = kitMap.getOrDefault(kitName, new HashMap<>());
			final OptionalInt limit = projectMap.getOrDefault(project, OptionalInt.empty());
			if(limit.isPresent() && returnValue.get() < limit.getAsInt()) returnValue.set(limit.getAsInt());
		});
		return returnValue.get() > -2 ? OptionalInt.of(returnValue.get()) : OptionalInt.empty();
	}
	
	public OptionalInt getKitLimitWorld(Project project, Location location, Player player) {
		final String worldName = location.getWorld().getName();
		final AtomicInteger returnValue = new AtomicInteger(-2);
		getKitPermissionSet(player, worldMap.keySet()).stream().forEach(kitName -> {
			HashMap<String, OptionalInt> projectMap = worldMap.getOrDefault(kitName, new HashMap<>());
			final OptionalInt limit = projectMap.getOrDefault(worldName, OptionalInt.empty());
			if(limit.isPresent() && returnValue.get() < limit.getAsInt()) returnValue.set(limit.getAsInt());
		});
		return returnValue.get() > -2 ? OptionalInt.of(returnValue.get()) : OptionalInt.empty();
	}
	
	private List<String> getKitPermissionSet(Player player, Set<String> stringSet) {
		return stringSet.stream().filter(entry -> player.hasPermission(permissionConsumer.get().replace("<kitName>", entry.toLowerCase()))).collect(Collectors.toList());
	}
	
	public boolean isPresent(OptionalInt optionalInt) {
		return optionalInt.isPresent() && optionalInt.getAsInt() > -2;
	}
}
