package de.Ste3et_C0st.FurnitureLib.Utilitis.cache;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerCache {

	private HashMap<UUID, DiceOfflinePlayer> offlinePlayerCache = new HashMap<UUID, DiceOfflinePlayer>();
	
	private Function<UUID, OfflinePlayer> offlinePlayerFunction = uuid -> {
		final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		return player != null && player.hasPlayedBefore() ? player : null;
	};
	
	public boolean contains(UUID uuid) {
		return offlinePlayerCache.containsKey(uuid);
	}
	
	public void addPlayer(OfflinePlayer offlinePlayer) {
		if(!contains(offlinePlayer.getUniqueId())) {
			offlinePlayerCache.put(offlinePlayer.getUniqueId(), new DiceOfflinePlayer(offlinePlayer));
		}
	}
	
	public Optional<DiceOfflinePlayer> getPlayer(UUID uuid){
		if(contains(uuid)) {
			return Optional.of(offlinePlayerCache.get(uuid));
		}else {
			OfflinePlayer player = offlinePlayerFunction.apply(uuid);
			if(player != null) {
				DiceOfflinePlayer offlinePlayer = new DiceOfflinePlayer(player);
				this.offlinePlayerCache.put(uuid, offlinePlayer);
				return Optional.ofNullable(offlinePlayer);
			}
			return Optional.empty();
		}
	}
	
	public Optional<DiceOfflinePlayer> getPlayer(String userName){
		return offlinePlayerCache.values().stream().filter(entry -> entry.getName().equalsIgnoreCase(userName)).findFirst();
	}
}
