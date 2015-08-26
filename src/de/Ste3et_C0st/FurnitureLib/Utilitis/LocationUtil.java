package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class LocationUtil {

	public final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	public List<BlockFace> axisList = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    public final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    //Return the all BlockFaces with 45 degress
    public BlockFace yawToFaceRadial(float yaw) {
            return radial[Math.round(yaw / 45f) & 0x7];
    }
    
    //Return the all BlockFaces with 90 degress
    public BlockFace yawToFace(float yaw) {
            return axis[Math.round(yaw / 90f) & 0x3];
    }
    
    public BlockFace yawToFace(float yaw, float pitch) {
        if(pitch<-80){
        	return BlockFace.UP;
        }else if(pitch>80){
        	return BlockFace.DOWN;
        }
    	return axis[Math.round(yaw / 90f) & 0x3];
    }
    
    public EulerAngle degresstoRad(EulerAngle degressAngle){
    	return new EulerAngle(degressAngle.getX() * Math.PI / 180, degressAngle.getY() * Math.PI / 180, degressAngle.getZ() * Math.PI / 180);
    }
    
    public EulerAngle Radtodegress(EulerAngle degressAngle){
    	return new EulerAngle(degressAngle.getX() * 180 / Math.PI, degressAngle.getY() * 180 / Math.PI, degressAngle.getZ() * 180 / Math.PI);
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
    
	public short getFromDey(short s){
		return (short) (15-s);
	}
	
	public Color getDyeFromDurability(short s){
		if(s==0){return Color.fromRGB(25, 25, 25);}
		if(s==1){return Color.fromRGB(153, 51, 51);}
		if(s==2){return Color.fromRGB(102, 127, 51);}
		if(s==3){return Color.fromRGB(102, 76, 51);}
		if(s==4){return Color.fromRGB(51, 76, 178);}
		if(s==5){return Color.fromRGB(127, 63, 178);}
		if(s==6){return Color.fromRGB(76, 127, 153);}
		if(s==7){return Color.fromRGB(153, 153, 153);}
		if(s==8){return Color.fromRGB(76, 76, 76);}
		if(s==9){return Color.fromRGB(242, 127, 165);}
		if(s==10){return Color.fromRGB(127, 204, 25);}
		if(s==11){return Color.fromRGB(229, 229, 51);}
		if(s==12){return Color.fromRGB(102, 153, 216);}
		if(s==13){return Color.fromRGB(178, 76, 216);}
		if(s==14){return Color.fromRGB(216, 127, 51);}
		if(s==15){return Color.fromRGB(255, 255, 255);}
		return Color.fromRGB(255, 255, 255);
	}
	
	
    
    public boolean isDay(World w) {
        long time = w.getTime();
     
        if(time > 0 && time < 12300) {
            return true;
        } else {
            return false;
        }
    }
    
    public Vector getRelativ(Vector v1, Double x, BlockFace bf){
    	switch(bf){
    	case NORTH: v1.add(new Vector(0, 0,x)); break;
    	case EAST: v1.add(new Vector(x, 0, 0)); break;
    	case SOUTH: v1.add(new Vector(0, 0,-x)); break;
    	case WEST: v1.add(new Vector(x, 0, 0)); break;
    	case DOWN:v1.add(new Vector(0, -x, 0));break;
    	case UP:v1.add(new Vector(0, x, 0));break;
		default: v1.add(new Vector(x, 0, 0)); break;
    	}
    	return v1;
    }
    
    public BlockFace StringToFace(final String face) {
        switch (face) {
            case "NORTH": return BlockFace.NORTH;
            case "EAST": return BlockFace.EAST;
            case "SOUTH": return BlockFace.SOUTH;
            case "WEST": return BlockFace.WEST;
            case "UP": return BlockFace.UP;
            case "DOWN": return BlockFace.DOWN;
            case "NORTH_NORTH_EAST": return BlockFace.NORTH_NORTH_EAST;
            case "NORTH_NORTH_WEST": return BlockFace.NORTH_NORTH_WEST;
            case "NORTH_WEST": return BlockFace.NORTH_WEST;
            case "EAST_NORTH_EAST": return BlockFace.EAST_NORTH_EAST;
            case "EAST_SOUTH_EAST": return BlockFace.EAST_SOUTH_EAST;
            case "SOUTH_EAST": return BlockFace.SOUTH_EAST;
            case "SOUTH_SOUTH_EAST": return BlockFace.SOUTH_SOUTH_EAST;
            case "SOUTH_SOUTH_WEST": return BlockFace.SOUTH_SOUTH_WEST;
            case "SOUTH_WEST": return BlockFace.SOUTH_WEST;
            case "WEST_NORTH_WEST": return BlockFace.WEST_NORTH_WEST;
            case "WEST_SOUTH_WEST": return BlockFace.WEST_SOUTH_WEST;
            default: return BlockFace.NORTH;
        }
    }
    
    @SuppressWarnings("deprecation")
	public Block setSign(BlockFace face, Location l) {
			l.getBlock().setType(Material.AIR);
			l.getBlock().setType(Material.WALL_SIGN);
			Block block = l.getBlock();
			BlockState state = l.getBlock().getState();
			state.setRawData((byte) getFacebyte(yawToFace(FaceToYaw(face.getOppositeFace()) - 90)));
    		state.update(false);
			return block;
    }
    
    public byte getFacebyte(BlockFace b){
    	switch (b) {
		case NORTH:return 0x4;
		case EAST:return 0x2;
		case SOUTH:return 0x5;
		case WEST:return 0x3;
		default:return 0x5;
		}
    }
    
    @SuppressWarnings("deprecation")
    public Location setBed(BlockFace face, Location l) {
		Block block = l.getBlock();
        BlockState bedFoot = block.getState();
        BlockState bedHead = bedFoot.getBlock().getRelative(face.getOppositeFace()).getState();
    	
    	switch (face) {
		case NORTH:
			l.getBlock().setType(Material.AIR);
    		l.getBlock().setType(Material.BED_BLOCK);
            bedFoot.setType(Material.BED_BLOCK);
            bedHead.setType(Material.BED_BLOCK);
            bedFoot.setRawData((byte) 0);
            bedHead.setRawData((byte) 8);
            bedFoot.update(true, false);
            bedHead.update(true, true);
			return bedHead.getLocation();
		case EAST:
			l.getBlock().setType(Material.AIR);
    		l.getBlock().setType(Material.BED_BLOCK);
            bedFoot.setType(Material.BED_BLOCK);
            bedHead.setType(Material.BED_BLOCK);
            bedFoot.setRawData((byte) 1);
            bedHead.setRawData((byte) 9);
            bedFoot.update(true, false);
            bedHead.update(true, true);
            return bedHead.getLocation();
		case SOUTH:
    		l.getBlock().setType(Material.AIR);
    		l.getBlock().setType(Material.BED_BLOCK);
            bedFoot.setType(Material.BED_BLOCK);
            bedHead.setType(Material.BED_BLOCK);
            bedFoot.setRawData((byte) 2);
            bedHead.setRawData((byte) 10);
            bedFoot.update(true, false);
            bedHead.update(true, true);
            return bedHead.getLocation();
		case WEST:
    		l.getBlock().setType(Material.AIR);
    		l.getBlock().setType(Material.BED_BLOCK);
            bedFoot.setType(Material.BED_BLOCK);
            bedHead.setType(Material.BED_BLOCK);
            bedFoot.setRawData((byte) 3);
            bedHead.setRawData((byte) 11);
            bedFoot.update(true, false);
            bedHead.update(true, true);
            return bedHead.getLocation();
		default: return null;
		}
    }
    
    @SuppressWarnings("deprecation")
    public Block setHalfBed(BlockFace face, Location l) {
    	if(face == BlockFace.NORTH){
    		Block block = l.getBlock();
            BlockState bedHead = block.getState();
            bedHead.setType(Material.BED_BLOCK);
            bedHead.setRawData((byte) 9);
            bedHead.update(true, false);
            return block;
    	}else if(face == BlockFace.EAST){
    		Block block = l.getBlock();
    		BlockState bedHead = block.getState();
            bedHead.setType(Material.BED_BLOCK);
            bedHead.setRawData((byte) 10);
            bedHead.update(true, false);
            return block;
    	}else if(face == BlockFace.SOUTH){
    		Block block = l.getBlock();
    		BlockState bedHead = block.getState();
            bedHead.setType(Material.BED_BLOCK);
            bedHead.setRawData((byte) 11);
            bedHead.update(true, false);
            return block;
    	}else if(face == BlockFace.WEST){
    		Block block = l.getBlock();
    		BlockState bedHead = block.getState();
            bedHead.setType(Material.BED_BLOCK);
            bedHead.setRawData((byte) 8);
            bedHead.update(true, false);
            return block;
    	}
		return null;
    }
    
    private Double round(Double d){
    	BigDecimal b = new BigDecimal(d);
    	b = b.setScale(2,BigDecimal.ROUND_HALF_UP);
    	return b.doubleValue();
    }
    
	public Location getRelativ(Location loc, BlockFace b, Double z, Double x){
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
	
    public int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public Location getCenter(Location loc) {
        return new Location(loc.getWorld(),
            getRelativeCoord(loc.getBlockX()),
            getRelativeCoord(loc.getBlockY()),
            getRelativeCoord(loc.getBlockZ()));
    }
    
    private static double getRelativeCoord(int i) {
        double d = i;
        if(d<0){d+=.5;}else{d+=.5;}
        return d;
    }
    
    public short getfromDyeColor(DyeColor c){
    	switch (c) {
		case BLACK: return 0;
		case BLUE: return 4;
		case BROWN: return 3;
		case CYAN: return 6;
		case GRAY: return 8;
		case SILVER: return 7;
		case WHITE: return 15;
		case GREEN: return 2;
		case LIGHT_BLUE: return 12;
		case LIME: return 10;
		case MAGENTA: return 13;
		case ORANGE: return 14;
		case PINK: return 9;
		case PURPLE: return 5;
		case RED: return 1;
		case YELLOW: return 11;
		default: return 15;
		}
    }
    
}
