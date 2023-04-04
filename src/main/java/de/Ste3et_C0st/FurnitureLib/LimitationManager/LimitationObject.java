package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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
        System.out.println("tryloadDefPlayer");
        this.addDefault();
    }

    public void addDefault() {
    	try {
    		final File file = new File(LimitationManager.getLimitationFolder(), type.name().toLowerCase() + ".yml");
    		final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    		configuration.options().copyDefaults(true);
    		if (type == LimitationType.PLAYER) {
                FurnitureManager.getInstance().getProjects().forEach(pro -> {
                	configuration.addDefault("PlayerLimit." + section + ".projects." + pro.getName(), 10);
                	this.projectList.putIfAbsent(pro.getName(), configuration.getInt("PlayerLimit." + section + ".projects." + pro.getName(), -1));
                });
                configuration.addDefault("PlayerLimit." + section + ".total.enable", false);
                configuration.addDefault("PlayerLimit." + section + ".total.amount", -1);
                
                this.total = configuration.getBoolean("PlayerLimit." + section + ".total.enable", false);
                this.totalAmount = configuration.getInt("PlayerLimit." + section + ".total.amount", -1);
            }
    		
    		configuration.save(file);
    	}catch (Exception ex) {
			ex.printStackTrace();
		}
    }

    public void addDefault(String projectName) {
    	try {
    		final File file = new File(LimitationManager.getLimitationFolder(), type.name().toLowerCase() + ".yml");
    		final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    		configuration.options().copyDefaults(true);
    		if (type.equals(LimitationType.PLAYER)) {
    			configuration.addDefault("PlayerLimit." + section + ".projects." + projectName, 10);
    			projectList.putIfAbsent(projectName, configuration.getInt("PlayerLimit." + section + ".projects." + projectName, -1));
            }
    		configuration.save(file);
    	}catch (Exception ex) {
    		ex.printStackTrace();
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

}