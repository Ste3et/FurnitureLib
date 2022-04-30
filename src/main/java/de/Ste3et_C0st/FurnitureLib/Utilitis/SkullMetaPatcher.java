package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;

public class SkullMetaPatcher {

	private static boolean needPatcher = false;
	
	static {
		needPatcher = Bukkit.getServer().getName().toLowerCase().contains("paper") == false;
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
        					ItemMeta headMeta = stack.getItemMeta();
        					NBTTagList textureCompound = propertiesCompound.getList("textures");
        					NBTTagCompound texturestring = textureCompound.get(0);
        					String base64String = texturestring.getString("Value");
        					WrappedGameProfile gameProfile = makeProfile(base64String);
        					try {
        					    Field profileField = headMeta.getClass().getDeclaredField("profile");
        					    profileField.setAccessible(true);
        					    profileField.set(headMeta, gameProfile.getHandle());
        					} catch (NoSuchFieldException | SecurityException e) {
        					    e.printStackTrace();
        					} catch (IllegalArgumentException | IllegalAccessException e) {
        					    e.printStackTrace();
        					} finally {
        						stack.setItemMeta(headMeta);
        					}
        				}
        			}
        		}
        	}
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
	
}
