package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.util.HashMap;

import org.bukkit.Location;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public class ChunkLimitation extends LimitationType{

	private HashMap<String, Integer> amountMap = new HashMap<String, Integer>();
	
	@Override
	public void init() {
		
	}
	
	@Override
	public int getAmount(Location location, Project project) {
		return (int) FurnitureManager.getInstance().getInChunk(location.getChunk()).stream().filter(entry -> entry.getProject().equalsIgnoreCase(project.getName())).count();
	}

	@Override
	public int getMaxCount(Project project) {
		return amountMap.getOrDefault(project.getName(), -1);
	}

	@Override
	public boolean canPlace(Location location, Project project) {
		return getAmount(location, project) < getMaxCount(project);
	}
	
}