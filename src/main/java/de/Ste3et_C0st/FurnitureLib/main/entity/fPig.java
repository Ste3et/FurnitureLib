package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

public class fPig extends fEntity {

    public static EntityType type = EntityType.PIG;
    private boolean saddle = false;
    private Pig entity;

    public fPig(Location loc, ObjectID obj) {
        super(loc, type, type.getTypeId(), obj);
    }

    public fPig setSaddle(boolean b) {
        getWatcher().setObject(new WrappedDataWatcherObject(12, Registry.get(Boolean.class)), b);
        this.saddle = b;
        return this;
    }

    public boolean hasSaddle() {
        return this.saddle;
    }

    public Pig toRealEntity() {
        if (entity != null) {
            if (!entity.isDead()) {
                return entity;
            }
        }
        entity = (Pig) getObjID().getWorld().spawnEntity(getLocation(), getEntityType());
        entity.setSaddle(saddle);
        return entity;
    }

    public boolean isRealEntity() {
		return entity != null;
	}

    public void setEntity(Entity entity) {
        if (entity instanceof Pig) this.entity = (Pig) entity;
    }

    public NBTTagCompound getMetaData() {
        getDefNBT(this);
        setMetadata("Saddle", this.hasSaddle());
        return getNBTField();
    }

    @Override
    public void loadMetadata(NBTTagCompound metadata) {
        loadDefMetadata(metadata);
        boolean s = (metadata.getInt("Saddle") == 1);
        this.setSaddle(s);
    }

    @Override
    public fEntity clone() {
        return null;
    }
}
