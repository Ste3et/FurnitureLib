package de.Ste3et_C0st.FurnitureLib.SchematicLoader;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelFileLoader;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ExecuteTimer;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

import org.bukkit.configuration.file.YamlConfiguration;

public class ProjectManager {

    public synchronized void loadProjectFiles() {
    	ExecuteTimer timer = new ExecuteTimer();
        ModelFileLoader.loadModelFiles();
        System.out.println("FurnitureLib Load " + FurnitureManager.getInstance().getProjects().size() + " model schematics into Ram. Took " + timer.getMilliString());
    }

    public String getHeader(YamlConfiguration file, String fileName) {
        try {
            return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return fileName;
        }
    }
}
