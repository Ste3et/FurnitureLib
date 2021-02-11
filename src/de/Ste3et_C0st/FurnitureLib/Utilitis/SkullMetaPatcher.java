package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class SkullMetaPatcher {

	private static Class<?> craftSkullMeta;
	private static Field profileField;
	
	static {
		try {
			craftSkullMeta = Class.forName("org.bukkit.craftbukkit." + FurnitureLib.getBukkitVersion() + ".inventory");
			profileField = craftSkullMeta.getField("profile");
			profileField.setAccessible(true);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ItemStack convertStack(ItemStack stack) {
		if(stack.getType() == Material.PLAYER_HEAD) {
			if(stack.hasItemMeta()) {
				SkullMeta meta = (SkullMeta) stack.getItemMeta();
				Object obcSkullMeta = craftSkullMeta.cast(meta);
				try {
					WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromHandle(profileField.get(obcSkullMeta));
					Collection<WrappedSignedProperty> property = wrappedGameProfile.getProperties().get("texture");
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return stack;
	}
	
}
