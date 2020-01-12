package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;

public class LimitationManager {

	FurnitureLib lib;
	public List<LimitationObject> objectList = new ArrayList<LimitationObject>();
	public LimitationType type;
	
	public LimitationManager(FurnitureLib lib, LimitationType limitationType){
		this.lib = lib;
		this.type = limitationType;
		loadDefault();
	}
	
	private Integer returnIntProject(Player p, Project pro){
		if(pro==null) return 0;
		return (int) FurnitureManager.getInstance().getFromPlayer(p.getUniqueId()).stream().filter(obj -> obj.getProjectOBJ().equals(pro)).count();
	}
	
	private Integer returnIntProjectTotal(Player p){
		return FurnitureManager.getInstance().getFromPlayer(p.getUniqueId()).size();
	}
	
	private Integer returnIntProjectChunk(Chunk c, Project pro){
		int i = 0;
		if(pro == null) return i;
		return (int) FurnitureManager.getInstance().getInChunk(c).stream().filter(obj -> obj.getProject().equals(pro.getName())).count();
	}
	
	private Integer returnProjectWorld(World w, Project pro){
		int i = 0;
		if(w==null) return i;
		if(pro==null) return i;
		return (int) FurnitureManager.getInstance().getInWorld(w).stream().filter(obj -> obj.getProject().equals(pro.getName())).count();
	}
	
	public boolean canPlace(Player p, ObjectID obj){
		if(p.isOp()) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.admin")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.bypass.limit")) return true;
		Project pro = obj.getProjectOBJ();
		LimitationObject limitOBJ = getLimitOBJ(p, pro);
		
		if(limitOBJ != null) {
			if(limitOBJ.total && limitOBJ.totalAmount == -1) return true;
		}
		
		if(this.type.equals(LimitationType.PLAYER)) {
			int player = returnIntProject(p, pro);
			int playerTotal = returnIntProjectTotal(p);
			int limitGlobal = this.lib.getLimitGlobal();
			//Permissions range check start
			if(limitGlobal > 0) {
				for(int i = limitGlobal; i > 0; i--) {
					if(p.hasPermission("furniture.globallimit." + i)) {
						if(playerTotal < i) {
							String s = lib.getLangManager().getString("message.LimitAouncer");
							s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player+1+"").replace("#MAX#", i+"");
							p.sendMessage(s);
							return true;
						}else {
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedMaximum"));
							return false;
						}
					}
				}
			}
			//Permissions range check end
			
			int maxPlayer = limitOBJ.getAmountFromType(pro.getName());
			FurnitureLib.debug("LimitationManager -> {Player} " + player + "/" + maxPlayer);
			if(maxPlayer < 0) return true;
			if(player < maxPlayer) {
				String s = lib.getLangManager().getString("message.LimitAouncer");
				s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player+1+"").replace("#MAX#", maxPlayer+"");
				p.sendMessage(s);
				return true;
			}else {
				p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedMaximum"));
				return false;
			}
		}else if(this.type.equals(LimitationType.WORLD)) {
			int maxWorld = limitOBJ.total ? limitOBJ.totalAmount : pro.getAmountWorld(obj.getWorld());
			int world = returnProjectWorld(obj.getWorld(), pro);
			FurnitureLib.debug("LimitationManager -> {World} " + world + "/" + maxWorld);
			if(maxWorld < 0) return true;
			if(world < maxWorld) {
				String s = lib.getLangManager().getString("message.LimitAouncer");
				s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", world+1+"").replace("#MAX#", maxWorld+"");
				p.sendMessage(s);
				return true;
			}else {
				p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedWorld"));
				return false;
			}
		}else if(this.type.equals(LimitationType.CHUNK)) {
			int maxChunk = (Objects.nonNull(limitOBJ) && limitOBJ.total) ? limitOBJ.totalAmount  : pro.getAmountChunk();
			int chunk = returnIntProjectChunk(obj.getChunk(), pro);
			FurnitureLib.debug("LimitationManager -> {Chunk} " + chunk + "/" + maxChunk);
			if(maxChunk < 0) return true;
			if(chunk < maxChunk) {
				String s = lib.getLangManager().getString("message.LimitAouncer");
				s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", chunk+1+"").replace("#MAX#", maxChunk+"");
				p.sendMessage(s);
				return true;
			}else {
				p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedChunk"));
				return false;
			}
		}
		return true;
	}
//	
//	public void sendAnouncer(Player p, ObjectID obj){
//		if(p.isOp()) return;
//		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.admin")) return;
//		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.bypass.limit")) return;
//		
//		Project pro = obj.getProjectOBJ();
//		LimitationObject limitOBJ = getLimitOBJ(p, pro);
//		
//		int world = returnProjectWorld(obj.getWorld(), pro);
//		int chunk = returnIntProjectChunk(obj.getChunk(), pro);
//		int player = returnIntProject(p, pro);
//		int playerTotal = returnIntProjectTotal(p);
//		
//		int maxWorld = pro.getAmountWorld(obj.getWorld());
//		int maxChunk = pro.getAmountChunk();
//		int maxPlayer = -1;
//		
//		if(limitOBJ!=null) maxPlayer = limitOBJ.getAmountFromType(pro.getName());
//		
//		if(world>=maxWorld){
//			if(maxWorld!=-1){p.sendMessage(lib.getLangManager().getString("LimitReachedWorld"));return;}
//		}
//		
//		if(chunk>=maxChunk){
//			if(maxChunk!=-1){p.sendMessage(lib.getLangManager().getString("LimitReachedChunk"));return;}
//		}
//		
//		if(maxPlayer != -1){
//			if(player < maxPlayer){
//				if(limitOBJ!=null){
//					if(limitOBJ.total){
//						if(limitOBJ.totalAmount == -1) return;
//						if(playerTotal < limitOBJ.totalAmount){
//							String s = lib.getLangManager().getString("LimitAouncerMaximum");
//							s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player+1+"").replace("#MAX#", maxPlayer+"").replace("#AMOUNT#", (limitOBJ.totalAmount - (playerTotal + 1)) + "");
//							p.sendMessage(s);
//							return;
//						}else{
//							p.sendMessage(lib.getLangManager().getString("LimitReachedMaximum"));
//							return;
//						}
//					}
//				}
//				String s = lib.getLangManager().getString("LimitAouncer");
//				s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player+1+"").replace("#MAX#", maxPlayer+"");
//				p.sendMessage(s);
//				return;
//			}
//			p.sendMessage(lib.getLangManager().getString("LimitReached"));
//		}
//	}
	
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
			objectList.stream().forEach(obj -> {
				obj.addDefault(project);
				obj.loadProjects(project);
			});
		}
	}
	
	public LimitationObject getLimitOBJ(Player p, Project project){
		LimitationObject lobj = null;
		if(this.type.equals(LimitationType.PLAYER)){
			int i = -1;
			for(LimitationObject obj : this.objectList){
				if(obj.def){
					if(obj.getAmountFromType(project.getName()) >= i){
						i = obj.getAmountFromType(project.getName());
						lobj = obj;
					}
				}else if(lib.getPermission().hasPerm(p, obj.permission)){
					if(obj.getAmountFromType(project.getName()) >= i){
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
