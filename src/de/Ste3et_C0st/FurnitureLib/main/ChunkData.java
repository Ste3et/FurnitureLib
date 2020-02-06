package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DoubleKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackObjectIDs;

public class ChunkData{

	private DoubleKey<Integer> points;
	private String world;
	private boolean loaded = false;

	public ChunkData(Chunk c) {
		points = new DoubleKey<Integer>(c.getX(), c.getZ());
		this.world = c.getWorld().getName();
	}

	public ChunkData(int x, int z, String world) {
		points = new DoubleKey<Integer>(x, z);
		this.world = world;
	}

	public ChunkData load() {
		if(!loaded) {
			FurnitureLib.getInstance().getSQLManager().loadAsynchron(this, new CallbackObjectIDs() {
				@Override
				public void onResult(HashSet<ObjectID> idList) {
					if(!idList.isEmpty()) {
						Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
							idList.stream().forEach(obj -> {
								Project pro = obj.getProjectOBJ();
								try {
//									if(Objects.nonNull(pro.getFunctionClass()) && Objects.isNull(obj.getFunctionObject())) {
//										Class<?> c = pro.getFunctionClass();
//										Object o = c.getConstructor(ObjectID.class).newInstance(obj);
//										obj.setFunctionObject(o);
//									}
									obj.setFinish();
								} catch (Exception e) {
									e.printStackTrace();
								}finally {
									obj.setFinish();
									obj.sendAllInView();
								}
							});
							FurnitureManager.getInstance().addObjectID(idList);
						});
					}
				}
			});
			this.loaded = true;
		}
		return this;
	}
	
//	public void spawn(Project pro, ObjectID obj){
//		if(pro==null)return;
//		if(pro.getClass()==null)return;
//		if(obj==null)return;
//		Class<?> c = pro.getclass();
//		if(c==null ){return;}
//		try {
//			Object o = c.getConstructor(ObjectID.class).newInstance(obj);
//			if(obj.getFunctionObject() == null) obj.setFunctionObject(o);
//			obj.setFinish();
//		} catch (InvocationTargetException e) {
//			e.getCause().printStackTrace();
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//	}

	public boolean isLoaded() {
		return this.loaded;
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
