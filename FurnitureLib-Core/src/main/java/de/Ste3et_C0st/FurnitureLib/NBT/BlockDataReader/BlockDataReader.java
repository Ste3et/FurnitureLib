package de.Ste3et_C0st.FurnitureLib.NBT.BlockDataReader;

import java.util.Optional;

import org.bukkit.block.data.BlockData;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;

public abstract class BlockDataReader {
	
	public abstract Optional<BlockData> read(NBTTagCompound compound);
	
}
