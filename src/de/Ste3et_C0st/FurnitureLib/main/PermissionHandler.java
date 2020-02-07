package de.Ste3et_C0st.FurnitureLib.main;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionHandler {

    public boolean VaultInstalled = false;
    private Permission permission = null;

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

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

}
