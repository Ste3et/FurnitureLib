package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class manageCommand extends iCommand {

    public manageCommand(String subCommand, String... args) {
        super(subCommand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            getLHandler().sendMessage(sender, "message.WrongArgument");
            return;
        }
        if (sender instanceof Player) {
            if (hasCommandPermission(sender)) {
                command.manageList.add((Player) sender);
                getLHandler().sendMessage(sender, "message.ManageModeEntered");
                return;
            } else {
                getLHandler().sendMessage(sender, "message.NoPermissions");
            }
        }
    }

}
