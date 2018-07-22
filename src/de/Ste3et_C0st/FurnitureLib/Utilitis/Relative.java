package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Relative {

	private double offsetX, offsetY, offsetZ;
	private BlockFace face;
	private Location firstLocation, secondLocation;
	private final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	private final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    
	public Relative(Location loc, double offsetX, double offsetY, double offsetZ, BlockFace face){
		setOffsetX(offsetX);
		setOffsetY(offsetY);
		setOffsetZ(offsetZ);
		setFace(face);
		setFirstLocation(loc);
		setSecondLocation(getRelativ(getFirstLocation(), getFace(), getOffsetX(), getOffsetZ()).add(0, getOffsetY(), 0));
	}
	
	public Relative(Location loc, Location loc2){
		Vector v1 = loc.toVector();
		Vector v2 = loc2.toVector();
		setOffsetX(v1.getX()-v2.getX());
		setOffsetY(v1.getY()-v2.getY());
		setOffsetZ(v1.getZ()-v2.getZ());
		setFirstLocation(loc);
		setFace(yawToFace(loc.getYaw()));
		setSecondLocation(loc2);
	}

	public double getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(double offsetX) {
		this.offsetX = offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}

	public double getOffsetZ() {
		return offsetZ;
	}

	public void setOffsetZ(double offsetZ) {
		this.offsetZ = offsetZ;
	}

	public BlockFace getFace() {
		return face;
	}

	public void setFace(BlockFace face) {
		this.face = face;
	}

	public Location getFirstLocation() {
		return firstLocation;
	}

	public void setFirstLocation(Location firstLocation) {
		this.firstLocation = firstLocation;
	}

	public Location getSecondLocation() {
		return secondLocation;
	}

	private void setSecondLocation(Location secondLocation) {
		this.secondLocation = secondLocation;
	}
    
    public BlockFace yawToFaceRadial(float yaw) { return radial[Math.round(yaw / 45f) & 0x7];}
    public BlockFace yawToFace(float yaw) {return axis[Math.round(yaw / 90f) & 0x3];}
	
    public BlockFace yawToFace(float yaw, float pitch) {
        if(pitch<-80){
        	return BlockFace.UP;
        }else if(pitch>80){
        	return BlockFace.DOWN;
        }
    	return axis[Math.round(yaw / 90f) & 0x3];
    }
	
    public int FaceToYaw(final BlockFace face) {
        switch (face) {
            case NORTH: return 0;
            case NORTH_EAST: return 45;
            case EAST: return 90;
            case SOUTH_EAST: return 135;
            case SOUTH: return 180;
            case SOUTH_WEST: return 225;
            case WEST: return 270;
            case NORTH_WEST: return 315;
            default: return 0;
        }
    }
	
	private Location getRelativ(Location loc, BlockFace b, double z, double x){
		Location l = loc.clone();
		l.setYaw(FaceToYaw(b));
		switch (b) {
		case NORTH:
			l.add(x,0,z);
			break;
		case SOUTH:
			l.add(-x,0,-z);
			break;
		case WEST:
			l.add(z,0,-x);
			break;
		case EAST:
			l.add(-z,0,x);
			break;
		case NORTH_EAST:
			l.add(x,0,z);
			l.add(-z,0,x);
			break;
		case NORTH_NORTH_EAST:
			l.add(x,0,z);
			l.add(x,0,z);
			l.add(-z,0,x);
			break;
		case NORTH_NORTH_WEST:
			l.add(x,0,z);
			l.add(x,0,z);
			l.add(z,0,-x);
			break;
		case NORTH_WEST:
			l.add(x,0,z);
			l.add(z,0,-x);
			break;
		case EAST_NORTH_EAST:
			l.add(-z,0,x);
			l.add(x,0,z);
			l.add(-z,0,x);
			break;
		case EAST_SOUTH_EAST:
			l.add(-z,0,x);
			l.add(-x,0,-z);
			l.add(-z,0,x);
			break;
		case SOUTH_EAST:
			l.add(-x,0,-z);
			l.add(-z,0,x);
			break;
		case SOUTH_SOUTH_EAST:
			l.add(-x,0,-z);
			l.add(-x,0,-z);
			l.add(-z,0,x);
			break;
		case SOUTH_SOUTH_WEST:
			l.add(-x,0,-z);
			l.add(-x,0,-z);
			l.add(z,0,-x);
			break;
		case SOUTH_WEST:
			l.add(-x,0,-z);
			l.add(z,0,-x);
			break;
		case WEST_NORTH_WEST:
			l.add(z,0,-x);
			l.add(x,0,z);
			l.add(z,0,-x);
			break;
		case WEST_SOUTH_WEST:
			l.add(z,0,-x);
			l.add(-x,0,-z);
			l.add(z,0,-x);
			break;
		case DOWN:
			l.add(0,-z,0);
			break;
		case UP:
			l.add(0,z,0);
		default:
			l.add(x,0,z);
			break;
		}
		return l;
	}
	
}
