package de.Ste3et_C0st.FurnitureLib.NBT;

import de.Ste3et_C0st.FurnitureLib.NBT.BlockDataReader.BlockDataConverter1_20;
import de.Ste3et_C0st.FurnitureLib.NBT.BlockDataReader.BlockDataConverter1_20_5;
import de.Ste3et_C0st.FurnitureLib.NBT.BlockDataReader.BlockDataReader;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import com.comphenix.protocol.utility.MinecraftVersion;

import java.util.Objects;
import java.util.Optional;

public class CraftBlockData {

	private final static BlockDataReader READER;
	
	static {
		if(FurnitureLib.getVersion(new MinecraftVersion("1.20.5"))) {
			READER = new BlockDataConverter1_20_5();
		}else if(FurnitureLib.getVersion(new MinecraftVersion("1.20.3"))) {
			READER = new BlockDataConverter1_20();
		}else {
			READER = null;
		}
	}
	
	public Optional<BlockData> read(NBTTagCompound compound){
		if(Objects.isNull(READER)) return Optional.empty();
		return READER.read(compound);
	}
	
	public BlockData readData(NBTTagCompound compound){
		if(Objects.isNull(READER)) return Material.AIR.createBlockData();
		return READER.read(compound).orElse(Material.AIR.createBlockData()) ;
	}
	
	
	public static Optional<BlockDataReader> getReader() {
		return Optional.ofNullable(READER);
	}
}