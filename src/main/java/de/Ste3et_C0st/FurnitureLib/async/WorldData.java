package de.Ste3et_C0st.FurnitureLib.async;

import java.util.HashSet;
import java.util.Optional;

import de.Ste3et_C0st.FurnitureLib.Utilitis.DoubleKey;

public class WorldData {
	
	private final String string;
	private final HashSet<DoubleKey<Integer>> pointList = new HashSet<DoubleKey<Integer>>(); 
	
	public WorldData(String string) {
		this.string = string;
	}
	
	public void addPoint(int chunkX, int chunkZ) {
		this.pointList.add(DoubleKey.of(chunkX, chunkZ));
	}
	
	public boolean contains(int chunkX, int chunkZ) {
		return pointList.stream().filter(entry -> entry.getKey1().intValue() == chunkX && entry.getKey2().intValue() == chunkZ).findFirst().isPresent();
	}
	
	public void remove(int chunkX, int chunkZ) {
		Optional<DoubleKey<Integer>> optDoubleKey = pointList.stream().filter(entry -> entry.getKey1().intValue() == chunkX && entry.getKey2().intValue() == chunkZ).findFirst();
		if(optDoubleKey.isPresent()) pointList.remove(optDoubleKey.get());
	}
	
	public String getWorldName() {
		return string;
	}
	
}
