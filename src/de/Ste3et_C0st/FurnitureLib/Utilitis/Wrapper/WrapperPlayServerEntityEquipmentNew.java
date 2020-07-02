package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import de.Ste3et_C0st.FurnitureLib.Utilitis.Pair;

public class WrapperPlayServerEntityEquipmentNew extends AbstractPacket {

	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;
	
	public WrapperPlayServerEntityEquipmentNew(PacketContainer packet) {
		super(packet, TYPE);
	}
	
	public void addItemStack(ItemSlot slot, ItemStack stack) {
		//enumList.add(new Pair<EnumWrappers.ItemSlot, ItemStack>(slot, stack));
	}
	
	public void sendPacket(Player receiver) {
		try {
			super.sendPacket(receiver);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void addList() {
		//addList(Arrays.asList(new Pair<ItemSlot, ItemStack>(ItemSlot.CHEST, new ItemStack(Material.STONE))));
	}
	
	public static <T> List<?> addList(List<Pair<ItemSlot, ItemStack>> itemList){
		try {
			Class<?> pairClass = getListType();
			System.out.println(pairClass);
			Constructor<?> a = pairClass.getConstructor(EnumWrappers.getItemSlotClass(), MinecraftReflection.getItemStackClass());
			
			List<T> list = (List<T>) createListOfType(pairClass);
			for(Pair<ItemSlot, ItemStack> itemSlot : itemList) {
				Object enumItemSlot = EnumWrappers.getItemSlotConverter().getGeneric(itemSlot.getFirstKey());
				Object nmsItemStack = BukkitConverters.getItemStackConverter().getGeneric(itemSlot.getSecoundKey());
				Object pairObject = a.newInstance(a.newInstance(enumItemSlot, nmsItemStack));
				list.add((T) pairObject);
			}
			return list;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getListType() throws Exception{
		Field field = TYPE.getPacketClass().getDeclaredField("b");
		field.setAccessible(true);
		Type type = field.getGenericType();
		ParameterizedType pt = (ParameterizedType) type;
        Class<?> clazz = (Class<?>) pt.getActualTypeArguments()[0].getClass();
        return clazz;
	}
	
	public static <T> List<T> createListOfType(Class<T> type) {
	    return new ArrayList<T>();
	}
}
