package de.Ste3et_C0st.FurnitureLib.main.entity.decoration;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.persistence.PersistentDataContainer;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftBlockData;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class fFallingBlock extends fEntity{

	private DefaultKey<BlockData> blockDefaultKey = new DefaultKey<BlockData>(Material.STONE.createBlockData());
	
	public fFallingBlock(Location loc, ObjectID id) {
		super(loc, EntityType.FALLING_BLOCK, 0, id);
		
	}

	@Override
	protected Material getDestroyMaterial() {
		return Objects.nonNull(blockDefaultKey.getOrDefault()) ? blockDefaultKey.getOrDefault().getMaterial() : Material.AIR;
	}
	
	public void setBlockData(BlockData blockData) {
		this.blockDefaultKey.setValue(blockData);
		getWatcher().setObject(new WrappedDataWatcherObject(8, Registry.getBlockDataSerializer(false)), WrappedBlockData.createData(blockData));
	}

	public BlockData getBlockData() {
		return this.blockDefaultKey.getOrDefault();
	}

	@Override
	protected void readAdditionalSaveData(NBTTagCompound paramCompoundTag) {
		paramCompoundTag.getCompound("block_state", NBTTagCompound.class, compound -> {
        	CraftBlockData.getReader().ifPresent(reader -> {
        		fFallingBlock.this.setBlockData(reader.read(compound).get());
        	});
        });
	}

	@Override
	protected void writeAdditionalSaveData() {
		if(!this.blockDefaultKey.isDefault()) setMetadata("block_state", this.blockDefaultKey.getOrDefault().getAsString());
	}
}
