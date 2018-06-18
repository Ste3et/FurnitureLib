package de.Ste3et_C0st.FurnitureLib.Events.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public class onCrafting implements Listener{
	
	private FurnitureManager manager = FurnitureManager.getInstance();
	
	@EventHandler
	private void onCraftingEvent(PrepareItemCraftEvent e){
		if(manager.getProjects().isEmpty()){return;}
		Player p = (Player) e.getView().getPlayer();
		if(p.isOp()) return;
		if(e.getInventory()==null) return;
		if(e.getInventory().getResult()==null) return;
		ItemStack is = e.getInventory().getResult().clone();
		is.setAmount(1);
		for(Project pro : manager.getProjects()){
			if(is.equals(pro.getCraftingFile().getRecipe().getResult())){
				if(!hasPermissions(p, pro.getSystemID())){
					e.getInventory().setResult(null);
				}
			}
		}
	}
	
	private boolean hasPermissions(Player p, String name) {
		if(p.isOp()) return true;
 		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.admin")) return true;
 		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.player")) return true;
 		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.craft.*")) return true;
 		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.craft." + name)) return true;
 		if(FurnitureLib.getInstance().getPermissionList()!=null){
 			for(String s : FurnitureLib.getInstance().getPermissionList().keySet()){
 				if(FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.craft.all." + s)){
 					if(FurnitureLib.getInstance().getPermissionList().get(s).contains(name)){
 						return true;
 					}
 				}
 			}
 		}
 		return false;
}
}
