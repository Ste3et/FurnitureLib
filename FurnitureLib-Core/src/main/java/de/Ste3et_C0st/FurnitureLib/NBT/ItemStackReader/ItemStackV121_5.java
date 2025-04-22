package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTReadLimiter;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.InternalClassReader;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ItemStackV121_5 extends ItemStackReader{

	private static Class<?> clazz_NBTReadLimiter, clazz_CraftServer, clazz_HolderLookup_a, clazz_nbt_base;
	private static Method save, asBukkitCopy, method_save;
	private static Object provider;
	
	static {
		try {
			clazz_CraftServer = Class.forName(InternalClassReader.OBC + ".CraftServer");
			clazz_HolderLookup_a = Class.forName("net.minecraft.core.HolderLookup$a");
			clazz_NBTReadLimiter = Class.forName(getNbtFolder() + ".NBTReadLimiter");
			clazz_nbt_base = Class.forName(getNbtFolder() + ".NBTBase");
			
			//Get HolderLookup.a
			Object craftServerObject = clazz_CraftServer.cast(Bukkit.getServer()); // Cast BukkitServer to CraftServer
			Object nmsServer = craftServerObject.getClass().getMethod("getServer").invoke(craftServerObject); // Cast CraftServer to NMSServer
			provider = nmsServer.getClass().getMethod("ba").invoke(nmsServer);
			// finish
			
			
			save = clazz_nms_item.getMethod("a", clazz_HolderLookup_a, clazz_nbt_base);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
			method_save = clazz_nms_item.getMethod("a", clazz_HolderLookup_a);
			clazz_nbttools_method_a_input = clazz_nbttools.getMethod("a", InputStream.class, clazz_NBTReadLimiter);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ItemStack getItemStack(NBTTagCompound nbt) {
        try {
        	final Object nmsNBT = convertCompound(nbt);
            Optional<?> nms_item = (Optional<?>) save.invoke(null, provider, nmsNBT);
			return nms_item.isPresent() ? (ItemStack) asBukkitCopy.invoke(null, nms_item.get()) : new ItemStack(Material.AIR);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	@Override
	public Object convertCompound(NBTTagCompound nbtTagCompound) throws Exception {
		byte[] data = NBTCompressedStreamTools.toByte(this.convertMaterial(nbtTagCompound));
        return clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data), clazz_NBTReadLimiter.getMethod("a").invoke(null));
	}

	public NBTTagCompound getNBTTag(ItemStack is) throws Exception {		
        try {
            Object nms_item = asNMSCopy.invoke(null, is);
            Object nms_nbt = method_save.invoke(nms_item, clazz_HolderLookup_a.cast(provider));
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