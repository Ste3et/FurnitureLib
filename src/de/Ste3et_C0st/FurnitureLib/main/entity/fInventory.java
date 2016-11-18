package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class fInventory implements Cloneable{
	
	private ItemStack[] items = new ItemStack[6];
	private int entityId = 0;
	@Deprecated
	public ItemStack getItemInHand() {return this.items[0];}
	//private Vector3f v = new Vector3f();
	public ItemStack getItemInMainHand() {return this.items[0];}
	public ItemStack getItemInOffHand() {return this.items[1];}
	public ItemStack getBoots() {return this.items[2];}
	public ItemStack getLeggings() {return this.items[3];}
	public ItemStack getChestPlate() {return this.items[4];}
	public ItemStack getHelmet() {return this.items[5];}
	public fInventory(int entityId){
		this.entityId = entityId;
	}
	
	@Deprecated
	public void setItemInHand(ItemStack item) {this.setSlot(0, item);}
	
	public void setItemInMainHand(ItemStack item) {this.setSlot(0, item);}
	public void setItemInOffHand(ItemStack item) {this.setSlot(1, item);}
	public void setBoots(ItemStack item) {this.setSlot(2, item);}
	public void setLeggings(ItemStack item) {this.setSlot(3, item);}
	public void setChestPlate(ItemStack item) {this.setSlot(4, item);}
	public void setHelmet(ItemStack item) {this.setSlot(5, item);}
	public int getEntityID(){return this.entityId;}
	public ItemStack[] getIS(){return this.items;}
	
	public ItemStack getSlot(int slot) {
		if (slot < 0 || slot >= this.items.length) {
			return null;
		}

		return this.items[slot];
	}
	
	public ItemStack getSlot(String s) {
		switch (s) {
		case "MAINHAND":return getSlot(0);
		case "OFFHAND":return getSlot(1);
		case "FEET":return getSlot(2);
		case "LEGS":return getSlot(3);
		case "CHEST":return getSlot(4);
		case "HEAD":return getSlot(5);
		}
		return null;
	}
	
	public Integer getSlot(ItemStack is){
		if(is==null){return null;}
		for(int l = 0; l<=getIS().length;l++){
			if(getIS()[l]!=null&&getIS()[l].equals(is)){
				return l;
			}
		}
		return null;
	}

	public void setSlot(int slot, ItemStack item) {
		if (item != null && item.getType() == Material.AIR) {
			item = null;
		}

		if (slot < 0 || slot >= this.items.length) {
			return;
		}
		this.items[slot] = item;
	}
	
	public void setSlot(String s, ItemStack item) {
		if (item != null && item.getType() == Material.AIR) {
			item = null;
		}
		switch (s) {
		case "MAINHAND":setSlot(0, item);break;
		case "OFFHAND":setSlot(1, item);break;
		case "FEET":setSlot(2, item);break;
		case "LEGS":setSlot(3, item);break;
		case "CHEST":setSlot(4, item);break;
		case "HEAD":setSlot(5, item);break;
		}
	}
	
	public List<PacketContainer> createPackets() {
		List<PacketContainer> packetList = new ArrayList<PacketContainer>();
		for(int i = 0; i < 6; i++){
			ItemStack stack = this.getSlot(i);
			PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
			packet.getIntegers().write(0, entityId);
			packet.getItemSlots().write(0, ItemSlot.values()[i]);
			packet.getItemModifier().write(0, stack);
			packetList.add(packet);
		}
		return packetList;
	}
	
	public boolean isEmpty() {
		for (ItemStack item : this.items) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public fInventory clone() {
		fInventory inv = new fInventory(this.entityId);
		for (int i = 0; i < 5; i++) {
			inv.setSlot(i, this.getSlot(i));
		}
		return inv;
	}
}