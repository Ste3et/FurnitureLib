package de.Ste3et_C0st.FurnitureLib.NBT;

import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackReader;
import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackV113;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CraftItemStack {

	private static ItemStackReader reader = new ItemStackV113();
	
    public ItemStack getItemStack(NBTTagCompound nbtTagCompound) {
        if(Objects.nonNull(reader)) {
        	return reader.getItemStack(nbtTagCompound);
        }
        return null;
    }

    public NBTTagCompound getNBTTag(ItemStack is) throws Exception {
    	return reader.getNBTTag(is);
    }
}