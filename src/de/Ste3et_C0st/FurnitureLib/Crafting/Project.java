package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.LimitationManager.LimitationManager;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelHandler;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class Project {
	private String project;
	private CraftingFile file;
	private Plugin plugin;
	private Class<? extends Furniture> clazz;

	private Integer chunkLimit = -1;
	private config limitationConfig;
	private FileConfiguration limitationFile;
	private HashMap<World, Integer> limitationWorld = new HashMap<World, Integer>();
	private List<JsonObject> functionList;
	private CenterType type = CenterType.RIGHT;
	private boolean EditorProject = false, silent = false;
	private ModelHandler modelschematic = null;

	public String getName() {
		return project;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public CraftingFile getCraftingFile() {
		return file;
	}

	public CenterType getCenterType() {
		return this.type;
	}

	public PlaceableSide getPlaceableSide() {
		return getModelschematic().getPlaceableSide();
	}

	public String getSystemID() {
		return getCraftingFile().getSystemID();
	}

	public boolean isEditorProject() {
		return this.EditorProject;
	}

	public Class<? extends Furniture> getFunctionClazz() {
		return this.clazz;
	}

	public Project setCraftingFile(CraftingFile file) {
		this.file = file;
		return this;
	}

	public Project setPlaceableSide(PlaceableSide side) {
		this.modelschematic.setPlaceableSide(side);
		return this;
	}

	public Project setEditorProject(boolean b) {
		this.EditorProject = b;
		return this;
	}

	public Project setName(String name) {
		this.project = name;
		return this;
	}

	public List<JsonObject> getFunctions() {
		return this.functionList;
	}

	public boolean isSilent() {
		return this.silent;
	}

	public int getWitdh() {
		BoundingBox box = getModelschematic().getBoundingBox();
		int witdh = Math.abs(box.getMax().getBlockX() - box.getMin().getBlockX());
		return witdh + 1;
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
		if (limitationWorld.containsKey(w)) {
			return limitationWorld.get(w);
		} else {
			return -1;
		}
	}

	public int getAmountChunk() {
		return this.chunkLimit;
	}

	public void setSilent(boolean b) {
		silent = b;
	}

	public Project setSize(Integer length, Integer height, Integer width, CenterType type) {
		if (Objects.nonNull(getModelschematic())) {
			length = length - 1;
			height = height - 1;
			width = width - 1;
			
			Vector pos1 = new Vector();
			Vector pos2 = new Vector(width,height,length);
			
			if(type.equals(CenterType.RIGHT)) {
				pos2.setZ(-length);
			}else if(type.equals(CenterType.CENTER)) {
				width = Math.round((width) / 2);
				pos1.setX(-width);
				pos2.setX(width);
				pos2.setZ(-length);
			}if(type.equals(CenterType.LEFT)) {
				pos2.setZ(-length);
				pos2.setX(-width);
			}
			getModelschematic().setSize(pos1,pos2);
			this.type = type;
		}
		return this;
	}

	/**
	 * Create a new Project instance load the modelFile and calculate the boundingbox.
	 * 
	 * @param name This is the Internal SystemName
	 * @param plugin The Plugin who register the Project
 	 * @param craftingFile Recipe File for the crafting recipe and the spawn item
	 * @param side The placeable side of the Furniture
	 * @param clazz The Function class for the Project
	 * 
	 * @return Project return this Object
	 */
	public Project(String name, Plugin plugin, InputStream craftingFile, PlaceableSide side,Class<? extends Furniture> clazz) {
		this.project = name;
		this.plugin = plugin;
		this.file = new CraftingFile(name, craftingFile);
		this.functionList = this.file.loadFunction();
		this.clazz = clazz;
		try {
			if(Objects.nonNull(this.file)) {
				this.modelschematic = new ModelHandler(this.file.getFilePath());
			}else {
				this.modelschematic = new ModelHandler(this.project);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		FurnitureLib.getInstance().getFurnitureManager().addProject(this);
		this.loadDefaults();
		FurnitureLib.getInstance().getLimitManager().loadDefault(this.project);
	}
	
	public Project(String name, Plugin plugin, InputStream craftingFile, Class<? extends Furniture> clazz) {
		this(name, plugin, craftingFile, PlaceableSide.TOP, Objects.isNull(clazz) ? ProjectLoader.class : clazz);
	}

	public Project(String name, Plugin plugin, InputStream craftingFile) {
		this(name, plugin, craftingFile, PlaceableSide.TOP, ProjectLoader.class);
	}

	public ModelHandler getModelschematic() {
		return this.modelschematic;
	}

	public boolean hasPermissions(Player p) {
		if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.player"))
			return true;
		if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.place." + getSystemID()))
			return true;
		if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.place.all"))
			return true;
		if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.place.all." + getPlugin().getName()))
			return true;
		if (FurnitureLib.getInstance().getPermissionList() != null) {
			for (String s : FurnitureLib.getInstance().getPermissionList().keySet()) {
				if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.place.all." + s)) {
					if (FurnitureLib.getInstance().getPermissionList().get(s).contains(this.getName())) {
						return true;
					}
				}
			}
		}
		p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NoPermissions"));
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

	public List<ObjectID> getObjects() {
		return FurnitureManager.getInstance().getObjectList().stream()
				.filter(obj -> !obj.getSQLAction().equals(SQLAction.REMOVE))
				.filter(obj -> obj.getProject().equalsIgnoreCase(getName())).collect(Collectors.toList());
	}

	public Project applyFunction() {
		getObjects().forEach(this::applyFunction);
		return this;
	}
	
	public Project applyFunction(ObjectID obj) {
		if(Objects.isNull(getFunctionClazz())) return this;
		try {
			obj.setFunctionObject(getFunctionClazz().getConstructor(ObjectID.class).newInstance(obj));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
}
