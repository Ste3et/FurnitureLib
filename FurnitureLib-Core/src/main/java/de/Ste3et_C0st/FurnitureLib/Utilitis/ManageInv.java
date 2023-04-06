package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class ManageInv{

	/**
	 * Use FurnitureLib.getInstance().getInventoryManager().openInventory(String key, Object ... objects);
	 * @Deprecated
	 **/
	@Deprecated
	public ManageInv(Player player, ObjectID obj){
		FurnitureLib.getInstance().getInventoryManager().openInventory("manage", player, obj);
	}
}
