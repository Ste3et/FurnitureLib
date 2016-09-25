package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class toggleCommand {
	
	public toggleCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		if(args.length!=1){sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));return;}
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
	}
}
