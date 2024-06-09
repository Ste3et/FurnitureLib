package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTReadLimiter;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.InternalClassReader;

public class ItemStack_Paper_V120_5 extends ItemStackReader{

	private static Class<?> clazz_NBTReadLimiter, clazz_CraftServer, clazz_HolderLookup_a;
	private static Method parse, asBukkitCopy, save;
	private static Object provider;
	
	static{
		try {
			clazz_obc_CraftItemStack = Class.forName(InternalClassReader.OBC + ".inventory.CraftItemStack");
			clazz_nms_item = Class.forName(getItemStackClass());
			clazz_nms_nbt  = Class.forName(getNbtFolder() + ".CompoundTag");
			clazz_nbttools = Class.forName(getNbtFolder() +  ".NbtIo");
			clazz_CraftServer = Class.forName(InternalClassReader.OBC + ".CraftServer");
			clazz_HolderLookup_a = Class.forName("net.minecraft.core.HolderLookup$Provider"); // HolderLookup_Provider -> HolderLookup_a
			clazz_NBTReadLimiter = Class.forName(getNbtFolder() + ".NbtAccounter"); // NbtAccounter -> NBTReadLimiter
			
			save = clazz_nms_item.getMethod("save", clazz_HolderLookup_a);
			parse = clazz_nms_item.getMethod("parseOptional", clazz_HolderLookup_a, clazz_nms_nbt);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", clazz_nms_item);
			asNMSCopy = clazz_obc_CraftItemStack.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
			clazz_nbttools_method_a_output = clazz_nbttools.getMethod("writeCompressed", clazz_nms_nbt, OutputStream.class);
			clazz_nbttools_method_a_input = clazz_nbttools.getMethod("readCompressed", InputStream.class, clazz_NBTReadLimiter);
			
			//Get HolderLookup.a
			Object craftServerObject = clazz_CraftServer.cast(Bukkit.getServer()); // Cast BukkitServer to CraftServer
			Object nmsServer = craftServerObject.getClass().getMethod("getServer").invoke(craftServerObject); // Cast CraftServer to NMSServer
			provider = nmsServer.getClass().getMethod("registryAccess").invoke(nmsServer); // Get RegistryAccess.Frozen -> HolderLookup.a
			// finish
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ItemStack getItemStack(NBTTagCompound nbtTagCompound) {
		try {
            Object nms_item = parse.invoke(null, provider, convertCompound(nbtTagCompound));
			return (ItemStack) asBukkitCopy.invoke(null, nms_item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

	@Override
	public Object convertCompound(NBTTagCompound nbtTagCompound) throws Exception {
		byte[] data = NBTCompressedStreamTools.toByte(nbtTagCompound);
        return clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data), clazz_NBTReadLimiter.getMethod("unlimitedHeap").invoke(null));
	}

	public NBTTagCompound getNBTTag(ItemStack is) throws Exception {		
        try {
            Object nms_item = asNMSCopy.invoke(null, is);
            Object nms_nbt = save.invoke(nms_item, clazz_HolderLookup_a.cast(provider));
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
