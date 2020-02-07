package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.command.CommandSender;


public class saveCommand extends iCommand {

    public saveCommand(String subCommand, String... args) {
        super(subCommand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) return;
        if (args.length == 1) {
            FurnitureLib.getInstance().getFurnitureManager().saveAsynchron(sender);
        }
    }

}
