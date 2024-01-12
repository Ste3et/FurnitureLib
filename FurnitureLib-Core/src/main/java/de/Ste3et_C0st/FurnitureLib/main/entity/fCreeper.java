package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class fCreeper extends fContainerEntity{

    public static EntityType type = EntityType.CREEPER;
    private boolean charged = false, ignited = false;
    
    @SuppressWarnings("deprecation")
    public fCreeper(Location loc, ObjectID obj) {
        super(loc, type, type.getTypeId(), obj);
    }

    public boolean isCharged() {
        return charged;
    }

    public fCreeper setCharged(boolean b) {
        getWatcher().setObject(new WrappedDataWatcherObject(12, Registry.get(Boolean.class)), b);
        this.charged = b;
        return this;
    }

    public boolean isIgnited() {
        return ignited;
    }

    public fCreeper setIgnited(boolean b) {
        getWatcher().setObject(new WrappedDataWatcherObject(13, Registry.get(Boolean.class)), b);
        this.ignited = b;
        return this;
    }

    @Override
    protected void writeAdditionalSaveData() {
        setMetadata("Ignite", this.isIgnited());
        setMetadata("Charged", this.isCharged());
    }

    @Override
    protected void readAdditionalSaveData(NBTTagCompound metadata) {
        boolean i = (metadata.getInt("Ignite") == 1), f = (metadata.getInt("Charged") == 1);
        this.setIgnited(i).setCharged(f);
    }
	
	@Override
	protected Material getDestroyMaterial() {
		return Material.TNT;
	}
	
    public fCreeper copyEntity(Entity entity) {
    	super.copyEntity(entity);
    	if(entity instanceof LivingEntity) {
    		this.setEntityEquipment(((LivingEntity) entity).getEquipment());
    	}
    	return this;
    }
}
