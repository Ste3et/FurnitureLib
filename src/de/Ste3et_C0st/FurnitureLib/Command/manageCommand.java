package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class manageCommand {

	public manageCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		if(args.length!=1){sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));return;}
		if(sender instanceof Player){
			if(FurnitureLib.getInstance().hasPerm(sender, "furniture.manage") || FurnitureLib.getInstance().hasPerm(sender, "furniture.player")){
				command.manageList.add((Player) sender);
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ManageModeEntered"));
				return;
			}else{
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
			}
		}
	}

}
