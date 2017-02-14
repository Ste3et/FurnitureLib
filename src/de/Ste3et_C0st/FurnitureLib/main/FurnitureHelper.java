package de.Ste3et_C0st.FurnitureLib.main;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class FurnitureHelper{

	private BlockFace b;
	private World w;
	private ObjectID obj;
	private FurnitureManager manager;
	private FurnitureLib lib;
	private LocationUtil lutil;
	private Plugin plugin;
	
	public FurnitureHelper(ObjectID id){
		if(id==null){return;}
		this.lib = FurnitureLib.getInstance();
		this.lutil = lib.getLocationUtil();
		this.manager = lib.getFurnitureManager();
		this.b = lutil.yawToFace(id.getStartLocation().getYaw());
		this.w = id.getStartLocation().getWorld();
		this.plugin = id.getProjectOBJ().getPlugin();
		this.obj = id;
	}
	
	public fArmorStand spawnArmorStand(Location loc){return getManager().createArmorStand(getObjID(), loc);}
	public Location getLocation(){
		Location loc = obj.getStartLocation().getBlock().getLocation();
		loc.setYaw(obj.getStartLocation().getYaw());
		return loc;}
	public BlockFace getBlockFace(){return this.b;}
	public World getWorld(){return this.w;}
	public ObjectID getObjID(){return this.obj;}
	public FurnitureManager getManager(){return this.manager;}
	public FurnitureLib getLib(){return this.lib;}
	public LocationUtil getLutil(){return this.lutil;}
	public Plugin getPlugin(){return this.plugin;}
	public List<fEntity> getfAsList(){
		if(this.obj==null) return null;
		if(this.getObjID()==null) return null;
		if(this.getObjID().getPacketList()==null) return null;
		return getObjID().getPacketList();}
	public boolean isFinish(){return getObjID().isFinish();}
	public float getYaw(){return getLutil().FaceToYaw(getBlockFace());}
	public Location getRelative(Location loc, BlockFace face, double z, double x){return getLutil().getRelativ(loc, b, z, x);}
	public Location getRelative(Location loc, double z, double x){return getLutil().getRelativ(loc, getBlockFace(), z, x);}
	public void destroy(Player p){getObjID().remove(p);}
	public void send(){getManager().send(obj);}
	public void update(){getManager().updateFurniture(obj);}
	public void delete(){this.obj=null;}
	public void consumeItem(Player p){
		if(p.getGameMode().equals(GameMode.CREATIVE) && FurnitureLib.getInstance().useGamemode()) return;
		Integer i = p.getInventory().getHeldItemSlot();
		ItemStack is = p.getInventory().getItemInMainHand();
		if((is.getAmount()-1)<=0){
			is.setType(Material.AIR);
		}else{
			is.setAmount(is.getAmount()-1);
		}

		p.getInventory().setItem(i, is);
		p.updateInventory();
	}
	
	public boolean canBuild(Player p){return FurnitureLib.getInstance().canBuild(p, getObjID(), Type.EventType.BREAK);} 
	public boolean canInteract(Player p){return FurnitureLib.getInstance().canBuild(p, getObjID(), Type.EventType.INTERACT);} 
	
	public Location getCenter(){
		Location loc = getLutil().getCenter(getLocation());
		loc.setYaw(getLutil().FaceToYaw(getBlockFace()));
		return loc;}
	
	public fEntity entityByCustomName(String str){
		for(fEntity entity : getfAsList()){
			if(entity.getCustomName().equalsIgnoreCase(str)){
				return entity;
			}
		}
		return null;
	}
}
