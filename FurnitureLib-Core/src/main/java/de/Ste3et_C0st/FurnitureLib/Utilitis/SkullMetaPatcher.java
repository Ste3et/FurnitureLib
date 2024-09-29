package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class SkullMetaPatcher {

	private static boolean needPatcher = FurnitureLib.isVersionOrAbove("1.20.4") ? true : FurnitureLib.isPaper() == false;
	
	public static ItemStack patchStack(ItemStack stack) {
		if(needPatcher == false) return stack;
		if(Objects.isNull(stack)) return stack;
		if(stack.getItemMeta() instanceof SkullMeta) {
			try {
				final Object craftBukkitMeta = MinecraftReflection.getCraftBukkitClass("inventory.CraftMetaSkull").cast(stack.getItemMeta());
				final Field profileField = MinecraftReflection.getCraftBukkitClass("inventory.CraftMetaSkull").getDeclaredField("profile");
				profileField.setAccessible(true);
				final Object savedObject = profileField.get(craftBukkitMeta);
				final WrappedGameProfile profile = WrappedGameProfile.fromHandle(savedObject);
				//System.out.println(profile.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stack;
	}
	
	public static ItemStack patch(ItemStack stack, NBTTagCompound compound) {
		if(needPatcher) {
    		if(compound.hasKeyOfType("tag", 10)) {
        		NBTTagCompound tagCompound = compound.getCompound("tag");
        		if(tagCompound.hasKeyOfType("SkullOwner", 10)) {
        			NBTTagCompound skullCompound = tagCompound.getCompound("SkullOwner");
        			if(skullCompound.hasKeyOfType("Properties", 10)) {
        				NBTTagCompound propertiesCompound = skullCompound.getCompound("Properties");
        				if(propertiesCompound.hasKeyOfType("textures", 9)) {
        					NBTTagList textureCompound = propertiesCompound.getList("textures");
        					NBTTagCompound texturestring = textureCompound.get(0);
        					String base64String = texturestring.getString("Value");
        					WrappedGameProfile gameProfile = makeProfile(base64String);
        					patch(stack, gameProfile);
        				}
        			}
        		}
        	}
    	}	
		return stack;
	}
	
	public static ItemStack patch(ItemStack stack, WrappedGameProfile gameProfile) {
		ItemMeta headMeta = stack.getItemMeta();
		try {
			
			Field field = headMeta.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			
			if(field.getType().getSimpleName().contains("ResolvableProfile")) {
				Object object = Class.forName("net.minecraft.world.item.component.ResolvableProfile").getConstructor(gameProfile.getHandleType()).newInstance(gameProfile.getHandle());
				field.set(headMeta, object);
			}else {
				field.set(headMeta, gameProfile.getHandle());
			}
		   
		} catch (Exception e) {
		    e.printStackTrace();
		}  finally {
			stack.setItemMeta(headMeta);
		}
		return stack;
	}
	
	public static boolean shouldPatch() {
		return needPatcher;
	}
	
    public static WrappedGameProfile makeProfile(String b64) {
		UUID id = new UUID(
				b64.substring(b64.length() - 20).hashCode(),
				b64.substring(b64.length() - 10).hashCode()
		);
		WrappedGameProfile profile = new WrappedGameProfile(id, "Player");
		profile.getProperties().put("textures", new WrappedSignedProperty("textures", b64, "furniture"));
		return profile;
	}
    
    public static ItemStack createStack(String b64) {
    	ItemStack stack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
    	patch(stack, makeProfile(b64));
    	return stack;
    }
	
}
