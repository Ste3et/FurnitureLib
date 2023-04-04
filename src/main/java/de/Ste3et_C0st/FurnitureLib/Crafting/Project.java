package de.Ste3et_C0st.FurnitureLib.Crafting;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelHandler;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.PermissionHandler;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

import org.bukkit.Location;
import org.bukkit.World;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Project {
    
	private String project;
    private CraftingFile file;
    private Plugin plugin;
    
    private Integer chunkLimit = -1;
    private HashMap<World, Integer> limitationWorld = new HashMap<World, Integer>();
    private List<JsonObject> functionList;
    private CenterType type = CenterType.RIGHT;
    private boolean EditorProject = false, silent = false;
    private ModelHandler modelschematic = null;
    private Function<ObjectID, Furniture> furnitureObject = ProjectLoader::new;
    private YamlConfiguration configuartion = null;
    private boolean enabled = true;
    
    /**
     * Create a new Project instance load the modelFile and calculate the boundingbox.
     *
     * @param name This is the Internal SystemName
     * @param plugin The Plugin who register the Project
     * @param craftingFile Recipe File for the crafting recipe and the spawn item
     * @param side The placeable side of the Furniture
     * @param functionObject The Function Interface
     * @return Project return this Object
     */
    public Project(String name, Plugin plugin, InputStream craftingFile, PlaceableSide side, Function<ObjectID, Furniture> functionObject) {
    	if(FurnitureLib.getInstance().isEnabledPlugin()) {
    		this.project = name;
            this.plugin = plugin;
            
            File configFile = new File(CraftingFile.getPath(name));
        	this.configuartion = CraftingFile.loadDefaultConfig(craftingFile, YamlConfiguration.loadConfiguration(configFile), CraftingFile.getPath(name));
        	
    		if(this.configuartion.getBoolean(name + ".enabled", true) == false) {
    			this.enabled = false;
        		return;
        	}
    		
    		this.file = new CraftingFile(name, this.configuartion);
    		
            if(!this.file.isEnabledModel()) {
            	return;
            }
            
            String configHeader = this.file.getFileHeader();
    		String systemID = this.configuartion.getString(configHeader + ".system-ID", "");
    		this.project = project.equalsIgnoreCase(systemID) ? project : systemID;
            
            this.functionList = this.file.loadFunction();
            this.furnitureObject = functionObject;
            this.modelschematic = new ModelHandler(this.getConfig(), this.getCraftingFile().getFileHeader());
            FurnitureLib.getInstance().getFurnitureManager().addProject(this);
            this.loadDefaults();
            PermissionHandler.registerPermission("furniture.craft.*","furniture.craft." + name.toLowerCase());
            PermissionHandler.registerPermission("furniture.place.*","furniture.place." + name.toLowerCase());
            PermissionHandler.registerPermission("furniture.sit.*","furniture.sit." + name.toLowerCase());
    	}else {
    		FurnitureLib.debug("FurnitureLib is not enabled check your console!", 10);
    	}
    }
    
    public Project(String name, Plugin plugin, InputStream craftingFile, Function<ObjectID, Furniture> functionObject) {
    	this(name, plugin, craftingFile, PlaceableSide.TOP, functionObject);
    }
    
    public Project(String name, Plugin plugin, InputStream craftingFile, PlaceableSide side, Class<? extends Furniture> clazz) {
    	this(name, plugin, craftingFile, side, getFunctionInterface(clazz));
    }

    public Project(String name, Plugin plugin, InputStream craftingFile, Class<? extends Furniture> clazz) {
        this(name, plugin, craftingFile, PlaceableSide.TOP, Objects.isNull(clazz) ? ProjectLoader::new : getFunctionInterface(clazz));
    }

    public Project(String name, Plugin plugin, InputStream craftingFile) {
        this(name, plugin, craftingFile, PlaceableSide.TOP, ProjectLoader::new);
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

    /**
     * Get the Informations about the Crafting Recipe
     * @return CraftingFile
     */
    public CraftingFile getCraftingFile() {
        return file;
    }
    
    public YamlConfiguration getConfig() {
    	return this.configuartion;
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
        final BoundingBox box = getModelschematic().getBoundingBox();
        int width = Math.abs(box.getMax().getBlockX() - box.getMin().getBlockX());
        return width + 1;
    }

    public int getHeight() {
    	final BoundingBox box = getModelschematic().getBoundingBox();
        return (int) box.getHeight() + 1;
    }

    public int getLength() {
    	final BoundingBox box = getModelschematic().getBoundingBox();
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
        LanguageManager.send(player, "message.NoPermissions");
        return false;
    }

    public void loadDefaults() {
    	FurnitureLib.getInstance().getFurnitureConfig().getLimitManager().getTypes().forEach(entry -> {
    		entry.updateConfig(this);
    	});
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
        return this.getObjects().size();
    }

    public Project applyFunction() {
    	 if (this.isEnabled() == false) return this;
    	this.getObjects().forEach(this::applyFunction);
        return this;
    }
    
    public boolean isEnabled() {
    	return this.enabled;
    }

    public Project applyFunction(ObjectID obj) {
        if (Objects.isNull(this.furnitureObject)) return this;
        if (this.isEnabled() == false) return this;
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
	
	public double updateFile() {
		File file = getCraftingFile().filePath;
		if(haveModelSchematic()) {
			double prev = fileSize(file);
			this.getModelschematic().save(file);
			double after = fileSize(file);
			return prev - after;
		}
		return 0d;
	}
	
	private double fileSize(File file) {
		return (Math.round((double) file.length() / 1024 * 10d) / 10d);
	}
	
	public static Function<ObjectID, Furniture> getFunctionInterface(Class<? extends Furniture> clazz) {
   	 return objectID -> {
       	try {
       		return clazz.getConstructor(objectID.getClass()).newInstance(objectID);
       	}catch(Exception ex) {
       		ex.printStackTrace();
       	}
       	return null;
       };
	}
	
	public void fixMetadata(ObjectID objectID) {
		if(Objects.nonNull(this.getModelschematic())) {
			this.getModelschematic().getEntityMap(objectID.getStartLocation(), objectID.getBlockFace()).stream().filter(Objects::nonNull).forEach(entry -> {
				Optional<fEntity> source = objectID.getEntityByVector(entry.getLocation().toVector());
				if(Objects.nonNull(source) && source.isPresent()) {
					source.get().copyMetadata(entry);
				}
			});
		}
	}
	
	public ObjectID createObjectID(Location location) {
		ObjectID objectID = new ObjectID(getName(), getPlugin().getName(), location);
		return objectID;
	}
	
	public ObjectID spawnProject(Location location) {
		return FurnitureLib.getInstance().spawn(this, location);
	}
}
