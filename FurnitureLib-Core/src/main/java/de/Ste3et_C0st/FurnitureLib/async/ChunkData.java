package de.Ste3et_C0st.FurnitureLib.async;

import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public class ChunkData {

	private final int chunkX, chunkZ;
    private String world;
    private boolean loaded = false;
    private final ReentrantLock lock = new ReentrantLock();
    
    public ChunkData(final Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }
    
    public ChunkData(final Location location) {
    	this(location.getBlockX() >> 4, location.getBlockZ() >> 4, location.getWorld().getName());
    }

    public ChunkData(final int x,final int z,final String world) {
        this.chunkX = x;
        this.chunkZ = z;
        this.world = world;
    }

    public ChunkData load(World world) {
        if (!loaded && this.lock.isLocked() == false) {
        	FurnitureLib.getInstance().getSQLManager().loadAsynchron(this, world).thenAccept(idList -> {
        		if (!idList.isEmpty()) {
        			idList.forEach(obj -> {
                        try {
                            obj.setFinish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        obj.setFinish();
                        obj.sendAll();
                    });
        			this.loaded = true;
        			//	Spawn :	/tp -168.05 80.62 -261.81
        			//  Placed: /tp -1680.05 80.62 -2610.81
                    FurnitureManager.getInstance().addObjectID(idList);
        			this.lock.unlock();
                }else {
                	this.lock.unlock();
                }
        	});
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
        return this.chunkX;
    }

    public int getZ() {
        return this.chunkZ;
    }

    public String getWorld() {
        return this.world;
    }

    public boolean equals(Chunk c) {
        return c.getX() == getX() && c.getZ() == getZ() && world == c.getWorld().getName();
    }
}
