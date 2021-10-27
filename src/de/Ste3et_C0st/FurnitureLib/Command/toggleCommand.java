package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurniturePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            	if(FurniturePlayer.wrap(Player.class.cast(sender)).isBedrockPlayer()) {
            		sender.sendMessage(getLHandler().getString("message.FurnitureToggleCantChange"));
            		return;
            	}
            	
                if (hasCommandPermission(sender)) {
                	this.toggle(sender, Player.class.cast(sender));
                }
            }
        } else if (args.length == 2) {
            if (hasCommandPermission(sender, ".other")) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(getLHandler().getString("message.PlayerNotOnline", new StringTranslator("#PLAYER#", args[1])));
                    return;
                }
                
                this.toggle(sender, player);
            }
        } else {
            sender.sendMessage(getLHandler().getString("message.WrongArgument"));
            return;
        }
    }
   
    private void toggle(CommandSender sender, Player player) {
    	if(FurniturePlayer.wrap(Player.class.cast(sender)).isBedrockPlayer()) {
    		sender.sendMessage(getLHandler().getString("message.FurnitureToggleCantChange"));
    		return;
    	}
    	
    	UUID uuid = player.getUniqueId();
    	String stringUUID = uuid.toString();
    	Optional<UUID> optinal = getUUIDField(stringUUID);
    	
    	FurnitureLib.debug("FurnitureToggle Debug [#1]", 100);
    	FurnitureLib.debug("Optional UUID is Present " + optinal.isPresent(), 100);
    	
    	if(optinal.isPresent() == false) {
    		sendFeedback(sender, player, "message.FurnitureToggleCMDOff");
    		FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
    		FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(uuid);
    		FurnitureLib.debug("FurnitureToggle Entry add: " + getUUIDField(stringUUID).isPresent(), 100);
    	}else {
    		sendFeedback(sender, player, "message.FurnitureToggleCMDOn");
			int chunkX = player.getLocation().getBlockX() >> 4, chunkZ = player.getLocation().getBlockZ() >> 4;
            FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player, chunkX, chunkZ);
            FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().remove(uuid);
            FurnitureLib.debug("FurnitureToggle Entry removed: " + !getUUIDField(stringUUID).isPresent(), 100);
    	}
    }
    
    private Optional<UUID> getUUIDField(String uuid){
    	return FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().stream().filter(entry -> entry.toString().equalsIgnoreCase(uuid)).findFirst();
    }
    
    
    private void sendFeedback(CommandSender sender, Player player, String message) {
    	if(sender.getName().equalsIgnoreCase(player.getName())) {
    		player.sendMessage(getLHandler().getString(message));
    		return;
    	}
    	player.sendMessage(getLHandler().getString(message));
    }
}
