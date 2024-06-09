package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTReadLimiter;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.InternalClassReader;

public class ItemStackV120_5 extends ItemStackReader{

	private static Class<?> nmsNBTReadLimiter, craftServer, HolderLookup_a;
	private static Method save, asBukkitCopy, method_save;
	private static Object provider;
	
	static {
		try {
			craftServer = Class.forName(InternalClassReader.OBC + ".CraftServer");
			HolderLookup_a = Class.forName("net.minecraft.core.HolderLookup$a");
			nmsNBTReadLimiter = Class.forName(getNbtFolder() + ".NBTReadLimiter");
			
			//Get HolderLookup.a
			Object craftServerObject = craftServer.cast(Bukkit.getServer()); // Cast BukkitServer to CraftServer
			Object nmsServer = craftServerObject.getClass().getMethod("getServer").invoke(craftServerObject); // Cast CraftServer to NMSServer
			provider = nmsServer.getClass().getMethod("bc").invoke(nmsServer); // Get RegistryAccess.Frozen -> HolderLookup.a
			// finish
			
			save = clazz_nms_item.getMethod("a", HolderLookup_a, clazz_nms_nbt);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
			method_save = clazz_nms_item.getMethod("a", HolderLookup_a);
			clazz_nbttools_method_a_input = clazz_nbttools.getMethod("a", InputStream.class, nmsNBTReadLimiter);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public ItemStack getItemStack(NBTTagCompound nbt) {
        try {
            Object nms_item = save.invoke(null, provider, convertCompound(nbt));
			return (ItemStack) asBukkitCopy.invoke(null, nms_item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	@Override
	public Object convertCompound(NBTTagCompound nbtTagCompound) throws Exception {
		byte[] data = NBTCompressedStreamTools.toByte(nbtTagCompound);
        return clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data), nmsNBTReadLimiter.getMethod("a").invoke(null));
	}

	public NBTTagCompound getNBTTag(ItemStack is) throws Exception {		
        try {
            Object nms_item = asNMSCopy.invoke(null, is);
            Object nms_nbt = method_save.invoke(nms_item, HolderLookup_a.cast(provider));
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