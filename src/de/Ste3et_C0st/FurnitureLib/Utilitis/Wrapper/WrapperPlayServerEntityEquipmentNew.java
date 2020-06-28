package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import de.Ste3et_C0st.FurnitureLib.Utilitis.Pair;

public class WrapperPlayServerEntityEquipmentNew extends AbstractPacket {

	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;
	private final List<Pair<ItemSlot, ItemStack>> enumList = new ArrayList<Pair<ItemSlot, ItemStack>>();
	private final static Class<?> pairClass;
	
	static {
		try {
			
		}catch(Exception ex) {
			
		}
	}
	
	public WrapperPlayServerEntityEquipmentNew(PacketContainer packet) {
		super(packet, TYPE);
	}
	
	public void addItemStack(ItemSlot slot, ItemStack stack) {
		enumList.add(new Pair<EnumWrappers.ItemSlot, ItemStack>(slot, stack));
	}
	
	public void sendPacket(Player receiver) {
		
		super.sendPacket(receiver);
	}
}
