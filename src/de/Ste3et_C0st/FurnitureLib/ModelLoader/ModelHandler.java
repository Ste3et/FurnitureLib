package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.util.BoundingBox;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ModelHandler extends Modelschematic{

	public ModelHandler(File file) throws FileNotFoundException {
		super(file);
	}
	
	public ModelHandler(InputStream stream){
		super(stream);
	}
	
	public void spawn(ObjectID id) {
		Location startLocation = id.getStartLocation().add(.5, 0, .5);
		BlockFace direction = FurnitureLib.getInstance().getLocationUtil().yawToFace(id.getStartLocation().getYaw()).getOppositeFace();
		id.addEntities(addEntity(startLocation, direction, id));
		id.addBlock(addBlocks(startLocation, direction));
		id.sendAll();
	}
	
	private List<Block> addBlocks(Location startLocation, BlockFace direction) {
		List<Block> blockList = new ArrayList<Block>();
		getBlockMap().entrySet().forEach(entry -> {
			ModelVector rotateVector = rotateVector(entry.getValue(), direction);
			Block block = startLocation.clone().add(rotateVector.toVector()).getBlock();
			BlockData blockData = entry.getKey().clone();
			if(Directional.class.isInstance(blockData)) {
				Directional directional = Directional.class.cast(blockData);
				
				BlockFace originalBlockFace = directional.getFacing();
				float originalYaw = FurnitureLib.getInstance().getLocationUtil().FaceToYaw(originalBlockFace);
				float yawDirection = FurnitureLib.getInstance().getLocationUtil().FaceToYaw(direction);
				float newYaw = originalYaw + yawDirection;
				
				directional.setFacing(FurnitureLib.getInstance().getLocationUtil().yawToFace(newYaw));
			}
			block.setBlockData(blockData, false);
			blockList.add(block);
		});
		return blockList;
	}
	
	private List<fEntity> addEntity(Location startLocation, BlockFace direction, ObjectID id) {
		List<fEntity> entityList = new ArrayList<fEntity>();
		getEntityMap().entrySet().forEach(entry -> {
			fEntity entity = entry.getKey().clone();
			ModelVector rotateVector = rotateVector(entry.getValue(), direction);
			Location entityLocation = startLocation.clone().add(rotateVector.toVector());
			entityLocation.setYaw(rotateVector.getYaw());
			entityLocation.setPitch(rotateVector.getPitch());
			entity.setLocation(entityLocation);
			entity.setObjectID(id);
			entityList.add(entity);
		});
		return entityList;
	}
	
	public BoundingBox getBoundingBox() {
		return this.boundingbox;
	}
	
	public void setBoundingBox(BoundingBox boundingbox) {
		super.boundingbox = boundingbox;
	}
	
}
