package de.Ste3et_C0st.FurnitureLib.Utilitis.cache;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class DiceOfflinePlayer {

	private OfflinePlayer offlinePlayer;
	private final UUID uuid;
	private String name;
	private long lastSeen;
	
	public DiceOfflinePlayer(OfflinePlayer offlinePlayer) {
		this.offlinePlayer = offlinePlayer;
		this.uuid = offlinePlayer.getUniqueId();
		this.name = offlinePlayer.getName();
		this.setLastSeen(offlinePlayer.getLastPlayed());
	}

	public String getName() {
		return name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public OfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}
	
	public boolean isOnline() {
		return offlinePlayer.isOnline();
	}
	
	public void update(OfflinePlayer player) {
		this.name = player.getName();
		this.lastSeen = player.getLastPlayed();
		this.offlinePlayer = player;
	}
	
}
