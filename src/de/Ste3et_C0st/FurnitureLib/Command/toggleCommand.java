package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class toggleCommand extends iCommand {

    public toggleCommand(String subCommand, String... args) {
        super(subCommand);
        setTab("players");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                if (hasCommandPermission(sender)) {
                    if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(((Player) sender).getUniqueId())) {
                        FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(((Player) sender).getUniqueId());
                        FurnitureLib.getInstance().getFurnitureManager().removeFurniture((Player) sender);
                        sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleCMDOff"));
                    } else {
                        FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().remove(((Player) sender).getUniqueId());
                        FurnitureLib.getInstance().getFurnitureManager().updatePlayerView((Player) sender);
                        sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleCMDOn"));
                    }
                }
            }
        } else if (args.length == 2) {
            if (hasCommandPermission(sender, ".other")) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.PlayerNotOnline"));
                    return;
                }
                if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(player.getUniqueId())) {
                    FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(player.getUniqueId());
                    FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
                    sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleCMDOff"));
                    if(!sender.getName().equalsIgnoreCase(args[1])) {
                    	player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleCMDOff"));
                    }
                } else {
                    FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().remove(player.getUniqueId());
                    FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
                    sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleCMDOn"));
                    if(!sender.getName().equalsIgnoreCase(args[1])) {
                    	player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleCMDOn"));
                    }
                }
            }
        } else {
            sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.WrongArgument"));
            return;
        }
    }
}
