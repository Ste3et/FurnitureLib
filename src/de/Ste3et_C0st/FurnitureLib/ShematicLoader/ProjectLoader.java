package de.Ste3et_C0st.FurnitureLib.ShematicLoader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
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
				|| customName.startsWith("/")
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
			HashSet<ProjectMaterial> data = new HashSet<ProjectMaterial>();
			AtomicBoolean b = new AtomicBoolean(false);
			config.getConfigurationSection(header+".projectData.blockList").getKeys(false).stream().forEach(key -> {
				String dataKey = header+".projectData.blockList." + key;
				double x = config.getDouble(dataKey + ".xOffset");
				double y = config.getDouble(dataKey + ".yOffset");
				double z = config.getDouble(dataKey + ".zOffset");
				
				String str = config.getString(dataKey + ".blockData", "");
				String materialStr = config.getString(dataKey + ".material", "");
				
				Location armorLocation = getRelative(getLocation(), getBlockFace(), -z, -x).add(0, y, 0);

				if(str.isEmpty()) {
					if(!materialStr.isEmpty()) {
						String blockDataString = "minecraft:" + materialStr.toLowerCase();
						if(config.isSet(dataKey + ".Rotation")){
							blockDataString += "[facing="+config.getString(dataKey + ".Rotation")+"]";
						}
						str = blockDataString;
					}
				}
				
				if(!armorLocation.getBlock().isEmpty()) {b.set(true); return;}
				
				if(!str.isEmpty()) {
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
					data.add(new ProjectMaterial(dataKey, armorLocation, blockData));
				}
			});
			
			if(b.get()) return true;
			if(!data.isEmpty()) {
				data.stream().forEach(entry -> {
					Block block = getWorld().getBlockAt(entry.getLocation());
					block.setBlockData(entry.getData(), false);
					if(entry.getData().getMaterial().name().toUpperCase().endsWith("BED")) {
						BlockFace face = ((Directional) entry.getData()).getFacing();
						Block top = block.getRelative(face);
						top.setBlockData(entry.getData(), false);
						Bed bed = (Bed) top.getBlockData();
						bed.setPart(Part.HEAD);
						top.setBlockData(bed, false);
						blockList.add(top);
					}else if(entry.getData().getMaterial().name().toUpperCase().endsWith("DOOR")) {
						if(!entry.getData().getMaterial().name().toUpperCase().contains("TRAP")) {
							Block top = block.getRelative(BlockFace.UP);
							top.setBlockData(entry.getData(), false);
							Door door = (Door) top.getBlockData();
							door.setHalf(Half.TOP);
							top.setBlockData(door, false);
							blockList.add(top);
						}
					}else if(entry.getData().getMaterial().equals(Material.PLAYER_HEAD) || entry.getData().getMaterial().equals(Material.PLAYER_WALL_HEAD)) {
						System.out.println(entry.getKey() + ".headMeta");
						if(config.contains(entry.getKey() + ".headMeta")) {
							UUID uuid = UUID.fromString(config.getString(entry.getKey() + ".headMeta"));
							OfflinePlayer player = uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
							if(player != null) {
								System.out.println("headMeta: load");
								Skull s = (Skull) block.getState();
								s.setOwningPlayer(player);
								s.update(true, false);
							}else {
								System.out.println("offlinePlayer = null");
							}
						}
					}
					blockList.add(block);
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
		boolean canInteract = canInteract(player, false);
		boolean function = hasFunction();
		
		FurnitureLib.debug("ProjectLoader -> onClick[project:"+ getObjID().getProject() + "]");
		FurnitureLib.debug("ProjectLoader -> onClick[hasFunction:"+ function + "]");
		FurnitureLib.debug("ProjectLoader -> onClick[canInteract:"+ canInteract + "]");
		
		if(function && canInteract) {
			if(this.inv != null) {
				this.inv.openInventory(player);
				return;
			}
			if(runFunction(player)) {
				update();
				return;
			}
		}else if(function && !canInteract) {
			if(!runPublicFunctions(player)) {
				player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NoPermissions"));
				return;
			}else {
				return;
			}
		}
		
		runPublicFunctions(player);
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
