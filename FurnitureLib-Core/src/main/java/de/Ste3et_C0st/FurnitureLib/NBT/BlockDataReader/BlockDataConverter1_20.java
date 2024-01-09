package de.Ste3et_C0st.FurnitureLib.NBT.BlockDataReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Optional;

import org.bukkit.block.data.BlockData;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;

public class BlockDataConverter1_20 extends BlockDataReader{

	private static Class<?> registries, worldClass, resourceKeyClass;
	private static Class<?> nmsNBTReadLimiter, clazz_nbttools, calzz_gameProfile, craftBlockDataClass;
	private static Method clazz_nbttools_method_a_input;
	
	static {
		try {
			registries = Class.forName("net.minecraft.core.registries");
			worldClass = Class.forName("net.minecraft.world.level.World");
			resourceKeyClass = Class.forName("net.minecraft.resources.ResourceKey");
			nmsNBTReadLimiter = Class.forName("net.minecraft.nbt.NBTReadLimiter");
			clazz_nbttools = Class.forName("net.minecraft.nbt.NBTCompressedStreamTools");
			calzz_gameProfile = Class.forName("net.minecraft.nbt.GameProfileSerializer");
			craftBlockDataClass = Class.forName("org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData");
			
			clazz_nbttools_method_a_input = clazz_nbttools.getMethod("a", InputStream.class, nmsNBTReadLimiter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Optional<BlockData> read(NBTTagCompound compound) {
		//GameProfileSerializer.a((HolderGetter)dM().a(Registries.f), nbt.p("block_state"))
		try {
			byte[] data = NBTCompressedStreamTools.toByte(compound);
            Object nbtTag = clazz_nbttools_method_a_input.invoke(null, new ByteArrayInputStream(data), nmsNBTReadLimiter.getMethod("a").invoke(null));
			
			Object NMSblock_entity_type = registries.getDeclaredField("f");
			Object HolderLookup = worldClass.getDeclaredMethod("a", resourceKeyClass).invoke(null, NMSblock_entity_type);
			
			Object iBlockData = calzz_gameProfile.getDeclaredMethod("a", HolderLookup.getClass(), nbtTag.getClass()).invoke(null, HolderLookup, nbtTag);
			Object craftBlockData = craftBlockDataClass.getDeclaredMethod("fromData", iBlockData.getClass()).invoke(null, iBlockData);
			
			return Optional.ofNullable(BlockData.class.cast(craftBlockData));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return Optional.empty();
	}

}
