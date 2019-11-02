package de.Ste3et_C0st.FurnitureLib.ShematicLoader;

import java.io.FileNotFoundException;
import org.bukkit.configuration.file.YamlConfiguration;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelFileLoader;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class ProjectManager {

	public synchronized void loadProjectFiles(){
		ModelFileLoader.loadModelFiles();
	}

	public String getHeader(YamlConfiguration file, String fileName){
		try{
			return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];
		}catch(ArrayIndexOutOfBoundsException ex){
			return fileName;
		}
	}
}
