package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class ModelFileLoader {

	public static void loadModelFiles() {
		File folder = new File(FurnitureLib.isNewVersion() ? "plugins/FurnitureLib/models/" : "plugins/FurnitureLib/Crafting/");
		if(folder.exists()){
			Arrays.asList(folder.listFiles()).stream().forEach(file -> {
				loadModelFile(file);
			});
		}
	}
	
	public static void loadModelFile(File file) {
		try(InputStream stream = new FileInputStream(file)) {
			String name = file.getName().replace(".dModel", "").replace(".yml", "");
			Project pro = new Project(name, FurnitureLib.getInstance(), stream, PlaceableSide.TOP, ProjectLoader.class);
			pro.applyFunction();
			pro.setEditorProject(true);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
