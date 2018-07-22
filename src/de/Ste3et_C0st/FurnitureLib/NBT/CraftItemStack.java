package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class CraftItemStack {

	public NBTTagCompound getNBTTag(ItemStack is) throws Exception{
		try{
			Class<?> clazz_cis = Class.forName("org.bukkit.craftbukkit."+FurnitureLib.getInstance().getBukkitVersion()+".inventory.CraftItemStack");
			Object nms_item = clazz_cis.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class).invoke(null, is);
			Object nms_nbt = Class.forName("net.minecraft.server."+FurnitureLib.getInstance().getBukkitVersion()+".NBTTagCompound").newInstance();
			nms_item.getClass().getMethod("save", nms_nbt.getClass()).invoke(nms_item, nms_nbt);
			Class<?> clazz_nbttools =  Class.forName("net.minecraft.server."+FurnitureLib.getInstance().getBukkitVersion()+".NBTCompressedStreamTools");
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			clazz_nbttools.getMethod("a", nms_nbt.getClass(),OutputStream.class).invoke(null, nms_nbt,(OutputStream)os);
			byte[] out = os.toByteArray();
			NBTTagCompound c = NBTCompressedStreamTools.read(out,NBTReadLimiter.unlimited);
			return c;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public ItemStack getItemStack(NBTTagCompound nbt){
		return getItemStackNew(nbt);
	}
	
	private ItemStack getItemStackNew(NBTTagCompound nbt){
		try{
//			Class<?> clazz_cis = Class.forName("org.bukkit.craftbukkit."+FurnitureLib.getInstance().getBukkitVersion()+".inventory.CraftItemStack");
//			Class<?> clazz_nms_item = Class.forName("net.minecraft.server."+FurnitureLib.getInstance().getBukkitVersion()+".ItemStack");
//			Class<?> clazz_nms_nbt = Class.forName("net.minecraft.server."+FurnitureLib.getInstance().getBukkitVersion()+".NBTTagCompound");
//			Class<?> clazz_nbttools =  Class.forName("net.minecraft.server."+FurnitureLib.getInstance().getBukkitVersion()+".NBTCompressedStreamTools");
//			byte[] data = NBTCompressedStreamTools.toByte(nbt);
//			Object mns_nbt = clazz_nbttools.getMethod("a",InputStream.class).invoke(null, (InputStream) new ByteArrayInputStream(data));
//			
//			
//			Object nms_item = clazz_nms_item.getMethod("a",clazz_nms_nbt).invoke(null, mns_nbt);
			
			
			byte[] data = NBTCompressedStreamTools.toByte(nbt);
			net.minecraft.server.v1_13_R1.NBTTagCompound nmsNbt = net.minecraft.server.v1_13_R1.NBTCompressedStreamTools.a(new ByteArrayInputStream(data));
			net.minecraft.server.v1_13_R1.ItemStack nms_item = net.minecraft.server.v1_13_R1.ItemStack.a(nmsNbt);
			org.bukkit.inventory.ItemStack item = (org.bukkit.inventory.ItemStack) org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack.asBukkitCopy(nms_item);
			//			org.bukkit.inventory.ItemStack item = (org.bukkit.inventory.ItemStack) clazz_cis.getMethod("asBukkitCopy", clazz_nms_item).invoke(null, nms_item);

//			
//			for(Method m : Class.forName("net.minecraft.server." + FurnitureLib.getInstance().getBukkitVersion() + ".DataConverterItemName").getMethods()) {
//				String str = m.getName() + "(";
//				
//				for(Class<?> s : m.getParameterTypes()) {
//					str+=s.getName() + ",";
//				}
//				
//				str.substring(0, str.length() - 1);
//				str+=") = " + m.getReturnType().getName();
//				
//				System.out.println(str);
//			}
//			
//			for(Constructor<?> c : Class.forName("net.minecraft.server." + FurnitureLib.getInstance().getBukkitVersion() + ".DataConverterItemName").getConstructors()) {
//				String str = c.getName() + "(";
//				for(Class<?> s : c.getParameterTypes()) {
//					str+=s.getName() + ",";
//				}
//				str.substring(0, str.length() - 1);
//				str+=") = " + c.getName();
//				System.out.println(str);
//			}
			
			return item;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}