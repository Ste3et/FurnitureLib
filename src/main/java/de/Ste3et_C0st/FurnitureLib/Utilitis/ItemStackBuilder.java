package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder {

	private ItemStack stack = null;
	
	public ItemStackBuilder(Material mat) {
		stack = new ItemStack(mat);
	}
	
	public ItemStackBuilder setName(String str) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(str);
		stack.setItemMeta(meta);
		return this;
	}
	
	public ItemStackBuilder setLore(String ...str) {
		return setLore(Arrays.asList(str));
	}
	
	public ItemStackBuilder setLore(List<String> loreList) {
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(loreList);
		stack.setItemMeta(meta);
		return this;
	}
	
	public ItemStackBuilder setAmount(int amount) {
		stack.setAmount(amount);
		return this;
	}
	
	public ItemStackBuilder setDurability(short durability) {
		stack.setDurability(durability);
		return this;
	}
	
	public ItemStackBuilder setType(Material mat) {
		stack.setType(mat);
		return this;
	}
	
	public ItemMeta getMeta() {
		return this.stack.getItemMeta();
	}
	
	public ItemStackBuilder setMeta(ItemMeta itemMeta) {
		this.stack.setItemMeta(itemMeta);
		return this;
	}
	
	public ItemStack build() {
		return this.stack;
	}
	
	public static ItemStackBuilder of(Material material) {
		return of(new ItemStack(material));
	}
	
	public static ItemStackBuilder of(String string) {
		return of(Material.valueOf(string));
	}
	
	public static ItemStackBuilder of(ItemStack stack) {
		final ItemStackBuilder builder = new ItemStackBuilder(stack.getType());
		builder.setMeta(stack.getItemMeta());
		return builder;
	}
}
