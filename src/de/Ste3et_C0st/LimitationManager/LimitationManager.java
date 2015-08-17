package de.Ste3et_C0st.LimitationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class LimitationManager {

	FurnitureLib lib;
	public HashMap<UUID, List<ObjectID>> playerList = new HashMap<UUID, List<ObjectID>>();
	
	public LimitationManager(FurnitureLib lib){
		this.lib = lib;
		for(ObjectID obj : lib.getFurnitureManager().getObjectList()){
			if(obj.getUUID()!=null){
				addPlayer(obj.getUUID(), obj);
			}
		}
	}
	
	public void addPlayer(UUID uuid, ObjectID obj){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		if(playerList.containsKey(uuid)){objList=playerList.get(uuid);}
		objList.add(obj);
		this.playerList.put(uuid, objList);
	}
	
	public void removePlayer(UUID uuid, ObjectID obj){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		if(playerList.containsKey(uuid)){objList = playerList.get(uuid);}else{return;}
		objList.remove(obj);
		this.playerList.put(uuid, objList);
	}
	
	private Integer returnIntProject(Player p, Project pro){
		int i = 0;
		if(playerList.containsKey(p.getUniqueId())){
			for(ObjectID obj : playerList.get(p.getUniqueId())){
				if(obj.getProjectOBJ().equals(pro)){
					i++;
				}
			}
		}
		return i;
	}
	
	private Integer returnIntProjectChunk(Chunk c, Project pro){
		int i = 0;
		for(ObjectID obj : lib.getFurnitureManager().getObjectList()){
			if(obj.getChunk().equals(c)&&obj.getProjectOBJ().equals(pro)){
				i++;
			}
		}
		return i;
	}
	
	private Integer returnProjectWorld(World w, Project pro){
		int i = 0;
			for(ObjectID obj : lib.getFurnitureManager().getObjectList()){
				if(obj.getWorld().equals(w) && obj.getProjectOBJ().equals(pro)){
					i++;
				}
			}
		return i;
	}
	
	public boolean canPlace(Player p, ObjectID obj){
		Project pro = obj.getProjectOBJ();
		Integer world = returnProjectWorld(obj.getWorld(), pro);
		Integer chunk = returnIntProjectChunk(obj.getChunk(), pro);
		Integer player = returnIntProject(p, pro);
		Integer perAmount = pro.hasPermissionsAmount(p);
		
		Integer maxWorld = pro.getAmountWorld(obj.getWorld());
		Integer maxChunk = pro.getAmountChunk();
		Integer maxPlayer = pro.getAmountPlayer();
		if(p.isOp() || p.hasPermission("furniture.admin") || p.hasPermission("furniture.bypass.limit")){return true;}
		if(world>=maxWorld){
			if(maxWorld!=-1){return false;}
		}
		
		if(chunk>=maxChunk){
			if(maxChunk!=-1){return false;}
		}
		
		if(player>=maxPlayer){
			
			if(maxPlayer!=-1){
				if(player<perAmount){return true;}
				return false;
			}
		}
		return true;
	}
}
