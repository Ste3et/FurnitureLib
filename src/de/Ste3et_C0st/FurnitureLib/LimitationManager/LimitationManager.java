package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class LimitationManager {

	FurnitureLib lib;
	
	
	public HashMap<UUID, List<ObjectID>> playerList = new HashMap<UUID, List<ObjectID>>();
	public List<LimitationObject> objectList = new ArrayList<LimitationObject>();
	public LimitationType type;
	
	public LimitationManager(FurnitureLib lib, LimitationType limitationType){
		this.lib = lib;
		this.type = limitationType;
		for(ObjectID obj : lib.getFurnitureManager().getObjectList()){
			if(obj.getUUID()!=null){
				addPlayer(obj.getUUID(), obj);
			}
		}
		loadDefault();
	}
	
	
	
	public void addPlayer(UUID uuid, ObjectID obj){
		List<ObjectID> objList = playerList.getOrDefault(uuid, new ArrayList<ObjectID>());
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
		if(pro==null) return i;
			if(playerList.containsKey(p.getUniqueId())){				
				for(ObjectID obj : playerList.get(p.getUniqueId())){
					if(obj!=null&&obj.getProjectOBJ()!=null){
						if(obj.getSQLAction()==null) continue;
						if(obj.getSQLAction().equals(SQLAction.REMOVE)) continue;
						if(obj.getProjectOBJ().equals(pro)){
							i++;
					}
				}
			}
		}
		return i;
	}
	
	private Integer returnIntProjectTotal(Player p){
		int i = 0;
		if(playerList.containsKey(p.getUniqueId())){
			for(ObjectID obj : playerList.get(p.getUniqueId())){
				if(obj!=null&&obj.getProjectOBJ()!=null){
					if(obj.getSQLAction()==null) continue;
					if(obj.getSQLAction().equals(SQLAction.REMOVE)) continue;
					i++;
				}
			}
		}
		return i;
	}
	
	private Integer returnIntProjectChunk(Chunk c, Project pro){
		int i = 0;
		if(pro == null) return i;
		for(ObjectID obj : lib.getFurnitureManager().getObjectList()){
			if(obj == null) continue;
			if(obj.getSQLAction()==null) continue;
			if(obj.getSQLAction().equals(SQLAction.REMOVE)) continue;
			if(obj.getProjectOBJ() == null) continue;
			if(obj.getChunk().equals(c)&&obj.getProjectOBJ().equals(pro)){
				i++;
			}
		}
		return i;
	}
	
	private Integer returnProjectWorld(World w, Project pro){
		int i = 0;
		if(w==null) return i;
		if(pro==null) return i;
			for(ObjectID obj : lib.getFurnitureManager().getObjectList()){
				if(obj==null) continue;
				if(obj.getSQLAction()==null) continue;
				if(obj.getSQLAction().equals(SQLAction.REMOVE)) continue;
				if(obj.getWorld()==null) continue;
				if(obj.getProjectOBJ()==null) continue;
				if(obj.getWorld().equals(w) && obj.getProjectOBJ().equals(pro)){
					i++;
				}
			}
		return i;
	}
	
	public boolean canPlace(Player p, ObjectID obj){
		if(p.isOp()) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.admin")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.bypass.limit")) return true;
		
		Project pro = obj.getProjectOBJ();
		LimitationObject limitOBJ = getLimitOBJ(p, pro);
		
		int world = returnProjectWorld(obj.getWorld(), pro);
		int chunk = returnIntProjectChunk(obj.getChunk(), pro);
		int player = returnIntProject(p, pro);
		int playerTotal = returnIntProjectTotal(p);
		
		int maxWorld = pro.getAmountWorld(obj.getWorld());
		int maxChunk = pro.getAmountChunk();
		int maxPlayer = -1;
		
		if(limitOBJ!=null) maxPlayer = limitOBJ.getAmountFromType(pro.getName());
		if(world>=maxWorld){if(maxWorld!=-1){return false;}}
		if(chunk>=maxChunk){if(maxChunk!=-1){return false;}}
		
		if(maxPlayer != -1){
			if(player < maxPlayer){
				if(limitOBJ!=null){
					if(limitOBJ.total){
						if(limitOBJ.totalAmount == -1) return true;
						if(playerTotal < limitOBJ.totalAmount){
							return true;
						}else{
							return false;
						}
					}
				}
				return true;
			}
			return false;
		}
		return true;
	}
	
	public void sendAnouncer(Player p, ObjectID obj){
		if(p.isOp()) return;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.admin")) return;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.bypass.limit")) return;
		
		Project pro = obj.getProjectOBJ();
		LimitationObject limitOBJ = getLimitOBJ(p, pro);
		
		int world = returnProjectWorld(obj.getWorld(), pro);
		int chunk = returnIntProjectChunk(obj.getChunk(), pro);
		int player = returnIntProject(p, pro);
		int playerTotal = returnIntProjectTotal(p);
		
		int maxWorld = pro.getAmountWorld(obj.getWorld());
		int maxChunk = pro.getAmountChunk();
		int maxPlayer = -1;
		
		if(limitOBJ!=null) maxPlayer = limitOBJ.getAmountFromType(pro.getName());
		
		if(world>=maxWorld){
			if(maxWorld!=-1){p.sendMessage(lib.getLangManager().getString("LimitReachedWorld"));return;}
		}
		
		if(chunk>=maxChunk){
			if(maxChunk!=-1){p.sendMessage(lib.getLangManager().getString("LimitReachedChunk"));return;}
		}
		
		if(maxPlayer != -1){
			if(player < maxPlayer){
				if(limitOBJ!=null){
					if(limitOBJ.total){
						if(limitOBJ.totalAmount == -1) return;
						if(playerTotal < limitOBJ.totalAmount){
							String s = lib.getLangManager().getString("LimitAouncerMaximum");
							s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player+1+"").replace("#MAX#", maxPlayer+"").replace("#AMOUNT#", (limitOBJ.totalAmount - (playerTotal + 1)) + "");
							p.sendMessage(s);
							return;
						}else{
							p.sendMessage(lib.getLangManager().getString("LimitReachedMaximum"));
							return;
						}
					}
				}
				String s = lib.getLangManager().getString("LimitAouncer");
				s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player+1+"").replace("#MAX#", maxPlayer+"");
				p.sendMessage(s);
				return;
			}
			p.sendMessage(lib.getLangManager().getString("LimitReached"));
		}
	}
	
	public void loadDefault(){
		if(this.type.equals(LimitationType.PLAYER)){
			config c = new config(lib);
			FileConfiguration file = c.getConfig(this.type.name().toLowerCase(), "/limitation/");
			LimitationObject defaultSection = new LimitationObject(type, "default");
			if(file.isConfigurationSection("PlayerLimit")){
				for(String s : file.getConfigurationSection("PlayerLimit").getKeys(false)){
					if(!s.equalsIgnoreCase("default")){
						LimitationObject limitOBJ = new LimitationObject(type, s);
						if(!objectList.contains(limitOBJ)){
							objectList.add(limitOBJ);
						}
					}
				}
			}
			if(!objectList.contains(defaultSection)){
				objectList.add(defaultSection);
			}
		}
	}

	public void loadDefault(String project) {
		if(this.type.equals(LimitationType.PLAYER)){
			for(LimitationObject obj : objectList){
				obj.addDefault(project);
				obj.loadProjects(project);
			}
		}
	}
	
	public LimitationObject getLimitOBJ(Player p, Project project){
		LimitationObject lobj = null;
		if(this.type.equals(LimitationType.PLAYER)){
			int i = -1;
			for(LimitationObject obj : this.objectList){
				if(obj.def){
					if(obj.getAmountFromType(project.getName()) > i){
						i = obj.getAmountFromType(project.getName());
						lobj = obj;
					}
				}else if(lib.getPermission().hasPerm(p, obj.permission)){
					if(obj.getAmountFromType(project.getName()) > i){
						i = obj.getAmountFromType(project.getName());
						lobj = obj;
					}
				}
			}
		}
		return lobj;
	}
	
	public LimitationObject getDefault(){
		for(LimitationObject obj : this.objectList){
			if(obj.def){
				return obj;
			}
		}
		return null;
	}
}
