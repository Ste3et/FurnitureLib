package de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader;

import java.io.OutputStream;
import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.utility.MinecraftVersion;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.InternalClassReader;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public abstract class ItemStackReader {

	protected static Class<?> clazz_obc_CraftItemStack, clazz_nms_nbt, clazz_nbttools, clazz_nms_item;
	protected static Method asNMSCopy;
	protected static Method clazz_nbttools_method_a_output, clazz_nbttools_method_a_input;
	
	static {
		try {
			boolean isRemapped = FurnitureLib.getVersion(new MinecraftVersion("1.20.5")) && FurnitureLib.isPaper();
			
			if(isRemapped == false) {
				clazz_obc_CraftItemStack = Class.forName(InternalClassReader.OBC + ".inventory.CraftItemStack");
				asNMSCopy = clazz_obc_CraftItemStack.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
				clazz_nms_item = Class.forName(getItemStackClass());
				
				
				if(FurnitureLib.isPaper()) {
					clazz_nms_nbt  = Class.forName(getNbtFolder() + ".CompoundTag");
					clazz_nbttools = Class.forName(getNbtFolder() +  ".NbtIo");
				}else {
					clazz_nms_nbt  = Class.forName(getNbtFolder() + ".NBTTagCompound");
					clazz_nbttools = Class.forName(getNbtFolder() +  ".NBTCompressedStreamTools");
				}
				
				clazz_nbttools_method_a_output = clazz_nbttools.getMethod("a", clazz_nms_nbt, OutputStream.class);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public abstract ItemStack getItemStack(NBTTagCompound nbtTagCompound);
	public abstract NBTTagCompound getNBTTag(ItemStack stack) throws Exception;
	
	static String getNbtFolder() {
		return FurnitureLib.getVersionInt() > 16 ? InternalClassReader.NBT : "net.minecraft.server." + InternalClassReader.packetVersion;
	}
	
    static String getItemStackClass() {
    	return FurnitureLib.getVersionInt() > 16 ? "net.minecraft.world.item.ItemStack" : "net.minecraft.server." + InternalClassReader.packetVersion + ".ItemStack";
	}
    
    public abstract Object convertCompound(NBTTagCompound nbtTagCompound) throws Exception;
}
