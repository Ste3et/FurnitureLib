package de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.state;

import org.bukkit.block.BlockState;

public class ModelBlockState {

	public ModelBlockState() {
		
	}
	
	public void updateState(BlockState state) {
		state.update(false, false);
	}
	
}
