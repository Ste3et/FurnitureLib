package de.Ste3et_C0st.FurnitureLib.ModelLoader.function;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class FunctionBuilder {
	
	private List<FunctionObject> functionList = new ArrayList<FunctionObject>();
	
	public FunctionBuilder(FunctionObject object) {
		this.functionList.add(object);
	}
	
	public FunctionBuilder append(FunctionObject object) {
		this.functionList.add(object);
		return this;
	}
	
	public boolean run(Player player) {
		for(FunctionObject object : functionList) {
			if(!object.run(player)) {
				return false;
			}
		}
		return true;
	}
	
}