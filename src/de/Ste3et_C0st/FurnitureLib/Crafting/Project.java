package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjectLoader;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class Project{
	private String project;
	private CraftingFile file;
	private Plugin plugin;
	private Class<?> clas;
	private Integer witdh = 0,height = 0,length = 0,chunkLimit = -1;
	private config limitationConfig;
	private FileConfiguration limitationFile;
	private HashMap<World, Integer> limitationWorld = new HashMap<World, Integer>();
	private CenterType type = CenterType.RIGHT;
	private PlaceableSide side;
	private boolean EditorProject = false, silent = false;
	private InputStream model = null;
	private List<String> complete = new ArrayList<String>();
	public InputStream getModel(){return this.model;}
	public String getName(){return project;}
	public Plugin getPlugin(){return plugin;}
	public CraftingFile getCraftingFile(){return file;}
	public Class<?> getclass(){return this.clas;}
	public CenterType getCenterType(){return this.type;}
	public PlaceableSide getPlaceableSide(){return this.side;}
	public String getSystemID(){return getCraftingFile().getSystemID();}
	public boolean isEditorProject(){return this.EditorProject;}
	public Project setModel(InputStream stream){this.model = stream;return this;}
	public Project setCraftingFile(CraftingFile file){this.file = file;return this;}
	public Project setPlugin(Plugin plugin) {this.plugin = plugin;return this;}
	public Project setPlaceableSide(PlaceableSide side){this.side = side;return this;}
	public Project setEditorProject(boolean b){this.EditorProject = b;return this;}
	public Project setClass(Class<?> clas) {this.clas = clas;return this;}
	public Project setName(String name){this.project = name;return this;}
	public boolean isSilent(){return this.silent;}
	public int getWitdh(){return this.witdh;}
	public int getHeight(){return this.height;}
	public int getLength(){return this.length;}
	public int getAmountWorld(World w){if(limitationWorld.containsKey(w)){return limitationWorld.get(w);}else{return -1;}}
	public int getAmountChunk(){return this.chunkLimit;}
	public void setSilent(boolean b){silent = b;}
	public boolean isCompleteLimitation(String s){
		if(this.complete.contains(s)){
			return true;
		}
		return false;
	}
	
	public Project setSize(Integer witdh, Integer height, Integer length, CenterType type){		
		this.witdh = witdh;
		this.height = height;
		this.length = length;
		this.type = type;
		return this;
	}
	
	public Project(String name, Plugin plugin,InputStream craftingFile,PlaceableSide side, Class<?> clas){
		this.project = name;
		this.plugin = plugin;
		this.clas = clas;
		this.file = new CraftingFile(name, craftingFile);
		this.side = side;
		FurnitureLib.getInstance().getFurnitureManager().addProject(this);
		addDefaultWorld();
		addDefault("chunk");
		addDefault("player");
		this.chunkLimit = getDefault("chunk");
		FurnitureLib.getInstance().getLimitManager().loadDefault(this.project);
	}
	
	public Project(String name, Plugin plugin,InputStream craftingFile){
		this.project = name;
		this.plugin = plugin;
		this.clas = ProjectLoader.class;
		this.file = new CraftingFile(name, craftingFile);
		this.side = this.file.getPlaceAbleSide();
		FurnitureLib.getInstance().getFurnitureManager().addProject(this);
		addDefaultWorld();
		addDefault("chunk");
		addDefault("player");
		this.chunkLimit = getDefault("chunk");
		FurnitureLib.getInstance().getLimitManager().loadDefault(this.project);
	}
	
	public Project(String name, Plugin plugin,PlaceableSide side, Class<?> clas){
		this.project = name;
		this.plugin = plugin;
		this.clas = clas;
		FurnitureLib.getInstance().getFurnitureManager().addProject(this);
		addDefaultWorld();
		addDefault("chunk");
		addDefault("player");
		this.chunkLimit = getDefault("chunk");
		this.side = side;
		FurnitureLib.getInstance().getLimitManager().loadDefault(this.project);
	}
	
	public boolean hasPermissions(Player p){
		if(p.isOp()) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.admin")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.player")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.place." + getSystemID())) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.place.all")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.place.all." + getPlugin().getName())) return true;
		if(FurnitureLib.getInstance().getPermissionList()!=null){
			for(String s : FurnitureLib.getInstance().getPermissionList().keySet()){
				if(FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.place.all." + s)){
					if(FurnitureLib.getInstance().getPermissionList().get(s).contains(this.getName())){
						return true;
					}
				}
			}
		}
		p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
		return false;
	}
	
	private void addDefaultWorld(){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig("world", "/limitation/");
		for(World w : Bukkit.getWorlds()){
			this.limitationFile.addDefault("Projects." + w.getName() + "." + getSystemID(), -1);
		}
		this.limitationFile.options().copyDefaults(true);
		this.limitationConfig.saveConfig("world", this.limitationFile, "/limitation/");
		getAmountWorld();
	}
	
	private void getAmountWorld(){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig("world", "/limitation/");
		for(World w : Bukkit.getWorlds()){
			Integer i = this.limitationFile.getInt("Projects." + w.getName() + "." + getSystemID());
			limitationWorld.put(w, i);
		}
	}

	private void addDefault(String conf){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
		if(conf.equalsIgnoreCase("chunk")){
			if(!this.limitationFile.isSet("ChunkLimit.total")){
				this.limitationFile.addDefault("ChunkLimit.total.enable", false);
				this.limitationFile.addDefault("ChunkLimit.total.amount", -1);
			}
			
			if(!this.limitationFile.isSet("ChunkLimit.projects." + getSystemID())){
				this.limitationFile.addDefault("ChunkLimit.projects." + getSystemID(), -1);
			}
		}
		this.limitationFile.options().copyDefaults(true);
		this.limitationConfig.saveConfig(conf, this.limitationFile, "/limitation/");
	}
	
	private Integer getDefault(String conf){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
		if(conf.equalsIgnoreCase("chunk")){
			if(limitationFile.getBoolean("ChunkLimit.total.enable", false)){
				return limitationFile.getInt("ChunkLimit.total.amount", -1);
			}else{
				return limitationFile.getInt("ChunkLimit.projects." + getSystemID(), -1);
			}
		}
		return -1;
	}
}
