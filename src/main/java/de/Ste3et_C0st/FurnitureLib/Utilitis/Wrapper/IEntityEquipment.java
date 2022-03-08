package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import de.Ste3et_C0st.FurnitureLib.main.entity.fInventory;

public interface IEntityEquipment {

	public void setItem(ItemSlot slot, ItemStack stack);
	public IEntityEquipment writeEntityID(int entityID);
	public PacketContainer getHandle();
	
}
