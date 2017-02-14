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
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class Project extends ProjectSettings{
	private String project;
	private CraftingFile file;
	private Plugin plugin;
	private Class<?> clas;
	private Integer witdh = 0,height = 0,length = 0,chunkLimit = -1,playerLimit = -1;
	private config limitationConfig;
	private FileConfiguration limitationFile;
	private HashMap<World, Integer> limitationWorld = new HashMap<World, Integer>();
	private CenterType type = CenterType.RIGHT;
	private PlaceableSide side;
	private boolean EditorProject = false;
	private InputStream model = null;
	private int gear = -98451, maxSpeed = -98451, middle = -98451;
	private boolean isSearch = false, isCar = false;
	private HashMap<String, Integer> permissionList = new HashMap<String, Integer>();
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
	public boolean isDriveable(){return isCar;}
	public Project setModel(InputStream stream){this.model = stream;return this;}
	public Project setCraftingFile(CraftingFile file){this.file = file;return this;}
	public Project setPlugin(Plugin plugin) {this.plugin = plugin;return this;}
	public Project setPlaceableSide(PlaceableSide side){this.side = side;return this;}
	public Project setEditorProject(boolean b){this.EditorProject = b;return this;}
	public Project setClass(Class<?> clas) {this.clas = clas;return this;}
	public Project setName(String name){this.project = name;return this;}
	public int getMiddle(){return this.middle;}
	public int getMaxSpeed(){return this.maxSpeed;}
	public int getGear(){return this.gear;}
	public int getWitdh(){return this.witdh;}
	public int getHeight(){return this.height;}
	public int getLength(){return this.length;}
	public int getAmountWorld(World w){if(limitationWorld.containsKey(w)){return limitationWorld.get(w);}else{return -1;}}
	public int getAmountChunk(){return this.chunkLimit;}
	public int getAmountPlayer(){return this.playerLimit;}

	
	public int hasPermissionsAmount(Player p){
		int i = -1;
		if(!permissionList.isEmpty()){
			for(String s : permissionList.keySet()){
				if(FurnitureLib.getInstance().hasPerm(p,s)){
					int j = permissionList.get(s);
					if(j>i){i = j;}
				}
			}
		}
		return i;
	}
	
	public boolean isCompleteLimitation(String s){
		if(this.complete.contains(s)){
			return true;
		}
		return false;
	}

	public void checkDriveable(List<fEntity> entityList){
		if(!isSearch){
			int i = 0;
			for(fEntity entity : entityList){
				String s = entity.getCustomName().toLowerCase();
				if(s.startsWith("#car_middle#(")){
					s = s.replace("#car_middle#(", "");
					s = s.replace(")", "");
					if(s.contains(",")){
						String[] args = s.split(",");
						for(String str : args){
							if(str.contains("speed:")){
								str = str.replace("speed:", "");
								maxSpeed = Integer.parseInt(str);
							}else if(str.contains("gear:")){
								str = str.replace("gear:", "");
								gear = Integer.parseInt(str);
							}
						}
					}
					middle = i;
				}
				i++;
			}
			isSearch = true;
			if(gear!=-98451&&maxSpeed!=-98451&&middle!=-98451){
			    isCar = true;
			}
		}
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
		this.playerLimit = getDefault("player");
	}
	
	public Project(String name, Plugin plugin,InputStream craftingFile){
		this.project = name;
		this.plugin = plugin;
		this.clas = ProjectLoader.class;
		this.file = new CraftingFile(name, craftingFile);
		this.side = PlaceableSide.TOP;
		FurnitureLib.getInstance().getFurnitureManager().addProject(this);
		addDefaultWorld();
		addDefault("chunk");
		addDefault("player");
		this.chunkLimit = getDefault("chunk");
		this.playerLimit = getDefault("player");
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
		this.playerLimit = getDefault("player");
		this.side = side;
	}
	
	public boolean hasPermissions(Player p){
		if(p.isOp()) return true;
		if(FurnitureLib.getInstance().hasPerm(p,"Furniture.admin")) return true;
		if(FurnitureLib.getInstance().hasPerm(p,"Furniture.Player")) return true;
		if(FurnitureLib.getInstance().hasPerm(p,"Furniture.place." + getSystemID())) return true;
		if(FurnitureLib.getInstance().hasPerm(p,"Furniture.place.all")) return true;
		if(FurnitureLib.getInstance().hasPerm(p,"Furniture.place.all." + getPlugin().getName())) return true;
		if(FurnitureLib.getInstance().getPermissionList()!=null){
			for(String s : FurnitureLib.getInstance().getPermissionList().keySet()){
				if(FurnitureLib.getInstance().hasPerm(p, "Furniture.place.all." + s)){
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
		if(conf.equalsIgnoreCase("player")){
			if(!this.limitationFile.isSet("PlayerLimit.default.total")){
				this.limitationFile.addDefault("PlayerLimit.default.total.enable", false);
				this.limitationFile.addDefault("PlayerLimit.default.total.amount", 10);
				this.limitationFile.addDefault("PlayerLimit.default.total.complete", false);
			}
			if(!this.limitationFile.isSet("PlayerLimit.default.projects" + getSystemID())){
				this.limitationFile.addDefault("PlayerLimit.default.projects." + getSystemID(), 10);
			}
		}else if(conf.equalsIgnoreCase("chunk")){
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
		if(conf.equalsIgnoreCase("player")){
			for(String str : limitationFile.getConfigurationSection("PlayerLimit").getKeys(false)){
				if(str.equalsIgnoreCase("default")) continue;
				String permission = "furniture.limit." + str;
				Integer i = 10;
				if(limitationFile.getBoolean("PlayerLimit." + str + ".total.enable", false)){
					i = limitationFile.getInt("PlayerLimit."+ str +".total.amount", 10);
					if(limitationFile.getBoolean("PlayerLimit." + str + ".total.complete", false)){
						complete.add(str);
					}
				}else{
					i = limitationFile.getInt("PlayerLimit."+ str +".projects." + getSystemID(), 10);
				}
				permissionList.put(permission, i);
			}
			if(limitationFile.getBoolean("PlayerLimit.default.total.enable", false)){
				if(limitationFile.getBoolean("PlayerLimit.default.total.complete", false)){
					complete.add("default");
				}
				return limitationFile.getInt("PlayerLimit.default.total.amount", 10);
			}else{
				return limitationFile.getInt("PlayerLimit.default.projects." + getSystemID(), 10);
			}		
		}else if(conf.equalsIgnoreCase("chunk")){
			if(limitationFile.getBoolean("ChunkLimit.total.enable", false)){
				return limitationFile.getInt("ChunkLimit.total.amount", -1);
			}else{
				return limitationFile.getInt("ChunkLimit.projects." + getSystemID(), -1);
			}
		}
		return -1;
	}
	
	public void rename(String string){
		
	}
}
