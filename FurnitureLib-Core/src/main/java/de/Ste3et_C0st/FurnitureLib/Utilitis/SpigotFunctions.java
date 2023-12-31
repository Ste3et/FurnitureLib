package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

public class SpigotFunctions implements ServerFunction{

	@Override
	public ItemStack displayName(ItemStack stack, BaseComponent[] component) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(convertToLegacy(BungeeComponentSerializer.get().deserialize(component)));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public ItemStack lore(ItemStack stack, List<BaseComponent[]> component) {
		List<String> legacyLore = component.stream().map(BungeeComponentSerializer.get()::deserialize).map(this::convertToLegacy).collect(Collectors.toList());
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(legacyLore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private String convertToLegacy(Component component) {
		return LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

	@Override
	public void onEnable() {
		FurnitureLib.debug("SpigotFunctions created");
	}

}
