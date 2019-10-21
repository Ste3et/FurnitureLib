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
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BoundingBox;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Modelschematic{
	
	private HashMap<fEntity, ModelVector> entityMap = new HashMap<fEntity, ModelVector>();
	private HashMap<BlockData, ModelVector> blockDataMap = new HashMap<BlockData, ModelVector>();
	protected BoundingBox boundingbox;
	
	public Modelschematic(InputStream stream){
		try{
			InputStreamReader reader = new InputStreamReader(stream);
			YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
			String yamlHeader = getHeader(config);
			this.loadEntitys(yamlHeader, config);
			this.loadBlockData(yamlHeader, config);
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
					this.blockDataMap.put(blockData, new ModelVector(x, z, y));
				}
			});
		}
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
					if(Objects.nonNull(vector) && Objects.nonNull(entity)) this.entityMap.put(entity, vector);
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
}