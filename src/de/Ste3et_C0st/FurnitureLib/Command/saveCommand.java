package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.CommandSender;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;


public class saveCommand  extends iCommand{

	public saveCommand(String subCommand, String ...args) {
		super(subCommand);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!hasCommandPermission(sender)) return;
		if(args.length==1){
			FurnitureLib.getInstance().getFurnitureManager().saveAsynchron(sender);
		}
	}

}
