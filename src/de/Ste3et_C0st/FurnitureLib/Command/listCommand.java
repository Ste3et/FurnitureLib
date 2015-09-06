package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder;
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
			for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
				List<ObjectID> objectList = getByType(pro);
				objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
			}
			new objectToSide(objList, p, 1);
		}else if(args.length==2){
			if(args[1].equalsIgnoreCase("Type")){
				if(!command.noPermissions(sender, "furniture.list.type")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					List<ObjectID> objectList = getByType(pro);
					objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
			}else if(args[1].equalsIgnoreCase("World")){
				if(!command.noPermissions(sender, "furniture.list.world")) return;
				for(World w : Bukkit.getWorlds()){
					List<ObjectID> objectList = getByWorld(w);
					objList.add(new JsonBuilder("§6- " + w.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
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
			}else if(FurnitureLib.getInstance().isInt(args[1])){
				if(!command.noPermissions(sender, "furniture.list")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					List<ObjectID> objectList = getByType(pro);
					objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
				new objectToSide(objList, p, Integer.parseInt(args[1]));
				return;
			}else{
				command.sendHelp(p);
				return;
			}
			new objectToSide(objList, p, 1);
		}else if(args.length==3){
			if(args[1].equalsIgnoreCase("Type")){
				if(!command.noPermissions(sender, "furniture.list.type")) return;
				for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
					List<ObjectID> objectList = getByType(pro);
					objList.add(new JsonBuilder("§6- " +pro.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
			}else if(args[1].equalsIgnoreCase("World")){
				if(!command.noPermissions(sender, "furniture.list.world")) return;
				for(World w : Bukkit.getWorlds()){
					List<ObjectID> objectList = getByWorld(w);
					objList.add(new JsonBuilder("§6- " + w.getName()).withHoverEvent(HoverAction.SHOW_TEXT,("§eObjecte: §c" + objectList.size())));
				}
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
			}
			new objectToSide(objList, p, Integer.parseInt(args[2]));
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
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getProjectOBJ().equals(pro)){
				if(!obj.getSQLAction().equals(SQLAction.REMOVE)){
					objList.add(obj);
				}
			}
		}
		return objList;
	}

}
