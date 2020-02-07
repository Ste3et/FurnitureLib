package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.command.CommandSender;

public class versionCommand extends iCommand {

    public versionCommand(String subCommand, String... args) {
        super(subCommand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) return;
        sender.sendMessage("§7FurnitureLib: " + FurnitureLib.getInstance().getDescription().getVersion());
    }
}
