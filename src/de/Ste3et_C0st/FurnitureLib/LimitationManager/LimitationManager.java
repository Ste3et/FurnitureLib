package de.Ste3et_C0st.FurnitureLib.LimitationManager;

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
	
	public void removePlayer(ObjectID obj){
		@SuppressWarnings("unchecked")
		HashMap<UUID, List<ObjectID>> objList = (HashMap<UUID, List<ObjectID>>) playerList.clone();
		List<ObjectID> objL = new ArrayList<ObjectID>();
		UUID uui = null;
		for(UUID uuid : objList.keySet()){
			if(objList.get(uuid).contains(obj)){
				objL = playerList.get(uuid);
				objL.remove(obj);
				uui = uuid;
				break;
			}
		}
		if(uui!=null&&objL!=null){
			this.playerList.put(uui, objL);
		}
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
		
		if(p.isOp() || FurnitureLib.getInstance().hasPerm(p,"furniture.admin") || FurnitureLib.getInstance().hasPerm(p,"furniture.bypass.limit")){return true;}
		if(world>=maxWorld){
			if(maxWorld!=-1){return false;}
		}
		
		if(chunk>=maxChunk){
			if(maxChunk!=-1){return false;}
		}
		
		if(player>=maxPlayer){if(maxPlayer!=-1){if(player<perAmount){return true;}p.sendMessage("ERROR0");return false;}
		
		if(player>=perAmount){
			if(perAmount!=-1){p.sendMessage("ERROR1:" + perAmount);return false;}
		}
		}
		return true;
	}
	
	public void sendAuncer(Player p, ObjectID obj){
		Project pro = obj.getProjectOBJ();
		int world = returnProjectWorld(obj.getWorld(), pro);
		int chunk = returnIntProjectChunk(obj.getChunk(), pro);
		int player = returnIntProject(p, pro);
		
		int perAmount = pro.hasPermissionsAmount(p);
		int maxWorld = pro.getAmountWorld(obj.getWorld());
		int maxChunk = pro.getAmountChunk();
		int maxPlayer = pro.getAmountPlayer();
		
		int max = -1;
		int current = -1;
		
		if(maxWorld!=0&&maxWorld!=-1){
			max=maxWorld;
			current=world;
		}
		if((maxChunk!=0&&maxChunk!=-1)&&(maxChunk>max)){
			max=maxChunk;
			current=chunk;
		}
		if((maxPlayer!=0&&maxPlayer!=-1)&&(maxPlayer>max)){
			max=maxPlayer;
			current=player;
		}
		if((perAmount!=0&&perAmount!=-1)&&(perAmount>max)){
			max=perAmount;
			current=player;
		}
		
		if(max!=-1&&current!=-1){
			String s = lib.getLangManager().getString("LimitAouncer");
			s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", current+1+"").replace("#MAX#", max+"");
			p.sendMessage(s);
		}
	}
}
