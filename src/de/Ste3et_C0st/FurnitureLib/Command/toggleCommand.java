package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class toggleCommand {
	
	public toggleCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		if(args.length == 1){
			if(sender instanceof Player){
				if(FurnitureLib.getInstance().hasPerm(sender, "furniture.toggle") || FurnitureLib.getInstance().hasPerm(sender, "furniture.player")){
					if(!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(((Player) sender).getUniqueId())){
						FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(((Player) sender).getUniqueId());
						FurnitureLib.getInstance().getFurnitureManager().removeFurniture((Player) sender);
						sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleCMDOff"));
					}else{
						FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().remove(((Player) sender).getUniqueId());
						FurnitureLib.getInstance().getFurnitureManager().updatePlayerView((Player) sender);
						sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleCMDOn"));
					}
				}
			}
		}else if(args.length == 2){
			if(FurnitureLib.getInstance().hasPerm(sender, "furniture.toggle.another") || FurnitureLib.getInstance().hasPerm(sender, "furniture.player")){
				Player player = Bukkit.getPlayer(args[1]);
				if(player == null){sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline"));}
				if(!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(player.getUniqueId())){
					FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(player.getUniqueId());
					FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
					sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleCMDOff"));
					player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleCMDOff"));
				}else{
					FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().remove(player.getUniqueId());
					FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
					sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleCMDOn"));
					player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleCMDOn"));
				}
			}
		}else{
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));return;
		}
	}
}
