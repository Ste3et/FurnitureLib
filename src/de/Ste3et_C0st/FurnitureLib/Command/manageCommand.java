package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class manageCommand extends iCommand{

	public manageCommand(String subCommand, String ...args) {
		super(subCommand);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length!=1){sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.WrongArgument"));return;}
		if(sender instanceof Player){
			if(hasCommandPermission(sender)){
				command.manageList.add((Player) sender);
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.ManageModeEntered"));
				return;
			}else{
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NoPermissions"));
			}
		}
	}

}
