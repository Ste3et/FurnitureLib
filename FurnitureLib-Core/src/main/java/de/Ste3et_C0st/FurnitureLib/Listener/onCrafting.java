package de.Ste3et_C0st.FurnitureLib.Listener;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class onCrafting implements Listener {

    private FurnitureManager manager = FurnitureManager.getInstance();

    @EventHandler
    private void onCraftingEvent(PrepareItemCraftEvent event) {
        if (manager.getProjects().isEmpty()) {
            return;
        }
        final Player player = FurnitureLib.isVersionOrAbove("1.21") ? (Player) event.getView().getPlayer() : getPlayerFromView(event);
        if (player == null) return;
        if (player.isOp()) return;
        if (event.getInventory() == null) return;
        if (event.getInventory().getResult() == null) return;
        ItemStack is = event.getInventory().getResult().clone();
        is.setAmount(1);
        for (Project pro : manager.getProjects()) {
            if (pro == null) continue;
            if (pro.getCraftingFile() == null) continue;
            if (pro.getCraftingFile().getRecipe() == null) continue;
            if (pro.getCraftingFile().getRecipe().getResult() == null) continue;
            if (is.equals(pro.getCraftingFile().getRecipe().getResult())) {
                if (!hasPermissions(player, pro.getSystemID())) {
                	event.getInventory().setResult(null);
                }
            }
        }
    }
    
    private Player getPlayerFromView(InventoryEvent event) {
    	try {
    		Object inventoryView = event.getView();
    		Method getPlayerMethod = inventoryView.getClass().getMethod("getPlayer");
    		getPlayerMethod.setAccessible(true);
    		return (Player) getPlayerMethod.invoke(inventoryView);
    	}catch (Exception e) {
    		return null;
		}
    }

    private boolean hasPermissions(Player p, String name) {
        if (p.isOp()) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.admin")) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.player")) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.craft.*")) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.craft." + name)) return true;
        if (FurnitureLib.getInstance().getPermissionList() != null) {
            for (String s : FurnitureLib.getInstance().getPermissionList().keySet()) {
                if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.craft.all." + s)) {
                    if (FurnitureLib.getInstance().getPermissionList().get(s).contains(name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
