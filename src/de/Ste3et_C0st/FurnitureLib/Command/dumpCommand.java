package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.CommandSender;

import de.Ste3et_C0st.FurnitureLib.main.DumpHandler;

public class dumpCommand extends iCommand {

	public dumpCommand(String subCommand) {
		super(subCommand);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!hasCommandPermission(sender)) return;
		new DumpHandler(sender);
	}

}
