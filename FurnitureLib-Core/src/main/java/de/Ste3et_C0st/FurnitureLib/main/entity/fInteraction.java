package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fInteraction extends fSize implements Interactable{

	public static EntityType type = EntityType.valueOf("INTERACTION");
	private final DefaultKey<Boolean> response = new DefaultKey<Boolean>(false);
	
	public fInteraction(Location loc, ObjectID id) {
		super(loc, type, 0, id, 1F, 1F);
	}

	@Override
	public fInteraction copyEntity(Entity entity) {
    	super.copyEntity(entity);
    	if(entity instanceof Interaction) {
    		Interaction interaction = Interaction.class.cast(entity);
    		this.setDimansion(interaction.getInteractionWidth(), interaction.getInteractionWidth());
    		this.setResponse(interaction.isResponsive());
    	}
    	return this;
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

	protected void writeAdditionalSaveData() {
		super.writeSizeData();
		if(!this.response.isDefault()) setMetadata("response", this.response.getOrDefault());
	}
	
	@Override
	protected void readAdditionalSaveData(NBTTagCompound metadata) {
        super.readSizeData(metadata);
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

	@Override
	public boolean canInteractWith() {
		return this.getWidth() > 0f && this.getHeight() > 0f;
	}
}
