package de.Ste3et_C0st.ProtectionLibRework.main;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.ProtectionLib.main.ProtectionLib;

public abstract class protectionObj {

	private Player p;
	private Location loc;
	private Plugin pl;
	private boolean isEnabled = true;
	
	/**
	 * Hooks into a new ProtectionPlugin
	 * @param plugin
	 */
	public protectionObj(Plugin plugin){
		this.pl = plugin;
		ProtectionLib.getInstance().getConfig().addDefault("config." + getPlugin().getName() + ".enabled", true);
	}
	
	/**
	 * Get the plugin who is hooked in
	 * @return The plugin who is hooked in
	 */
	public Plugin getPlugin(){
		return pl;
	}
	
	
	/**
	 * Checks the Building rights on the location for the player
	 * @param player
	 * @param loc
	 * @return a boolean if the player can build on a Location
	 */
	public abstract boolean canBuild(Player player, Location loc);
	/**
	 * Checks if the player is the Owner of a region
	 * @param player
	 * @param loc
	 * @return a boolean if the player is an owner of the region
	 */
	public abstract boolean isOwner(Player player, Location loc);
	
	/**
	 * Check if is a protected region is present
	 */
	public abstract boolean isProtectedRegion(Location loc);

	/**
	 * Get the Player of the object
	 * @return the Player of the object
	 */
	@Deprecated
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Set the Player of the object
	 * @param p
	 */
	@Deprecated
	public void setPlayer(Player p) {
		this.p = p;
	}

	/**
	 * Get the Location of the object
	 * @return the Location of the Object
	 */
	@Deprecated
	public Location getLocation() {
		return loc;
	}

	/**
	 * Set the location of the object
	 * @param loc
	 */
	@Deprecated
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	public boolean canSpawn(Player player, Location location) {
		return true;
	}
	
	public boolean registerFlag(Plugin plugin, String string, boolean bool) {
		return false;
	}
	
	public boolean queryFlag(String string, Player player, Location location) {return true;}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
	
	public void update() {
		isEnabled = ProtectionLib.getInstance().getConfig().getBoolean("config." + getPlugin().getName() + ".enabled", true);
	}
}
