package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class LimitationObject {

    public HashMap<String, Integer> projectList = new HashMap<String, Integer>();
    public boolean total = false, def = false, global = false;
    public int totalAmount = -1;
    public String permission = "";
    public String section = "";
    public LimitationType type = null;

    public LimitationObject(LimitationType type, String section) {
        this.type = type;
        this.section = section;
        if (type.equals(LimitationType.PLAYER)) {
            if (section.equalsIgnoreCase("default")) {
                def = true;
            } else {
                permission = "furniture.limit." + section;
            }
        }
        addDefault();
        loadProjects();
        loadTotal();
    }

    public void addDefault() {
        config c = new config(FurnitureLib.getInstance());
        FileConfiguration file = c.getConfig(type.name().toLowerCase(), "/limitation/");
        if (type.equals(LimitationType.PLAYER)) {
            FurnitureManager.getInstance().getProjects().forEach(pro -> {
                file.addDefault("PlayerLimit." + section + ".projects." + pro.getName(), 10);
            });
            file.addDefault("PlayerLimit." + section + ".total.enable", false);
            file.addDefault("PlayerLimit." + section + ".total.amount", -1);
        }
        file.options().copyDefaults(true);
        c.saveConfig(type.name().toLowerCase(), file, "/limitation/");
    }

    public void loadProjects() {
        config c = new config(FurnitureLib.getInstance());
        FileConfiguration file = c.getConfig(type.name().toLowerCase(), "/limitation/");
        if (file.isConfigurationSection("PlayerLimit." + section + ".projects")) {
            for (String s : file.getConfigurationSection("PlayerLimit." + section + ".projects").getKeys(false)) {
                int i = file.getInt("PlayerLimit." + section + ".projects." + s, -1);
                if (!projectList.containsKey(s)) {
                    projectList.put(s, i);
                }
            }
        }
    }

    public void addDefault(String projectName) {
        config c = new config(FurnitureLib.getInstance());
        FileConfiguration file = c.getConfig(type.name().toLowerCase(), "/limitation/");
        if (type.equals(LimitationType.PLAYER)) {
            file.addDefault("PlayerLimit." + section + ".projects." + projectName, 10);
        }
        file.options().copyDefaults(true);
        c.saveConfig(type.name().toLowerCase(), file, "/limitation/");
    }

    public void loadProjects(String projectName) {
        config c = new config(FurnitureLib.getInstance());
        FileConfiguration file = c.getConfig(type.name().toLowerCase(), "/limitation/");
        if (!projectList.containsKey(projectName)) {
            int i = file.getInt("PlayerLimit." + section + ".projects." + projectName, -1);
            projectList.put(projectName, i);
        }
    }

    public int getAmountFromType(String s) {
        for (String name : this.projectList.keySet()) {
            if (name.equalsIgnoreCase(s)) {
                return this.projectList.get(s);
            }
        }
        return 0;
    }

    public void loadTotal() {
        config c = new config(FurnitureLib.getInstance());
        FileConfiguration file = c.getConfig(type.name().toLowerCase(), "/limitation/");
        if (file.getBoolean("PlayerLimit." + section + ".total.enable", false)) {
            this.total = true;
            this.totalAmount = file.getInt("PlayerLimit." + section + ".total.amount");
        }
    }

}