package de.Ste3et_C0st.FurnitureLib.ModelLoader.function.entityFunctions;

import org.bukkit.Material;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Function {

	private final fEntity entity;
	private final int functionInt = 89;
	
	@SuppressWarnings("unchecked")
	public Function(fEntity fEntity) {
		this.entity = fEntity;
		if(this.entity.getCustomNBT().isEmpty() == true) return;
		
		this.entity.getCustomNBT().c().stream().forEach(key -> {
			if(String.class.isInstance(key) == true) 
			if(this.entity.getCustomNBT().hasKeyOfType((String) key, 10)) {
				final NBTTagCompound functionCompound = this.entity.getCustomNBT().getCompound((String) key);
				if(functionCompound.getInt("type", 0) == this.functionInt) {
					final String selector = functionCompound.getString("selector", "N/A");
				}
			}
		});
		
		/*
		 * function a
		 * 		type -> entityFunction
		 * 		selector -> replaceMaterial
		 * 		defaultMaterial -> STONE
		 * 		replacedBy -> StringList{
		 * 			- DÖNER
		 * 			- ....
		 *		}
		 *		consumeItem: true/false
		 *	function b
		 *		type -> entityFunction
		 *		selector -> ....
		 */
	}
	
}
