package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

public class WrapperPlayServerEntityEquipmentOld extends AbstractPacket implements IEntityEquipment{

	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;
	
	public WrapperPlayServerEntityEquipmentOld() {
		super(new PacketContainer(TYPE), TYPE);
	}
	
	public WrapperPlayServerEntityEquipmentOld writeEntityID(int entityID) {
		this.handle.getIntegers().write(0, entityID);
		return this;
	}
	
	public void writeItemSlot(ItemSlot slot) {
		this.handle.getItemSlots().write(0, slot);
	}
	
	public void writeItemStack(ItemStack stack) {
		this.handle.getItemModifier().write(0, stack);
	}

	@Override
	public void setItem(ItemSlot slot, ItemStack stack) {
		writeItemStack(stack);
		writeItemSlot(slot);
	}
}
