package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.inventory.EquipmentSlot;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class EquipmentStack {

	private final ItemSlot itemSlot;
	private final int slotNumber;
	private final BodyPart bodyPart;
	
	public EquipmentStack(ItemSlot itemSlot, int slotNumber, BodyPart part) {
		this.itemSlot = itemSlot;
		this.slotNumber = slotNumber;
		this.bodyPart = part;
	}

	public ItemSlot getItemSlot() {
		return itemSlot;
	}

	public int getSlotNumber() {
		return slotNumber;
	}
	
	public BodyPart toBodyPart() {
		return this.bodyPart;
	}
	
	public static EquipmentStack getEquipmentStack(EquipmentSlot slot) {
    	return getEquipmentStack(slot.name());
	}
	
	public static EquipmentStack getEquipmentStack(String slotName) {
    	switch(slotName) {
			case "MAINHAND" : return new EquipmentStack(ItemSlot.MAINHAND, 0, BodyPart.RIGHT_ARM);
			case "HAND" : return new EquipmentStack(ItemSlot.MAINHAND, 0, BodyPart.RIGHT_ARM);
			case "OFFHAND" : return new EquipmentStack(ItemSlot.OFFHAND, 1, BodyPart.LEFT_ARM);
			case "OFF_HAND" : return new EquipmentStack(ItemSlot.OFFHAND, 1, BodyPart.LEFT_ARM);
			case "FEET" : return new EquipmentStack(ItemSlot.FEET, 2, BodyPart.RIGHT_LEG);
			case "LEGS" : return new EquipmentStack(ItemSlot.LEGS, 3, BodyPart.RIGHT_LEG);
			case "CHEST" : return new EquipmentStack(ItemSlot.CHEST, 4, BodyPart.BODY);
			case "HEAD" : return new EquipmentStack(ItemSlot.HEAD, 5, BodyPart.HEAD);
			case "BODY" : return new EquipmentStack(ItemSlot.valueOf("BODY"), 6, BodyPart.BODY);
			case "SADDLE" : return new EquipmentStack(ItemSlot.valueOf("SADDLE"), 6, BodyPart.BODY);
			default : return new EquipmentStack(ItemSlot.HEAD, 5, BodyPart.HEAD);
		}
	}
}
