package de.Ste3et_C0st.FurnitureLib.main.entity.monster;

import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.Interactable;
import de.Ste3et_C0st.FurnitureLib.main.entity.SizeableEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fContainerEntity;

public class fMagmaCube extends fContainerEntity implements SizeableEntity, Interactable{

	public static EntityType type = EntityType.MAGMA_CUBE;
	
	private final PacketContainer attribute = new PacketContainer(PacketType.Play.Server.UPDATE_ATTRIBUTES);
	private final WrappedAttribute.Builder scaleAttribute = FurnitureLib.isVersionOrAbove("1.20.5") ? FurnitureLib.isVersionOrAbove("1.21.3") ? WrappedAttribute.newBuilder().attributeKey("scale") : WrappedAttribute.newBuilder().attributeKey("generic.scale").baseValue(1D) : null;
	private final DefaultKey<Double> scaleValue = new DefaultKey<Double>(0D);
	private final DefaultKey<Integer> slimeSize = new DefaultKey<Integer>(1);
	
	public fMagmaCube(Location loc, ObjectID id) {
		super(loc, type, 83, id);
		this.attribute.getIntegers().write(0, this.getEntityID());
	}

	@Override
	public boolean canInteractWith() {
		return false;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return getEntitySize().toBoundingBox().expand(getAttributeScale());
	}

	@Override
	public EntitySize getEntitySize() {
		return new EntitySize(0.5202 * this.slimeSize.getOrDefault(), 0.5202 * this.slimeSize.getOrDefault());
	}

	@Override
	protected Material getDestroyMaterial() {
		return Material.SHULKER_BOX;
	}

	@Override
	protected void readAdditionalSaveData(NBTTagCompound metadata) {
		super.readInventorySaveData(metadata);
		this.setScale(metadata.getDouble("scaleAttribute", 0D));
		this.setSize(metadata.getInt("slimeSize", 1));
	}

	@Override
	protected void writeAdditionalSaveData() {
		if(!this.scaleValue.isDefault()) setMetadata("scaleAttribute", this.getAttributeScale());
		if(!this.slimeSize.isDefault()) setMetadata("slimeSize", this.getSize());
	}

	@Override
	protected PacketContainer additionalData() {
		if(this.scaleAttribute == null) return null;
		this.attribute.getAttributeCollectionModifier().write(0, Arrays.asList(scaleAttribute.build()));
		return this.attribute;
	}
	
	public void setScale(double scale) {
		this.scaleValue.setValue(scale);
		if(scale != 0D) {
			scaleAttribute.baseValue(scale);
		}
	}
	
	public boolean canWriteScale() {
		return this.scaleAttribute != null;
	}
	
	public double getAttributeScale() {
		return canWriteScale() ? this.scaleAttribute.build().getFinalValue() : 0D;
	}
	
	public int getSize() {
		return this.slimeSize.getOrDefault();
	}
	
	public fMagmaCube setSize(int size) {
		size = size < 1 ? 1 : size;
		this.slimeSize.setValue(size);
		getWatcher().setObject(new WrappedDataWatcherObject(16, Registry.get(Integer.class)), size);
		return this;
	}
}
