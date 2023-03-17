package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fBlock_display extends fDisplay{

	public static EntityType type = EntityType.BLOCK_DISPLAY;
	private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0, 0));
	private final DefaultKey<BlockData> blockDefaultKey = new DefaultKey<BlockData>(Material.AIR.createBlockData());
	
	public fBlock_display(Location loc, ObjectID id) {
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
	public fBlock_display clone() {
		final fBlock_display display = new fBlock_display(null, getObjID());
		display.copyMetadata(this);
		return display;
	}

	@Override
	public void copyMetadata(fEntity entity) {
		if(entity instanceof fBlock_display) {
			super.copyMetadata(entity);
			fBlock_display blockDisplay = this.getClass().cast(entity);
			this.setBlockData(blockDisplay.getBlockData());
		}
	}

	@Override
	public void setEntity(Entity e) {
		
	}
	
	public fBlock_display setBlockData(final Material material) {
		return this.setBlockData(material.createBlockData());
	}
	
	public fBlock_display setBlockData(final BlockData blockData) {
		this.blockDefaultKey.setValue(blockData);
		getWatcher().setObject(new WrappedDataWatcherObject(22, Registry.getBlockDataSerializer(false)), WrappedBlockData.createData(blockData));
		return this;
	}
	
	public BlockData getBlockData() {
		return this.blockDefaultKey.getOrDefault();
	}
	
	public EntitySize getEntitySize() {
		return this.entitySize.getOrDefault();
	}
	
	@Override
	public NBTTagCompound getMetaData() {
		super.getMetaData();
		if(!this.blockDefaultKey.isDefault()) setMetadata("blockData", this.blockDefaultKey.getOrDefault().getAsString());
		return getNBTField();
	}
	
	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        if(metadata.hasKeyOfType("blockData", 8)) Bukkit.createBlockData(metadata.getString("blockData"));
	}
}
