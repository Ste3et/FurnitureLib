package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.IEntityEquipment;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.WrapperPlayServerEntityEquipmentNew;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.WrapperPlayServerEntityEquipmentOld;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class fInventory implements Cloneable {

    private ItemStack[] items = new ItemStack[6];
    private int entityId = 0;
    
    private static Function<fInventory, List<IEntityEquipment>> entityEquipmentFunction;
    
    static {
    	if(FurnitureLib.getVersionInt() > 15) {
        	entityEquipmentFunction = fInventory -> {
        		List<IEntityEquipment> packetList = new ArrayList<IEntityEquipment>();
        		WrapperPlayServerEntityEquipmentNew equipmentInterface = new WrapperPlayServerEntityEquipmentNew().writeEntityID(fInventory.entityId);
            	for(int i = 0; i < 6; i++) {
            		ItemStack stack = fInventory.getSlot(i);
            		EquipmentSlot slot = EquipmentSlot.values()[i];
            		equipmentInterface.setItem(slot.getItemSlot(), stack);
            	}
            	packetList.add(equipmentInterface);
            	return packetList;
        	};
        }else {
        	entityEquipmentFunction = fInventory -> {
        		List<IEntityEquipment> packetList = new ArrayList<IEntityEquipment>();
        		for (int i = 0; i < 6; i++) {
        			WrapperPlayServerEntityEquipmentOld equipmentInterface = new WrapperPlayServerEntityEquipmentOld().writeEntityID(fInventory.entityId);
                    ItemStack stack = fInventory.getSlot(i);
            		EquipmentSlot slot = EquipmentSlot.values()[i];
            		equipmentInterface.setItem(slot.getItemSlot(), stack);
            		packetList.add(equipmentInterface);
        		}
        		return packetList;
        	};
        }
    }
    
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
    
    public HashMap<EquipmentSlot, ItemStack> getStackMap(){
    	final Predicate<ItemStack> stackPredicate = stack -> Objects.nonNull(stack) && stack.getType() != Material.AIR;
    	final HashMap<EquipmentSlot, ItemStack> result = new HashMap<fInventory.EquipmentSlot, ItemStack>();
    	EnumSet.allOf(EquipmentSlot.class).stream()
    		.filter(entry -> stackPredicate.test(getSlot(entry.getSlot())))
    		.forEach(entry -> {
    			result.put(entry, getSlot(entry.getSlot()));
    		});
    	return result;
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
    	return entityEquipmentFunction.apply(this).stream().map(IEntityEquipment::getHandle).collect(Collectors.toList());
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
        
        public BodyPart toBodyPart() {
        	switch(this) {
        		case MAINHAND: return BodyPart.RIGHT_ARM;
        		case OFFHAND: return BodyPart.LEFT_ARM;
        		case HEAD: return BodyPart.HEAD;
        		case CHEST: return BodyPart.BODY;
        		default: return BodyPart.RIGHT_LEG;
        	}
        }
    }
}