package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AdventureHandling {

	private BukkitAudiences adventure = null;
	
	public AdventureHandling(Plugin plugin) {
		this.adventure = BukkitAudiences.create(plugin);
	}
	
	public void sendMessage(CommandSender sender, Component component) {
		if(sender instanceof Player) {
    		this.adventure.player(Player.class.cast(sender)).sendMessage(component);
    	}else if(sender instanceof ConsoleCommandSender) {
    		this.adventure.console().sendMessage(component);
    	}else {
        	sender.sendMessage("hmm");
    	}
    }

	public void sendActionBar(CommandSender sender, Component component) {
		if(FurnitureLib.getVersionInt() < 16) {
			final String legacyString = LegacyComponentSerializer.legacySection().serialize(component);
			component = LegacyComponentSerializer.legacySection().deserialize(legacyString);
		}
		
		if(sender instanceof Player) {
    		this.adventure.player(Player.class.cast(sender)).sendActionBar(component);
    	}else if(sender instanceof ConsoleCommandSender) {
    		this.adventure.console().sendActionBar(component);
    	}else {
        	sender.sendMessage("hmm");
    	}
	}
	
	public void close() {
    	if(this.adventure != null) {
    	    this.adventure.close();
    	    this.adventure = null;
    	}
    }
	
	public void sendConsoleMessage(Component component) {
		this.adventure.console().sendMessage(component);
	}
	
}
