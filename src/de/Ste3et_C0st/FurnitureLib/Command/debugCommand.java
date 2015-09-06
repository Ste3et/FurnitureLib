package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class debugCommand {
	public debugCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		if(args.length!=1){sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));return;}
		if(sender instanceof Player){
			if(command.noPermissions(sender, "furniture.debug")){
				command.playerList.add((Player) sender);
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("DebugModeEntered"));
			}else{
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
			}
		}
	}
}
