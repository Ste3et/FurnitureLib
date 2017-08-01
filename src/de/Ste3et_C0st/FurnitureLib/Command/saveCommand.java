package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;


public class saveCommand {

	public saveCommand(final CommandSender sender, Command cmd, String arg2,String[] args){
		if(!command.noPermissions(sender, "furniture.save")) return;
		if(args.length==1){
			FurnitureLib.getInstance().getFurnitureManager().saveAsynchron(sender);
		}
	}

}
