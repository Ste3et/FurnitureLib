package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
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
	public void setBodyPose(EulerAngle a){setPose(a,BodyPart.BODY);}
	public void setLeftArmPose(EulerAngle a){setPose(a,BodyPart.LEFT_ARM);}
	public void setRightArmPose(EulerAngle a){setPose(a,BodyPart.RIGHT_ARM);}
	public void setLeftLegPose(EulerAngle a){setPose(a,BodyPart.LEFT_LEG);}
	public void setRightLegPose(EulerAngle a){setPose(a,BodyPart.RIGHT_LEG);}
	public void setHeadPose(EulerAngle a){setPose(a,BodyPart.HEAD);}
	public void setObjID(ObjectID objID) {this.objID = objID;}
	public void setArmorID(int i){this.armorstandID = i;}
	public int getArmorID(){return this.armorstandID;}
	public boolean hasArms(){return this.arms;}
	public boolean hasBasePlate(){return this.baseplate;}
	public boolean hasGravity(){return this.gravity;}
	public boolean isMarker(){return this.marker;}
	public boolean isSmall(){return this.small;}
	
	public fArmorStand(Location location, ObjectID obj) {
		super(location, EntityType.ARMOR_STAND);
		this.armorstandID = FurnitureLib.getInstance().getFurnitureManager().getLastID();
		this.setObjID(obj);
		this.pro = obj.getProjectOBJ();
		
	}

	public EulerAngle getPose(BodyPart part){
		if(!angle.containsKey(part)){return part.getDefAngle();}
		return angle.get(part);
	}
	
	public void setPose(EulerAngle angle, BodyPart part){
		if(angle==null){return;}
		if(part==null){return;}
		this.angle.put(part, angle);
		angle = FurnitureLib.getInstance().getLocationUtil().Radtodegress(angle);
		Vector3f v = new Vector3f();
		getHandle().setObject(part.getField(), v.a(angle));
	}
	
	public void delete(){
		remove();
		FurnitureLib.getInstance().getFurnitureManager().remove(this);
	}
	
	public void setSmall(boolean b){
		byte b0 = getHandle().getByte(10);

		if (b)
			b0 = (byte)(b0 | 0x1);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFE);
		}
		getHandle().setObject(10, Byte.valueOf(b0));
		this.small = b;
	}

	public void setArms(boolean b) {
		byte b0 = getHandle().getByte(10);
		if (b)
			b0 = (byte)(b0 | 0x4);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFB);
		}
		getHandle().setObject(10, Byte.valueOf(b0));
		this.arms = b;
	}

	public void setGravity(boolean b) {
		byte b0 = getHandle().getByte(10);
		if (b)
			b0 = (byte)(b0 | 0x2);
		else {
			b0 = (byte)(b0 & 0xFFFFFFFD);
		}
		getHandle().setObject(10, Byte.valueOf(b0));
		this.gravity = b;
	}
	
	  public void setBasePlate(boolean b)
	  {
		b = !b;
	    byte b0 = getHandle().getByte(10);
	    if (b) {
	      b0 = (byte)(b0 | 0x8);
	    } else {
	      b0 = (byte)(b0 & 0xFFFFFFF7);
	    }
	    getHandle().setObject(10, Byte.valueOf(b0));
	    this.baseplate = b;
	  }
	
	public void setMarker(boolean b){
		b = !b;
		byte b0 = getHandle().getByte(10);

		if (b)
			b0 = (byte)(b0 | 0x10);
		else {
			b0 = (byte)(b0 & 0xFFFFFFEF);
		}
		getHandle().setObject(10, Byte.valueOf(b0));
		this.marker = !b;
	}
}
