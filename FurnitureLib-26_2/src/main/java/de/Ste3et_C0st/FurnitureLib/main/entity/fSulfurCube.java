package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fSulfurCube extends fContainerEntity{

	public static EntityType type = EntityType.valueOf("SULFUR_CUBE");
	private DefaultKey<Boolean> babyState = new DefaultKey<Boolean>(Boolean.TRUE);
	private final PacketContainer attribute = new PacketContainer(PacketType.Play.Server.UPDATE_ATTRIBUTES);
	private final WrappedAttribute.Builder scaleAttribute = FurnitureLib.isVersionOrAbove("1.20.5") ? FurnitureLib.isVersionOrAbove("1.21.3") ? WrappedAttribute.newBuilder().attributeKey("scale").baseValue(1D) : WrappedAttribute.newBuilder().attributeKey("generic.scale").baseValue(1D) : null;
	private final DefaultKey<Double> scaleValue = new DefaultKey<Double>(0D);
	private final DefaultKey<Integer> sizeValue = new DefaultKey<Integer>(1);
	
	public fSulfurCube(Location loc, ObjectID id) {
		super(loc, type, 0, id);
		this.attribute.getIntegers().write(0, this.getEntityID());
	}

	@Override
	protected Material getDestroyMaterial() {
		return getBlockData().getMaterial().isAir() ? Material.SULFUR : getBlockData().getMaterial();
	}
	
	public void setBaby(boolean state) {
		getWatcher().setObject(new WrappedDataWatcherObject(16, Registry.get(Boolean.class)), state);
		this.babyState.setValue(state);
	}
	
	public boolean isBaby() {
		return this.babyState.getOrDefault();
	}

	public void setBlockData(BlockData blockData) {
		this.setBody(blockData.getMaterial().asItemType().createItemStack());
	}

	public BlockData getBlockData() {
		return Objects.isNull(this.getInventory().getChestPlate()) ? Material.AIR.createBlockData() : this.getInventory().getChestPlate().getType().isBlock() ? this.getInventory().getChestPlate().getType().createBlockData() : Material.AIR.createBlockData();
	}
	
	public void setSize(int i) {
		i = i < 0 ? 0 : i > 3 ? 3 : i;
		getWatcher().setObject(new WrappedDataWatcherObject(18, Registry.get(Integer.class)), i);
		this.sizeValue.setValue(i);
	}

	@Override
	protected void readAdditionalSaveData(NBTTagCompound metadata) {
		super.readInventorySaveData(metadata);
		this.setBaby(metadata.getInt("IsBaby", 1) == 1);
		this.setSize(metadata.getInt("Size", 1));
		this.setScale(metadata.getDouble("scaleAttribute", 0D));
	}

	@Override
	protected void writeAdditionalSaveData() {
		super.writeInventoryData();
		if(!this.scaleValue.isDefault()) setMetadata("scaleAttribute", this.getScale());
		if(!this.sizeValue.isDefault()) setMetadata("Size", this.getScale());
		if(!this.babyState.isDefault()) setMetadata("IsBaby", this.babyState.getOrDefault());
	}
	
	public boolean canWriteScale() {
		return this.scaleAttribute != null;
	}
	
	public double getScale() {
		return canWriteScale() ? this.scaleAttribute.build().getFinalValue() : 0D;
	}
	
	@Override
	protected PacketContainer additionalData() {
		if(this.scaleAttribute == null) return null;
		this.attribute.getAttributeCollectionModifier().write(0, Arrays.asList(scaleAttribute.build()));
		return this.attribute;
	}
	
	public fSulfurCube setScale(double scale) {
		this.scaleValue.setValue(scale);
		if(scale != 0D) {
			scaleAttribute.baseValue(scale);
		}
		return this;
	}
}
