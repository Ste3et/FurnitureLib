package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

public class WrapperPlayServerEntityEquipmentOld extends AbstractPacket {

	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;
	
	public WrapperPlayServerEntityEquipmentOld() {
		super(new PacketContainer(TYPE), TYPE);
	}
	
	public void writeEntityID(int entityID) {
		this.handle.getIntegers().write(0, entityID);
	}
	
	public void writeItemSlot(ItemSlot slot) {
		this.handle.getItemSlots().write(0, slot);
	}
	
	public void writeItemStack(ItemStack stack) {
		this.handle.getItemModifier().write(0, stack);
	}
}
