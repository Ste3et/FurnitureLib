package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract class fSize extends fEntity {

	private final DefaultKey<Float> width, height;
	protected final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0, 0, 0));
	
	public fSize(Location loc, EntityType type, int entityID, ObjectID id, float width, float height) {
		super(loc, type, entityID, id);
		this.width = new DefaultKey<Float>(width);
		this.height = new DefaultKey<Float>(height);
	}

	protected abstract int widthField();
	protected abstract int heightField();
	
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
	
	protected void writeSizeData() {
		if(!this.width.isDefault()) setMetadata("width", this.width.getOrDefault());
		if(!this.height.isDefault()) setMetadata("height", this.height.getOrDefault());
	}
	
	protected void readSizeData(NBTTagCompound metadata) {
        this.setWidth(metadata.getFloat("width", this.width.getDefault()));
        this.setHeight(metadata.getFloat("height", this.height.getDefault()));
	}
	
	public BoundingBox getBoundingBox() {
		return BoundingBox.of(new Vector(), new Vector(getWidth(), getHeight(), getWidth()));
	}
}
