package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionHandler {

    public boolean VaultInstalled = false;
    private net.milkbowl.vault.permission.Permission permission = null;

    public PermissionHandler() {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            VaultInstalled = true;
            setupPermissions();
        }
    }

    public boolean hasPerm(CommandSender sender, String str) {
        if (sender == null) return true;
        if (str == null || str.isEmpty()) return true;

        if (sender.isOp()) return true;
        str = str.toLowerCase();
        if (!VaultInstalled) {
            if (sender.hasPermission("furniture.admin")) return true;
            return sender.hasPermission(str);
        } else {
            if (permission.has(sender, "furniture.admin")) return true;
            return permission.has(sender, str);
        }
    }
    
    public boolean hasPermRaw(CommandSender sender, String str) {
    	if (sender == null) return true;
        if (str == null || str.isEmpty()) return true;
        if (VaultInstalled == false) {
        	return sender.hasPermission(str);
        }else {
        	return permission.has(sender, str);
        }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public static boolean registerPermission(String name) {
    	try {
			Bukkit.getPluginManager().addPermission(new Permission(name));
			return true;
		}catch (Exception e) {
			return false;
		}
	}
    
    public static void registerPermission(String name, PermissionDefault defaultPerm) {
    	Bukkit.getPluginManager().addPermission(new Permission(name, defaultPerm));
    }
    
    public static boolean registerPermission(String parentPermission, String name) {
    	try {
    		Permission permission = new Permission(name);
			permission.addParent(parentPermission, true);
			Bukkit.getPluginManager().addPermission(permission);
			return true;
		}catch (Exception e) {
			return false;
		}
    }
}
