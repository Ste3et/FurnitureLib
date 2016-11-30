package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import com.comphenix.protocol.wrappers.Vector3F;

public class fArmorStand extends fEntity {

	private int armorstandID;
	private boolean arms=false,small=false,marker=true,baseplate=true;
	private HashMap<BodyPart, EulerAngle> angle = new HashMap<Type.BodyPart, EulerAngle>();
	private Project pro;
	private ArmorStand stand = null;
	
	public EulerAngle getBodyPose(){return getPose(BodyPart.BODY);}
	public EulerAngle getLeftArmPose(){return getPose(BodyPart.LEFT_ARM);}
	public EulerAngle getRightArmPose(){return getPose(BodyPart.RIGHT_ARM);}
	public EulerAngle getLeftLegPose(){return getPose(BodyPart.LEFT_LEG);}
	public EulerAngle getRightLegPose(){return getPose(BodyPart.RIGHT_LEG);}
	public EulerAngle getHeadPose(){return getPose(BodyPart.HEAD);}
	public Project getProject(){return this.pro;}
	public fArmorStand setBodyPose(EulerAngle a){setPose(a,BodyPart.BODY);return this;}
	public fArmorStand setLeftArmPose(EulerAngle a){setPose(a,BodyPart.LEFT_ARM);return this;}
	public fArmorStand setRightArmPose(EulerAngle a){setPose(a,BodyPart.RIGHT_ARM);return this;}
	public fArmorStand setLeftLegPose(EulerAngle a){setPose(a,BodyPart.LEFT_LEG);return this;}
	public fArmorStand setRightLegPose(EulerAngle a){setPose(a,BodyPart.RIGHT_LEG);return this;}
	public fArmorStand setHeadPose(EulerAngle a){setPose(a,BodyPart.HEAD);return this;}
	public fArmorStand setObjID(ObjectID objID) {setObjectID(objID);return this;}
	public fArmorStand setArmorID(int i){this.armorstandID = i;return this;}
	public int getArmorID(){return this.armorstandID;}
	public boolean hasArms(){return this.arms;}
	public boolean hasBasePlate(){return this.baseplate;}
	public boolean isMarker(){return this.marker;}
	public boolean isSmall(){return this.small;}
	
	public fArmorStand(Location loc, ObjectID obj) {
		super(loc, EntityType.ARMOR_STAND, obj);
		this.armorstandID = FurnitureLib.getInstance().getFurnitureManager().getLastID();
		this.setObjID(obj);
		this.pro = obj.getProjectOBJ();
	}

	public EulerAngle getPose(BodyPart part){
		if(!angle.containsKey(part)){return part.getDefAngle();}
		return angle.get(part);
	}
	
	  public fArmorStand setPose(EulerAngle angle, BodyPart part)
	  {
	    if (angle == null) {
	      return this;
	    }
	    if (part == null) {
	      return this;
	    }
	    this.angle.put(part, angle);
	    angle = FurnitureLib.getInstance().getLocationUtil().Radtodegress(angle);
	    setObject(getWatcher(), new Vector3F((float)angle.getX(),(float)angle.getY(),(float)angle.getZ()), getField().getFieldFromPose(part));
	    return this;
	  }
	
	public fArmorStand setSmall(boolean b){
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), getField().getBitMask());
		if (b)
			b0 = (byte)(b0 | 0x1);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		setObject(getWatcher(), Byte.valueOf(b0),getField().getBitMask());
		this.small = b;
		return this;
	}

	public fArmorStand setArms(boolean b) {
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), getField().getBitMask());
		if (b)
			b0 = (byte)(b0 | 0x4);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFB);
		}
		setObject(getWatcher(), Byte.valueOf(b0),getField().getBitMask());
		this.arms = b;
		return this;
	}
	
	  public fArmorStand setBasePlate(boolean b)
	  {
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), getField().getBitMask());
	    if (!b) {
	      b0 = (byte)(b0 | 0x8);
	    } else {
	      b0 = (byte)(b0 & 0xFFFFFFF7);
	    }
	    setObject(getWatcher(), Byte.valueOf(b0),getField().getBitMask());
	    this.baseplate = b;
	    return this;
	  }
	
	public fArmorStand setMarker(boolean b){
		b = !b;
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), getField().getBitMask());
		if (b)
			b0 = (byte)(b0 | 0x10);
		else {
			b0 = (byte)(b0 & 0xFFFFFFEF);
		}
		setObject(getWatcher(), Byte.valueOf(b0),getField().getBitMask());
		this.marker = !b;
		return this;
	}
	
	public NBTTagCompound getMetadata(){
		return getMetaData(this);
	}
	
	public fArmorStand clone(Relative relative){
		fArmorStand nStand = new fArmorStand(relative.getSecondLocation(), getObjID());
		fInventory inv = new fInventory(nStand.getEntityID());
		for(int i = 0; i<7;i++){
			if(getInventory().getSlot(i)==null) continue;
			inv.setSlot(i, getInventory().getSlot(i));
		}
		nStand.setInventory(inv);
		nStand.setSmall(this.isSmall());
		nStand.setInvisible(this.isInvisible());
		nStand.setMarker(this.isMarker());
		nStand.setGlowing(this.isGlowing());
		nStand.setArms(this.hasArms());
		nStand.setBasePlate(this.hasBasePlate());
		nStand.setFire(this.isFire());
		nStand.setName(this.getCustomName());
		nStand.setNameVasibility(this.isCustomNameVisible());
		for(BodyPart part : BodyPart.values()){
			nStand.setPose(this.getPose(part), part);
		}
		FurnitureLib.getInstance().getFurnitureManager().addArmorStand(nStand);
		return nStand;
	}
	
	public ArmorStand toRealArmorStand(){
		if(stand!=null){if(!stand.isDead()){return stand;}}
		stand = (ArmorStand) getWorld().spawnEntity(getLocation(), getEntityType());
		stand.setArms(this.hasArms());
		stand.setVisible(this.isInvisible());
		stand.setSmall(isSmall());
		stand.setArms(hasArms());
		stand.setBasePlate(hasBasePlate());
		stand.setGravity(hasGravity());
		stand.setGlowing(isGlowing());
		stand.setAI(false);
		stand.setHeadPose(getHeadPose());
		stand.setLeftArmPose(getLeftArmPose());
		stand.setRightArmPose(getRightArmPose());
		stand.setLeftLegPose(getLeftLegPose());
		stand.setRightLegPose(getRightLegPose());
		stand.setBodyPose(getBodyPose());
		stand.setHelmet(getHelmet());
		stand.setChestplate(getChestPlate());
		stand.setCustomName(getCustomName());
		stand.setCustomNameVisible(isCustomNameVisible());
		stand.setLeggings(getLeggings());
		stand.setBoots(getBoots());
		return stand;
	}
	
	public boolean isRealArmorStand(){
		if(stand==null) return false;
		return true;
	}
	
	public void setStand(ArmorStand stand){
		this.stand = null;
	}
}
