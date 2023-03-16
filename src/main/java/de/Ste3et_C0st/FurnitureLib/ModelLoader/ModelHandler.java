package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.ModelBlock;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.BlockManager;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModelHandler extends Modelschematic {

	public ModelHandler(File file, String fileHeader) throws FileNotFoundException {
        super(file, fileHeader);
    }

    public ModelHandler(InputStream stream, String fileHeader) {
        super(stream, fileHeader);
    }
    
    public ModelHandler(YamlConfiguration configuration, String fileHeader) {
        super(configuration, fileHeader);
    }

    public ModelHandler(String name) {
        super(name);
    }

    public void spawn(ObjectID id) {
    	FurnitureLib.debug("FurnitureLib {ModelHandler} -> Spawn [" + id.getProject() + "]");
        Location startLocation = id.getStartLocation().add(.5, 0, .5);
        BlockFace direction = LocationUtil.yawToFace(id.getStartLocation().getYaw()).getOppositeFace();
        id.addEntities(addEntity(startLocation, direction, id));
        id.addBlock(addBlocks(startLocation, direction));
        FurnitureLib.debug("FurnitureLib {ModelHandler} -> Spawn Send Models [" + id.getProject() + "]");
        id.send(startLocation.getWorld().getPlayers());
        FurnitureLib.debug("FurnitureLib {ModelHandler} -> Spawn Finish [" + id.getProject() + "]");
    }
    
    public CompletableFuture<Void> spawnWithAnimation(ObjectID id) {
    	FurnitureLib.debug("FurnitureLib {ModelHandler} -> Spawn [" + id.getProject() + "]");
        Location startLocation = id.getStartLocation().add(.5, 0, .5);
        BlockFace direction = LocationUtil.yawToFace(id.getStartLocation().getYaw()).getOppositeFace();
    	List<fEntity> entities = addEntity(startLocation, direction, id);
    	
    	return null;
    }

    public HashMap<Location, ModelBlock> getBlockData(Location startLocation, BlockFace direction) {
        HashMap<Location, ModelBlock> locationList = new HashMap<>();
        getBlockMap().forEach((key, value) -> {
			ModelVector rotateVector = rotateVector(key, direction);
			Location location = startLocation.clone().add(rotateVector.toVector());
			location.setYaw(0);
			location.setPitch(0);
			locationList.put(location, value);
		});
        return locationList;
    }
    
    public HashSet<fEntity> getEntityMap(Location startLocation, BlockFace direction){
    	HashSet<fEntity> locationList = new HashSet<>();
    	 FurnitureLib.debug("FurnitureLib {ModelHandler} -> Calculate Entities");
    	 getEntityMap().forEach((key, value) -> {
 			fEntity entity = value.clone();
 			ModelVector rotateVector = rotateVector(key, direction);
 			Location entityLocation = startLocation.clone().add(rotateVector.toVector());
 			entityLocation.setYaw(rotateVector.getYaw());
 			entityLocation.setPitch(rotateVector.getPitch());
 			entity.setLocation(entityLocation);
 			locationList.add(entity);
 			String customName = entity.getCustomName();
 			if (customName.equalsIgnoreCase("#ITEM#")
 					|| customName.equalsIgnoreCase("#BLOCK#")
 					|| customName.equalsIgnoreCase("#SITZ#")
 					|| customName.startsWith("#Light")
 					|| customName.startsWith("/")
 					|| customName.toUpperCase().startsWith("#DYE_")) {
 				entity.setNameVisibility(false);
 			}
 		 });
    	 FurnitureLib.debug("FurnitureLib {ModelHandler} -> Calculate Entities Finish");
    	 return locationList;
    }

    public List<Block> addBlocks(Location startLocation, BlockFace direction) {
        List<Block> blockList = new ArrayList<Block>();
        FurnitureLib.debug("FurnitureLib {ModelHandler} -> Calculate Blocks");
        this.getBlockData(startLocation, direction).entrySet().stream().sorted((e1, e2) -> Double.compare(e1.getKey().getY(), e2.getKey().getY()) ).forEach(entry -> {
			Block b = entry.getKey().getBlock();
			entry.getValue().place(b.getLocation(), direction);
			blockList.add(b);
		});
        
        return blockList;
    }
    
    public List<Location> getBlockLocations(Location startLocation, BlockFace direction) {
    	 List<Location> blockList = new ArrayList<Location>();
    	 FurnitureLib.debug("FurnitureLib {ModelHandler} -> Calculate Blocks");
    	 this.getBlockData(startLocation, direction).entrySet().stream().sorted((e1, e2) -> Double.compare(e1.getKey().getY(), e2.getKey().getY()) ).forEach(entry -> {
 			blockList.add(entry.getKey());
 		});
    	 FurnitureLib.debug("FurnitureLib {ModelHandler} -> Calculate Blocks Finish");
    	 return blockList;
    }

    private List<fEntity> addEntity(Location startLocation, BlockFace direction, ObjectID id) {
    	HashSet<fEntity> entityMap = new HashSet<>(this.getEntityMap(startLocation, direction));
    	List<fEntity> entityList = new ArrayList<fEntity>();
    	entityMap.stream().forEach(entry -> {
    		entry.setObjectID(id);
    		entityList.add(entry);
    	});
        return entityList;
    }
    
//    private ItemStack fixItemStack(ItemStack stack) {
//    	
//    	
//    	
//    	return stack;
//    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.of(super.min, super.max);
    }

    public void setBoundingBox(BoundingBox boundingbox) {
        super.min = boundingbox.getMin();
        super.max = boundingbox.getMax();
    }

    public void setSize(Vector min, Vector max) {
        super.min = min;
        super.max = max;
    }

    public void setMax(Vector vector) {
        super.max = vector;
    }

    public void setPlaceableSide(PlaceableSide side) {
        this.placeableSide = side;
    }

    public Vector getPoint1() {
        return super.min;
    }

    public Vector getPoint2() {
        return super.max;
    }

    public boolean isPlaceable(Location loc) {
        BlockFace direction = LocationUtil.yawToFace(loc.getYaw());
        return isPlaceable(loc, direction);
    }

    public boolean isPlaceable(Location startLocation, BlockFace direction) {
        AtomicBoolean returnValue = new AtomicBoolean(true);
        if (Objects.nonNull(startLocation)) {
            ModelVector min = rotateVector(new ModelVector(this.min), direction.getOppositeFace());
            ModelVector max = rotateVector(new ModelVector(this.max), direction.getOppositeFace());
            BoundingBox box = BoundingBox.of(min.toVector(), max.toVector());
            box.shift(startLocation.clone().add(0,0,0));
            List<Vector> vectorList = getBlocksInArea(box.getMin(), box.getMax());
            World world = startLocation.getWorld();
            vectorList.forEach(vector -> {
                Location location = vector.toLocation(world);
                Block block = location.getBlock();
                if (block.getType().isSolid()) {
                	if(FurnitureLib.getVersionInt() < 16) {
                		returnValue.set(false);
                        LocationUtil.particleBlock(block);
                	}
                	
                	if(FurnitureLib.getInstance().getBlockManager().isPaper()) {
//                		if(block.isBuildable() == true) {
//                    		returnValue.set(false);
//                            LocationUtil.particleBlock(block);
//                    	}
                	}
                	
                }
            });
        }
        return returnValue.get();
    }
    
    public boolean checkPermission(Player player, Location startLocation, BlockFace direction) {
    	 AtomicBoolean returnValue = new AtomicBoolean(true);
         if(FurnitureLib.getInstance().getPermManager().useProtectionLib() == false) return true;
    	 if (Objects.nonNull(startLocation)) {
             ModelVector min = rotateVector(new ModelVector(this.min), direction.getOppositeFace());
             ModelVector max = rotateVector(new ModelVector(this.max), direction.getOppositeFace());
             BoundingBox box = BoundingBox.of(min.toVector(), max.toVector());
             box.shift(startLocation.clone().add(0,0,0));
             List<Vector> vectorList = getBlocksInArea(box.getMin(), box.getMax());
             World world = startLocation.getWorld();
             vectorList.forEach(vector -> {
                 Location location = vector.toLocation(world);
                 if (FurnitureLib.getInstance().getPermManager().canBuild(player, location) == false) {
                     returnValue.set(false);
                     LocationUtil.particleBlock(location.getBlock());
                 }
             });
         }
         return returnValue.get();
    }

    public void setSize(Integer length, Integer height, Integer width, CenterType type) {
        length = length - 1;
        height = height - 1;
        width = width - 1;

        Vector pos1 = new Vector();
        Vector pos2 = new Vector(width, height, length);

        if (type.equals(CenterType.RIGHT)) {
            pos2.setZ(-length);
        } else if (type.equals(CenterType.CENTER)) {
            width = Math.round((width) / 2);
            pos1.setX(-width);
            pos2.setX(width);
            pos2.setZ(-length);
        }
        if (type.equals(CenterType.LEFT)) {
            pos2.setZ(-length);
            pos2.setX(-width);
        }
        setSize(pos1, pos2);
    }

}
