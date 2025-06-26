package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTReadLimiter;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;

public class ItemStackV121_6 extends ItemStackReader{
	
	private static Class<?> NbtIo, NbtAccounter, InputStreamClass, NmsStack, NbtOps, NbtCompoundTag, Mojangpair;
	private static Method readCompressed, decode, encode, dataResult, mojangPairgetFirst, asBukkitCopy;
	
	static{
		try {
			NbtIo = Class.forName(getNbtFolder() + ".NBTCompressedStreamTools");
			NbtAccounter = Class.forName(getNbtFolder() + ".NBTReadLimiter");
			NbtOps = Class.forName(getNbtFolder() + ".DynamicOpsNBT");
			NbtCompoundTag = Class.forName(getNbtFolder() + ".NBTTagCompound");
			Mojangpair = Class.forName("com.mojang.datafixers.util.Pair");
			
			
			NmsStack = Class.forName(getItemStackClass());
			InputStreamClass = InputStream.class;
			
			readCompressed = NbtIo.getMethod("a", InputStreamClass, NbtAccounter);
			dataResult = Class.forName("com.mojang.serialization.DataResult").getMethod("result");
			mojangPairgetFirst = Mojangpair.getMethod("getFirst");
			decode = Class.forName("com.mojang.serialization.Decoder").getMethod("decode", Class.forName("com.mojang.serialization.DynamicOps"), Object.class);
			encode = Class.forName("com.mojang.serialization.Encoder").getMethod("encode", Object.class, Class.forName("com.mojang.serialization.DynamicOps"), Object.class);
			asBukkitCopy = clazz_obc_CraftItemStack.getMethod("asBukkitCopy", NmsStack);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public ItemStack getItemStack(NBTTagCompound nbtTagCompound) {
		try {
			final Object nmsNbt = convertCompound(nbtTagCompound);
			
			final Object CODEC = NmsStack.getField("b").get(null);
			final Object NbtOpsInstance = NbtOps.getField("a").get(null);
			final Object obj = decode.invoke(CODEC, NbtOpsInstance, nmsNbt);
			final Optional<?> optional = (Optional<?>) dataResult.invoke(obj, null);
			return optional.isPresent() ? asBukkitCopy(optional.get()) : new ItemStack(Material.AIR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
	}
	
	private ItemStack asBukkitCopy(Object object) throws Exception {
		return (ItemStack) asBukkitCopy.invoke(null, mojangPairgetFirst.invoke(object));
	}

	@Override
	public Object convertCompound(NBTTagCompound nbtTagCompound) throws Exception {
		byte[] data = NBTCompressedStreamTools.toByte(this.convertMaterial(nbtTagCompound));
		Object heap = NbtAccounter.getMethod("a").invoke(null);
        return readCompressed.invoke(null, new ByteArrayInputStream(data), heap);
	}

	public NBTTagCompound getNBTTag(ItemStack is) throws Exception {		
        try {
        	Object nms_item = asNMSCopy.invoke(null, is);
        	Object newTag = NbtCompoundTag.getConstructor().newInstance();
        	
        	final Object CODEC = NmsStack.getField("b").get(null);
        	final Object NbtOpsInstance = NbtOps.getField("a").get(null);
        	final Object obj = encode.invoke(CODEC, nms_item, NbtOpsInstance, newTag);
        	final Optional<?> optional = (Optional<?>) dataResult.invoke(obj, null);
        	
        	if(optional.isPresent()) {
        		ByteArrayOutputStream os = new ByteArrayOutputStream();
        		NbtIo.getMethod("a", optional.get().getClass(), OutputStream.class).invoke(null, optional.get(), os);
        		return NBTCompressedStreamTools.read(os.toByteArray(), NBTReadLimiter.unlimited);
        	}
        	return new NBTTagCompound();
        } catch (Exception e) {
            e.printStackTrace();
            return new NBTTagCompound();
        }
	}
}