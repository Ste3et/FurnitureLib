package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.Database.Serializer;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.ModelBlock;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.ModelBlockAquaticUpdate;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.ModelBlockCombatUpdate;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.entity.Interactable;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Modelschematic{
	
	private HashMap<ModelVector, fEntity> entityMap = new HashMap<ModelVector, fEntity>();
	private HashMap<ModelVector, ModelBlock> blockDataMap = new HashMap<ModelVector, ModelBlock>();
	protected Vector min = new Vector(), max = new Vector();
	protected PlaceableSide placeableSide = PlaceableSide.TOP;
	protected String name;
	//protected ExecuteTimer timer = new ExecuteTimer();
	
	public Modelschematic(InputStream stream, String fileHeader){
		this(YamlConfiguration.loadConfiguration(new InputStreamReader(stream)), fileHeader);
    }
	
	public Modelschematic(YamlConfiguration configuration, String fileHeader) {
		this.name = fileHeader;
		FurnitureLib.debug(this.name + " header found.");
		this.loadEntitiesTypo(fileHeader, configuration);
		this.loadBlockData(fileHeader, configuration);
		this.placeableSide = PlaceableSide.valueOf(configuration.getString(fileHeader + ".placeAbleSide", "TOP").toUpperCase());
	}

    public Modelschematic(File file, String fileHeader) throws FileNotFoundException {
        this(new FileInputStream(file), fileHeader);
    }

    public Modelschematic(String name) {
		this.name = name;
	}
	
	public HashMap<ModelVector, fEntity> getEntityMap(){
		return this.entityMap;
	}
	
	public HashMap<ModelVector, ModelBlock> getBlockMap(){
		return this.blockDataMap;
	}
	
	public PlaceableSide getPlaceableSide() {
		return this.placeableSide;
	}
	
	public String getHeader(YamlConfiguration config){
		return (String) config.getConfigurationSection("").getKeys(false).toArray()[0];
	}
	
	public fEntity readNBTtag(NBTTagCompound compound) {
		final String entityString = (compound.hasKey("EntityType") ? compound.getString("EntityType") : "armor_stand").toLowerCase();
		fEntity entity = FurnitureManager.getInstance().readEntity(entityString, null, null);
		if(Objects.nonNull(entity)) {
			entity.loadMetadata(compound);
		}else {
			FurnitureLib.getInstance().send("Incorect Entity found:");
			FurnitureLib.getInstance().send(entityString + " isn't supported but is incudet in: " + this.name);
			return null;
		}
		return entity;
	}
	
	private void loadBlockData(String yamlHeader, YamlConfiguration config) {
		boolean aquatic = FurnitureLib.isNewVersion();
		String loadParser = yamlHeader + "." + (aquatic ? ModelBlockAquaticUpdate.CONFIGKEY : ModelBlockCombatUpdate.CONFIGKEY);
		FurnitureLib.debug(this.name + " load: " + (aquatic ? ModelBlockAquaticUpdate.CONFIGKEY : ModelBlockCombatUpdate.CONFIGKEY) + " (BlockList)");
		if(config.isConfigurationSection(loadParser)) {
			FurnitureLib.debug(this.name + " load: " + (aquatic ? ModelBlockAquaticUpdate.CONFIGKEY : ModelBlockCombatUpdate.CONFIGKEY) + " isConfigurationSection = true");
			config.getConfigurationSection(loadParser).getKeys(false).stream().forEach(key -> {
				ModelBlock block = aquatic ? new ModelBlockAquaticUpdate(config, loadParser + "." + key) : new ModelBlockCombatUpdate(config, loadParser + "." + key);
				if(Objects.nonNull(block) && Material.AIR != block.getMaterial()) {
					this.blockDataMap.put(block.getVector(), block);
					this.setMax(block.getVector());
				}
			});
		}
	}
	
	private void setMax(ModelVector vector) {
		this.min = Vector.getMinimum(vector.toVector(), this.min);
		this.max = Vector.getMaximum(vector.toVector(), this.max);
	}
	
	private void loadEntitiesTypo(String yamlHeader, YamlConfiguration config) {
		String configString = FurnitureLib.isNewVersion() ? yamlHeader+".projectData.entities" : yamlHeader+".ProjectModels.ArmorStands";
		String typoConfigString = FurnitureLib.isNewVersion() ? yamlHeader+".projectData.entitys" : yamlHeader+".ProjectModels.ArmorStands";
		FurnitureLib.debug(this.name + " load: " + configString + " (Entities)");
		if(config.isConfigurationSection(configString)) {
			FurnitureLib.debug(this.name + " load: " + configString + " isConfigurationSection = true");
			loadEntities(configString, config);
		}else if(config.isConfigurationSection(typoConfigString)) {
			FurnitureLib.debug(this.name + " load: " + configString + " isConfigurationSection = true");
			loadEntities(typoConfigString, config);
		}
	}
	
	private void loadEntities(String configString, YamlConfiguration config) {
		//I know "entitys" is a typo but it can't fix easly here;
		FurnitureLib.debug(this.name + " load: " + configString + " isConfigurationSection = true");
		config.getConfigurationSection(configString).getKeys(false).stream()
		.forEach(key -> {
			Optional<byte[]> optinalKey = decodeBase64toByte(config.getString(configString + "." + key, ""), key);
			if(optinalKey.isPresent()) {
				try(ByteArrayInputStream bin = new ByteArrayInputStream(optinalKey.get())) {
					final NBTTagCompound entityData = NBTCompressedStreamTools.read(bin);
					final ModelVector vector = new ModelVector(entityData.getCompound("Location"));
					final fEntity entity = readNBTtag(entityData);
					if(Objects.nonNull(vector) && Objects.nonNull(entity)) {
						this.entityMap.put(vector, entity);
						//this.setMax(vector);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		FurnitureLib.debug(this.name + " load: " + configString + " has loadet: " + this.entityMap.size() + " Entities");
	}
	
	private Optional<byte[]> decodeBase64toByte(String encodedString, String key) {
		try {
			return Optional.of(Base64.getDecoder().decode(encodedString));
		}catch (IllegalArgumentException e) {
			System.err.println(this.name + " is a corrupted dModel file entity {" + key + "}");
			System.err.println("FurnitureLib try to skip these entity: " + e.getMessage());
			return Optional.empty();
		}
	}

    protected List<Vector> getBlocksInArea(Vector start, Vector end) {
        List<Vector> vectorList = new ArrayList<>();
        int topBlockX = (Math.max(start.getBlockX(), end.getBlockX()));
        int bottomBlockX = (Math.min(start.getBlockX(), end.getBlockX()));

        int topBlockY = (Math.max(start.getBlockY(), end.getBlockY()));
        int bottomBlockY = (Math.min(start.getBlockY(), end.getBlockY()));

        int topBlockZ = (Math.max(start.getBlockZ(), end.getBlockZ()));
        int bottomBlockZ = (Math.min(start.getBlockZ(), end.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Vector vector = new Vector(x, y, z);
                    vectorList.add(vector);
                }
            }
        }
        return vectorList;
    }

    protected ModelVector rotateVector(ModelVector vector, BlockFace direction) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();
        direction = getPlaceableSide().equals(PlaceableSide.SIDE) ? direction.getOppositeFace() : direction;
        ModelVector returnVector = new ModelVector(x, y, z, vector.getYaw(), vector.getPitch());
        switch (direction) {
            case SOUTH:
                returnVector = new ModelVector(-x, y, -z, vector.getYaw() + 180f, vector.getPitch());
                break;
            case WEST:
                returnVector = new ModelVector(z, y, -x, vector.getYaw() + 270f, vector.getPitch());
                break;
            case EAST:
                returnVector = new ModelVector(-z, y, x, vector.getYaw() + 90f, vector.getPitch());
                break;
            default:
                break;
        }

        return returnVector;
    }
    
    public boolean isDestroyAble() {
    	AtomicBoolean returnBoolean = new AtomicBoolean(getBlockMap().isEmpty() ? false : true);
    	if(returnBoolean.get() == false) {
    		returnBoolean.set(getEntityMap().values().stream().filter(Interactable.class::isInstance).map(Interactable.class::cast).filter(Interactable::canInteractWith).findFirst().isPresent());
    	}
    	return returnBoolean.get();
    }
    
    public void save(File file) {
    	YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    	saveEntities(configuration);
    	try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void saveEntities(YamlConfiguration configuration) {
    	AtomicInteger integer = new AtomicInteger(0);
    	if(FurnitureLib.isNewVersion()) {
    		this.entityMap.entrySet().stream().forEach(entry -> {
        		NBTTagCompound metadata = entry.getValue().getMetaData();
        		metadata.set("Location", entry.getKey().toNBTTagCompound());
        		byte[] bytes = Serializer.armorStandtoBytes(metadata);
        		String base64 = Base64.getEncoder().encodeToString(bytes);
        		configuration.set(this.name + ".projectData.entities." + integer.getAndIncrement(), base64);
        	});
    	}else {
    		this.entityMap.entrySet().stream().forEach(entry -> {
        		NBTTagCompound metadata = entry.getValue().getMetaData();
        		metadata.set("Location", entry.getKey().toNBTTagCompound());
        		byte[] bytes = Serializer.armorStandtoBytes(metadata);
        		String base64 = Base64.getEncoder().encodeToString(bytes);
        		configuration.set(this.name + ".ProjectModels.ArmorStands." + integer.getAndIncrement(), base64);
        	});
    	}
    }
}