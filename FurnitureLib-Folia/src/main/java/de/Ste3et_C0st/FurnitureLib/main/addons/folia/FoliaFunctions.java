package de.Ste3et_C0st.FurnitureLib.main.addons.folia;

import java.util.List;
import java.util.Objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.addons.Modulnterface;
import de.Ste3et_C0st.FurnitureLib.main.addons.ServerFunction;
import net.md_5.bungee.api.chat.BaseComponent;

public class FoliaFunctions implements Modulnterface, ServerFunction{

	@Override
	public void onLoad() {
		FurnitureLib.debug("FoliaFunctions created");
	}
	
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
	public ItemMeta setDisplayName(ItemMeta meta, BaseComponent[] baseComponent) {
		if(Objects.nonNull(meta)) {
			meta.setDisplayNameComponent(baseComponent);
		}
		return meta;
	}

	@Override
	public ItemMeta setLore(ItemMeta meta, List<BaseComponent[]> component) {
		if(Objects.nonNull(meta)) {
			meta.setLoreComponents(component);
		}
		return meta;
	}

	@Override
	public BaseComponent[] displayName(ItemStack stack) {
		return stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayNameComponent() : null;
	}

}
