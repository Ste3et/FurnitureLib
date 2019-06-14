package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class debugCommand extends iCommand{
	
	public debugCommand(String subCommand, String ...args) {
		super(subCommand);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length!=1){sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.WrongArgument"));return;}
		if(sender instanceof Player){
			if(hasCommandPermission(sender)){
				command.playerList.add((Player) sender);
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.DebugModeEntered"));
			}else{
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NoPermissions"));
			}
		}
	}
}
