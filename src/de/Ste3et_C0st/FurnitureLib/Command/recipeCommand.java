package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class recipeCommand {

	public recipeCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		Project pro = null;
		if(args.length>1){
			pro = getProject(args[1]);
			if(pro == null){
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ProjectNotFound").replaceAll("#PROJECT#", args[1]));
				return;
			}
		}else{
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
			return;
		}
		
		if(sender==null) return;
		
		if(args.length==2){
			if(sender instanceof Player){
				if(!command.noPermissions(sender, "furniture.recipe")) return;
				FurnitureLib.getInstance().getCraftingInv().openCrafting((Player) sender, pro, false);
				return;
			}else{
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
				return;
			}
		}else if(args.length==3){
			if(sender instanceof Player == false){
				if(Bukkit.getPlayer(args[2])!=null){
					FurnitureLib.getInstance().getCraftingInv().openCrafting(Bukkit.getPlayer(args[2]), pro, false);
					return;
				}else{
					String s = FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline");
					s = s.replace("#PLAYER#", args[2]);
					sender.sendMessage(s);
					return;
				}
			}else{
				if(args[2].equalsIgnoreCase("edit")){
					if(!command.noPermissions(sender, "furniture.recipe.edit")) return;
					pro = getProject(args[1]);
					if(pro == null){
						sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ProjectNotFound").replaceAll("#PROJECT#", args[1]));
						return;
					}
					FurnitureLib.getInstance().getCraftingInv().openCrafting((Player) sender, pro, true);
					return;
				}else if(args[2].equalsIgnoreCase("remove")){
					if(!command.noPermissions(sender, "furniture.recipe.remove")) return;
					pro = getProject(args[1]);
					if(pro == null){
						sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ProjectNotFound").replaceAll("#PROJECT#", args[1]));
						return;
					}
					pro.getCraftingFile().removeCrafting(pro.getCraftingFile().getItemstack());
					//pro.getCraftingFile().setCraftingDisabled(true);
					sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("CraftingRemove"));
					return;
				}else{
					sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
					return;
				}
			}
		}else{
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
			return;
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
}
