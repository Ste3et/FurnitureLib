package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder.ClickAction;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder.HoverAction;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class listCommand {

	public listCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		Player p = null;
		if(sender instanceof Player){p = (Player) sender;}
		List<JsonBuilder> objList = new ArrayList<JsonBuilder>();
		if(args.length==1){
			if(!command.noPermissions(sender, "furniture.list")) return;
			boolean recipe = false, give = false, detail = true;
			if(FurnitureLib.getInstance().hasPerm(sender, "furniture.recipe")){recipe = true;}
			if(FurnitureLib.getInstance().hasPerm(sender, "furniture.give")){give = true;}
			if(FurnitureLib.getInstance().hasPerm(sender, "furniture.debug")){detail = false;}
			
			for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
				String s = null;
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
					objList.add(new JsonBuilder("§6- " + name).withHoverEvent(HoverAction.SHOW_TEXT,(s)).withClickEvent(ClickAction.RUN_COMMAND, "/furniture give " + pro.getName()));
				}else if(recipe){
					objList.add(new JsonBuilder("§6- " + name).withHoverEvent(HoverAction.SHOW_TEXT,(s)).withClickEvent(ClickAction.RUN_COMMAND, "/furniture recipe " + pro.getName()));
				}else {
					objList.add(new JsonBuilder("§6- " + name).withHoverEvent(HoverAction.SHOW_TEXT,(s)));
				}
			}
			new objectToSide(objList, p, 1, "/furniture list");
		}else if(args.length==2){
			String subcommand = "";
			if(args[1].equalsIgnoreCase("Type")){
				if(!command.noPermissions(sender, "furniture.list.type")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					List<ObjectID> objectList = getByType(pro);
					objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
				subcommand = " type";
			}else if(args[1].equalsIgnoreCase("World")){
				if(!command.noPermissions(sender, "furniture.list.world")) return;
				for(World w : Bukkit.getWorlds()){
					List<ObjectID> objectList = getByWorld(w);
					objList.add(new JsonBuilder("§6- " + w.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
				subcommand = " world";
			}else if(args[1].equalsIgnoreCase("Plugin")){
				if(!command.noPermissions(sender, "furniture.list.plugin")) return;
				List<String> plugins = new ArrayList<String>();
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					String plugin = pro.getPlugin().getName();
					if(!plugins.contains(plugin)){
						objList.add(new JsonBuilder("§c" + plugin));
						for(Project project : getByPlugin(plugin)){objList.add(new JsonBuilder("§7- " + project.getName()));}
						plugins.add(plugin);
					}
				}
				subcommand = " plugin";
			}else if(args[1].equalsIgnoreCase("models")){
				if(!command.noPermissions(sender, "furniture.list.models")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					if(pro.isEditorProject()){
						List<ObjectID> objectList = getByModel(pro);
						objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
					}
				}
				subcommand = " models";
			}else if(FurnitureLib.getInstance().isInt(args[1])){
				if(!command.noPermissions(sender, "furniture.list")) return;
				boolean recipe = false, give = false, detail = true;
				if(FurnitureLib.getInstance().hasPerm(sender, "furniture.recipe")){recipe = true;}
				if(FurnitureLib.getInstance().hasPerm(sender, "furniture.give")){give = true;}
				if(FurnitureLib.getInstance().hasPerm(sender, "furniture.debug")){detail = false;}
				
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					String s = null;
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
						objList.add(new JsonBuilder("§6- " + name).withHoverEvent(HoverAction.SHOW_TEXT,(s)).withClickEvent(ClickAction.RUN_COMMAND, "/furniture give " + pro.getName()));
					}else if(recipe){
						objList.add(new JsonBuilder("§6- " + name).withHoverEvent(HoverAction.SHOW_TEXT,(s)).withClickEvent(ClickAction.RUN_COMMAND, "/furniture recipe " + pro.getName()));
					}else {
						objList.add(new JsonBuilder("§6- " + name).withHoverEvent(HoverAction.SHOW_TEXT,(s)));
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
					objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
				subcommand = " type";
			}else if(args[1].equalsIgnoreCase("World")){
				if(!command.noPermissions(sender, "furniture.list.world")) return;
				for(World w : Bukkit.getWorlds()){
					List<ObjectID> objectList = getByWorld(w);
					objList.add(new JsonBuilder("§6- " + w.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
				subcommand = " world";
			}else if(args[1].equalsIgnoreCase("models")){
				if(!command.noPermissions(sender, "furniture.list.models")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					if(pro.isEditorProject()){
						List<ObjectID> objectList = getByModel(pro);
						objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));	
					}
				}
				subcommand = " models";
			}else if(args[1].equalsIgnoreCase("Plugin")){
				if(!command.noPermissions(sender, "furniture.list.plugin")) return;
				List<String> plugins = new ArrayList<String>();
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					String plugin = pro.getPlugin().getName();
					if(!plugins.contains(plugin)){
						objList.add(new JsonBuilder("§c" + plugin));
						for(Project project : getByPlugin(plugin)){objList.add(new JsonBuilder("§7- " + project.getName()));}
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
