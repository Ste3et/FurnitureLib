package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class giveCommand {

	public giveCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		if(!command.noPermissions(sender, "furniture.give")){return;}
		
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
		if(args.length==2){
			if(sender instanceof Player){
				Player player = (Player) sender;
				player.getInventory().addItem(pro.getCraftingFile().getRecipe().getResult());
				player.updateInventory();
				return;
			}else{
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
				return;
			}
		}else if(args.length == 3){
			if(FurnitureLib.getInstance().isInt(args[2])){
				if(sender instanceof Player){
					int i = Integer.parseInt(args[2]);
					if(i==0) i = 1;
					Player player = (Player) sender;
					ItemStack is = pro.getCraftingFile().getRecipe().getResult().clone();
					is.setAmount(i);
					player.getInventory().addItem(is);
					player.updateInventory();
					return;
				}else{
					sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
					return;
				}
			}else if(Bukkit.getPlayer(args[2]) != null){
				if(!command.noPermissions(sender, "furniture.give.player")){return;}
				Player p2 = Bukkit.getPlayer(args[2]);
				Player p = null;
				if(p2.isOnline()){
					p2.getInventory().addItem(pro.getCraftingFile().getRecipe().getResult());
					p2.updateInventory();
					if(sender instanceof Player){p = (Player) sender;}
					if(p!=null&&p.getUniqueId().equals(p2.getUniqueId())){return;}
					String str = FurnitureLib.getInstance().getLangManager().getString("GivePlayer");
					str = str.replace("#PLAYER#", p2.getName());
					str = str.replace("#PROJECT#", pro.getName());
					str = str.replace("#AMOUNT#", 1+"");
					sender.sendMessage(str);
					return;
				}else{
					String s = FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline");
					s = s.replace("#PLAYER#", args[2]);
					sender.sendMessage(s);
					return;
				}
			}else{
				String s = FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline");
				s = s.replace("#PLAYER#", args[2]);
				sender.sendMessage(s);
				return;
			}
		}else if(args.length == 4){
			if(Bukkit.getPlayer(args[2]) != null){
				if(!command.noPermissions(sender, "furniture.give.player")){return;}
				Player p2 = Bukkit.getPlayer(args[2]);
				Player p = null;
				if(p2.isOnline()){
					if(FurnitureLib.getInstance().isInt(args[3])){
						int i = Integer.parseInt(args[3]);
						if(i==0) i = 1;
						ItemStack is = pro.getCraftingFile().getRecipe().getResult().clone();
						is.setAmount(i);
						p2.getInventory().addItem(is);
						p2.updateInventory();
						if(sender instanceof Player){p = (Player) sender;}
						if(p!=null&&p.getUniqueId().equals(p2.getUniqueId())){return;}
						String str = FurnitureLib.getInstance().getLangManager().getString("GivePlayer");
						str = str.replace("#PLAYER#", p2.getName());
						str = str.replace("#PROJECT#", pro.getName());
						str = str.replace("#AMOUNT#", i+"");
						sender.sendMessage(str);
						return;
					}else{
						sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
						return;
					}
				}else{
					String s = FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline");
					s = s.replace("#PLAYER#", args[2]);
					sender.sendMessage(s);
					return;
				}
			}else{
				String s = FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline");
				s = s.replace("#PLAYER#", args[2]);
				sender.sendMessage(s);
				return;
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
