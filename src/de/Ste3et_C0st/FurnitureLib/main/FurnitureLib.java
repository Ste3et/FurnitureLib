package de.Ste3et_C0st.FurnitureLib.main;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.logging.Logger;

import net.milkbowl.vault.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.SQLManager;
import de.Ste3et_C0st.FurnitureLib.Database.Serialize;
import de.Ste3et_C0st.FurnitureLib.Events.ChunkOnLoad;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureEvents;
import de.Ste3et_C0st.FurnitureLib.Utilitis.CraftingInv;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Protection.ProtectionManager;
import de.Ste3et_C0st.LimitationManager.LimitationManager;

public class FurnitureLib extends JavaPlugin{

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger("Minecraft");
	private LocationUtil lUtil;
	private static FurnitureLib instance;
	private FurnitureManager manager;
	private Connection con;
	private Serialize serialize;
	private ProtectionManager Pmanager;
	private LightManager lightMgr;
	private Boolean useGamemode = true;
	private CraftingInv craftingInv;
	private LanguageManager lmanager;
	private SQLManager sqlManager;
	private LimitationManager limitManager;
	
	public LanguageManager getLangManager(){return this.lmanager;}
	public LightManager getLightManager(){return this.lightMgr;}
	public ProtectionManager getPermManager(){return this.Pmanager;}
	public LimitationManager getLimitManager(){return this.limitManager;}
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		instance = this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		this.lUtil = new LocationUtil();
		this.manager = new FurnitureManager();
		this.serialize = new Serialize();
		this.lightMgr = new LightManager();
		this.lmanager = new LanguageManager(instance, getConfig().getString("config.Language"));
		this.useGamemode = getConfig().getBoolean("config.NormalGamemodeRemove");
		try{
			getConfig().addDefaults(YamlConfiguration.loadConfiguration(getResource("config.yml")));
			getConfig().options().copyDefaults(true);
			saveConfig();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if ((getServer().getPluginManager().isPluginEnabled("Vault")) && (getConfig().getBoolean("config.UseMetrics"))) {
		      try
		      {
		        Metrics metrics = new Metrics(this);
		        metrics.start();
		      }
		      catch (IOException localIOException) {}
		}
		new FurnitureEvents(instance, manager);
		getServer().getPluginManager().registerEvents(new ChunkOnLoad(), this);
		getCommand("furniture").setExecutor(new command(manager, this));
		this.Pmanager = new ProtectionManager(instance);
		getLogger().info("==========================================");
		getLogger().info("FurnitureLibary Version: " + this.getDescription().getVersion());
		getLogger().info("Furniture Autor: " + this.getDescription().getAuthors().get(0));
		getLogger().info("Furniture Website: " + this.getDescription().getWebsite());
		getLogger().info("Furniture start load");
		this.sqlManager = new SQLManager(instance);
		this.sqlManager.loadALL();
		getLogger().info("Furniture load finish");
		getLogger().info("==========================================");
		this.craftingInv = new CraftingInv(this);
		this.limitManager = new LimitationManager(this);
		if(getConfig().getBoolean("config.timer.Enable")){
			int time = getConfig().getInt("config.timer.time");
			sqlManager.saveIntervall(time);
		}
	}
	
	@Override
	public void onDisable(){	
		getLogger().info("==========================================");
		getLogger().info("Furniture shutdown started");
		sqlManager.save();
		sqlManager.stop();
		getLogger().info("ArmorStandPackets saved");
		if(!getFurnitureManager().getAsList().isEmpty()){
			for(ArmorStandPacket as : getFurnitureManager().getAsList()){
				as.destroy();
			}
		}
		getLogger().info("==========================================");
	}
	
	  public Boolean isDouble(String s){
		  try{
			  Double.parseDouble(s);
			  return true;
		  }catch(NumberFormatException e){
			  return false;
		  }
	  }
	  
	  public Boolean isBoolean(String s){
		  try {
			  s = s.toLowerCase();
			  Boolean.parseBoolean(s);
			  return true;
		} catch (Exception e) {
			return false;
		}
	  }
	  
	  public Boolean isInt(String s){
		  try{
			  Integer.parseInt(s);
			  return true;
		  }catch(NumberFormatException e){
			  return false;
		  }
	  }
	  
	  public void removeItem(Player p){
		  if(useGamemode()&&p.getGameMode().equals(GameMode.CREATIVE)){return;}
		  Integer slot = p.getInventory().getHeldItemSlot();
		  ItemStack itemStack = p.getInventory().getItemInHand().clone();
		  itemStack.setAmount(itemStack.getAmount()-1);
		  p.getInventory().setItem(slot, itemStack);
		  p.updateInventory();
	  }
	
		public boolean canPlace(Location l, Project pro, Player p){
			BlockFace b = lUtil.yawToFace(l.getYaw()).getOppositeFace();
			for(ObjectID obj : manager.getObjectList()){
				Vector v1 = obj.getStartLocation().toVector();
				Vector v2 = l.toVector();
				if(v1.equals(v2)){
					p.sendMessage(getLangManager().getString("FurnitureOnThisPlace"));
					return false;
				}
			}
			
			Integer i = (int) l.getY();
			for(int x = 0; x<=pro.getWitdh();x++){
				for(int y = 0; y<=pro.getHeight();y++){
					for(int z = 0; z<=pro.getLength();z++){
						Location loc = lUtil.getRelativ(l, b,(double) -x,(double) z).add(0, y, 0);
						Integer integer = (int) loc.getY();
						if(!integer.equals(i)){
							if(loc.getBlock().getType()!=null&&!loc.getBlock().getType().equals(Material.AIR)){
									p.sendMessage(getLangManager().getString("NotEnoughSpace"));
									return false;
							}
						}

					}
				}
			}
			
			if(l.getBlock()!=null){
				if(l.getBlock().getRelative(BlockFace.UP)!=null){
					Material m = l.getBlock().getRelative(BlockFace.UP).getType();
					if(!m.equals(Material.AIR)){
						p.sendMessage(getLangManager().getString("NotEnoughSpace"));
						return false;
					}
				}
			}
			return true;
		}
	
	public void spawn(Project pro, Location l){
		Class<?> c = pro.getclass();
		if(c==null ){return;}
		Constructor<?> ctor = c.getConstructors()[0];
			try {
			ctor.newInstance(l, FurnitureLib.getInstance(), pro.getName(), pro.getPlugin(), null, null);
		} catch (Exception e) {}
	}
	
	public CraftingInv getCraftingInv(){return this.craftingInv;}
	public Serialize getSerialize(){ return this.serialize;}
	public String getBukkitVersion() {return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];}
	public static FurnitureLib getInstance(){return instance;}
	public LocationUtil getLocationUtil(){return this.lUtil;}
	public FurnitureManager getFurnitureManager(){return this.manager;}
	public Connection getConnection(){return this.con;}
	public ObjectID getObjectID(String c, String plugin, Location loc){return new ObjectID(c, plugin, loc);}
	public boolean canBuild(Player p, ObjectID id, EventType type){ return Pmanager.canBuild(p, id, type);}
	public Boolean useGamemode() {return useGamemode;}
}
