package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;

public abstract class fSerializer extends fProtocol {

    private NBTTagCompound metadata = new NBTTagCompound();

    public fSerializer(EntityType type, ObjectID id) {
        super(type, id);
    }

    protected NBTTagCompound getNBTField() {
        return this.metadata;
    }

    protected void setMetadata(String field, String value) {
        metadata.setString(field, value);
    }

    protected void setMetadata(String field, Boolean value) {
        setMetadata(field, value ? 1 : 0);
    }

    protected void setMetadata(String field, Integer value) {
        metadata.setInt(field, value);
    }

    protected void setMetadata(String field, Byte value) {
        metadata.setByte(field, value);
    }

    protected void setMetadata(String field, Double value) {
        metadata.setDouble(field, value);
    }

    protected void setMetadata(String field, Float value) {
        metadata.setFloat(field, value);
    }

    protected void setMetadata(String field, Long value) {
        metadata.setLong(field, value);
    }

    protected void setMetadata(String field, Short value) {
        metadata.setShort(field, value);
    }

    protected void setMetadata(String field, byte[] value) {
        metadata.setByteArray(field, value);
    }

    protected void setMetadata(String field, int[] value) {
        metadata.setIntArray(field, value);
    }

    protected void setMetadata(Location value) {
        set("Location", getFromLocation(value));
    }

    protected void setMetadata(fInventory inventory) {
        set("Inventory", getFromInventory(inventory));
    }
    
    protected void setMetadata(ItemStack stack) {
    	this.setMetadata("stack", stack);
    }
    
    protected void setMetadata(String key, ItemStack stack) {
        if (Objects.isNull(stack)) return;
        if (Material.AIR == stack.getType()) return;
        try {
			set("stack", new CraftItemStack().getNBTTag(stack));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    protected void set(String field, NBTTagCompound value) {
        metadata.set(field, value);
    }

    //public void getDefNBT(fEntity entity) {};

    private NBTTagCompound getFromLocation(Location loc) {
        NBTTagCompound location = new NBTTagCompound();
        if(Objects.nonNull(loc)) {
        	location.setDouble("X", loc.getX());
            location.setDouble("Y", loc.getY());
            location.setDouble("Z", loc.getZ());
            location.setFloat("Yaw", loc.getYaw());
            location.setFloat("Pitch", loc.getPitch());
            //location.setString("World", loc.getWorld().getUID().toString());
        }
        return location;
    }

    private NBTTagCompound getFromInventory(fInventory fInventory) {
        NBTTagCompound inventory = new NBTTagCompound();
        for (ItemSlot itemSlot : EnumWrappers.ItemSlot.values()) {
        	String name = itemSlot.toString();
            ItemStack is = fInventory.getSlot(name);
            if (Objects.isNull(is)) continue;
            if (Material.AIR == is.getType()) continue;
            try {
                inventory.set(name, new CraftItemStack().getNBTTag(is));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inventory;
    }

    protected void setBitMask(boolean flag, int field, int i) {
        byte b0 = (byte) 0;
        if (getWatcher().hasIndex(field)) {
            b0 = (byte) getWatcher().getObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)));
        }
        if (flag) {
            getWatcher().setObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)), (byte) (b0 | 1 << i));
        } else {
            getWatcher().setObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)), (byte) (b0 & ~(1 << i)));
        }
    }

    public String toString(fArmorStand stand) {
        return Base64.getEncoder().encodeToString(getByte(getNBTField()));
    }

    public byte[] getByte(NBTTagCompound compound) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.write(compound, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
        return out.toByteArray();
    }
    
    public abstract fEntity copyEntity(Entity entity);
}
