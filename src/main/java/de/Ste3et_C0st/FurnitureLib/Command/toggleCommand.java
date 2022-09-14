package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurniturePlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

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
            		getLHandler().sendMessage(sender, "message.FurnitureToggleCantChange");
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
                    getLHandler().sendMessage(sender, "message.PlayerNotOnline", new StringTranslator("player", args[1]));
                    return;
                }
                
                this.toggle(sender, player);
            }
        } else {
            getLHandler().sendMessage(sender, "message.WrongArgument");
            return;
        }
    }
   
    private void toggle(CommandSender sender, Player player) {
    	if(FurniturePlayer.wrap(Player.class.cast(sender)).isBedrockPlayer()) {
    		getLHandler().sendMessage(sender, "message.FurnitureToggleCantChange");
    		return;
    	}
    	
    	UUID uuid = player.getUniqueId();
    	String stringUUID = uuid.toString();
    	Predicate<UUID> predicate = entry -> entry.toString().equalsIgnoreCase(stringUUID);
    	Optional<UUID> optinal = getUUIDField(predicate);
    	
    	if(optinal.isPresent() == false) {
    		sendFeedback(sender, player, "message.FurnitureToggleCMDOff");
    		FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
    		FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().add(uuid);
    	}else {
    		sendFeedback(sender, player, "message.FurnitureToggleCMDOn");
			int chunkX = player.getLocation().getBlockX() >> 4, chunkZ = player.getLocation().getBlockZ() >> 4;
            FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player, chunkX, chunkZ);
            FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().removeIf(predicate);
    	}
    }
    
    private Optional<UUID> getUUIDField(Predicate<UUID> predicate){
    	return FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().stream().filter(predicate::test).findFirst();
    }
    
    
    private void sendFeedback(CommandSender sender, Player player, String message) {
    	if(sender.getName().equalsIgnoreCase(player.getName())) {
    		getLHandler().sendMessage(player, message);
    		return;
    	}
    	getLHandler().sendMessage(player, message);
    }
}
