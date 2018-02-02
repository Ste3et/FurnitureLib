package de.Ste3et_C0st.FurnitureLib.ShematicLoader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Directional;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureBreakEvent;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.FurnitureBlockBreakEventListener;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.FurnitureBlockClickEventListener;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.FurnitureEntityBreakEventListener;
import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.FurnitureEntityClickEventListener;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ProjectLoader extends Furniture{
	private Object[] enumItemSlots = EnumWrappers.ItemSlot.values();
	public String header;
	private ProjektInventory inv=null;
	public ProjectLoader(ObjectID id){
		super(id);
		try{
//			System.out.println("ProjectLoader: " + getObjID().getProject());
//			System.out.println("1");
			YamlConfiguration config = new YamlConfiguration();
			//			System.out.println("2");
			config.load(new File("plugins/FurnitureLib/Crafting/", getObjID().getProject()+".yml"));
			//			System.out.println("3");
			header = getHeader(config);
			//System.out.println("4");
			Player player = setBlock(id.getStartLocation(), config, true);
			//System.out.println("5");
			if(player!=null){
				if(!player.getGameMode().equals(GameMode.CREATIVE) || !FurnitureLib.getInstance().creativePlace()){
					player.getInventory().addItem(id.getProjectOBJ().getCraftingFile().getRecipe().getResult());
				}
				getObjID().setSQLAction(SQLAction.REMOVE);
				return;
			}
			//System.out.println("6");
			if(isFinish()){
				registerInventory();
				registerEvents();
				return;
			}
			//System.out.println("7");
			spawn(id.getStartLocation(), config, true);
			registerInventory();
			registerEvents();
			//System.out.println("8");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void registerEvents(){
		if(!getObjID().getPacketList().isEmpty()){
			Bukkit.getPluginManager().registerEvents(new FurnitureEntityClickEventListener(getObjID(), inv), FurnitureLib.getInstance());
			Bukkit.getPluginManager().registerEvents(new FurnitureEntityBreakEventListener(getObjID(), inv), FurnitureLib.getInstance());
		}
		if(!getObjID().getBlockList().isEmpty()){
			Bukkit.getPluginManager().registerEvents(new FurnitureBlockClickEventListener(getObjID(), inv), FurnitureLib.getInstance());
			Bukkit.getPluginManager().registerEvents(new FurnitureBlockBreakEventListener(getObjID(), inv), FurnitureLib.getInstance());
		}
	}
	
	public ProjectLoader(ObjectID id, boolean rotate){
		super(id);
		try{
			YamlConfiguration config = new YamlConfiguration();
			config.load(new File("plugins/FurnitureLib/Crafting/", getObjID().getProject()+".yml"));
			header = getHeader(config);
			Player player = setBlock(id.getStartLocation(), config, rotate);
			if(player!=null){
				getObjID().setSQLAction(SQLAction.REMOVE);
				return;
			}
			
			if(isFinish()){
				registerInventory();
				toggleLight(false);
				Bukkit.getPluginManager().registerEvents(this, id.getProjectOBJ().getPlugin());
				return;
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
						if(is==null) is = new ItemStack(Material.AIR, 1);
						packet.getInventory().setSlot(object.toString(), is);
					}
				}
				NBTTagCompound euler = metadata.getCompound("EulerAngle");
				for(BodyPart part : BodyPart.values()){
					packet.setPose(eulerAngleFetcher(euler.getCompound(part.toString())), part);
				}
				packet.setBasePlate(b).setSmall(s1).setMarker(m).setArms(a).setGlowing(g).setNameVasibility(n).setName(customName).setFire(f).setGlowing(g).setInvisible(i);
				if(customName.equalsIgnoreCase("#ITEM#") 
				|| customName.equalsIgnoreCase("#BLOCK#") 
				|| customName.equalsIgnoreCase("#SITZ#") 
				|| customName.toUpperCase().startsWith("#DYE_")){
					packet.setNameVasibility(false);
				}
			}
			send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<Integer, HashMap<Location, ProjectMaterial>> getBlockMap(Location loc, YamlConfiguration config, boolean rotate){
		HashMap<Integer, HashMap<Location, ProjectMaterial>> map = new HashMap<Integer, HashMap<Location, ProjectMaterial>>();
		try {
			if(config.isConfigurationSection(header+".ProjectModels.Block")){
				int i = 0;
				for(String s : config.getConfigurationSection(header+".ProjectModels.Block").getKeys(false)){
					HashMap<Location, ProjectMaterial> block = new HashMap<Location, ProjectMaterial>();
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
					ProjectMaterial material = new ProjectMaterial(m, b);
					if(config.isSet(header+".ProjectModels.Block." + s + ".Rotation")){
						material.setBlockFace(BlockFace.valueOf(config.getString(header+".ProjectModels.Block." + s + ".Rotation")));
					}
					
//					if(config.isSet(header+".ProjectModels.Block." + s + ".Inventory")){
//						InventoryType type = InventoryType.valueOf(config.getString(header+".ProjectModels.Block." + s + ".Inventory.type"));
//						Inventory inv = Bukkit.createInventory(null, type);
//						for(String j : config.getConfigurationSection(header+".ProjectModels.Block." + s + ".Inventory").getKeys(false)){
//							if(!j.equalsIgnoreCase("type")){
//								String base64 = config.getString(header+".ProjectModels.Block." + s + ".Inventory." + j);
//								byte[] bString = Base64.decodeBase64(base64);
//								ByteArrayInputStream bin = new ByteArrayInputStream(bString);
//								NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
//								bin.close();
//								ItemStack stack = new CraftItemStack().getItemStack(compound);
//								inv.setItem(Integer.parseInt(j), stack);
//							}
//						}
//						material.setInventory(inv);
//					}
					
					block.put(armorLocation, material);
					map.put(i, block);
					i++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return map;
	}
	
	@SuppressWarnings("deprecation")
	private Player setBlock(Location loc, YamlConfiguration config, boolean rotate){
		HashMap<Integer, HashMap<Location, ProjectMaterial>> map = getBlockMap(loc, config, rotate);
		
		List<Block> blockList = new ArrayList<Block>();
		boolean b = true, c = isFinish(), k = true;
		UUID uuid = getObjID().getUUID();
		Player p = Bukkit.getPlayer(uuid);
		for(Integer i : map.keySet()){
			for(Location blockLocation : map.get(i).keySet()){
				ProjectMaterial material = map.get(i).get(blockLocation);
				if(!material.getMaterial().equals(Material.AIR)){
					if(!c){
						if(p!=null&&p.isOnline()){
							if(blockLocation.getBlock().getType().isSolid()){
								if(!blockLocation.getBlock().getType().equals(material.getMaterial())){
									b = false;
									getLutil().particleBlock(blockLocation.getBlock(), p);
									continue;
								}}
							
							if(material.getMaterial().equals(Material.BED_BLOCK)){
								
								Location bottom = blockLocation.clone().subtract(0, 1, 0);
								if(!bottom.getBlock().getType().isSolid()){
									k = false;
								}
							}else if(material.getMaterial().name().endsWith("DOOR")){
								Location bottom = blockLocation.clone().subtract(0, 1, 0);
								if(!bottom.getBlock().getType().isSolid()){
									getLutil().particleBlock(bottom.getBlock(), p, Particle.REDSTONE, 0);
									k = false;
								}else if(bottom.getBlock().getType().name().endsWith("STAIRS")){
									getLutil().particleBlock(bottom.getBlock(), p, Particle.REDSTONE, 0);
									k = false;
								}else if(bottom.getBlock().getType().equals(Material.BEACON)){
									getLutil().particleBlock(bottom.getBlock(), p, Particle.REDSTONE, 0);
									k = false;
								}
							}
						}
					}
				}
			}
		}
		
		if(b && k){
			p = null;
			for(Integer i : map.keySet()){
				for(Location blockLocation : map.get(i).keySet()){
					ProjectMaterial material = map.get(i).get(blockLocation);
					if(!material.getMaterial().equals(Material.AIR)){
						if(blockLocation.getBlock().isEmpty()){
							Block block = blockLocation.getBlock();
							block.setType(material.getMaterial(), false);
							block.setData(material.getByte(), false);
							BlockState state = block.getState();
							
//							if(material.getInventory()!=null){
//								Inventory inv = ((InventoryHolder) state).getInventory();
//								inv.setContents(material.getInventory().getContents());							
//							}
							
							if(material.getMaterial().equals(Material.TORCH)){
								
							}else if(material.isDirectional()){
								BlockFace start = BlockFace.NORTH;
								BlockFace newFace = getBlockFace();
								BlockFace oldBlockFace = material.getBlockFace();
								float yaw1 = getLutil().FaceToYaw(start);
								float yaw2 = getLutil().FaceToYaw(newFace);
								float yaw3 = getLutil().FaceToYaw(oldBlockFace);
								float newYaw4 = yaw1 + yaw2 + yaw3;
								BlockFace face = getLutil().yawToFace(newYaw4);
								
								if(material.getMaterial().equals(Material.BED_BLOCK)){
									block.setType(Material.AIR);
									getLutil().setBed(face, blockLocation);
									registerBlock(block);
									continue;
								}if(material.getMaterial().name().endsWith("DOOR")){
									//Bottom block
									Door d = (Door) state.getData();
									d.setTopHalf(false);
									d.setFacingDirection(face);
									
									//Top Block
									Block top = blockLocation.clone().add(0, 1, 0).getBlock();
									
									top.setType(d.getItemType());
									BlockState stateTop = top.getState();
									Door topDoor = (Door) stateTop.getData();
									topDoor.setFacingDirection(d.getFacing());
									topDoor.setTopHalf(true);
									state.setData(d);
									stateTop.setData(topDoor);
									stateTop.update(true);
									state.update(true);
									registerBlock(block);
									continue;
								}else{
									Directional directional = (Directional) state.getData();
									directional.setFacingDirection(face);
									state.setData((MaterialData) directional);
								}
							}
							state.update(true);
						}
						if(blockLocation.getBlock().getType().equals(Material.BED_BLOCK) || blockLocation.getBlock().getType().name().endsWith("DOOR")){
							registerBlock(blockLocation.getBlock());
						}else{
							blockList.add(blockLocation.getBlock());
						}
					}
				}
			}
		}else if(!b){
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NotEnoughSpace"));
		}else if(!k){
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("BlockOfInstability"));
		}
		getObjID().addBlock(blockList);
		return p;
	}
	
	private void registerBlock(Block b){
		List<Block> locationList = new ArrayList<Block>();
		locationList.add(b);
		if(b.getType().equals(Material.BED_BLOCK)){
			BlockState state = b.getState();
			Directional direction = (Directional) state.getData();
			BlockState top = state.getBlock().getRelative(direction.getFacing()).getState();
			locationList.add(top.getBlock());
		}else if(b.getType().name().endsWith("DOOR")){
			Block top = b.getLocation().clone().add(0, 1, 0).getBlock();
			locationList.add(top);
		}
		
		getObjID().addBlock(locationList);
	}
	
	private EulerAngle eulerAngleFetcher(NBTTagCompound eularAngle){
		Double X = eularAngle.getDouble("X");
		Double Y = eularAngle.getDouble("Y");
		Double Z = eularAngle.getDouble("Z");
		return new EulerAngle(X, Y, Z);
	}
	
	public String getHeader(YamlConfiguration file){return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];}

	@Override
	public void spawn(Location arg0) {}

	@Override
	public void onFurnitureBreak(FurnitureBreakEvent e) {}

	@Override
	public void onFurnitureClick(FurnitureClickEvent e) {}
}
