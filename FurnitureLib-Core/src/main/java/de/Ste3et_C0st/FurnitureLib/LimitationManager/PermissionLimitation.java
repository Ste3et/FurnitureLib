package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;

public class PermissionLimitation extends Limitation{
	
	private static final String PERMISSION = "furniture.globallimit.";
	
	public PermissionLimitation() {
		super(LimitationType.PERMISSION);
	}
	
	@Override
	public int getAmount(Predicate<ObjectID> projectAmount) {
		return (int) FurnitureManager.getInstance().getAllExistObjectIDs().filter(projectAmount).count();
	}

	@Override
	public boolean canPlace(Location location, Project project, Player player) {
		return getAmount(buildFilter(location, project, player)) < getLimit(project, location, player);
	}

	@Override
	public int getLimit(Project project, Location location, Player player) {
		for (int i = FurnitureConfig.getFurnitureConfig().getLimitGlobal(); i > 0; i--) {
            if (player.hasPermission(PERMISSION + i)) {
                return i;
            }
        }
		return 0;
	}

	@Override
	public void writeConfig() {}

	@Override
	public void updateConfig(Project project) {}

	@Override
	public Predicate<ObjectID> buildFilter(Location location, Project project, Player player) {
		return objectID -> objectID.hasProjectOBJ() && objectID.getUUID().equals(player.getUniqueId());
	}
}
