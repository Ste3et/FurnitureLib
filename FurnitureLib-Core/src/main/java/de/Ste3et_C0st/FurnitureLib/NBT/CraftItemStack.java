package de.Ste3et_C0st.FurnitureLib.NBT;

import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackReader;
import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackV111_112;
import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackV113;
import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackV120_1;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.utility.MinecraftVersion;

import java.util.Objects;
import java.util.Optional;

public class CraftItemStack {

	private final static ItemStackReader READER;
	
	static {
		if(FurnitureLib.getVersion(new MinecraftVersion("1.20.3"))) {
			READER = new ItemStackV120_1();
		}else if(FurnitureLib.getVersionInt() < 13) {
			READER = new ItemStackV111_112();
		}else {
			READER = new ItemStackV113();
		}
	}
	
    public ItemStack getItemStack(NBTTagCompound nbtTagCompound) {
        if(Objects.nonNull(READER)) {
        	return READER.getItemStack(nbtTagCompound);
        }
        return null;
    }

    public NBTTagCompound getNBTTag(ItemStack is) throws Exception {
    	return READER.getNBTTag(is);
    }
    
    public static Optional<ItemStackReader> getReader() {
    	return Optional.ofNullable(READER);
    }
}