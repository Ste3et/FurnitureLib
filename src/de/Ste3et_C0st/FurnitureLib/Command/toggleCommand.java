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
    	if(sender.hasPermission("furniturelib.hidemodels")) {
    		sender.sendMessage(getLHandler().getString("message.FurnitureToggleCantChange"));
    		return;
    	}
        if (args.length == 1) {
            if (sender instanceof Player) {
                if (hasCommandPermission(sender)) {
                    if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(((Player) sender).getUniqueId())) {
                        FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(((Player) sender).getUniqueId());
                        FurnitureLib.getInstance().getFurnitureManager().removeFurniture((Player) sender);
                        sender.sendMessage(getLHandler().getString("message.FurnitureToggleCMDOff"));
                    } else {
                    	Player player = (Player) sender;
                    	int chunkX = player.getLocation().getBlockX() >> 4, chunkZ = player.getLocation().getBlockZ() >> 4;
                    	FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().remove(player.getUniqueId());
                        Bukkit.getScheduler().runTaskLaterAsynchronously(FurnitureLib.getInstance(), () -> {
                        	FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player, chunkX, chunkZ);
                        }, 5);
                        sender.sendMessage(getLHandler().getString("message.FurnitureToggleCMDOn"));
                    }
                }
            }
        } else if (args.length == 2) {
            if (hasCommandPermission(sender, ".other")) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(getLHandler().getString("message.PlayerNotOnline"));
                    return;
                }
                
                if(player.hasPermission("furniturelib.hidemodels")) {
            		sender.sendMessage(getLHandler().getString("message.FurnitureToggleCantChange"));
            		return;
            	}
                
                if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(player.getUniqueId())) {
                    FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(player.getUniqueId());
                    FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
                    sender.sendMessage(getLHandler().getString("message.FurnitureToggleCMDOff"));
                    if(!sender.getName().equalsIgnoreCase(args[1])) {
                    	player.sendMessage(getLHandler().getString("message.FurnitureToggleCMDOff"));
                    }
                } else {
                	int chunkX = player.getLocation().getBlockX() >> 4, chunkZ = player.getLocation().getBlockZ() >> 4;
                    FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().remove(player.getUniqueId());
                    FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player, chunkX, chunkZ);
                    sender.sendMessage(getLHandler().getString("message.FurnitureToggleCMDOn"));
                    if(!sender.getName().equalsIgnoreCase(args[1])) {
                    	player.sendMessage(getLHandler().getString("message.FurnitureToggleCMDOn"));
                    }
                }
            }
        } else {
            sender.sendMessage(getLHandler().getString("message.WrongArgument"));
            return;
        }
    }
}
