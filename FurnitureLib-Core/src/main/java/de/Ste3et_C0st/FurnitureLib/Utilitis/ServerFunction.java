package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;

public interface ServerFunction {
	public ItemStack displayName(ItemStack stack, BaseComponent[] baseComponent);
	public ItemStack lore(ItemStack stack, List<BaseComponent[]> component);
	public void onEnable();
}
