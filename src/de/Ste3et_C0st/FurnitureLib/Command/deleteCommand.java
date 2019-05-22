package de.Ste3et_C0st.FurnitureLib.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class deleteCommand extends iCommand {

	public deleteCommand(String subCommand, String permissions, String ...args) {
		super(subCommand, permissions);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args){
		if(args.length==2){
				if(!hasCommandPermission(sender)) return;
				String systemID = args[1];
				Project project = getProject(systemID);
				if(project!=null){
					if(project.isEditorProject()){
						List<ObjectID> id = getObject(project);
						int i = 0;
						if(id!=null) i = id.size();
						sender.sendMessage("§cThen you want to delete this Model");
						sender.sendMessage("§cPlease confirm the delete from §a" + i + "§c Models");
						sender.sendMessage("§cPlease type §a§n/furniture delete System-ID confirm§r §cto remove it");
						sender.sendMessage("§cYou have 20 secounds time to do it");
						FurnitureLib.getInstance().deleteMap.put(project, System.currentTimeMillis());
					}else{
						sender.sendMessage("§cThis is no FurnitureMaker model");
						sender.sendMessage("§cYou can use §a§n/furniture recipe System-ID remove§r §cto disable it");
						return;
					}
				}else{
					sender.sendMessage("§cThe project §n" + systemID + " §c does not exist");
				}
		}else if(args.length==3){
				if(!hasCommandPermission(sender)) return;
				if(args[2].equalsIgnoreCase("confirm")){
					String systemID = args[1];
					Project project = getProject(systemID);
					if(project!=null){
						if(project.isEditorProject()){
							if(FurnitureLib.getInstance().deleteMap.containsKey(project)){
								Long l1 = FurnitureLib.getInstance().deleteMap.get(project);
								Long l2 = System.currentTimeMillis();
								Long l3 = l2 - l1;
								int sekunden = (int)(l3 / 1000);
								if(sekunden<=20){
									FurnitureLib.getInstance().deleteMap.remove(project);
									List<ObjectID> id = getObject(project);
									if(id!=null){
										for(ObjectID ids : id){
											ids.remove();
										}
									}
									project.getCraftingFile().removeCrafting(project.getCraftingFile().getItemstack());
									//project.getCraftingFile().setCraftingDisabled(true);
									String str = project.getCraftingFile().getFileName();
									File file1 = new File("plugins/FurnitureLib/Crafting", str + ".yml");
									File file2 = new File("plugins/FurnitureLib/plugin/DiceEditor", str + ".yml");
									if(file1 != null && file1.exists()) file1.delete();
									if(file2 != null && file2.exists()) file2.delete();
									FurnitureLib.getInstance().getFurnitureManager().getProjects().remove(project);
									sender.sendMessage("§2The Furniture Model §a" + systemID + " §2have been removed");
								}else{
									sender.sendMessage("§cYou where to lame please put §a§n/furniture delete System-ID");
									FurnitureLib.getInstance().deleteMap.remove(project);
								}
							}else{
								sender.sendMessage("§cYou must be type §a§n/furniture delete System-ID");
							}
						}else{
							sender.sendMessage("§cThis is no FurnitureMaker model");
							sender.sendMessage("§cYou can use §a§n/furniture recipe System-ID remove§r §cto disable it");
							return;
						}
					}else{
						sender.sendMessage("§cThe project §n" + systemID + " §c does not exist");
					}
				}else{
					sender.sendMessage("§cYou must be type §a§n/furniture delete System-ID confirm");
				}
		}else{
			command.sendHelp((Player) sender);return;
		}
	}
	
	private Project getProject(String project){
		for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(pro.getName().equalsIgnoreCase(project)){
				return pro;
			}
		}
		return null;
	}
	
	private List<ObjectID> getObject(Project pro){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			try{
				if(obj.getProjectOBJ().equals(pro)){
					if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
					objList.add(obj);
				}
			}catch(Exception ex){
				continue;
			}
		}
		return objList;
	}
}
