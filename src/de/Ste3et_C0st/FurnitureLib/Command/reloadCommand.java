package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class reloadCommand {

	public reloadCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		//furniture purge <days>
		if(!command.noPermissions(sender, "furniture.reload")) return;
		FurnitureLib.getInstance().reloadPluginConfig();
		sender.sendMessage("Furniture Config Reloadet !");
		return;
	}
	
}
