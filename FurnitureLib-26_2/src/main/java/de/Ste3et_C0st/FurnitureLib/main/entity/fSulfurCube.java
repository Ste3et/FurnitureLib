package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftBlockData;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fSulfurCube extends fEntity{

	public static EntityType type = EntityType.valueOf("SULFUR_CUBE");
	private DefaultKey<BlockData> blockDefaultKey = new DefaultKey<BlockData>(Material.AIR.createBlockData());
	
	public fSulfurCube(Location loc, ObjectID id) {
		super(loc, type, 0, id);
	}

	@Override
	protected Material getDestroyMaterial() {
		return blockDefaultKey.getOrDefault().getMaterial();
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
        		fSulfurCube.this.setBlockData(reader.read(compound).get());
        	});
        });
	}

	@Override
	protected void writeAdditionalSaveData() {
		if(!this.blockDefaultKey.isDefault()) setMetadata("block_state", this.blockDefaultKey.getOrDefault().getAsString());
	}
	
	@Override
	protected PacketContainer additionalData() {
		return null;
	}
}
