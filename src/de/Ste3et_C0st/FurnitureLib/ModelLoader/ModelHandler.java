package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ModelHandler extends Modelschematic{

	public ModelHandler(File file) throws FileNotFoundException {
		super(file);
	}
	
	public ModelHandler(InputStream stream){
		super(stream);
	}
	
	public ModelHandler(String name) {
		super(name);
	}
	
	public void spawn(ObjectID id) {
		Location startLocation = id.getStartLocation().add(.5, 0, .5);
		BlockFace direction = FurnitureLib.getInstance().getLocationUtil().yawToFace(id.getStartLocation().getYaw()).getOppositeFace();
		id.addEntities(addEntity(startLocation, direction, id));
		id.addBlock(addBlocks(startLocation, direction));
		id.sendAll();
	}
	
	public HashMap<Location, ModelBlock> getBlockData(Location startLocation, BlockFace direction) {
		HashMap<Location, ModelBlock> locationList = new HashMap<Location, ModelBlock>();
		getBlockMap().entrySet().forEach(entry -> {
			ModelVector rotateVector = rotateVector(entry.getKey(), direction);
			locationList.put(startLocation.clone().add(rotateVector.toVector()), entry.getValue());
		});
		return locationList;
	}
	
	public List<Block> addBlocks(Location startLocation, BlockFace direction) {
		List<Block> blockList = new ArrayList<Block>();
		this.getBlockData(startLocation, direction).entrySet().forEach(entry -> {
			Block b = entry.getKey().getBlock();
			entry.getValue().place(b.getLocation(), direction);
			blockList.add(b);
		});
		return blockList;
	}
	
	private List<fEntity> addEntity(Location startLocation, BlockFace direction, ObjectID id) {
		List<fEntity> entityList = new ArrayList<fEntity>();
		getEntityMap().entrySet().forEach(entry -> {
			fEntity entity = entry.getValue().clone();
			ModelVector rotateVector = rotateVector(entry.getKey(), direction);
			Location entityLocation = startLocation.clone().add(rotateVector.toVector());
			entityLocation.setYaw(rotateVector.getYaw());
			entityLocation.setPitch(rotateVector.getPitch());
			entity.setLocation(entityLocation);
			entity.setObjectID(id);
			entityList.add(entity);
			String customName = entity.getCustomName();
			if(customName.equalsIgnoreCase("#ITEM#") 
					|| customName.equalsIgnoreCase("#BLOCK#") 
					|| customName.equalsIgnoreCase("#SITZ#") 
					|| customName.startsWith("#Light")
					|| customName.startsWith("/")
					|| customName.toUpperCase().startsWith("#DYE_")){
						entity.setNameVasibility(false);
			}
		});
		return entityList;
	}
	
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
		BlockFace direction = FurnitureLib.getInstance().getLocationUtil().yawToFace(loc.getYaw());
		return isPlaceable(loc, direction);
	}
	
	public boolean isPlaceable(Location startLocation, BlockFace direction) {
		AtomicBoolean returnValue = new AtomicBoolean(true);
		if(Objects.nonNull(startLocation)) {
			ModelVector min = rotateVector(new ModelVector(this.min), direction.getOppositeFace());
			ModelVector max = rotateVector(new ModelVector(this.max), direction.getOppositeFace());
			BoundingBox box = BoundingBox.of(min.toVector(), max.toVector());
			box.shift(startLocation);
			List<Vector> vectorList = getBlocksInArea(box.getMin(), box.getMax());
			World world = startLocation.getWorld();
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
	
	public void setSize(Integer length, Integer height, Integer width, CenterType type) {
		length = length - 1;
		height = height - 1;
		width = width - 1;
		
		Vector pos1 = new Vector();
		Vector pos2 = new Vector(width,height,length);
		
		if(type.equals(CenterType.RIGHT)) {
			pos2.setZ(-length);
		}else if(type.equals(CenterType.CENTER)) {
			width = Math.round((width) / 2);
			pos1.setX(-width);
			pos2.setX(width);
			pos2.setZ(-length);
		}if(type.equals(CenterType.LEFT)) {
			pos2.setZ(-length);
			pos2.setX(-width);
		}
		setSize(pos1,pos2);
	}
	
}
