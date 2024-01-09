package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;

public class ItemStackV120_1 extends ItemStackReader{

	private static Class<?> nmsNBTReadLimiter;
	private static Method a, asBukkitCopy;
	
	static {
		try {
			nmsNBTReadLimiter = Class.forName(getNbtFolder() + ".NBTReadLimiter");
			a = clazz_nms_item.getMethod("a", clazz_nms_nbt);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
			clazz_nbttools_method_a_input = clazz_nbttools.getMethod("a", InputStream.class, nmsNBTReadLimiter);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ItemStack getItemStack(NBTTagCompound nbt) {
        try {
            Object nms_item = a.invoke(null, convertCompound(nbt));
			return (ItemStack) asBukkitCopy.invoke(null, nms_item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	@Override
	public Object convertCompound(NBTTagCompound nbtTagCompound) throws Exception {
		byte[] data = NBTCompressedStreamTools.toByte(nbtTagCompound);
        return  clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data), nmsNBTReadLimiter.getMethod("a").invoke(null));
	}
	
}
