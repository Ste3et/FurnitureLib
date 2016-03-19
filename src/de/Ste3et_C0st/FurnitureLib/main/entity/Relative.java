package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.math.BigDecimal;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class Relative {

	private double offsetX, offsetY, offsetZ;
	private BlockFace face;
	private Location firstLocation, secondLocation;
	
	public Relative(Location loc, double offsetX, double offsetY, double offsetZ, BlockFace face){
		setOffsetX(offsetX);
		setOffsetY(offsetY);
		setOffsetZ(offsetZ);
		setFace(face);
		setFirstLocation(loc);
		setSecondLocation(getRelativ(getFirstLocation(), getFace(), getOffsetX(), getOffsetZ()).add(0, getOffsetY(), 0));
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
	
    private Double round(Double d){
    	BigDecimal b = new BigDecimal(d);
    	b = b.setScale(2,BigDecimal.ROUND_HALF_UP);
    	return b.doubleValue();
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
		x = round(x);
		z = round(z);

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
