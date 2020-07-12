package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

public interface IEntityEquipment {

	public void setItem(ItemSlot slot, ItemStack stack);
	
}
