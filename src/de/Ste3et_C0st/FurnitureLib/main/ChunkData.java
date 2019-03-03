package de.Ste3et_C0st.FurnitureLib.main;

import java.util.HashSet;

import org.bukkit.Chunk;

import de.Ste3et_C0st.FurnitureLib.Utilitis.CallbackBoolean;

public class ChunkData{

	private int x, z;
	private String world;
	private HashSet<ObjectID> objectSet = new HashSet<ObjectID>();
	private boolean loadet = false;

	public ChunkData(Chunk c) {
		this.x = c.getX();
		this.z = c.getZ();
		this.world = c.getWorld().getName();
	}

	public ChunkData(int x, int z, String world) {
		this.x = x;
		this.z = z;
		this.world = world;
	}

	public ChunkData load() {
		FurnitureLib.getInstance().getSQLManager().loadAsynchron(getX(), getZ(), getWorld(), new CallbackBoolean() {
			@Override
			public void onResult(HashSet<ObjectID> idList) {
				if(!idList.isEmpty()) {
					objectSet = idList;
					System.out.println(idList.size());
					System.out.println("TPS: " + FurnitureLib.getInstance().getSQLManager().getTPS());
				}
			}
		});
		this.loadet = true;
		return this;
	}

	public boolean isLoadet() {
		return this.loadet;
	}
	
	public HashSet<ObjectID> getHashSet(){
		return this.objectSet;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getZ() {
		return this.z;
	}
	
	public String getWorld() {
		return this.world;
	}
	
	public boolean equals(Chunk c) {
		return c.getX() == x && c.getZ() == z && world == c.getWorld().getName();
	}
}
