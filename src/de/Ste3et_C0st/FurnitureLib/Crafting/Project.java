package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class Project{
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
	
	public InputStream getModel(){return this.model;}
	public String getName(){return project;}
	public Plugin getPlugin(){return plugin;}
	public CraftingFile getCraftingFile(){return file;}
	public Class<?> getclass(){return clas;}
	public CenterType getCenterType(){return this.type;}
	public PlaceableSide getPlaceableSide(){return this.side;}
	public String getSystemID(){return getCraftingFile().getSystemID();}
	public boolean isEditorProject(){return this.EditorProject;}
	public void setModel(InputStream stream){this.model = stream;}
	public void setCraftingFile(CraftingFile file){this.file = file;}
	public void setPlugin(Plugin plugin) {this.plugin = plugin;}
	public void setPlaceableSide(PlaceableSide side){this.side = side;}
	public void setEditorProject(boolean b){this.EditorProject = b;}
	public void setClass(Class<?> clas) {this.clas = clas;}
	public void setName(String name){this.project = name;}
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
					if(j>i){i = permissionList.get(s);}
				}
			}
		}
		return i;
	}
	
	
	public boolean isDriveable(){return isCar;}
	
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
	
	public void setSize(Integer witdh, Integer height, Integer length, CenterType type){
		this.witdh = witdh;
		this.height = height;
		this.length = length;
		this.type = type;
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
		if(plugin!=null&&plugin.getName().equalsIgnoreCase("FurnitureMaker")){setEditorProject(true);}
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
		if(plugin!=null&&plugin.getName().equalsIgnoreCase("FurnitureMaker")){setEditorProject(true);}
	}
	
	public boolean hasPermissions(Player p){
		if(FurnitureLib.getInstance().hasPerm(p,"Furniture.Player") || FurnitureLib.getInstance().hasPerm(p,"Furniture.place." + getSystemID()) || p.isOp() || FurnitureLib.getInstance().hasPerm(p,"Furniture.admin")){
			return true;
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
		this.limitationFile.addDefault("Projects." + getSystemID(), -1);
		if(conf.equalsIgnoreCase("player")){
			if(!this.limitationFile.isSet("Projects." + getSystemID())){
				this.limitationFile.addDefault("PermissionsLimit.test." + getSystemID(), 10);
			}
		}
		this.limitationFile.options().copyDefaults(true);
		this.limitationConfig.saveConfig(conf, this.limitationFile, "/limitation/");
	}
	
	private Integer getDefault(String conf){
		this.limitationConfig = new config(FurnitureLib.getInstance());
		this.limitationFile = this.limitationConfig.getConfig(conf, "/limitation/");
		if(conf.equalsIgnoreCase("player")){
			if(this.limitationFile.isSet("PermissionsLimit")){
				if(this.limitationFile.isConfigurationSection("PermissionsLimit")){
					for(String s : this.limitationFile.getConfigurationSection("PermissionsLimit").getKeys(false)){
						if(this.limitationFile.isSet("PermissionsLimit." + s + "." + getSystemID())){
							String permission = "furniture.limit." + s;
							Integer i = this.limitationFile.getInt("PermissionsLimit." + s + "." + getSystemID());
							permissionList.put(permission, i);
						}
					}
				}
			}
		}
		return this.limitationFile.getInt("Projects." + getSystemID());
	}
	
	public void rename(String string){
		
	}
}
