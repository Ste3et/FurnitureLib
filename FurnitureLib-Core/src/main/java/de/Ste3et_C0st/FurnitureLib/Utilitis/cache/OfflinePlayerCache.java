package de.Ste3et_C0st.FurnitureLib.Utilitis.cache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerCache {

	HashSet<DiceOfflinePlayer> offlinePlayerCache = new HashSet<DiceOfflinePlayer>();
	
	public OfflinePlayerCache() {
		Arrays.asList(Bukkit.getOfflinePlayers()).stream().filter(OfflinePlayer::hasPlayedBefore).forEach(player -> {
			offlinePlayerCache.add(new DiceOfflinePlayer(player));
		});
	}
	
	public boolean contains(UUID uuid) {
		return offlinePlayerCache.stream().filter(entry -> entry.getUuid().equals(uuid)).findFirst().isPresent();
	}
	
	public void addPlayer(OfflinePlayer offlinePlayer) {
		if(!contains(offlinePlayer.getUniqueId())) {
			offlinePlayerCache.add(new DiceOfflinePlayer(offlinePlayer));
		}
	}
	
	public Optional<DiceOfflinePlayer> getPlayer(UUID uuid){
		return offlinePlayerCache.stream().filter(entry -> entry.getUuid().equals(uuid)).findFirst();
	}
	
	public Optional<DiceOfflinePlayer> getPlayer(String userName){
		return offlinePlayerCache.stream().filter(entry -> entry.getName().equalsIgnoreCase(userName)).findFirst();
	}
	
}
