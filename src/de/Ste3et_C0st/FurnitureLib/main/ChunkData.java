package de.Ste3et_C0st.FurnitureLib.main;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import de.Ste3et_C0st.FurnitureLib.Utilitis.CallbackBoolean;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DoubleKey;

public class ChunkData{

	private DoubleKey<Integer> points;
	private String world;
	private boolean loadet = false;

	public ChunkData(Chunk c) {
		points = new DoubleKey<Integer>(c.getX(), c.getZ());
		this.world = c.getWorld().getName();
	}

	public ChunkData(int x, int z, String world) {
		points = new DoubleKey<Integer>(x, z);
		this.world = world;
	}

	public ChunkData load() {
		FurnitureLib.getInstance().getSQLManager().loadAsynchron(this, new CallbackBoolean() {
			@Override
			public void onResult(HashSet<ObjectID> idList) {
				if(!idList.isEmpty()) {
					Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
						idList.forEach(obj -> {
							try {
								obj.setFunctionObject(obj.getProjectOBJ().getclass().getDeclaredConstructor(ObjectID.class).newInstance(obj));
							} catch (Exception e) {
								e.printStackTrace();
							}finally {
								obj.setFinish();
							}
						});
					});
				}
			}
		});
		this.loadet = true;
		return this;
	}

	public boolean isLoadet() {
		return this.loadet;
	}
	
	public int getX() {
		return points.getKey1();
	}
	
	public int getZ() {
		return points.getKey2();
	}
	
	public String getWorld() {
		return this.world;
	}
	
	public boolean equals(Chunk c) {
		return c.getX() == getX() && c.getZ() == getZ() && world == c.getWorld().getName();
	}
}
