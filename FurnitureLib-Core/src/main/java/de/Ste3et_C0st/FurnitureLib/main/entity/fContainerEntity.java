package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.util.List;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.events.PacketContainer;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SkullMetaPatcher;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract class fContainerEntity extends fEntity{

	private fInventory entityInventory;
	
	public fContainerEntity(Location loc, EntityType type, int entityID, ObjectID id) {
		super(loc, type, entityID, id);
		this.entityInventory = new fInventory(getEntityID());
	}
	
	public void setEntityEquipment(EntityEquipment equipment) {
		this.setHelmet(equipment.getHelmet());
		this.setChestPlate(equipment.getChestplate());
		this.setLeggings(equipment.getLeggings());
		this.setBoots(equipment.getBoots());
		this.setItemInMainHand(equipment.getItemInMainHand());
		this.setItemInOffHand(equipment.getItemInOffHand());
	}
	
    public fInventory getEquipment() {
        return this.entityInventory;
    }

    public fInventory getInventory() {
        return this.entityInventory;
    }

    public fContainerEntity setInventory(fInventory inv) {
        this.entityInventory = inv;
        return this;
    }
    
    public ItemStack getBoots() {
        return getInventory().getBoots();
    }

    public fEntity setBoots(ItemStack is) {
        getInventory().setBoots(is);
        return this;
    }

    public ItemStack getHelmet() {
        return getInventory().getHelmet();
    }

    public fEntity setHelmet(ItemStack is) {
        getInventory().setHelmet(is);
        return this;
    }

    public ItemStack getChestPlate() {
        return getInventory().getChestPlate();
    }

    public fEntity setChestPlate(ItemStack is) {
        getInventory().setChestPlate(is);
        return this;
    }

    public ItemStack getLeggings() {
        return getInventory().getLeggings();
    }

    public fEntity setLeggings(ItemStack is) {
        getInventory().setLeggings(is);
        return this;
    }
    
    public ItemStack getItemInMainHand() {
        return getInventory().getItemInMainHand();
    }

    public fEntity setItemInMainHand(ItemStack is) {
        getInventory().setItemInMainHand(is);
        return this;
    }

    public ItemStack getItemInOffHand() {
        return getInventory().getItemInOffHand();
    }

    public fEntity setItemInOffHand(ItemStack is) {
        getInventory().setItemInOffHand(is);
        return this;
    }
    
    public void sendInventoryPacket(final Player player) {
        List<PacketContainer> packets = this.entityInventory.createPackets();
        
        if (packets.isEmpty())
            return;
        try {
        	
            for (final PacketContainer packet : packets) {
                if (player == null || packet == null || getManager() == null) {
                    return;
                }
                getManager().sendServerPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
	public void readInventorySaveData(NBTTagCompound metadata) {
    	final CraftItemStack craftItemStack = new CraftItemStack();

    	metadata.getCompound("Inventory", NBTTagCompound.class, inventory -> {
    		inventory.c().stream().forEach(entry -> {
    			String name = (String) entry;
    			if (inventory.getString(name).equalsIgnoreCase("NONE") == false) {
    				NBTTagCompound compound = inventory.getCompound(name);
                    ItemStack is = craftItemStack.getItemStack(compound);
                    if(Objects.nonNull(is)) {
                    	if(is.getType().name().equalsIgnoreCase("PLAYER_HEAD") && SkullMetaPatcher.shouldPatch()) {
                        	is = SkullMetaPatcher.patch(is, compound);
                        }
                        this.getInventory().setSlot(name, is);
                    }
                }
    		});
    	});
    	
    	metadata.getCompound("HandItems", NBTTagList.class, handItems -> {
    		this.setItemInMainHand(craftItemStack.getItemStack(handItems.get(0)));
    		this.setItemInOffHand(craftItemStack.getItemStack(handItems.get(1)));
    	});
    	
    	metadata.getCompound("ArmorItems", NBTTagList.class, armorItems -> {
    		this.setHelmet(craftItemStack.getItemStack(armorItems.get(3)));
    		this.setChestPlate(craftItemStack.getItemStack(armorItems.get(2)));
    		this.setLeggings(craftItemStack.getItemStack(armorItems.get(1)));
    		this.setBoots(craftItemStack.getItemStack(armorItems.get(0)));
    	});
    }
    
    public void writeInventoryData() {
    	if(!getInventory().isEmpty()) setMetadata(this.getInventory());
    }
    
    @Override
    public void send(Player player) {
    	super.send(player);
    	this.sendInventoryPacket(player);
    }
    
    @Override
    public void update(Player player) {
    	super.update(player);
    	this.sendInventoryPacket(player);
    }
}
