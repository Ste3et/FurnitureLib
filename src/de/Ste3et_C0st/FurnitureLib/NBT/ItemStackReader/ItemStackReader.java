package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTReadLimiter;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public abstract class ItemStackReader {

	protected static Class<?> clazz_obc_CraftItemStack, clazz_nms_nbt, clazz_nbttools, clazz_nms_item;
	private static Method asNMSCopy, method_save;
	protected static Method clazz_nbttools_method_a_output, clazz_nbttools_method_a_input;
	
	static {
		try {
			clazz_obc_CraftItemStack = Class.forName("org.bukkit.craftbukkit." + FurnitureLib.getBukkitVersion() + ".inventory.CraftItemStack");
			clazz_nms_nbt  = Class.forName("net.minecraft.server." + FurnitureLib.getBukkitVersion() + ".NBTTagCompound");
			clazz_nbttools = Class.forName("net.minecraft.server." + FurnitureLib.getBukkitVersion() + ".NBTCompressedStreamTools");
			clazz_nms_item = Class.forName("net.minecraft.server." + FurnitureLib.getBukkitVersion() + ".ItemStack");
			
			asNMSCopy = clazz_obc_CraftItemStack.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
			method_save = clazz_nms_item.getMethod("save", clazz_nms_nbt);
			clazz_nbttools_method_a_input = clazz_nbttools.getMethod("a", InputStream.class);
			clazz_nbttools_method_a_output = clazz_nbttools.getMethod("a", clazz_nms_nbt, OutputStream.class);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public abstract ItemStack getItemStack(NBTTagCompound nbtTagCompound);
	
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
