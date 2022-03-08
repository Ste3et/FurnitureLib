package de.Ste3et_C0st.FurnitureLib.ModelLoader.Block;

import java.util.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelVector;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.state.ModelBlockSkullState;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.state.ModelBlockState;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.functions.projectFunction;

public abstract class ModelBlock {

    protected ModelVector vector;
    protected ModelBlockState blockState = null;
    
    public ModelBlock(ModelVector vector) {
        this.vector = vector;
    }

    public ModelBlock(YamlConfiguration yamlConfiguration, String key) {
    	
    }

    public abstract Material getMaterial();

    public abstract void place(Location loc);
    public abstract void place(Location loc, BlockFace face);
    
    public ModelVector getVector() {
        return this.vector;
    }
    
    public boolean haveBlockState() {
    	return Objects.nonNull(blockState);
    }
    
    protected void applyBlockState(Location loc) {
    	if(haveBlockState()) {
        	BlockState state = loc.getBlock().getState();
        	if(ModelBlockSkullState.class.isInstance(this.blockState)) {
        		ModelBlockSkullState.class.cast(this.blockState).updateState(state);
        	}
        }
    }
    
   
}
