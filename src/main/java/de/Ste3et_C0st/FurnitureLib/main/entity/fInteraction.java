package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fInteraction extends fSize{

	public static EntityType type = EntityType.INTERACTION;
	private final DefaultKey<Boolean> response = new DefaultKey<Boolean>(false);
	
	public fInteraction(Location loc, ObjectID id) {
		super(loc, type, 0, id, 1F, 1F);
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
		this.copyMetadata(interaction);
		return interaction;
	}

	@Override
	public void copyMetadata(final fEntity entity) {
		if(entity instanceof fInteraction) {
			super.copyMetadata(entity);
			final fInteraction interaction = (fInteraction) entity; 
			interaction.setDimansion(this.getWidth(), this.getHeight());
			interaction.setResponse(this.hasResponse());
		}
	}

	@Override
	public void setEntity(Entity e) {
		
	}
	
	public fInteraction setDimansion(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
		return this;
	}
	
	public fInteraction setResponse(final boolean bool) {
		this.response.setValue(bool);
		getWatcher().setObject(new WrappedDataWatcherObject(10, Registry.get(Boolean.class)), this.hasResponse());
		return this;
	}
	
	public Boolean hasResponse() {
		return this.response.getOrDefault();
	}

	@Override
	public NBTTagCompound getMetaData() {
		super.getMetaData();
		if(!this.response.isDefault()) setMetadata("response", this.response.getOrDefault());
		return getNBTField();
	}
	
	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        if(metadata.hasKeyOfType("response", 3)) this.setResponse(metadata.getInt("response") == 1);
	}
	
	@Override
	protected Material getDestroyMaterial() {
		return Material.AIR;
	}

	@Override
	protected int widthField() {
		return 8;
	}

	@Override
	protected int heightField() {
		return 9;
	}
}
