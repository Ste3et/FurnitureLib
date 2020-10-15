package de.Ste3et_C0st.FurnitureLib.Crafting;

import com.google.gson.JsonObject;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelHandler;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
//import de.Ste3et_C0st.FurnitureLib.Utilitis.ExecuteTimer;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.PermissionHandler;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Project {
    private String project;
    private CraftingFile file;
    private Plugin plugin;
    
    private Integer chunkLimit = -1;
    private config limitationConfig;
    private FileConfiguration limitationFile;
    private HashMap<World, Integer> limitationWorld = new HashMap<World, Integer>();
    private List<JsonObject> functionList;
    private CenterType type = CenterType.RIGHT;
    private boolean EditorProject = false, silent = false;
    private ModelHandler modelschematic = null;
    private Function<ObjectID, Furniture> furnitureObject = ProjectLoader::new;

    /**
     * Create a new Project instance load the modelFile and calculate the boundingbox.
     *
     * @param name This is the Internal SystemName
     * @param plugin The Plugin who register the Project
     * @param craftingFile Recipe File for the crafting recipe and the spawn item
     * @param side The placeable side of the Furniture
     * @param clazz The Function class for the Project
     * @return Project return this Object
     */
    public Project(String name, Plugin plugin, InputStream craftingFile, PlaceableSide side, Class<? extends Furniture> clazz) {
        //ExecuteTimer timer = new ExecuteTimer();
    	this.project = name;
        this.plugin = plugin;
        
        File configFile = new File(CraftingFile.getPath(name));
    	YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    	//System.out.println("Project start -> " + timer.getMilliString());
		if(config.getBoolean(name + ".enabled", true) == false) {
    		return;
    	}
		
		//System.out.println("CraftingFile start -> " + timer.getMilliString());
        this.file = new CraftingFile(name, craftingFile, config);
        //System.out.println("CraftingFile finish -> " + timer.getMilliString());
        
        if(!this.file.isEnabledModel()) {
        	return;
        }
        
        //System.out.println("Loadfunction (WIP) -> " + timer.getMilliString());
        this.functionList = this.file.loadFunction();
        
        this.furnitureObject = objectID -> {
        	try {
        		return clazz.getConstructor(objectID.getClass()).newInstance(objectID);
        	}catch(Exception ex) {
        		ex.printStackTrace();
        	}
        	return null;
        };
        
        try {
            if (Objects.nonNull(this.file)) {
            	//System.out.println("Use Modelhandler filepath " + timer.getMilliString());
                this.modelschematic = new ModelHandler(this.file.getFilePath());
            } else {
            	//System.out.println("Use Modelhandler project " + timer.getMilliString());
                this.modelschematic = new ModelHandler(this.project);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Porject Modelhandler loading finish " + timer.getMilliString());
        FurnitureLib.getInstance().getFurnitureManager().addProject(this);
        // System.out.println("Porject has been added " + timer.getMilliString());
        this.loadDefaults();
        FurnitureLib.getInstance().getLimitManager().loadDefault(this.project);
        //System.out.println("Porject is finish " + timer.getMilliString());
        PermissionHandler.registerPermission("furniture.craft.*","furniture.craft." + name.toLowerCase());
        PermissionHandler.registerPermission("furniture.place.*","furniture.place." + name.toLowerCase());
        PermissionHandler.registerPermission("furniture.sit.*","furniture.sit." + name.toLowerCase());
    }

    public Project(String name, Plugin plugin, InputStream craftingFile, Class<? extends Furniture> clazz) {
        this(name, plugin, craftingFile, PlaceableSide.TOP, Objects.isNull(clazz) ? ProjectLoader.class : clazz);
    }

    public Project(String name, Plugin plugin, InputStream craftingFile) {
        this(name, plugin, craftingFile, PlaceableSide.TOP, ProjectLoader.class);
    }

    public String getName() {
        return project;
    }

    public Project setName(String name) {
        this.project = name;
        return this;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public CraftingFile getCraftingFile() {
        return file;
    }

    public Project setCraftingFile(CraftingFile file) {
        this.file = file;
        return this;
    }

    public CenterType getCenterType() {
        return this.type;
    }

    public PlaceableSide getPlaceableSide() {
        return getModelschematic().getPlaceableSide();
    }

    public Project setPlaceableSide(PlaceableSide side) {
    	if(Objects.nonNull(this.modelschematic)) this.modelschematic.setPlaceableSide(side);
        return this;
    }

    public String getSystemID() {
        return getCraftingFile().getSystemID();
    }

    public boolean isEditorProject() {
        return this.EditorProject;
    }

    public Project setEditorProject(boolean b) {
        this.EditorProject = b;
        return this;
    }

//    public Class<? extends Furniture> getFunctionClazz() {
//        return this.clazz;
//    }

    public List<JsonObject> getFunctions() {
        return this.functionList;
    }

    public boolean isSilent() {
        return this.silent;
    }
    
    public boolean haveModelSchematic() {
    	return Objects.nonNull(modelschematic);
    }

    public void setSilent(boolean b) {
        silent = b;
    }

    public int getWidth() {
        BoundingBox box = getModelschematic().getBoundingBox();
        int width = Math.abs(box.getMax().getBlockX() - box.getMin().getBlockX());
        return width + 1;
    }

    public int getHeight() {
        BoundingBox box = getModelschematic().getBoundingBox();
        return (int) box.getHeight() + 1;
    }

    public int getLength() {
        BoundingBox box = getModelschematic().getBoundingBox();
        int length = Math.abs(box.getMax().getBlockZ() - box.getMin().getBlockZ());
        return length + 1;
    }

    public int getAmountWorld(World w) {
		return limitationWorld.getOrDefault(w, -1);
    }

    public int getAmountChunk() {
        return this.chunkLimit;
    }

    public Project setSize(Integer length, Integer height, Integer width, CenterType type) {
        if (Objects.nonNull(getModelschematic())) {
            length = length - 1;
            height = height - 1;
            width = width - 1;

            Vector pos1 = new Vector();
            Vector pos2 = new Vector(width, height, length);

            if (type.equals(CenterType.RIGHT)) {
                pos2.setZ(-length);
            } else if (type.equals(CenterType.CENTER)) {
                width = Math.round((width) / 2);
                pos1.setX(-width);
                pos2.setX(width);
                pos2.setZ(-length);
            }
            if (type.equals(CenterType.LEFT)) {
                pos2.setZ(-length);
                pos2.setX(-width);
            }
            getModelschematic().setSize(pos1, pos2);
            this.type = type;
        }
        return this;
    }

    public ModelHandler getModelschematic() {
        return this.modelschematic;
    }

    public boolean hasPermissions(Player player) {
    	String name = this.getName().toLowerCase();
    	if(FurnitureLib.getInstance().getPermission().hasPerm(player, "furniture.place." + name)) return true;
        if (FurnitureLib.getInstance().getPermissionList() != null) {
            for (String s : FurnitureLib.getInstance().getPermissionList().keySet()) {
                if (FurnitureLib.getInstance().getPermission().hasPerm(player, "furniture.place.all." + s)) {
                    if (FurnitureLib.getInstance().getPermissionList().get(s).contains(this.getName())) {
                        return true;
                    }
                }
            }
        }
        player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NoPermissions"));
        return false;
    }

    public void loadDefaults() {
        addDefaultWorld();
        addDefault("chunk");
        addDefault("player");
        this.chunkLimit = getDefault("chunk");
    }

    private void addDefaultWorld() {
        this.limitationConfig = new config(FurnitureLib.getInstance());
        this.limitationFile = this.limitationConfig.getConfig("world", "/limitation/");
        for (World w : Bukkit.getWorlds()) {
            if (w == null || getSystemID() == null)
                continue;
            if (getSystemID().isEmpty())
                continue;
            this.limitationFile.addDefault("Projects." + w.getName() + "." + getSystemID(), -1);
        }
        this.limitationFile.options().copyDefaults(true);
        this.limitationConfig.saveConfig("world", this.limitationFile, "/limitation/");
        getAmountWorld();
    }

    private void getAmountWorld() {
        this.limitationConfig = new config(FurnitureLib.getInstance());
        this.limitationFile = this.limitationConfig.getConfig("world", "/limitation/");
        for (World w : Bukkit.getWorlds()) {
            Integer i = this.limitationFile.getInt("Projects." + w.getName() + "." + getSystemID());
            limitationWorld.put(w, i);
        }
    }

    private void addDefault(String conf) {
        this.limitationConfig = new config(FurnitureLib.getInstance());
        this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
        if (conf.equalsIgnoreCase("chunk")) {
            if (!this.limitationFile.isSet("ChunkLimit.total")) {
                this.limitationFile.addDefault("ChunkLimit.total.enable", false);
                this.limitationFile.addDefault("ChunkLimit.total.amount", -1);
                this.limitationFile.addDefault("ChunkLimit.total.global", false);
            }

            if (!this.limitationFile.isSet("ChunkLimit.projects." + getSystemID())) {
                this.limitationFile.addDefault("ChunkLimit.projects." + getSystemID(), -1);
            }
        }
        this.limitationFile.options().copyDefaults(true);
        this.limitationConfig.saveConfig(conf, this.limitationFile, "/limitation/");
    }

    private Integer getDefault(String conf) {
        this.limitationConfig = new config(FurnitureLib.getInstance());
        this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
        if (conf.equalsIgnoreCase("chunk")) {
            if (limitationFile.getBoolean("ChunkLimit.total.enable", false)) {
                FurnitureLib.getInstance().getLimitManager().setGlobal(limitationFile.getBoolean("ChunkLimit.total.global", false));
                return limitationFile.getInt("ChunkLimit.total.amount", -1);
            } else {
                return limitationFile.getInt("ChunkLimit.projects." + getSystemID(), -1);
            }
        }
        return -1;
    }
    
    public String getDisplayName() {
    	if(Objects.nonNull(getCraftingFile().getItemstack())) {
    		if(getCraftingFile().getItemstack().hasItemMeta()) {
    			ItemMeta meta = getCraftingFile().getItemstack().getItemMeta();
    			if(meta.hasDisplayName()) {
    				return meta.getDisplayName();
    			}
    		}
    	}
    	return getName();
    }

    public List<ObjectID> getObjects() {
        return FurnitureManager.getInstance().getObjectList().stream()
                .filter(obj -> SQLAction.REMOVE != obj.getSQLAction())
                .filter(obj -> obj.getProject().equalsIgnoreCase(getName())).collect(Collectors.toList());
    }
    
    public long getObjectSize() {
        return FurnitureManager.getInstance().getObjectList().stream()
                .filter(obj -> SQLAction.REMOVE != obj.getSQLAction())
                .filter(obj -> obj.getProject().equalsIgnoreCase(getName())).count();
    }

    public Project applyFunction() {
        getObjects().forEach(this::applyFunction);
        return this;
    }

    public Project applyFunction(ObjectID obj) {
        if (Objects.isNull(this.furnitureObject)) return this;
        try {
        	obj.setFurnitureObject(furnitureObject.apply(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

	public boolean isDestroyable() {
		return this.haveModelSchematic() ? this.getModelschematic().isDestroyAble() : true;
	}
	
	public boolean updateFile(CommandSender sender) {
		File file = getCraftingFile().filePath;
		if(haveModelSchematic()) {
			String prev = fileSize(file);
			this.getModelschematic().save(file);
			String after = fileSize(file);
			sender.sendMessage("ModelFile: §d" + getName() + " §fupdated before: §c" + prev + " §fafter: §a" + after);
			return true;
		}
		return false;
	}
	
	private String fileSize(File file) {
		return (Math.round((double) file.length() / 1024 * 10d) / 10d) + " kb";
	}
}
