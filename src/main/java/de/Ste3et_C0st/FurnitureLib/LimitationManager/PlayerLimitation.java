package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.util.Arrays;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;

public class PlayerLimitation extends Limitation{

	private static final String KEY = "Player";
	private static final String headString = KEY + "Limit";
	
	public PlayerLimitation() {
		super(LimitationType.PLAYER);
		this.writeConfig();
	}
	
	@Override
	public int getAmount(Predicate<ObjectID> projectAmount) {
		return (int) FurnitureManager.getInstance().getAllExistObjectIDs().filter(projectAmount).count();
	}

	@Override
	public boolean canPlace(Location location, Project project, Player player) {
		return getAmount(buildFilter(location, project, player)) < getLimit(project);
	}

	@Override
	public void writeConfig() {
		final YamlConfiguration configuration = super.loadYaml();
		configuration.options().setHeader(Arrays.asList(
			"This is the PlayerLimitation file",
			"You can limit the max amount of Furnitures each Player",
			"total.enable = (bool) | set default value for each project",
			"total.global = (bool) | override the project limit and force use total.amount for each project"
		));
		super.writeGlobal(configuration, headString);
		FurnitureManager.getInstance().getProjects().forEach(project -> super.ioProjectLimit(headString, project, configuration));
		super.save(configuration, getFile());
	}

	@Override
	public void updateConfig(Project project) {
		if(this.amountMap.containsKey(project)) return;
		final YamlConfiguration configuration = super.loadYaml();
		super.ioProjectLimit(headString, project, configuration);
		super.save(configuration, getFile());
	}

	@Override
	public Predicate<ObjectID> buildFilter(Location location, Project project, Player player) {
		return objectID -> objectID.getProjectOBJ().equals(project) && objectID.getUUID().equals(player.getUniqueId());
	}
}
