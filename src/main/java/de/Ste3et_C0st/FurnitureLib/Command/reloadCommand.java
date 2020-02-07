package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.command.CommandSender;

public class reloadCommand extends iCommand {

    public reloadCommand(String subCommand, String... args) {
        super(subCommand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) return;
        FurnitureLib.getInstance().reloadPluginConfig();
        sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.reload"));
        return;
    }

}
