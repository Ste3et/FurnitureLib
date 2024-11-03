package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTReadLimiter;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ItemStackV113 extends ItemStackReader{

	private static Method a, asBukkitCopy, method_save;
	
	static {
		try {
			a = clazz_nms_item.getMethod("a", clazz_nms_nbt);
			clazz_nbttools_method_a_input = clazz_nbttools.getMethod("a", InputStream.class);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
			method_save = clazz_nms_item.getMethod(FurnitureLib.getVersionInt() > 17 ? "b" : "save", clazz_nms_nbt);
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
		byte[] data = NBTCompressedStreamTools.toByte(this.convertMaterial(nbtTagCompound));
		return clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data));
	}
	
	public NBTTagCompound getNBTTag(ItemStack is) throws Exception {		
        try {
            Object nms_item = asNMSCopy.invoke(null, is);
            Object nms_nbt = clazz_nms_nbt.newInstance();
            method_save.invoke(nms_item, nms_nbt);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            clazz_nbttools_method_a_output.invoke(null, nms_nbt, os);
            byte[] out = os.toByteArray();
			return NBTCompressedStreamTools.read(out, NBTReadLimiter.unlimited);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
}
