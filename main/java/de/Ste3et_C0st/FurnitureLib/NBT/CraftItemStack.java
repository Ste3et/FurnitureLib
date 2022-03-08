package de.Ste3et_C0st.FurnitureLib.NBT;

import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackReader;
import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackV109_110;
import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackV111_112;
import de.Ste3et_C0st.FurnitureLib.NBT.ItemStackReader.ItemStackV113;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CraftItemStack {

	private static ItemStackReader reader = null;
	
	static {
		if (FurnitureLib.getBukkitVersion().startsWith("v1_11") || FurnitureLib.getBukkitVersion().startsWith("v1_12")) {
            reader = new ItemStackV111_112();
        } else if (FurnitureLib.isNewVersion()) {
            reader = new ItemStackV113();
        } else {
        	reader = new ItemStackV109_110();
        }
	}
	
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