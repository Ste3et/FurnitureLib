package de.Ste3et_C0st.FurnitureLib.Paper;

import java.util.List;
import java.util.Objects;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ServerFunction;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.md_5.bungee.api.chat.BaseComponent;

public class PaperFunctions implements ServerFunction {

	@Override
	public void onEnable() {
		FurnitureLib.debug("PaperFunctions created");
	}
	
	@Override
	public ItemStack displayName(ItemStack stack, BaseComponent[] baseComponent) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayNameComponent(baseComponent);
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public ItemStack lore(ItemStack stack, List<BaseComponent[]> minimassageList) {
		ItemMeta meta = stack.getItemMeta();
		meta.setLoreComponents(minimassageList);
		stack.setItemMeta(meta);
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
