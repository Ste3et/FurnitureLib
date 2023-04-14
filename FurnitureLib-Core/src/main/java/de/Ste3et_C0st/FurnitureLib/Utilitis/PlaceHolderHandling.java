package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceHolderHandling {

	public String parsePlaceholders(final String input, final CommandSender sender) {
		return Player.class.isInstance(sender) ? PlaceholderAPI.setPlaceholders((OfflinePlayer) sender, input) : input;
	}
	
}
