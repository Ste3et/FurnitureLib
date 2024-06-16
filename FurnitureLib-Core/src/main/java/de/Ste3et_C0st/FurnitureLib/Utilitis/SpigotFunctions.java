package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

public class SpigotFunctions implements ServerFunction{

	private static final LegacyComponentSerializer COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection().toBuilder().useUnusualXRepeatedCharacterHexFormat().hexColors().build();
	
	@Override
	public void onEnable() {
		FurnitureLib.debug("SpigotFunctions created");
	}
	
	@Override
	public ItemStack displayName(ItemStack stack, BaseComponent[] component) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(convertToLegacy(BungeeComponentSerializer.get().deserialize(component)));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public ItemStack lore(ItemStack stack, List<BaseComponent[]> component) {
		List<String> legacyLore = new ArrayList<String>();
		if(stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
			legacyLore.addAll(stack.getItemMeta().getLore());
		}
		legacyLore.addAll(component.stream().map(BungeeComponentSerializer.get()::deserialize).map(this::convertToLegacy).collect(Collectors.toList()));
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(legacyLore);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private String convertToLegacy(Component component) {
		return COMPONENT_SERIALIZER.serialize(component);
	}

	@Override
	public ItemMeta setDisplayName(ItemMeta meta, BaseComponent[] baseComponent) {
		if(Objects.nonNull(meta)) {
			meta.setDisplayName(convertToLegacy(BungeeComponentSerializer.get().deserialize(baseComponent)));
		}
		return meta;
	}

	@Override
	public ItemMeta setLore(ItemMeta meta, List<BaseComponent[]> component) {
		if(Objects.nonNull(meta)) {
			List<String> legacyLore = component.stream().map(BungeeComponentSerializer.get()::deserialize).map(this::convertToLegacy).collect(Collectors.toList());
			meta.setLore(legacyLore);
		}
		return meta;
	}

}
