package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Modelschematic{
	
	private HashMap<fEntity, ModelVector> entityMap = new HashMap<fEntity, ModelVector>();
	private HashMap<BlockData, ModelVector> blockDataMap = new HashMap<BlockData, ModelVector>();
	private Vector min = new Vector(), max = new Vector();
	protected BoundingBox boundingbox = new BoundingBox();
	
	public Modelschematic(InputStream stream){
		try{
			InputStreamReader reader = new InputStreamReader(stream);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
			String yamlHeader = getHeader(config);
			this.loadEntitys(yamlHeader, config);
			this.loadBlockData(yamlHeader, config);
			this.boundingbox = BoundingBox.of(min, max);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Modelschematic(File file) throws FileNotFoundException{
		this(new FileInputStream(file));
	}
	
	public Modelschematic() {}
	
	public HashMap<fEntity, ModelVector> getEntityMap(){
		return this.entityMap;
	}
	
	public HashMap<BlockData, ModelVector> getBlockMap(){
		return this.blockDataMap;
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
					this.blockDataMap.put(blockData, vector);
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
						this.entityMap.put(entity, vector);
						//this.boundingbox = this.boundingbox.expand(new Vector(Math.round(vector.getX()), Math.round(vector.getY()), Math.round(vector.getZ())));
						//this.setMax(vector);
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
		if(Objects.nonNull(loc)) {
			BoundingBox box = this.boundingbox.clone();
			ModelVector vector = new ModelVector(loc);
		}
		return false;
	}
}