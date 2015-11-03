package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class TabCompleterHandler implements TabCompleter {
	List<String> str = Arrays.asList("list","give","debug","manage","recipe","remove");
	List<String> str2 = Arrays.asList("type","world","plugin");
	FurnitureLib lib;
	public TabCompleterHandler(FurnitureLib furnitureLib) {
		this.lib = furnitureLib;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player){
			if(cmd!=null&&cmd.getName().equalsIgnoreCase("furniture")){
				if(args.length==1){
					String s = args[0];
					return getTabCompleter(s, str);
				}
				if(args.length==2){
					String s = args[1];
					if(args[0].equalsIgnoreCase("list")){
						return getTabCompleter(s, str2);
					}else if(args[0].equalsIgnoreCase("give")){
						return getTabCompleter(s, getProjectNames());
					}else if(args[0].equalsIgnoreCase("remove")){
						List<String> stringList = getProjectNames();
						stringList.add("all");
						stringList.add("distance");
						stringList.add("lookat");
						stringList.add(new Random(10).nextInt()+"");
						return getTabCompleter(s, getProjectPlugins(stringList));
					}else if(args[0].equalsIgnoreCase("recipe")){
						return getTabCompleter(s, getProjectNames());
					}
				}
			}
		}
		return null;
	}

	private List<String> getTabCompleter(String s, List<String> strL){
		List<String> strAL = new ArrayList<String>();
		for(String str : strL){
			if(strAL.contains(str)){continue;}
			if(str.toLowerCase().startsWith(s.toLowerCase())){strAL.add(str);}
		}
		return strAL;
	}
	
	private List<String> getProjectPlugins(List<String> s){
		for(Project pro : lib.getFurnitureManager().getProjects()){
			if(!s.contains(pro.getPlugin().getName())){
				s.add(pro.getPlugin().getName());
			}
		}
		return s;
	}
	
	private List<String> getProjectNames(){
		List<String> projectName = new ArrayList<String>();
		for(Project pro : lib.getFurnitureManager().getProjects()){
			if(!projectName.contains(pro.getName())){
				projectName.add(pro.getName());
			}
		}
		return projectName;
	}
}
