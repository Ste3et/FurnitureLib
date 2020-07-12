package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;

public class WrapperPlayServerEntityEquipmentNew extends AbstractPacket implements IEntityEquipment{

	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;
	private List<Pair<ItemSlot, ItemStack>> itemList = new ArrayList<Pair<ItemSlot, ItemStack>>();
	
	public WrapperPlayServerEntityEquipmentNew() {
		super(new PacketContainer(TYPE), TYPE);
	}
	
	public void writeEntityID(int entityID) {
		this.handle.getIntegers().write(0, entityID);
	}
	
	public void setItem(ItemSlot slot, ItemStack stack) {
		Pair<ItemSlot, ItemStack> itemPair = new Pair<ItemSlot, ItemStack>(slot, stack);
		Optional<Pair<ItemSlot, ItemStack>> optPair = this.itemList.stream().filter(entry -> entry.getFirst() == slot).findFirst();
		if(optPair.isPresent()) this.itemList.remove(optPair.get());
		this.itemList.add(itemPair);
		getHandle().getSlotStackPairLists().write(0, itemList);
	}
	
	public void sendPacket(Player receiver) {
		try {
			super.sendPacket(receiver);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
