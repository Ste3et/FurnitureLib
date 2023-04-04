package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AdventureHandling {

	private BukkitAudiences adventure = null;
	
	public AdventureHandling(Plugin plugin) {
		this.adventure = BukkitAudiences.create(plugin);
	}
	
	public void sendMessage(CommandSender sender, Component component) {
    	if(sender instanceof Player player) {
    		
    		if(FurnitureLib.getVersionInt() < 16) {
    			final String legacyString = LegacyComponentSerializer.legacySection().serialize(component);
    			sender.sendMessage(legacyString);
    			return;
    		}
    		
    		this.adventure.player(player).sendMessage(component);
    	}else if(sender instanceof ConsoleCommandSender console) {
    		final Audience audience = this.adventure.console();
    		
    		if(FurnitureLib.getVersionInt() < 16) {
    			final String legacyString = LegacyComponentSerializer.legacySection().serialize(component);
    			sender.sendMessage(legacyString);
    			return;
    		}
    		
    		audience.sendMessage(component);
    	}else {
        	sender.sendMessage("");
    	}
    }
	
	public void close() {
    	if(this.adventure != null) {
    	    this.adventure.close();
    	    this.adventure = null;
    	}
    }
	
}
