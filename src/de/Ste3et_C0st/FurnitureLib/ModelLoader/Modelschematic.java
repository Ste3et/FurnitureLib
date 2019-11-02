package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Modelschematic{
	
	private HashMap<ModelVector, fEntity> entityMap = new HashMap<ModelVector, fEntity>();
	private HashMap<ModelVector, BlockData> blockDataMap = new HashMap<ModelVector, BlockData>();
	protected Vector min = new Vector(), max = new Vector();
	protected PlaceableSide placeableSide = PlaceableSide.TOP;
	protected String name;
	
	public Modelschematic(InputStream stream){
		try{
			InputStreamReader reader = new InputStreamReader(stream);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
			String yamlHeader = getHeader(config);
			this.name = yamlHeader;
			this.loadEntitys(yamlHeader, config);
			this.loadBlockData(yamlHeader, config);
			this.placeableSide = PlaceableSide.valueOf(config.getString(yamlHeader + ".placeAbleSide", "TOP").toUpperCase());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Modelschematic(File file) throws FileNotFoundException{
		this(new FileInputStream(file));
	}
	
	public Modelschematic() {}
	
	public HashMap<ModelVector, fEntity> getEntityMap(){
		return this.entityMap;
	}
	
	public HashMap<ModelVector, BlockData> getBlockMap(){
		return this.blockDataMap;
	}
	
	public PlaceableSide getPlaceableSide() {
		return this.placeableSide;
	}
	
	public String getHeader(YamlConfiguration config){
		return (String) config.getConfigurationSection("").getKeys(false).toArray()[0];
	}
	
	public fEntity readNBTtag(NBTTagCompound compound) {
		fEntity entity = FurnitureManager.getInstance().readEntity((compound.hasKey("EntityType") ? compound.getString("EntityType") : "armor_stand").toLowerCase(), null, null);
		entity.loadMetadata(compound);
		return entity;
	}
	
	private void loadBlockData(String yamlHeader, YamlConfiguration config) {
		if(config.isConfigurationSection(yamlHeader+".projectData.blockList")) {
			config.getConfigurationSection(yamlHeader+".projectData.blockList").getKeys(false).stream().forEach(key -> {
				String dataString = yamlHeader+".projectData.blockList." + key + ".";
				double x = config.getDouble(dataString + "xOffset");
				double y = config.getDouble(dataString + "yOffset");
				double z = config.getDouble(dataString + "zOffset");
				String str = config.getString(dataString + "blockData", "");
				String materialStr = config.getString(dataString + "material", "");
				ModelVector vector = new ModelVector(x, y, z);
				if(str.isEmpty()) {
					if(!materialStr.isEmpty()) {
						String blockDataString = "minecraft:" + materialStr.toLowerCase();
						if(config.isSet(dataString + "Rotation")){
							blockDataString += "[facing="+config.getString(dataString + "Rotation")+"]";
						}
						str = blockDataString;
					}
				}
				
				if(!str.isEmpty()) {
					BlockData blockData = Bukkit.createBlockData(str);
					this.blockDataMap.put(vector, blockData);
					this.setMax(vector);
				}
			});
		}
	}
	
	private void setMax(ModelVector vector) {
		this.min = Vector.getMinimum(vector.toVector(), this.min);
		this.max = Vector.getMaximum(vector.toVector(), this.max);
	}
	
	private void loadEntitys(String yamlHeader, YamlConfiguration config) {
		if(config.isConfigurationSection(yamlHeader+".projectData.entitys")) {
			config.getConfigurationSection(yamlHeader+".projectData.entitys").getKeys(false).stream().forEach(key -> {
				try {
					String md5 = config.getString(yamlHeader+".projectData.entitys."+key);
					byte[] by = Base64Coder.decode(md5);
					ByteArrayInputStream bin = new ByteArrayInputStream(by);
					NBTTagCompound entityData = NBTCompressedStreamTools.read(bin);
					ModelVector vector = new ModelVector(entityData.getCompound("Location"));
					fEntity entity = readNBTtag(entityData);
					if(Objects.nonNull(vector) && Objects.nonNull(entity)) {
						this.entityMap.put(vector, entity);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	protected ModelVector rotateVector(ModelVector vector, BlockFace direction){
		double x = vector.getX();
		double y = vector.getY();
		double z = vector.getZ();
		direction = getPlaceableSide().equals(PlaceableSide.SIDE) ? direction.getOppositeFace() : direction;
		ModelVector returnVector = new ModelVector(x, y, z, vector.getYaw(), vector.getPitch());
		switch(direction) {
			case SOUTH: returnVector = new ModelVector(-x, y, -z, vector.getYaw() + 180f, vector.getPitch());break;
			case WEST: returnVector = new ModelVector(z, y, -x, vector.getYaw() + 270f, vector.getPitch());break;
			case EAST: returnVector = new ModelVector(-z, y, x, vector.getYaw() + 90f, vector.getPitch());break;
			default: break;
		}
		
		return returnVector;
	}
	
	public boolean isPlaceable(Location loc) {
		BlockFace direction = FurnitureLib.getInstance().getLocationUtil().yawToFace(loc.getYaw());
		return isPlaceable(loc, direction);
	}
	
	public boolean isPlaceable(Location loc, BlockFace face) {
		AtomicBoolean returnValue = new AtomicBoolean(true);
		if(Objects.nonNull(loc)) {
			ModelVector min = rotateVector(new ModelVector(this.min), face.getOppositeFace());
			ModelVector max = rotateVector(new ModelVector(this.max), face.getOppositeFace());
			BoundingBox box = BoundingBox.of(min.toVector(), max.toVector());
			box.shift(loc);
			List<Vector> vectorList = getBlocksInArea(box.getMin(), box.getMax());
			World world = loc.getWorld();
			vectorList.forEach(vector -> {
				Location location = vector.toLocation(world);
				Block block = location.getBlock();
				if(block.getType().isSolid()) {
					returnValue.set(false);
					LocationUtil.particleBlock(block);
				}
			});
		}
		return returnValue.get();
	}
	
	private List<Vector> getBlocksInArea(Vector start, Vector end) {
        List<Vector> vectorList = new ArrayList<Vector>();
		int topBlockX = (start.getBlockX() < end.getBlockX() ? end.getBlockX() : start.getBlockX());
        int bottomBlockX = (start.getBlockX() > end.getBlockX() ? end.getBlockX() : start.getBlockX());

        int topBlockY = (start.getBlockY() < end.getBlockY() ? end.getBlockY() : start.getBlockY());
        int bottomBlockY = (start.getBlockY() > end.getBlockY() ? end.getBlockY() : start.getBlockY());

        int topBlockZ = (start.getBlockZ() < end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());
        int bottomBlockZ = (start.getBlockZ() > end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                	Vector vector = new Vector(x, y, z);
                	vectorList.add(vector);
                }
            }
        }
        return vectorList;
    }
	
}