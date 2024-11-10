package de.Ste3et_C0st.FurnitureLib.main.entity.monster;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.comphenix.protocol.wrappers.WrappedAttributeModifier;
import com.comphenix.protocol.wrappers.WrappedAttributeModifier.Operation;
import com.google.common.collect.Lists;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.Interactable;
import de.Ste3et_C0st.FurnitureLib.main.entity.SizeableEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fContainerEntity;

public class fShulker extends fContainerEntity implements SizeableEntity, Interactable{

	public static EntityType type = EntityType.SHULKER;
	private static final UUID attributeUUID = UUID.fromString("f7669d2b-b9f5-4378-86db-6cdf787df247");
	
	private final PacketContainer attribute = new PacketContainer(PacketType.Play.Server.UPDATE_ATTRIBUTES);
	private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(1.0, 1.0));
	private final WrappedAttribute.Builder scaleAttribute = FurnitureLib.isVersionOrAbove("1.20.5") ? FurnitureLib.isVersionOrAbove("1.21.3") ? WrappedAttribute.newBuilder().attributeKey("scale") : WrappedAttribute.newBuilder().attributeKey("generic.scale").baseValue(1D) : null;
	private final DefaultKey<Double> scaleValue = new DefaultKey<Double>(0D);
	
	public fShulker(Location loc, ObjectID id) {
		super(loc, type, 83, id);
		this.attribute.getIntegers().write(0, this.getEntityID());
	}

	@Override
	public boolean canInteractWith() {
		return false;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return entitySize.getOrDefault().toBoundingBox().expand(getAttributeScale());
	}

	@Override
	public EntitySize getEntitySize() {
		return entitySize.getOrDefault();
	}

	@Override
	protected Material getDestroyMaterial() {
		return Material.SHULKER_BOX;
	}

	@Override
	protected void readAdditionalSaveData(NBTTagCompound metadata) {
		super.readInventorySaveData(metadata);
		this.setScale(metadata.getDouble("scaleAttribute", 0D));
	}

	@Override
	protected void writeAdditionalSaveData() {
		if(!this.scaleValue.isDefault()) setMetadata("scaleAttribute", this.getAttributeScale());
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
}
