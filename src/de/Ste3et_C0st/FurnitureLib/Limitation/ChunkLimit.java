package de.Ste3et_C0st.FurnitureLib.Limitation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ChunkLimit {

	boolean enbale=true;
	config c;
	FileConfiguration file;
	Plugin plugin;
	List<LimitationObject> limitList = new ArrayList<LimitationObject>();
	
	public ChunkLimit(Plugin plugin){
		this.plugin = plugin;
	}
	
	public LimitationObject getLimitObject(World w, Integer x, Integer z){
		for(LimitationObject limitO : limitList){
			if(limitO.getWorld().getName().equals(w.getName())){
				if(limitO.getX().equals(x) && limitO.getZ().equals(z) ){
					return limitO;
				}
			}
		}
		return insertHash(w, x, z);
	}
	

	public void add(Location loc, Project pro){
		if(!enbale) return;
		Chunk chunk = loc.getChunk();
		if(chunk==null){return;}
		World w = loc.getWorld();
		LimitationObject limit = getLimitObject(w, chunk.getX(), chunk.getZ());
		limit.add(pro);
		limitList.add(limit);
	}
	
	public void remove(Location loc, Project pro){
		if(!enbale) return;
		Chunk chunk = loc.getChunk();
		if(chunk==null){return;}
		World w = loc.getWorld();
		LimitationObject limit = getLimitObject(w, chunk.getX(), chunk.getZ());
		limit.add(pro);
		limitList.remove(limit);
	}

	private LimitationObject insertHash(World w,Integer x, Integer z){
		LimitationObject obj = new LimitationObject(x, z, w);
		for(LimitationObject objs : limitList){
			if(obj.getWorld().equals(w) && obj.getX().equals(x) && obj.getZ().equals(z)){
				return objs;
			}
		}
		c = new config(plugin);
		file = c.getConfig(x+","+z, "/limitation/Chunk/" + w.getName() + "/");
		if(file!=null){
			if(!file.isSet("Project")) return obj;
			for(String project : file.getConfigurationSection("Project").getKeys(false)){
				if(FurnitureLib.getInstance().getFurnitureManager().getProject(project)!=null){
					Project proj = FurnitureLib.getInstance().getFurnitureManager().getProject(project);
					obj.set(proj, file.getInt("Project." + project));
				}
			}
			limitList.add(obj);
		}
		return obj;
	}
	
	public boolean canPlace(Location loc, Project pro){
		if(pro==null) return true;
		if(!enbale) return true;
		if(loc.getWorld()==null) return true;
		if(pro.getAmountChunk()==null) return true;
		if(pro.getAmountChunk()==-1){return true;}
		if(pro.getAmountChunk()==0){return false;}
		Chunk c = loc.getChunk();
		LimitationObject obj = insertHash(loc.getWorld(), c.getX(), c.getZ());
		Integer i = obj.getInteger(pro);
		if(i>pro.getAmountPlot()){return false;}
		return true;
	}
	
	@SuppressWarnings("unused")
	private boolean isExist(Project pro){
		return limitList.contains(pro);
	}
	
	@SuppressWarnings("unused")
	private Integer getAmount(World w, Integer x, Integer z, Project pro){
		Integer i = 0;
		if(limitList.isEmpty()) return i;
		return getLimitObject(w, x, z).getInteger(pro);
	}
	
	public void save(){
		if(!enbale) return;
		for(LimitationObject obj : limitList){
			resetWorld(obj);
			c = new config(plugin);
			file = c.getConfig(obj.getX()+","+obj.getZ(), "/limitation/Chunk/" + obj.getWorld().getName() + "/");
			for(Project pro : obj.getHash().keySet()){
				file.set("Project." + pro.getName(), obj.getInteger(pro));
			}
			c.saveConfig(obj.getX()+","+obj.getZ(), file, "/limitation/Chunk/" + obj.getWorld().getName() + "/");
		}
	}
	
	private void resetWorld(LimitationObject obj){
		if(!enbale) return;
		c = new config(plugin);
		file = c.getConfig(obj.getX()+","+obj.getZ(), "/limitation/Chunk/" + obj.getWorld().getName() + "/");
		file.set("Project", null);
		c.saveConfig(obj.getX()+","+obj.getZ(), file, "/limitation/Chunk/" + obj.getWorld().getName() + "/");
	}
}
