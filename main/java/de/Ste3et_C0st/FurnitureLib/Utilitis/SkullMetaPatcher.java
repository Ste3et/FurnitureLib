package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class SkullMetaPatcher {

	private final static Class<?> skullMetaClass;
	private static Field profileField;
	
	static {
		skullMetaClass = SkullMeta.class;
		try {
			profileField = skullMetaClass.getDeclaredField("profile");
			profileField.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static ItemStack convertStack(ItemStack stack) {
		if(stack.getType() == Material.PLAYER_HEAD) {
			if(stack.hasItemMeta()) {
				
			}
		}
		return stack;
	}
	
    static void sanitizeUUID(SkullMeta skullMeta) {
    	
    	try {
    		Field profileField = skullMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
    		
    		WrappedGameProfile wrappedProfile = WrappedGameProfile.fromHandle(profileField.get(skullMeta));
    		
    		if(Objects.nonNull(wrappedProfile)) {
    			
    			profileField.set(skullMeta, wrappedProfile.getHandle());
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
     }
}
