package de.Ste3et_C0st.FurnitureLib.SchematicLoader.modularFunctions;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureHelper;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class modularFunctionHandler {

	private static HashSet<modularFunction> modularFunctions = new HashSet<modularFunction>();
	
	static {
		modularFunctions.add(new lightFunction());
	}
	
	public static boolean performEntityActions(FurnitureHelper helper) {
		AtomicBoolean returnValue = new AtomicBoolean(false);
		
		modularFunctions.stream().forEach(entry -> {
			helper.getfAsList().stream().filter(fEntity::hasCustomName).filter(entry::testPredicate).forEach(entity -> {
				
			});
		});
		
		return returnValue.get();
	}
	
}
