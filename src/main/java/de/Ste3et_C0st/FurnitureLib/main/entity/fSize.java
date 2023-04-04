package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract class fSize extends fEntity {

	private final DefaultKey<Float> width, height;
	private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0, 0));
	
	public fSize(Location loc, EntityType type, int entityID, ObjectID id, float width, float height) {
		super(loc, type, entityID, id);
		this.width = new DefaultKey<Float>(width);
		this.height = new DefaultKey<Float>(height);
	}

	protected abstract int widthField();
	protected abstract int heightField();
	
	@Override
	public void copyMetadata(final fEntity entity) {
		if(entity instanceof fSize) {
			final fSize fEntity = this.getClass().cast(entity);
			fEntity.setWidth(this.getHeight());
			fEntity.setHeight(this.getWidth());
		}
	}
	
	public fSize setWidth(final float width) {
		this.width.setValue(width);
		this.entitySize.setValue(new EntitySize(this.getWidth(), this.getHeight()));
		getWatcher().setObject(new WrappedDataWatcherObject(widthField(), Registry.get(Float.class)), this.getWidth());
		return this;
	}
	
	public fSize setHeight(final float height) {
		this.height.setValue(height);
		this.entitySize.setValue(new EntitySize(this.getWidth(), this.getHeight()));
		getWatcher().setObject(new WrappedDataWatcherObject(heightField(), Registry.get(Float.class)), this.getHeight());
		return this;
	}
	
	public float getWidth() {
		return this.width.getOrDefault();
	};
	
	public float getHeight() {
		return this.height.getOrDefault();
	};
	
	public EntitySize getEntitySize() {
		return this.entitySize.getOrDefault();
	}
	
	@Override
	public NBTTagCompound getMetaData() {
		super.getMetaData();
		if(!this.width.isDefault()) setMetadata("width", this.width.getOrDefault());
		if(!this.height.isDefault()) setMetadata("height", this.height.getOrDefault());
		return getNBTField();
	}
	
	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        if(metadata.hasKeyOfType("width", 5)) this.setWidth(metadata.getFloat("width"));
        if(metadata.hasKeyOfType("height", 5)) this.setHeight(metadata.getFloat("height"));
	}
	
}
