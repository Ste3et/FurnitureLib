package de.Ste3et_C0st.FurnitureLib.async;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.Chunk;
import org.bukkit.World;

public class WorldData {
	
	private final String string;
	private final HashMap<Long, ChunkData> chunkHashMap = new HashMap<Long, ChunkData>();
	
	public WorldData(String string) {
		this.string = string;
	}
	
	public void addPoint(int chunkX, int chunkZ) {
		this.chunkHashMap.putIfAbsent(toChunkID(chunkX, chunkZ), new ChunkData(chunkX, chunkZ, string));
	}
	
	public boolean contains(int chunkX, int chunkZ) {
		return chunkHashMap.containsKey(toChunkID(chunkX, chunkZ));
	}
	
	public void remove(int chunkX, int chunkZ) {
		if(this.contains(chunkX, chunkZ)) this.chunkHashMap.remove(toChunkID(chunkX, chunkZ));
	}
	
	public String getWorldName() {
		return string;
	}
	
	public Optional<ChunkData> getChunk(int chunkX, int chunkZ){
		return Optional.ofNullable(this.chunkHashMap.getOrDefault(toChunkID(chunkX, chunkZ), null));
	}
	
	private long toChunkID(int chunkX, int chunkZ) {
		return chunkZ ^ (chunkX << 32);
	}
	
	public void loadData(World world, Chunk ... chunk) {
		Stream.of(chunk).forEach(entry -> {
			getChunk(entry.getX(), entry.getZ()).ifPresent(chunkData -> {
				if(chunkData.isLoaded() == false) {
					chunkData.load(null);
				}
			});
		});;
	}
	
}
