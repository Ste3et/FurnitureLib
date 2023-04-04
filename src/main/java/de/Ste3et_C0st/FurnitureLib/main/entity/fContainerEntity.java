package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.events.PacketContainer;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SkullMetaPatcher;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract class fContainerEntity extends fEntity{

	private fInventory entityInventory;
	
	public fContainerEntity(Location loc, EntityType type, int entityID, ObjectID id) {
		super(loc, type, entityID, id);
		this.entityInventory = new fInventory(getEntityID());
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
	public void loadMetadata(NBTTagCompound metadata) {
    	super.loadMetadata(metadata);
    	if(metadata.hasKeyOfType("Inventory", 10)) {
    		NBTTagCompound inventory = metadata.getCompound("Inventory");
    		inventory.c().stream().forEach(entry -> {
    			String name = (String) entry;
    			if (inventory.getString(name).equalsIgnoreCase("NONE") == false) {
    				
    				NBTTagCompound compound = inventory.getCompound(name);
                    ItemStack is = new CraftItemStack().getItemStack(compound);
                    if(is.getType().name().equalsIgnoreCase("PLAYER_HEAD") && SkullMetaPatcher.shouldPatch()) {
                    	is = SkullMetaPatcher.patch(is, compound);
                    }
                    this.getInventory().setSlot(name, is);
                }
    		});
    	}
    }
    
    @Override
    public NBTTagCompound getMetaData() {
    	super.getMetaData();
    	if(!getInventory().isEmpty()) setMetadata(this.getInventory());
    	return this.getNBTField();
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
