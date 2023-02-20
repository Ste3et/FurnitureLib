package de.Ste3et_C0st.ProtectionLibRework.main;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.ProtectionLib.main.ProtectionClass;
import de.Ste3et_C0st.ProtectionLib.main.protectionObj;

public class ProtectionLib {

	private static ProtectionLib INSTANCE;
	private FileConfiguration newConfig = null;
	private boolean isVaultEnable = false;
	
	//private ProtectionVaultPermission permissions = null;
	private final List<UUID> playerList = Lists.newArrayList();
	private final List<ProtectionClass> protectList = Lists.newArrayList();
	private final List<protectionObj> protectionClass = Lists.newArrayList();
	private final File folder = new File("plugins/ProtectionLib");
	private final File configFile = new File(folder, "config.yml");
	
	public ProtectionLib() {
		INSTANCE = this;
		getLogger().info("includet ProtectionLib started");
		this.reloadConfig();
	}
	
	public static ProtectionLib getInstance() {
		return INSTANCE;
	}
	
	public FileConfiguration getConfig() {
		if (newConfig == null) {
            reloadConfig();
        }
        return newConfig;
	}
	
	public void reloadConfig() {
		if(folder.exists() == false) folder.mkdirs();
		
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = FurnitureLib.getInstance().getResource("protectionLibconfig.yml");
        if (defConfigStream == null) {
            return;
        }

        newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }
	
	public boolean canBuild(Location loc, Player player){
		if(hasPermissions(player)) return true;
		return canBuild(loc, player, player);
	}
	
	public boolean canBuild(Location loc, Player player, Player sender){
		if(playerList.contains(sender.getUniqueId())) {
			if(getWatchers().isEmpty()) {
				player.sendMessage("§c§lProtectionLib is not hooked to any Plugin !");
			}else {
				protectionClass.stream().forEach(protection -> {
					if(protection.isEnabled()) {
						player.sendMessage("§f[§6canBuild§f->§a"+ protection.getClass().getSimpleName()+"§f] " + protection.getPlugin().getName() + ": " + protection.canBuild(player, loc));
						getLogger().log(Level.INFO, "ProtectionLib canBuild->" + protection.getClass().getSimpleName() + ": " + protection.canBuild(player, loc) + " for " + player.getName());
					}else {
						player.sendMessage("§f[§6canBuild§f->§c"+ protection.getClass().getSimpleName()+"§f] " + protection.getPlugin().getName() + ": §cdisabled");
					}
				});
			}
		}
		return !this.protectionClass.stream().filter(protectionObj::isEnabled).filter(protection -> protection.canBuild(player, loc) == false).findFirst().isPresent();
	}
	
	public boolean isOwner(Location loc, Player player){
		if(hasPermissions(player)) return true;
		return isOwner(loc, player, player);
	}
	
	public boolean isOwner(Location loc, Player player, Player sender){
		if(playerList.contains(sender.getUniqueId())) {
			if(getWatchers().isEmpty()) {
				player.sendMessage("§c§lProtectionLib is not hooked to any Plugin !");
			}else {
				protectionClass.stream().forEach(protection -> {
					if(protection.isEnabled()) {
						sender.sendMessage("§f[§6isOwner§f->§a"+protection.getClass().getSimpleName()+"§f] " +protection.getPlugin().getName() + ": " + protection.isOwner(player, loc));
						getLogger().log(Level.INFO, "ProtectionLib canBuild->" + protection.getClass().getSimpleName() + ": " + protection.isOwner(player, loc) + " for " + player.getName());
					}else {
						sender.sendMessage("§f[§6isOwner§f->§c"+protection.getClass().getSimpleName()+"§f] " +protection.getPlugin().getName() + ": §cdisabled");
					}
				});
			}
		}
		return !this.protectionClass.stream().filter(protectionObj::isEnabled).filter(protection -> protection.isOwner(player, loc) == false).findFirst().isPresent();
	}
	
	public boolean isProtectedRegion(Location location) {
		return this.protectionClass.stream().filter(protectionObj::isEnabled).filter(protection -> protection.isProtectedRegion(location)).findFirst().isPresent();
	}
	
	public boolean hasPermissions(Player p){
		if(p.isOp()) return true;
		return p.hasPermission("protectionlib.admin");
		//return isVaultEnable ? permissions.permission.has(p, "ProtectionLib.admin") : p.hasPermission("ProtectionLib.admin");
	}
	
	public List<protectionObj> getWatchers(){
		return this.protectionClass;
	}
	
	public Logger getLogger() {
		return Bukkit.getLogger();
	}
}
