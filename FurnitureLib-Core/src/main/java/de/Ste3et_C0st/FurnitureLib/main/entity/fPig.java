package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

public class fPig extends fContainerEntity{

    public static EntityType type = EntityType.PIG;
    private boolean saddle = false;

    @SuppressWarnings("deprecation")
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

    public NBTTagCompound getMetaData() {
    	super.getMetaData();
        setMetadata("Saddle", this.hasSaddle());
        return getNBTField();
    }
	
	@Override
	protected Material getDestroyMaterial() {
		return Material.STONE;
	}

	@Override
	protected void readAdditionalSaveData(NBTTagCompound paramCompoundTag) {
		boolean s = (paramCompoundTag.getInt("Saddle") == 1);
        this.setSaddle(s);
        super.readInventorySaveData(paramCompoundTag);
	}

	@Override
	protected void writeAdditionalSaveData() {
		setMetadata("Saddle", saddle);
	}
}
