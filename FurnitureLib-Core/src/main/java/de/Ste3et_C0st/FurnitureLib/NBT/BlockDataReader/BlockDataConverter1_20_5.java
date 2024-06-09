package de.Ste3et_C0st.FurnitureLib.NBT.BlockDataReader;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.InternalClassReader;

public class BlockDataConverter1_20_5 extends BlockDataReader{

	private static Class<?> registries, resourceKeyClass;
	private static Class<?> calzz_gameProfile, craftBlockDataClass;
	private static Class<?> craftWorldClass, iWorldReader, classHolderGetter;
	private static Object regsitrie;
	private static Method worldReaderHandle, holderLookup;
	
	static {
		try {
			registries = Class.forName("net.minecraft.core.registries.Registries");
			resourceKeyClass = Class.forName("net.minecraft.resources.ResourceKey");
			calzz_gameProfile = Class.forName("net.minecraft.nbt.GameProfileSerializer");
			craftBlockDataClass = Class.forName(InternalClassReader.OBC + ".block.data.CraftBlockData");
			craftWorldClass = Class.forName(InternalClassReader.OBC + ".CraftWorld");
			iWorldReader = Class.forName("net.minecraft.world.level.IWorldReader");
			classHolderGetter = Class.forName("net.minecraft.core.HolderGetter");
			
			regsitrie = registries.getDeclaredField("f").get(null);
			
			holderLookup = iWorldReader.getMethod("a", resourceKeyClass);
			worldReaderHandle = craftWorldClass.getMethod("getHandle");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Optional<BlockData> read(NBTTagCompound compound) {
		AtomicReference<BlockData> returnAtomic = new AtomicReference<>();
		
		CraftItemStack.getReader().ifPresent(itemReader -> {
			try {
				Object nbtTag = itemReader.convertCompound(compound);
				Object craftWorld = craftWorldClass.cast(Bukkit.getWorlds().stream().findFirst().get());
				Object nmsWorld = iWorldReader.cast(worldReaderHandle.invoke(craftWorld));
				Object HolderLookup = holderLookup.invoke(nmsWorld, regsitrie);
				Object iBlockData = calzz_gameProfile.getDeclaredMethod("a", classHolderGetter, nbtTag.getClass()).invoke(null, HolderLookup, nbtTag);
				Object craftBlockData = craftBlockDataClass.getDeclaredMethod("fromData", iBlockData.getClass()).invoke(null, iBlockData);
				returnAtomic.set(BlockData.class.cast(craftBlockData));
			}catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return Optional.ofNullable(returnAtomic.get());
	}

}
