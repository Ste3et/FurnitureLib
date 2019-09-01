package de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions;

import java.util.HashSet;

public class functionManager {

	@SuppressWarnings("serial")
	private static HashSet<projectFunction> functions = new HashSet<projectFunction>() {{
		add(new recolorFunction());
		add(new replaceFunction());
	}};
	
	public static void addFunction(projectFunction function) {
		if(!functions.contains(function)) functions.add(function);
	}
	
	public static HashSet<projectFunction> getFunctions() {
		return functions;
	}
	
	public static projectFunction getByName(String name) {
		return functions.stream().filter(function -> function.getClass().getSimpleName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static boolean containsByName(String name) {
		return getByName(name) != null;
	}
	
}
