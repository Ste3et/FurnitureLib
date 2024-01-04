package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;

public abstract class PermissionKitLimit extends Limitation{

	private static final String KEY = "Permission";
	private static final String headString = KEY + "Kit";
	private static final String PERMISSION = "furniture.limit.";
	
	private HashMap<String, HashMap<Project, Integer>> kitMap = new HashMap<>();
	
	private Supplier<String> permissionConsumer = () -> PERMISSION + getEnum().name().toLowerCase() + ".<kitName>";
	
	public PermissionKitLimit(LimitationType type) {
		super(type);
	}
	
	public YamlConfiguration loadYaml(List<String> headerList) {
		if(headerList.isEmpty() == false) {
			List<String> headerConfig = new ArrayList<>();
			headerConfig.addAll(headerList);
			headerConfig.addAll(Arrays.asList(
					"----------------------",
					"These Limitation type support permission Kits!",
					"PermissionKit:",
					"	<kitName>:",
					"		chair: -1",
					"---------------------",
					"the permission for these kit is " + permissionConsumer.get()
			));
			headerList = headerConfig;
		}
		return super.loadYaml(headerList);
	}
	
	@Override
	protected void ioProjectLimit(String headerString, Project project, YamlConfiguration configuration) {
		if(configuration.isConfigurationSection(headString)) {
			configuration.getConfigurationSection(headString).getKeys(false).forEach(key -> {
				final String kit = headString + "." + key;
				if(configuration.contains(kit + "." + project.getName())) {
					final int amount = configuration.getInt(kit + "." + project.getName(), -1);
					final HashMap<Project, Integer> kitMap = this.kitMap.getOrDefault(kit, new HashMap<>());
					kitMap.put(project, amount);
					this.kitMap.put(key, kitMap);
				}
			});
		}
		
		super.ioProjectLimit(headerString, project, configuration);
	}
	
	public OptionalInt getKitLimit(Project project, Location location, Player player) {
		final List<String> permissionMap = kitMap.keySet().stream().filter(entry -> player.hasPermission(permissionConsumer.get().replace("<kitName>", entry.toLowerCase()))).collect(Collectors.toList());
		return permissionMap.stream().filter(kitMap::containsKey).map(kitMap::get).mapToInt(entry -> entry.get(project)).max();
	}
}
