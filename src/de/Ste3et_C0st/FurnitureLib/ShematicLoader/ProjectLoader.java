package de.Ste3et_C0st.FurnitureLib.ShematicLoader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBlockBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBlockClickEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.Vector3f;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ProjectLoader extends Furniture implements Listener{
	private Object[] enumItemSlots = new Vector3f().b();
	public String header;
	private ProjektInventory inv=null;
	
	public ProjectLoader(ObjectID id){
		super(id);
		try{
			YamlConfiguration config = new YamlConfiguration();
			config.load(new File("plugins/FurnitureLib/plugin/DiceEditor/", getObjID().getProject()+".yml"));
			header = getHeader(config);
			Player player = setBlock(id.getStartLocation(), config, true);
			if(player!=null){
				player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NotEnoughSpace"));
				getObjID().setSQLAction(SQLAction.REMOVE);
				return;
			}
			
			if(isFinish()){
				registerInventory();
				toggleLight(false);
				Bukkit.getPluginManager().registerEvents(this, id.getProjectOBJ().getPlugin());
				return;
			}else{
				
			}
			spawn(id.getStartLocation(), config, true);
			Bukkit.getPluginManager().registerEvents(this, id.getProjectOBJ().getPlugin());
			registerInventory();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ProjectLoader(ObjectID id, boolean rotate){
		super(id);
		try{
			YamlConfiguration config = new YamlConfiguration();
			config.load(new File("plugins/FurnitureLib/plugin/DiceEditor/", getObjID().getProject()+".yml"));
			header = getHeader(config);
			Player player = setBlock(id.getStartLocation(), config, rotate);
			if(player!=null){
				player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NotEnoughSpace"));
				getObjID().setSQLAction(SQLAction.REMOVE);
				return;
			}
			
			if(isFinish()){
				registerInventory();
				toggleLight(false);
				Bukkit.getPluginManager().registerEvents(this, id.getProjectOBJ().getPlugin());
				return;
			}else{
				
			}
			spawn(id.getStartLocation(), config, rotate);
			Bukkit.getPluginManager().registerEvents(this, id.getProjectOBJ().getPlugin());
			registerInventory();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void registerInventory(){
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Inventory:")){
				if(inv==null){
					String[] split = stand.getName().split(":");
					if(split.length>1){
						int i = Integer.parseInt(split[2].replace("#", ""));
						this.inv = new ProjektInventory(i, getObjID());
						this.inv.load();
					}
				}
			}
		}
	}
	
	public String getHeader(YamlConfiguration file){
		return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];
	}

	@EventHandler
	public void onFurnitureBreak(FurnitureBreakEvent e) {
		if(getObjID()==null){return;}
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(e.isCancelled()) return;
		if(!e.getID().equals(getObjID())) return;
		if(!e.canBuild()){return;}
		e.remove();
	}
	
	@EventHandler
	public void onFurnitureBreak(FurnitureBlockBreakEvent e) {
		if(getObjID()==null){return;}
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(e.isCancelled()) return;
		if(!e.getID().equals(getObjID())) return;
		if(!e.canBuild()){return;}
		e.remove();
	}
	
	@EventHandler(ignoreCancelled = true)
	private void onPhysiks(BlockPhysicsEvent e){
		  if(getObjID() == null) return;
		  if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		  if (e.getBlock() == null) return;
		  if (!getObjID().getBlockList().contains(e.getBlock().getLocation())){return;}
		  e.setCancelled(true);
	}
	
	@EventHandler
	public void onFurnitureBreak(FurnitureBlockClickEvent e) {
		if(getObjID()==null){return;}
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(e.isCancelled()) return;
		if(!e.getID().equals(getObjID())) return;
		if(this.inv!=null){
			if(this.inv.getPlayer()==null){
				this.inv.openInventory(e.getPlayer());
				return;	
			}
		}
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Mount:")){
				if(stand.getPassanger()==null){
					stand.setPassanger(e.getPlayer());
					return;
				}
			}else if(stand.getName().startsWith("/")){
				if(!stand.getName().startsWith("/op")){
					String str = stand.getName();
					str = str.replaceAll("@player", e.getPlayer().getName());
					str = str.replaceAll("@uuid", e.getPlayer().getUniqueId().toString());
					str = str.replaceAll("@world", e.getPlayer().getWorld().getName());
					str = str.replaceAll("@x", e.getPlayer().getLocation().getX() + "");
					str = str.replaceAll("@y", e.getPlayer().getLocation().getY() + "");
					str = str.replaceAll("@z", e.getPlayer().getLocation().getZ() + "");
					e.getPlayer().chat(str);
				}
			}
		}
		
		toggleLight(true);
	}
	
	@EventHandler
	public void onFurnitureClick(FurnitureClickEvent e) {
		if(getObjID()==null){return;}
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(e.isCancelled()) return;
		if(!e.getID().equals(getObjID())) return;
		if(this.inv!=null){
			if(this.inv.getPlayer()==null){
				this.inv.openInventory(e.getPlayer());
				return;	
			}
		}
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Mount:")){
				if(stand.getPassanger()==null){
					stand.setPassanger(e.getPlayer());
					return;
				}
			}else if(stand.getName().startsWith("/")){
				if(!stand.getName().startsWith("/op")){
					String str = stand.getName();
					str = str.replaceAll("@player", e.getPlayer().getName());
					str = str.replaceAll("@uuid", e.getPlayer().getUniqueId().toString());
					str = str.replaceAll("@world", e.getPlayer().getWorld().getName());
					str = str.replaceAll("@x", e.getPlayer().getLocation().getX() + "");
					str = str.replaceAll("@y", e.getPlayer().getLocation().getY() + "");
					str = str.replaceAll("@z", e.getPlayer().getLocation().getZ() + "");
					e.getPlayer().chat(str);
				}
			}
		}
		toggleLight(true);
	}
	
	public void toggleLight(boolean change){
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Light:")){
				String[] str = stand.getName().split(":");
				String lightBool = str[2];
				if(change){
					if(lightBool.equalsIgnoreCase("off#")){
						stand.setName(stand.getName().replace("off#", "on#"));
						if(!stand.isFire()){stand.setFire(true);}
					}else if(lightBool.equalsIgnoreCase("on#")){
						stand.setName(stand.getName().replace("on#", "off#"));
						if(stand.isFire()){stand.setFire(false);}
					}
				}else{
					if(lightBool.equalsIgnoreCase("on#")){if(!stand.isFire()){stand.setFire(true);}}
				}
			}
		}
		update();
	}



	public void spawn(Location loc, YamlConfiguration config, boolean rotate) {
		try {
			for(String s : config.getConfigurationSection(header+".ProjectModels.ArmorStands").getKeys(false)){
				String md5 = config.getString(header+".ProjectModels.ArmorStands."+s);
				byte[] by = Base64.decodeBase64(md5);
				ByteArrayInputStream bin = new ByteArrayInputStream(by);
				NBTTagCompound metadata = NBTCompressedStreamTools.read(bin);
				String customName = metadata.getString("Name");
				NBTTagCompound location = metadata.getCompound("Location");
				double x = location.getDouble("X-Offset");
				double y = location.getDouble("Y-Offset");
				double z = location.getDouble("Z-Offset");
				float yaw = location.getFloat("Yaw");
				Location armorLocation = getRelative(getCenter(), getBlockFace(), -z, -x).add(0, y-.5, 0);
				armorLocation.setYaw(yaw+getYaw()-180);
				switch (getObjID().getProjectOBJ().getPlaceableSide()) {
				case SIDE:
					if(rotate){
						armorLocation = getRelative(getCenter(), getBlockFace(), z, x).add(0, y-.5, 0);
						armorLocation.setYaw(yaw+getYaw());
					}
					break;
				default:break;}
				boolean n = (metadata.getInt("NameVisible")==1),b = (metadata.getInt("BasePlate")==1),s1 = (metadata.getInt("Small")==1);
				boolean f = (metadata.getInt("Fire")==1),a = (metadata.getInt("Arms")==1),i = (metadata.getInt("Invisible")==1);
				boolean m = (metadata.getInt("Marker")==1),g = (metadata.getInt("Glowing")==1);
				fArmorStand packet = FurnitureLib.getInstance().getFurnitureManager().createArmorStand(getObjID(), armorLocation);
				NBTTagCompound inventory = metadata.getCompound("Inventory");
				for(Object object : enumItemSlots){
					if(!inventory.getString(object.toString()).equalsIgnoreCase("NONE")){
						ItemStack is = new CraftItemStack().getItemStack(inventory.getCompound(object.toString()+""));
						packet.getInventory().setSlot(object.toString(), is);
					}
				}
				NBTTagCompound euler = metadata.getCompound("EulerAngle");
				for(BodyPart part : BodyPart.values()){
					packet.setPose(eulerAngleFetcher(euler.getCompound(part.toString())), part);
				}
				packet.setBasePlate(b).setSmall(s1).setMarker(m).setArms(a).setGlowing(g).setNameVasibility(n).setName(customName).setFire(f).setGlowing(g).setInvisible(i);
			}
			send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private HashMap<Location, MaterialData> getBlockMap(Location loc, YamlConfiguration config, boolean rotate){
		HashMap<Location, MaterialData> map = new HashMap<Location, MaterialData>();
		try {
			if(config.isConfigurationSection(header+".ProjectModels.Block")){
				for(String s : config.getConfigurationSection(header+".ProjectModels.Block").getKeys(false)){
					double x = config.getDouble(header+".ProjectModels.Block." + s + ".X-Offset");
					double y = config.getDouble(header+".ProjectModels.Block." + s + ".Y-Offset");
					double z = config.getDouble(header+".ProjectModels.Block." + s + ".Z-Offset");
					Material m = Material.valueOf(config.getString(header+".ProjectModels.Block." + s + ".Type"));
					byte b = (byte) config.getInt(header+".ProjectModels.Block." + s + ".Data");
					Location armorLocation = getRelative(getLocation(), getBlockFace(), -z, -x).add(0, y, 0);
					if(rotate){
						switch (getObjID().getProjectOBJ().getPlaceableSide()) {
							case SIDE:armorLocation = getRelative(getLocation(), getBlockFace(), z, x).add(0, y, 0);break;
							default: break;
						}
					}
					MaterialData data = new MaterialData(m, b);
					map.put(armorLocation, data);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	@SuppressWarnings("deprecation")
	private Player setBlock(Location loc, YamlConfiguration config, boolean rotate){
		HashMap<Location, MaterialData> map = getBlockMap(loc, config, rotate);
		List<Block> blockList = new ArrayList<Block>();
		boolean b = true, c = isFinish();
		UUID uuid = getObjID().getUUID();
		Player p = Bukkit.getPlayer(uuid);
		for(Location location : map.keySet()){
			MaterialData data = map.get(location);
			if(!data.getItemType().equals(Material.AIR)){
				if(!c){
					if(p!=null&&p.isOnline()){
						if(location.getBlock().getType().isSolid()){if(!location.getBlock().getType().equals(data.getItemType())){b = false;getLutil().particleBlock(location.getBlock());}}
					}
				}
			}
		}
		
		if(b){
			p = null;
			for(Location location : map.keySet()){
				MaterialData data = map.get(location);
				if(!data.getItemType().equals(Material.AIR)){
					location.getBlock().setType(data.getItemType());
					location.getBlock().setData(data.getData(), true);
					blockList.add(location.getBlock());
				}
			}
		}
		getObjID().addBlock(blockList);
		return p;
	}
	
	private EulerAngle eulerAngleFetcher(NBTTagCompound eularAngle){
		Double X = eularAngle.getDouble("X");
		Double Y = eularAngle.getDouble("Y");
		Double Z = eularAngle.getDouble("Z");
		return new EulerAngle(X, Y, Z);
	}

	@Override
	public void spawn(Location arg0) {}
}
