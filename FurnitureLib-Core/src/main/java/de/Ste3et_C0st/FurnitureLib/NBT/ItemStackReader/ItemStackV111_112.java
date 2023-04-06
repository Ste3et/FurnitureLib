package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;

public class ItemStackV111_112 extends ItemStackReader{

	private static Method asBukkitCopy;
	private static Constructor<?> constructor_nms_item;
	
	static {
		try {
			constructor_nms_item = clazz_nms_item.getConstructor(clazz_nms_nbt);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ItemStack getItemStack(NBTTagCompound nbt) {
        try {
            byte[] data = NBTCompressedStreamTools.toByte(nbt);
            Object mns_nbt = clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data));
            Object nms_item = constructor_nms_item.newInstance(mns_nbt);
			return (ItemStack) asBukkitCopy.invoke(null, nms_item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}