package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
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
		getEntityMap().entrySet().forEach(entry -> {
			fEntity entity = entry.getKey().clone();
			ModelVector rotateVector = rotateVector(entry.getValue(), direction);
			Location entityLocation = startLocation.clone().add(rotateVector.toVector());
			entityLocation.setYaw(rotateVector.getYaw());
			entityLocation.setPitch(rotateVector.getPitch());
			entity.setLocation(entityLocation);
			entity.setObjectID(id);
			id.addEntity(entity);
		});
		id.sendAll();
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
	
	public BoundingBox getBoundingBox() {
		return this.boundingbox;
	}
	
	public void setBoundingBox(BoundingBox boundingbox) {
		super.boundingbox = boundingbox;
	}
	
}
