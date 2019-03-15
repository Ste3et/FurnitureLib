package de.Ste3et_C0st.FurnitureLib.ShematicLoader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ProjectLoader extends Furniture{
	
	public String header;
	private ProjektInventory inv=null;
	
	public ProjectLoader(ObjectID id){
		this(id, true);
	}
	
	public ProjectLoader(ObjectID id, boolean rotate){
		super(id);
		try{
			YamlConfiguration config = new YamlConfiguration();
			String filepart = "plugins/FurnitureLib/models/" + getObjID().getProject()+".dModel";
			config.load(new File(filepart));
			header = getHeader(config);
			UUID uuid = getObjID().getUUID();
			if(!getObjID().isFromDatabase()) {
				boolean blocked = setBlocks(id.getStartLocation(), config, rotate);
				if(blocked) {
					Player player = Bukkit.getPlayer(uuid);
					if(player != null) {
						if(!player.getGameMode().equals(GameMode.CREATIVE) || !FurnitureLib.getInstance().creativePlace()){
							player.getInventory().addItem(id.getProjectOBJ().getCraftingFile().getRecipe().getResult());
						}
					}
					getObjID().setSQLAction(SQLAction.REMOVE);
					return;
				}
			}else {
				registerBlocks(id.getStartLocation(), config, rotate);
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
			for(String s : config.getConfigurationSection(header+".projectData.entitys").getKeys(false)){
				String md5 = config.getString(header+".projectData.entitys."+s);
				byte[] by = null;
				
				try {
					by = Base64.getDecoder().decode(md5);
				}catch (Exception e) {
					by = Base64.getUrlDecoder().decode(md5);
				}
				
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
				String entity = metadata.hasKey("EntityType") ? metadata.getString("EntityType") : "armor_stand";
				fEntity packet = FurnitureManager.getInstance().createFromType(entity, armorLocation, getObjID());
				packet.loadMetadata(metadata);
				if(customName.equalsIgnoreCase("#ITEM#") 
				|| customName.equalsIgnoreCase("#BLOCK#") 
				|| customName.equalsIgnoreCase("#SITZ#") 
				|| customName.startsWith("#Light")
				|| customName.toUpperCase().startsWith("#DYE_")){
					packet.setNameVasibility(false);
				}
			}
			send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerBlocks(Location startLocation, YamlConfiguration config, boolean rotable) {
		List<Block> blockList = new ArrayList<Block>();
		if(config.isConfigurationSection(header+".projectData.blockList")) {
			for(String s : config.getConfigurationSection(header+".projectData.blockList").getKeys(false)) {
				double x = config.getDouble(header+".projectData.blockList." + s + ".xOffset");
				double y = config.getDouble(header+".projectData.blockList." + s + ".yOffset");
				double z = config.getDouble(header+".projectData.blockList." + s + ".zOffset");
				Location armorLocation = getRelative(getLocation(), getBlockFace(), -z, -x).add(0, y, 0);
				Block b = getWorld().getBlockAt(armorLocation);
				blockList.add(b);
				if(b.getType().name().toUpperCase().endsWith("BED")) {
					BlockFace face = ((Directional) b.getBlockData()).getFacing();
					Block top = b.getRelative(face);
					blockList.add(top);
				}else if(b.getType().name().toUpperCase().endsWith("DOOR")) {
					if(!b.getType().name().toUpperCase().contains("TRAP")) blockList.add(b.getRelative(BlockFace.UP));
				}
			}
		}
		getObjID().addBlock(blockList);
	}
	
	public boolean setBlocks(Location startLocation, YamlConfiguration config, boolean rotable) {
		List<Block> blockList = new ArrayList<Block>();
		if(config.isConfigurationSection(header+".projectData.blockList")) {
			HashMap<Location, BlockData> data = new HashMap<Location, BlockData>();
			for(String s : config.getConfigurationSection(header+".projectData.blockList").getKeys(false)) {
				double x = config.getDouble(header+".projectData.blockList." + s + ".xOffset");
				double y = config.getDouble(header+".projectData.blockList." + s + ".yOffset");
				double z = config.getDouble(header+".projectData.blockList." + s + ".zOffset");
				String materialStr = config.getString(header+".projectData.blockList." + s + ".material");
				Location armorLocation = getRelative(getLocation(), getBlockFace(), -z, -x).add(0, y, 0);
				String str = "";
				
				if(config.contains(header+".projectData.blockList." + s + ".blockData")) {
					try {
						str = config.getString(header+".projectData.blockList." + s + ".blockData");
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(str.isEmpty()) {
					if(materialStr == null || materialStr.isEmpty()) continue;
					String blockDataString = "minecraft:" + materialStr.toLowerCase();
					if(config.isSet(header+".projectData.blockList." + s + ".Rotation")){
						blockDataString += "[facing="+config.getString(header+".projectData.blockList." + s + ".Rotation")+"]";
					}
					str = blockDataString;
				}
				
				/**
				 * CheckPlaceAble
				 */
				
				
				if(!str.isEmpty()) {
					try {
						BlockData blockData = Bukkit.createBlockData(str);
						if(blockData instanceof Directional) {
							Directional r = (Directional) blockData;
							BlockFace original = r.getFacing();
							if(!(original.equals(BlockFace.UP) || original.equals(BlockFace.DOWN))) {
								float yaw = getLutil().FaceToYaw(original);
								yaw += getYaw();
								original = getLutil().yawToFace(yaw);
								((Directional) blockData).setFacing(original.getOppositeFace());
							}
						}
						if(!armorLocation.getBlock().isEmpty()) return true;
						data.put(armorLocation, blockData);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if(!data.isEmpty()) {
				data.entrySet().forEach(entry -> {
					Block b = getWorld().getBlockAt(entry.getKey());
					b.setBlockData(entry.getValue(), false);
					if(entry.getValue().getMaterial().name().toUpperCase().endsWith("BED")) {
						BlockFace face = ((Directional) entry.getValue()).getFacing();
						Block top = b.getRelative(face);
						top.setBlockData(entry.getValue(), false);
						Bed bed = (Bed) top.getBlockData();
						bed.setPart(Part.HEAD);
						top.setBlockData(bed, false);
						blockList.add(top);
					}else if(entry.getValue().getMaterial().name().toUpperCase().endsWith("DOOR")) {
						if(!entry.getValue().getMaterial().name().toUpperCase().contains("TRAP")) {
							Block top = b.getRelative(BlockFace.UP);
							top.setBlockData(entry.getValue(), false);
							Door door = (Door) top.getBlockData();
							door.setHalf(Half.TOP);
							top.setBlockData(door, false);
							blockList.add(top);
						}
					}
					blockList.add(b);
				});
			}
		}
		getObjID().addBlock(blockList);
		return false;
	}
	
	public String getHeader(YamlConfiguration file){return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];}

	@Override
	public void spawn(Location arg0) {}

	@Override
	public void onClick(Player player) {
		if(getObjID() == null) return;
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)) return;
		if(player == null) return;
		boolean b = false;
		if(canBuild(player, false)){
			if(this.inv != null) {
				this.inv.openInventory(player);
				return;
			}
			if(runFunction(player)) {
				update();
				return;
			}
			b = true;
		}
		if(!this.passangerFunction(player) && b) {
			player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
		}
	}
	
	/**
	 * This is the called Function onBreak
	 */
	@Override
	public void onBreak(Player player) {
		if(getObjID() == null) return;
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)) return;
		if(player == null) return;
		if(canBuild(player)) {
			if(this.inv != null) {
				for(ItemStack stack : inv.getInv().getContents()){
					if(stack != null){
						getWorld().dropItemNaturally(getLocation().clone().add(0, .5, 0), stack);
					}
				}
			}
			
			getfAsList().stream().filter(entity -> entity.getName().equalsIgnoreCase("#ITEM#") || entity.getName().equalsIgnoreCase("#BLOCK#")).
			forEach(entity -> {
				for(ItemStack stack : entity.getInventory().getIS()) {
					if(stack != null){
						getWorld().dropItemNaturally(getLocation().clone().add(0, .5, 0), stack);
					}
				}
			});
			
			this.destroy(player);
		}
	}
}
