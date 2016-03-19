package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class fArmorStand extends fEntity {

	private int armorstandID;
	private ObjectID objID;
	private boolean arms,small,marker=true,gravity,baseplate;
	private HashMap<BodyPart, EulerAngle> angle = new HashMap<Type.BodyPart, EulerAngle>();
	private Project pro;
	
	public EulerAngle getBodyPose(){return getPose(BodyPart.BODY);}
	public EulerAngle getLeftArmPose(){return getPose(BodyPart.LEFT_ARM);}
	public EulerAngle getRightArmPose(){return getPose(BodyPart.RIGHT_ARM);}
	public EulerAngle getLeftLegPose(){return getPose(BodyPart.LEFT_LEG);}
	public EulerAngle getRightLegPose(){return getPose(BodyPart.RIGHT_LEG);}
	public EulerAngle getHeadPose(){return getPose(BodyPart.HEAD);}
	public Project getProject(){return this.pro;}
	public ObjectID getObjID() {return objID;}
	public fArmorStand setBodyPose(EulerAngle a){setPose(a,BodyPart.BODY);return this;}
	public fArmorStand setLeftArmPose(EulerAngle a){setPose(a,BodyPart.LEFT_ARM);return this;}
	public fArmorStand setRightArmPose(EulerAngle a){setPose(a,BodyPart.RIGHT_ARM);return this;}
	public fArmorStand setLeftLegPose(EulerAngle a){setPose(a,BodyPart.LEFT_LEG);return this;}
	public fArmorStand setRightLegPose(EulerAngle a){setPose(a,BodyPart.RIGHT_LEG);return this;}
	public fArmorStand setHeadPose(EulerAngle a){setPose(a,BodyPart.HEAD);return this;}
	public fArmorStand setObjID(ObjectID objID) {this.objID = objID;return this;}
	public fArmorStand setArmorID(int i){this.armorstandID = i;return this;}
	public int getArmorID(){return this.armorstandID;}
	public boolean hasArms(){return this.arms;}
	public boolean hasBasePlate(){return this.baseplate;}
	public boolean hasGravity(){return this.gravity;}
	public boolean isMarker(){return this.marker;}
	public boolean isSmall(){return this.small;}
	
	public fArmorStand(Location loc, ObjectID obj) {
		super(loc, EntityType.ARMOR_STAND);
		getObject(getWatcher(), Byte.valueOf((byte) 0), 10);
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
	    Vector3f v = new Vector3f();
	    setObject(getWatcher(), v.a(angle), part.getField());
	    return this;
	  }
	
	public void delete(){
		remove();
		FurnitureLib.getInstance().getFurnitureManager().remove(this);
	}
	
	public fArmorStand setSmall(boolean b){
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), 10);
		if (b)
			b0 = (byte)(b0 | 0x1);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		setObject(getWatcher(), Byte.valueOf(b0),10);
		this.small = b;
		return this;
	}

	public fArmorStand setArms(boolean b) {
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), 10);
		if (b)
			b0 = (byte)(b0 | 0x4);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFB);
		}
		setObject(getWatcher(), Byte.valueOf(b0),10);
		this.arms = b;
		return this;
	}


	@Deprecated
	public fArmorStand setGravity(boolean b) {
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), 10);
		if (b)
			b0 = (byte)(b0 | 0x2);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFD);
		}
		setObject(getWatcher(), Byte.valueOf(b0),10);
		this.gravity = b;
		return this;
	}
	
	  public fArmorStand setBasePlate(boolean b)
	  {
		b = !b;
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), 10);
	    if (b) {
	      b0 = (byte)(b0 | 0x8);
	    } else {
	      b0 = (byte)(b0 & 0xFFFFFFF7);
	    }
	    setObject(getWatcher(), Byte.valueOf(b0),10);
	    this.baseplate = b;
	    return this;
	  }
	
	public fArmorStand setMarker(boolean b){
		b = !b;
		byte b0 = (byte) getObject(getWatcher(), Byte.valueOf((byte) 0), 10);
		if (b)
			b0 = (byte)(b0 | 0x10);
		else {
			b0 = (byte)(b0 & 0xFFFFFFEF);
		}
		setObject(getWatcher(), Byte.valueOf(b0),10);
		this.marker = !b;
		return this;
	}
	
	public NBTTagCompound getMetadata(){
		return getMetaData(this);
	}
	
	public fArmorStand clone(Relative relative){
		fArmorStand nStand = new fArmorStand(relative.getSecondLocation(), getObjID());
		nStand.setInventory(getInventory());
		nStand.setSmall(isSmall());
		nStand.setInvisible(isVisible());
		nStand.setMarker(isMarker());
		nStand.setGlowing(isGlowing());
		nStand.setArms(hasArms());
		nStand.setBasePlate(hasBasePlate());
		nStand.setFire(isFire());
		nStand.setName(getCustomName());
		nStand.setNameVasibility(isCustomNameVisible());
		for(BodyPart part : BodyPart.values()){
			nStand.setPose(getPose(part), part);
		}
		FurnitureLib.getInstance().getFurnitureManager().addArmorStand(nStand);
		return nStand;
	}
}
