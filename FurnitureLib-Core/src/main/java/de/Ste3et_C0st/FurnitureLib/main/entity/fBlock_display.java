package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftBlockData;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fBlock_display extends fDisplay{

	public static EntityType type = EntityType.valueOf("BLOCK_DISPLAY");
	private final DefaultKey<BlockData> blockDefaultKey = new DefaultKey<BlockData>(Material.AIR.createBlockData());
	
	public fBlock_display(Location loc, ObjectID id) {
		super(loc, type, 0, id);
	}
	
	@Override
	public fBlock_display copyEntity(Entity entity) {
		super.copyEntity(entity);
		if(entity instanceof BlockDisplay) {
			final BlockDisplay display = BlockDisplay.class.cast(entity);
			this.setBlockData(display.getBlock());
		}
		return this;
	}
	
	public fBlock_display setBlockData(final Material material) {
		return this.setBlockData(material.createBlockData());
	}
	
	public fBlock_display setBlockData(final BlockData blockData) {
		this.blockDefaultKey.setValue(blockData);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 22, Registry.getBlockDataSerializer(false)), WrappedBlockData.createData(blockData));
		return this;
	}
	
	public BlockData getBlockData() {
		return this.blockDefaultKey.getOrDefault();
	}
	
	@Override
	protected void writeAdditionalSaveData() {
		super.writeDisplaySaveData();
		if(!this.blockDefaultKey.isDefault()) setMetadata("block_state", this.blockDefaultKey.getOrDefault().getAsString());
	}
	
	@Override
	protected void readAdditionalSaveData(NBTTagCompound metadata) {
		super.readDisplayData(metadata);
        if(metadata.hasKeyOfType("blockData", 8)) this.setBlockData(Bukkit.createBlockData(metadata.getString("blockData")));
        if(metadata.hasKeyOfType("block_state", 8)) {
        	this.setBlockData(Bukkit.createBlockData(metadata.getString("block_state")));
        }
        
        metadata.getCompound("block_state", NBTTagCompound.class, compound -> {
        	CraftBlockData.getReader().ifPresent(reader -> {
        		fBlock_display.this.setBlockData(reader.read(compound).get());
        	});
        });
	}
	
	@Override
	protected Material getDestroyMaterial() {
		return getBlockData().getMaterial();
	}
}
