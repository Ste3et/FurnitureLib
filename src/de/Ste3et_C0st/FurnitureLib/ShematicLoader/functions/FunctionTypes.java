package de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions;

import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class FunctionTypes{
	
	public enum FunctionType{
		RECOLOR("recolor", new recolorFunction());
		
		String functionName = "";
		projectFunction function = null;
		
		FunctionType(String functionName, projectFunction function){
			this.functionName = functionName;
			this.function = function;
		}
		
		public String getFunctionName() {
			return this.functionName;
		}
		
		public boolean parse(JsonObject jsonObject, ObjectID id, Player p) {
			if(this.function != null) {
				return this.function.parse(jsonObject, id, p);
			}
			return false;
		}
	}
	
}