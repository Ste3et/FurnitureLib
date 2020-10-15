package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class disabledCommand implements CommandExecutor {

	private List<String> instructions = new ArrayList<String>();
	
	public disabledCommand(FurnitureLib furnitureLib, List<String> instructions) {
		this.instructions.addAll(instructions);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("furniture")) {
			if(sender.hasPermission("furniture.admin")) {
				instructions.stream().forEach(sender::sendMessage);
			}
			return true;
		}
		return false;
	}

}
