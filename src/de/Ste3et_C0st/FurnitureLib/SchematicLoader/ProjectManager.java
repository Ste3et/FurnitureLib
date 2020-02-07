package de.Ste3et_C0st.FurnitureLib.SchematicLoader;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelFileLoader;
import org.bukkit.configuration.file.YamlConfiguration;

public class ProjectManager {

    public synchronized void loadProjectFiles() {
        ModelFileLoader.loadModelFiles();
    }

    public String getHeader(YamlConfiguration file, String fileName) {
        try {
            return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return fileName;
        }
    }
}
