package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class fCreeper extends fContainerEntity{

    public static EntityType type = EntityType.CREEPER;
    private boolean charged = false, ignited = false;
    private Creeper entity = null;
    
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

    public Creeper toRealEntity() {
        if (entity != null) {
            if (!entity.isDead()) {
                return entity;
            }
        }
        entity = (Creeper) getObjID().getWorld().spawnEntity(getLocation(), getEntityType());
        entity.setPowered(charged);

        return entity;
    }

    public boolean isRealEntity() {
		return entity != null;
	}

    public void setEntity(Entity entity) {
        if (entity instanceof Creeper) this.entity = (Creeper) entity;
    }

    @Override
    public NBTTagCompound getMetaData() {
        super.getMetaData();
        setMetadata("Ignite", this.isIgnited());
        setMetadata("Charged", this.isCharged());
        return getNBTField();
    }

    @Override
    public void loadMetadata(NBTTagCompound metadata) {
    	super.loadMetadata(metadata);
        boolean i = (metadata.getInt("Ignite") == 1), f = (metadata.getInt("Charged") == 1);
        this.setIgnited(i).setCharged(f);
    }

    @Override
    public fEntity clone() {
        return null;
    }

	@Override
	public void copyMetadata(fEntity entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Material getDestroyMaterial() {
		return Material.TNT;
	}
}
