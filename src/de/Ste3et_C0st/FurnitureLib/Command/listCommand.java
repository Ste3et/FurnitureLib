package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class listCommand {

	
	
	/*
	 * 
	 * 			/furniture list    1        2        3         4
	 *			/furniture list <type> -w:World -r:radius -p:Owner page
	 * 
	 * 
	 * 
	 */
	
	
	
//	@SuppressWarnings("deprecation")
//	public listCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
//		Player p = null;
//		if(sender instanceof Player){p = (Player) sender;}else {return;}
//		
//		String type = args[1];
//		World world = null;
//		Integer radius = null;
//		OfflinePlayer player = null;
//		int page = 0;
//
//		for(String arg : args) {
//			if(arg.equalsIgnoreCase(type)) continue;
//			String a = arg.toLowerCase();
//			if(a.startsWith("-w:")) {
//				String wArg = a.replace("-w:", "");
//				if(Bukkit.getServer().getWorld(wArg) != null) {world = Bukkit.getServer().getWorld(wArg);}
//			}else if(a.startsWith("-r:")) {
//				String rArg = a.replace("-r:", "");
//				try {
//					radius = Integer.parseInt(rArg);
//					world = p.getWorld();
//				}catch (Exception e) {
//
//				}
//			}else if(a.startsWith("-p:")) {
//				String pArg = a.replace("-p:", "");
//				if(Bukkit.getOfflinePlayer(pArg) != null) {
//					OfflinePlayer pl = Bukkit.getOfflinePlayer(pArg);
//					if(pl.hasPlayedBefore()) {
//						player = pl;
//					}
//				}
//			}else{
//				try {
//					page = Integer.parseInt(arg);
//				}catch (Exception e) {
//
//				}
//			}
//		}
//		
//		HashSet<ObjectID> idList = FurnitureLib.getInstance().getFurnitureManager().getObjectList();
//		List<Project> projectList = FurnitureLib.getInstance().getFurnitureManager().getProjects();
//		
//		ArrayList<ObjectID> objList = new ArrayList<ObjectID>();
//		
//		for(ObjectID id : idList) {
//			ObjectID obj = null;
//			if(world != null) {
//				if(id.getWorld().equals(world)) {
//					obj = id;
//				}else {
//					obj = null;
//				}
//			}
//			
//			if(radius != null) {
//				if(id.getStartLocation().distance(p.getLocation()) <= radius) {
//					obj = id;
//				}else {
//					obj = null;
//				}
//			}
//			
//			if(player != null) {
//				if(id.getUUID() != null) {
//					if(id.getUUID().equals(player.getUniqueId())){
//						obj = id;
//					}else {
//						obj = null;
//					}
//				}else {
//					obj = null;
//				}
//			}
//			if(obj != null) objList.add(id);
//		}
//		
//	}

	public listCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		Player p = null;
		if(sender instanceof Player){p = (Player) sender;}
		List<ComponentBuilder> objList = new ArrayList<ComponentBuilder>();
		List<String> strList = new ArrayList<String>();
		HashMap<String, String> proList = new HashMap<String, String>();
		for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
			strList.add(pro.getName());
			String name = "";
			if(pro.getCraftingFile().getRecipe().getResult() != null){
				if(pro.getCraftingFile().getRecipe().getResult().hasItemMeta()){
					if(pro.getCraftingFile().getRecipe().getResult().getItemMeta().hasDisplayName()){
						name = ChatColor.stripColor(pro.getCraftingFile().getRecipe().getResult().getItemMeta().getDisplayName());
					}
				}
			}
			proList.put(pro.getName(), name);
		}
		
		SortedSet<String> keys = new TreeSet<String>(proList.keySet());
		SortedSet<String> values = new TreeSet<String>(proList.values());
		
		
		
		if(args.length==1){
			if(!command.noPermissions(sender, "furniture.list")) return;
			boolean recipe = false, give = false, detail = true;
			if(FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.recipe")){recipe = true;}
			if(FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.give")){give = true;}
			if(FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.debug")){detail = false;}
			
			for(String str : getProjects(keys, values, proList, give)){
				String s = "";
				Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(str);
				String name = pro.getName();
				if(detail){
					List<ObjectID> objectList = getByType(pro);
					s = "§eObjects: §c" + objectList.size();
					s += "\n§eSystemID: §c" + pro.getName();
				}
				
				if(pro.getCraftingFile().getRecipe().getResult() != null){
					if(pro.getCraftingFile().getRecipe().getResult().hasItemMeta()){
						if(pro.getCraftingFile().getRecipe().getResult().getItemMeta().hasDisplayName()){
							name = ChatColor.stripColor(pro.getCraftingFile().getRecipe().getResult().getItemMeta().getDisplayName());
						}
					}
				}
				
				if(give){
					objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture give " + pro.getName())));
				}else if(recipe){
					objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture recipe " + pro.getName())));
				}else {
					objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())));
				}
			}
			new objectToSide(objList, p, 1, "/furniture list");
		}else if(args.length==2){
			String subcommand = "";
			if(args[1].equalsIgnoreCase("Type")){
				if(!command.noPermissions(sender, "furniture.list.type")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					List<ObjectID> objectList = getByType(pro);
					objList.add(new ComponentBuilder("§6- " +pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
				}
				subcommand = " type";
			}else if(args[1].equalsIgnoreCase("World")){
				if(!command.noPermissions(sender, "furniture.list.world")) return;
				for(World w : Bukkit.getWorlds()){
					List<ObjectID> objectList = getByWorld(w);
					objList.add(new ComponentBuilder("§6- " + w.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
				}
				subcommand = " world";
			}else if(args[1].equalsIgnoreCase("Plugin")){
				if(!command.noPermissions(sender, "furniture.list.plugin")) return;
				List<String> plugins = new ArrayList<String>();
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					String plugin = pro.getPlugin().getName();
					if(!plugins.contains(plugin)){
						objList.add(new ComponentBuilder("§c" + plugin));
						for(Project project : getByPlugin(plugin)){objList.add(new ComponentBuilder("§7- " + project.getName()));}
						plugins.add(plugin);
					}
				}
				subcommand = " plugin";
			}else if(args[1].equalsIgnoreCase("models")){
				if(!command.noPermissions(sender, "furniture.list.models")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					if(pro.isEditorProject()){
						List<ObjectID> objectList = getByModel(pro);
						objList.add(new ComponentBuilder("§6- " +pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
					}
				}
				subcommand = " models";
			}else if(FurnitureLib.getInstance().isInt(args[1])){
				if(!command.noPermissions(sender, "furniture.list")) return;
				boolean recipe = false, give = false, detail = true;
				if(FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.recipe")){recipe = true;}
				if(FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.give")){give = true;}
				if(FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.debug")){detail = false;}
				
				for(String str : getProjects(keys, values, proList, give)){
					String s = "";
					Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(str);
					String name = pro.getName();
					if(detail){
						List<ObjectID> objectList = getByType(pro);
						s = "§eObjects: §c" + objectList.size();
						s += "\n§eSystemID: §c" + pro.getName();
					}
					
					if(pro.getCraftingFile().getRecipe().getResult() != null){
						if(pro.getCraftingFile().getRecipe().getResult().hasItemMeta()){
							if(pro.getCraftingFile().getRecipe().getResult().getItemMeta().hasDisplayName()){
								name = ChatColor.stripColor(pro.getCraftingFile().getRecipe().getResult().getItemMeta().getDisplayName());
							}
						}
					}
					
					if(give){
						objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture give " + pro.getName())));
					}else if(recipe){
						objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture recipe " + pro.getName())));
					}else {
						objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())));
					}
				}
				new objectToSide(objList, p, Integer.parseInt(args[1]), "/furniture list");
				return;
			}else{
				command.sendHelp(p);
				return;
			}
			new objectToSide(objList, p, 1, "/furniture list " + subcommand);
		}else if(args.length==3){
			String subcommand = "";
			if(args[1].equalsIgnoreCase("Type")){
				if(!command.noPermissions(sender, "furniture.list.type")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					List<ObjectID> objectList = getByType(pro);
					objList.add(new ComponentBuilder("§6- " +pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
				}
				subcommand = " type";
			}else if(args[1].equalsIgnoreCase("World")){
				if(!command.noPermissions(sender, "furniture.list.world")) return;
				for(World w : Bukkit.getWorlds()){
					List<ObjectID> objectList = getByWorld(w);
					objList.add(new ComponentBuilder("§6- " + w.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
				}
				subcommand = " world";
			}else if(args[1].equalsIgnoreCase("models")){
				if(!command.noPermissions(sender, "furniture.list.models")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					if(pro.isEditorProject()){
						List<ObjectID> objectList = getByModel(pro);
						objList.add(new ComponentBuilder("§6- " +pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));	
					}
				}
				subcommand = " models";
			}else if(args[1].equalsIgnoreCase("Plugin")){
				if(!command.noPermissions(sender, "furniture.list.plugin")) return;
				List<String> plugins = new ArrayList<String>();
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					String plugin = pro.getPlugin().getName();
					if(!plugins.contains(plugin)){
						objList.add(new ComponentBuilder("§c" + plugin));
						for(Project project : getByPlugin(plugin)){objList.add(new ComponentBuilder("§7- " + project.getName()));}
						plugins.add(plugin);
					}
				}
				subcommand = " plugin";
			}
			new objectToSide(objList, p, Integer.parseInt(args[2]), "/furniture list " + subcommand);
		}else{
			command.sendHelp(p);
		}
	}
	
	private SortedSet<String> getProjects(SortedSet<String> key, SortedSet<String> values, HashMap<String, String> hash, boolean detail){
		SortedSet<String> proList = new TreeSet<String>();
		//return admin SystemID sort
		if(!detail) {
			for(String str : values) {
				for(String k : hash.keySet()) {
					String v = hash.get(k);
					if(v.equalsIgnoreCase(str)) proList.add(k);
				}
			}
			return key;
		}
		return key;
	}
	
	private List<Project> getByPlugin(String plugin){
		List<Project> objList = new ArrayList<Project>();
		for(Project obj : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(obj.getPlugin().getName().equalsIgnoreCase(plugin)){
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getByWorld(World w){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getWorld().equals(w)){
				if(!obj.getSQLAction().equals(SQLAction.REMOVE)){
					objList.add(obj);
				}
			}
		}
		return objList;
	}
	
	private List<ObjectID> getByType(Project pro){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		if(pro==null) return objList;
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj==null) continue;
			if(obj.getProjectOBJ()==null) continue;
			if(obj.getProjectOBJ().equals(pro)){
				if(!obj.getSQLAction().equals(SQLAction.REMOVE)){
					objList.add(obj);
				}
			}
		}
		return objList;
	}
	
	private List<ObjectID> getByModel(Project pro){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		if(pro==null) return objList;
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj==null) continue;
			if(obj.getProjectOBJ()==null) continue;
			if(obj.getProjectOBJ().equals(pro)){
				if(!obj.getSQLAction().equals(SQLAction.REMOVE)){
					if(!obj.getProjectOBJ().isEditorProject()) continue;
					objList.add(obj);
				}
			}
		}
		return objList;
	}

}
