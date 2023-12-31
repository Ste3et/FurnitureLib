package de.Ste3et_C0st.FurnitureLib.Folia;

import java.util.List;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ServerFunction;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.md_5.bungee.api.chat.BaseComponent;

public class FoliaFunctions implements ServerFunction{

	@Override
	public ItemStack displayName(ItemStack stack, BaseComponent[] baseComponents) {
		stack.editMeta(meta -> meta.setDisplayNameComponent(baseComponents));
		return stack;
	}

	@Override
	public ItemStack lore(ItemStack stack, List<BaseComponent[]> baseComponents) {
		stack.editMeta(meta -> meta.setLoreComponents(baseComponents));
		return stack;
	}

	@Override
	public void onEnable() {
		FurnitureLib.debug("FoliaFunctions created");
	}

}
