package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;

public class ItemStackV113 extends ItemStackReader{

	private static Method a, asBukkitCopy;
	
	static {
		try {
			a = clazz_nms_item.getMethod("a", clazz_nms_nbt);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ItemStack getItemStack(NBTTagCompound nbt) {
        try {
            byte[] data = NBTCompressedStreamTools.toByte(nbt);
            Object nbtTag = clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data));
            Object nms_item = a.invoke(null, nbtTag);
			return (ItemStack) asBukkitCopy.invoke(null, nms_item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}
