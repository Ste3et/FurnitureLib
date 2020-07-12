package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureMoveEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.WrapperPlayServerEntityEquipmentNew;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class fInventory implements Cloneable {

    private ItemStack[] items = new ItemStack[6];
    private int entityId = 0;
    
    public fInventory(int entityId) {
        this.entityId = entityId;
    }

    public ItemStack getItemInMainHand() {
        return this.items[0];
    }

    public void setItemInMainHand(ItemStack item) {
        this.setSlot(0, item);
    }

    public ItemStack getItemInOffHand() {
        return this.items[1];
    }

    public void setItemInOffHand(ItemStack item) {
        this.setSlot(1, item);
    }

    public ItemStack getBoots() {
        return this.items[2];
    }

    public void setBoots(ItemStack item) {
        this.setSlot(2, item);
    }

    public ItemStack getLeggings() {
        return this.items[3];
    }

    public void setLeggings(ItemStack item) {
        this.setSlot(3, item);
    }

    public ItemStack getChestPlate() {
        return this.items[4];
    }

    public void setChestPlate(ItemStack item) {
        this.setSlot(4, item);
    }

    public ItemStack getHelmet() {
        return this.items[5];
    }

    public void setHelmet(ItemStack item) {
        this.setSlot(5, item);
    }

    public int getEntityID() {
        return this.entityId;
    }

    public ItemStack[] getIS() {
        return this.items;
    }

    public ItemStack getSlot(int slot) {
        if (slot < 0 || slot >= this.items.length) {
            return new ItemStack(Material.AIR, 1);
        }
        if (this.items[slot] == null) return new ItemStack(Material.AIR, 1);
        return this.items[slot];
    }

    public ItemStack getSlot(String s) {
        try {
            return getSlot(EquipmentSlot.valueOf(s).getSlot());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getSlot(ItemStack is) {
        if (is == null) {
            return null;
        }
        for (int l = 0; l <= getIS().length; l++) {
            if (getIS()[l] != null && getIS()[l].equals(is)) {
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
        if (item == null) item = new ItemStack(Material.AIR, 1);
        try {
            setSlot(EquipmentSlot.valueOf(s).getSlot(), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<PacketContainer> createPackets(){
    	if(FurnitureLib.getVersionInt() > 15) {
    		return createPacketsNew();
    	}
    	return createPacketsOld();
    }

    //under 1.16
    public List<PacketContainer> createPacketsOld() {
        List<PacketContainer> packetList = new ArrayList<PacketContainer>();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = this.getSlot(i);
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, entityId);
            packet.getItemSlots().write(0, ItemSlot.values()[i]);
            packet.getItemModifier().write(0, stack);
            packetList.add(packet);
        }
        return packetList;
    }
    
    //overOrEquals 1.16
    public List<PacketContainer> createPacketsNew(){
    	WrapperPlayServerEntityEquipmentNew equipment = new WrapperPlayServerEntityEquipmentNew();
    	equipment.writeEntityID(getEntityID());
    	for(int i = 0; i < 6; i++) {
    		ItemStack stack = this.getSlot(i);
    		EquipmentSlot slot = EquipmentSlot.values()[i];
    		equipment.setItem(slot.getItemSlot(), stack);
    	}
    	return Arrays.asList(equipment.getHandle());
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

    public enum EquipmentSlot {
        MAINHAND(0, ItemSlot.MAINHAND),
        OFFHAND(1, ItemSlot.OFFHAND),
        FEET(2, ItemSlot.FEET),
        LEGS(3, ItemSlot.LEGS),
        CHEST(4, ItemSlot.CHEST),
        HEAD(5, ItemSlot.HEAD);

        private int slot;
        private ItemSlot itemSlot;
        
        EquipmentSlot(int i, ItemSlot slot) {
            this.slot = i;
            this.itemSlot = slot;
        }

        public int getSlot() {
            return this.slot;
        }
        
        public ItemSlot getItemSlot() {
        	return this.itemSlot;
        }
    }
}