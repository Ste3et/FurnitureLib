package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleterHandler implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
        if (sender instanceof Player) {
            if (cmd != null && cmd.getName().equalsIgnoreCase("furniture")) {
                if (args.length == 1) {
                    String s = args[0];
                    return getTabCompleter(sender, s);
                }
                if (args.length > 1) {
                    iCommand iCommandParam = command.commands.stream().filter(c -> c.getSubCommand().equalsIgnoreCase(args[0])).findFirst().orElse(null);
                    if (iCommandParam != null) {
                        if (iCommandParam.getTabs() != null && iCommandParam.getTabs().length > 0) {
                            int index = args.length - 2;
                            if (index < iCommandParam.getTabs().length) {
                                String str = iCommandParam.getTabs()[index];
                                if (str.equalsIgnoreCase("installedModels")) {
                                    List<String> strAL = new ArrayList<>();
                                    FurnitureManager.getInstance().getProjects().stream()
                                            .filter(p -> p.getName().toLowerCase().contains(args[1].toLowerCase()))
                                            .forEach(p -> strAL.add(p.getName()));
                                    return strAL;
                                } else if (str.equalsIgnoreCase("editorProjects")) {
                                    List<String> strAL = new ArrayList<>();
                                    FurnitureManager.getInstance().getProjects().stream()
                                            .filter(p -> p.isEditorProject() && p.getName().toLowerCase().contains(args[1].toLowerCase()))
                                            .forEach(p -> strAL.add(p.getName()));
                                    return strAL;
                                } else if (str.equalsIgnoreCase("installedDModels")) {
                                    List<String> strAL = new ArrayList<>();
                                    FurnitureManager.getInstance().getProjects().stream()
                                            .filter(p -> p.isEditorProject() && p.getName().toLowerCase().contains(args[1].toLowerCase()))
                                            .forEach(p -> strAL.add(p.getName()));
                                    return strAL;
                                } else if (str.equalsIgnoreCase("players")) {
                                    return null;
                                }else if (iCommandParam instanceof listCommand) {
                                	List<String> strAL = Arrays.asList(str.split("/"));
                                	String current = args[args.length - 1].toLowerCase();
                                	if(current.isEmpty()) {
                                		return strAL;
                                	}else {
                                		if(current.equalsIgnoreCase("distance:")) {
                                			return Arrays.asList("distance:1","distance:10","distance:100");
                                		}else if(current.toLowerCase().startsWith("world:")){
                                			List<String> tab = new ArrayList<String>();
                                			String world = current.replace("world:", "");
                                			Bukkit.getWorlds().stream().filter(entry -> entry.getName().toLowerCase().contains(world.toLowerCase())).forEach(entry -> tab.add("world:" + entry.getName()));
                                			return tab;
										}else if(current.toLowerCase().startsWith("player:")){
											List<String> tab = new ArrayList<String>();
                                			String player = current.replace("player:", "");
                                			Bukkit.getOnlinePlayers().stream().filter(entry -> entry.getName().toLowerCase().contains(player.toLowerCase())).forEach(entry -> tab.add("player:" + entry.getName()));
                                			return tab;
										}else {
											return strAL.stream().filter(a -> a.toLowerCase().contains(current.toLowerCase())).collect(Collectors.toList());
										}
                                	}
                                }else if (iCommandParam instanceof removeCommand) {
                                	List<String> strAL = Arrays.asList(str.split("/"));
                                	String current = args[args.length - 1].toLowerCase();
                                	if(current.isEmpty()) {
                                		return strAL;
                                	}else {
                                		if (current.startsWith("project:")) {
                                			String string = current.replace("project:", "");
                                			if(string.isEmpty()) {
                                				FurnitureManager.getInstance().getProjects().forEach(entry -> strAL.add("project:" + entry.getName()));
                                				return strAL;
                                			}
                                			
                                            FurnitureManager.getInstance().getProjects().stream()
                                                    .filter(p -> p.getName().toLowerCase().contains(string))
                                                    .forEach(p -> strAL.add("project:" + p.getName()));
                                            if(strAL.isEmpty()) {
                                            	FurnitureManager.getInstance().getProjects().forEach(entry -> strAL.add("project:" + entry.getName()));
                                            }
                                            return strAL;
                                        }else if(current.startsWith("distance:")) {
                                			return Arrays.asList("distance:1","distance:10","distance:100");
                                		}else if(current.toLowerCase().startsWith("world:")){
                                			List<String> tab = new ArrayList<String>();
                                			String world = current.replace("world:", "");
                                			Bukkit.getWorlds().stream().filter(entry -> entry.getName().toLowerCase().contains(world.toLowerCase())).forEach(entry -> tab.add("world:" + entry.getName()));
                                			return tab;
										}else if(current.toLowerCase().startsWith("player:")){
											List<String> tab = new ArrayList<String>();
                                			String player = current.replace("player:", "");
                                			Bukkit.getOnlinePlayers().stream().filter(entry -> entry.getName().toLowerCase().contains(player.toLowerCase())).forEach(entry -> tab.add("player:" + entry.getName()));
                                			return tab;
										}else {
											return strAL.stream().filter(a -> a.toLowerCase().contains(current.toLowerCase())).collect(Collectors.toList());
										}
                                	}
                                } else {
                                    List<String> strAL = new ArrayList<String>();
                                    if (str.contains("/")) {
										strAL.addAll(Arrays.asList(str.split("/")));
                                        return strAL;
                                    } else {
                                        strAL.add(str);
                                        return strAL;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<String> getTabCompleter(CommandSender sender, String s) {
        List<String> strAL = new ArrayList<>();
        command.commands.stream()
                .filter(cmd -> cmd.getSubCommand().toLowerCase().contains(s.toLowerCase()))
                .filter(cmd -> sender.hasPermission(cmd.getFormattedPerms()))
                .forEach(cmd -> strAL.add(cmd.getSubCommand()));
        return strAL;
    }

//	private List<String> getProjectPlugins(List<String> s){
//		for(Project pro : lib.getFurnitureManager().getProjects()){
//			if(!s.contains(pro.getPlugin().getName())){
//				s.add(pro.getPlugin().getName());
//			}
//		}
//		return s;
//	}
//	
//	private List<String> getProjectNames(){
//		List<String> projectName = new ArrayList<String>();
//		for(Project pro : lib.getFurnitureManager().getProjects()){
//			if(!projectName.contains(pro.getName())){
//				projectName.add(pro.getName());
//			}
//		}
//		return projectName;
//	}
//	
//	private List<String> getModels(){
//		List<String> projectName = new ArrayList<String>();
//		for(Project pro : lib.getFurnitureManager().getProjects()){
//			if(!projectName.contains(pro.getName())){
//				if(pro.isEditorProject()){
//					projectName.add(pro.getName());
//				}
//			}
//		}
//		return projectName;
//	}
}
