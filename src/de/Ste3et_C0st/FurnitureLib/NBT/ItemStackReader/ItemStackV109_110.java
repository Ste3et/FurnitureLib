package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;

public class ItemStackV109_110 extends ItemStackReader{

	 private static Method createStack, asBukkitCopy;
     
     static {
    	 try {
    		 createStack = clazz_nms_item.getMethod("createStack", clazz_nms_nbt);
    		 asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
    	 }catch(Exception ex) {
    		 
    	 }
     }
	
     public ItemStack getItemStack(NBTTagCompound nbt) {
	        try {
	            byte[] data = NBTCompressedStreamTools.toByte(nbt);
	            Object mns_nbt = clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data));
	            Object nms_item = createStack.invoke(null, mns_nbt);
				return (ItemStack) asBukkitCopy.invoke(null, nms_item);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	 }
	
}
