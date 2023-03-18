package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fInteraction extends fEntity{

	public static EntityType type = EntityType.INTERACTION;
	private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0, 0));
	private final DefaultKey<Float> width = new DefaultKey<Float>(1F), height = new DefaultKey<Float>(1F);
	private final DefaultKey<Boolean> response = new DefaultKey<Boolean>(false);
	
	public fInteraction(Location loc, ObjectID id) {
		super(loc, type, 0, id);
	}
	
	@Override
	public Entity toRealEntity() {
		return null;
	}

	@Override
	public boolean isRealEntity() {
		return false;
	}

	@Override
	public fEntity clone() {
		final fInteraction interaction = new fInteraction(null, getObjID());
		interaction.copyMetadata(this);
		return interaction;
	}

	@Override
	public void copyMetadata(fEntity entity) {
		if(entity instanceof fInteraction) {
			fInteraction interaction = (fInteraction) entity; 
			this.setDimansion(interaction.getWidth(), interaction.getHeight());
			this.setResponse(interaction.hasResponse());
		}
	}

	@Override
	public void setEntity(Entity e) {
		
	}
	
	public float getWidth() {
		return this.width.getOrDefault();
	}
	
	public float getHeight() {
		return this.height.getOrDefault();
	}
	
	public fInteraction setDimansion(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
		return this;
	}
	
	public fInteraction setWidth(final float width) {
		this.width.setValue(width);
		//this.entitySize.setValue(new EntitySize(this.getWidth(), this.entitySize.getValue().getHeight()));
		getWatcher().setObject(new WrappedDataWatcherObject(8, Registry.get(Float.class)), this.getWidth());
		return this;
	}
	
	public fInteraction setHeight(final float width) {
		this.height.setValue(width);
		//this.entitySize.setValue(new EntitySize(this.entitySize.getValue().getWidth(), this.getHeight()));
		getWatcher().setObject(new WrappedDataWatcherObject(9, Registry.get(Float.class)), this.getHeight());
		return this;
	}
	
	public fInteraction setResponse(final boolean bool) {
		this.response.setValue(bool);
		getWatcher().setObject(new WrappedDataWatcherObject(10, Registry.get(Boolean.class)), this.hasResponse());
		return this;
	}
	
	public EntitySize getEntitySize() {
		return this.entitySize.getOrDefault();
	}
	
	public Boolean hasResponse() {
		return this.response.getOrDefault();
	}

	@Override
	public NBTTagCompound getMetaData() {
		super.getMetaData();
		if(!this.width.isDefault()) setMetadata("width", this.width.getOrDefault());
		if(!this.height.isDefault()) setMetadata("height", this.height.getOrDefault());
		if(!this.response.isDefault()) setMetadata("response", this.response.getOrDefault());
		return getNBTField();
	}
	
	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        if(metadata.hasKeyOfType("width", 5)) this.setWidth(metadata.getFloat("width"));
        if(metadata.hasKeyOfType("height", 5)) this.setHeight(metadata.getFloat("height"));
        if(metadata.hasKeyOfType("blockData", 3)) this.setResponse(metadata.getInt("blockData") == 1);
	}
	
	@Override
	protected Material getDestroyMaterial() {
		return Material.AIR;
	}
}
