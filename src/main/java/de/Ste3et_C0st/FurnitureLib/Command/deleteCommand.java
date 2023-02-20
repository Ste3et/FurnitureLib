package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.ChatComponentWrapper;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class deleteCommand extends iCommand {

	public deleteCommand(String subCommand, String... args) {
		super(subCommand);
		setTab("installedDModels");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 2) {
			if (!hasCommandPermission(sender))
				return;
			String systemID = args[1];
			Project project = getProject(systemID);
			if (project != null) {
				// if (project.isEditorProject()) {
				List<ObjectID> id = getObject(project);
				final int i = id != null ? id.size() : 0;
				sender.sendMessage("§cThen you want to delete this Model");
				sender.sendMessage("§cPlease confirm the delete from §a" + i + "§c Models");
				ComponentBuilder builder = new ComponentBuilder("§cPlease type")
						.append("§a§n/furniture delete "+ systemID +" confirm").event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/furniture delete " + systemID + " confirm"))
						.append("§r §cto remove it").reset();
				ChatComponentWrapper.sendChatComponent(Player.class.cast(sender), builder.create());
				sender.sendMessage("§cYou have 20 seconds left");
				FurnitureLib.getInstance().deleteMap.put(project, System.currentTimeMillis());
//                } else {
//                    sender.sendMessage("§cThis is no FurnitureMaker model");
//                    sender.sendMessage("§cYou can use §a§n/furniture recipe System-ID remove§r §cto disable it");
//                    return;
//                }
			} else {
				sender.sendMessage("§cThe project §n" + systemID + " §c does not exist");
			}
		} else if (args.length == 3) {
			if (!hasCommandPermission(sender))
				return;
			if (args[2].equalsIgnoreCase("confirm")) {
				String systemID = args[1];
				Project project = getProject(systemID);
				if (project != null) {
					if (FurnitureLib.getInstance().deleteMap.containsKey(project)) {
						Long l1 = FurnitureLib.getInstance().deleteMap.get(project);
						Long l2 = System.currentTimeMillis();
						Long l3 = l2 - l1;
						int seconds = (int) (l3 / 1000);
						if (seconds <= 20) {
							FurnitureLib.getInstance().deleteMap.remove(project);
							List<ObjectID> id = getObject(project);
							if (id != null) {
								for (ObjectID ids : id) {
									ids.remove();
								}
							}
							project.getCraftingFile().removeCrafting(project.getCraftingFile().getItemstack());
							// project.getCraftingFile().setCraftingDisabled(true);
							String str = project.getCraftingFile().getFileName();
							if(!project.isEditorProject()) {
								File file1 = new File("plugins/FurnitureLib/Crafting", str + ".yml");
								File file2 = new File("plugins/FurnitureLib/plugin/DiceEditor", str + ".yml");
								File file3 = new File("plugins/FurnitureLib/models", str + ".dModel");
								if (file1.exists()) {
									YamlConfiguration config = YamlConfiguration.loadConfiguration(file1);
									config.set(str + ".enabled", false);
									try {
										config.save(file1);
									} catch (IOException e) {
										e.printStackTrace();
									}
									FurnitureLib.getInstance().getFurnitureManager().getProjects().remove(project);
									FurnitureLib.getInstance().getFurnitureManager().getProjectMap().remove(project.getName().toLowerCase(), project);
									sender.sendMessage("§2The Furniture Model §a" + systemID + " §2has been disabled");
								}else if (file2.exists()) {
									YamlConfiguration config = YamlConfiguration.loadConfiguration(file2);
									config.set(str + ".enabled", false);
									try {
										config.save(file2);
									} catch (IOException e) {
										e.printStackTrace();
									}
									FurnitureLib.getInstance().getFurnitureManager().getProjects().remove(project);
									FurnitureLib.getInstance().getFurnitureManager().getProjectMap().remove(project.getName().toLowerCase(), project);
									sender.sendMessage("§2The Furniture Model §a" + systemID + " §2has been disabled");
								}else if (file3.exists()) {
									YamlConfiguration config = YamlConfiguration.loadConfiguration(file3);
									config.set(str + ".enabled", false);
									try {
										config.save(file3);
									} catch (IOException e) {
										e.printStackTrace();
									}
									FurnitureLib.getInstance().getFurnitureManager().getProjects().remove(project);
									FurnitureLib.getInstance().getFurnitureManager().getProjectMap().remove(project.getName().toLowerCase(), project);
									sender.sendMessage("§2The Furniture Model §a" + systemID + " §2has been disabled");
								}else {
									sender.sendMessage("§cThe Furniture Model §a" + systemID + " §ccould not deleted");
								}
							}else {
								File file1 = new File("plugins/FurnitureLib/Crafting", str + ".yml");
								File file2 = new File("plugins/FurnitureLib/plugin/DiceEditor", str + ".yml");
								File file3 = new File("plugins/FurnitureLib/models/", str + ".dModel");
								if(file1.exists()) {
									file1.delete();
									FurnitureLib.getInstance().getFurnitureManager().getProjects().remove(project);
									sender.sendMessage("§2The Furniture Model §a" + systemID + " §2has been removed");
								}else if(file2.exists()) {
									file2.delete();
									FurnitureLib.getInstance().getFurnitureManager().getProjects().remove(project);
									sender.sendMessage("§2The Furniture Model §a" + systemID + " §2has been removed");
								}else if(file3.exists()) {
									file3.delete();
									FurnitureLib.getInstance().getFurnitureManager().getProjects().remove(project);
									sender.sendMessage("§2The Furniture Model §a" + systemID + " §2has been removed");
								}else {
									sender.sendMessage("§cThe Furniture Model §a" + systemID + " §ccould not deleted");
								}
							}
						} else {
							sender.sendMessage("§cYou were too slow, please type §a§n/furniture delete System-ID");
							FurnitureLib.getInstance().deleteMap.remove(project);
						}
					} else {
						sender.sendMessage("§cYou must type §a§n/furniture delete System-ID");
					}
				} else {
					sender.sendMessage("§cThe project §n" + systemID + " §c does not exist");
				}
			} else {
				sender.sendMessage("§cYou must be type §a§n/furniture delete System-ID confirm");
			}
		} else {
			command.sendHelp((Player) sender);
			return;
		}
	}

	private Project getProject(String project) {
		for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
			if (pro.getName().equalsIgnoreCase(project)) {
				return pro;
			}
		}
		return null;
	}

	private List<ObjectID> getObject(Project pro) {
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
			try {
				if (obj.getProjectOBJ().equals(pro)) {
					if (obj.getSQLAction().equals(SQLAction.REMOVE)) {
						continue;
					}
					objList.add(obj);
				}
			} catch (Exception ex) {
				continue;
			}
		}
		return objList;
	}
}
