package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.util.Vector;
import org.joml.Vector3f;

import de.Ste3et_C0st.FurnitureLib.main.entity.fBlock_display;
import de.Ste3et_C0st.FurnitureLib.main.entity.fDisplay;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class EntitySize {

	private double x, y, z;

	public EntitySize(double width, double height) {
		this(width, height, width);
	}
	
	public EntitySize(double x, double y, double z) {
		this.x = x;
		this.z = y;
		this.y = z;
	}
	
	public EntitySize(Vector3f vector3f) {
		this(vector3f.x, vector3f.y, vector3f.z);
	}
	
	public static EntitySize of(double x, double y, double z) {
		return new EntitySize(x, y, z);
	}
	
	public static EntitySize of(Vector3f vector3f) {
		return new EntitySize(vector3f.x, vector3f.y, vector3f.z);
	}

	public double getWidth() {
		return this.x;
	}

	public double getHeight() {
		return this.y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public Vector3f toVector3f() {
		return new Vector3f((float) this.x,(float) this.y,(float) this.z);
	}
	
	public Vector toVector() {
		return new Vector((float) this.x,(float) this.y,(float) this.z);
	}
	
    public BoundingBox getBoundingBox(Vector vector) {
		return BoundingBox.of(vector, toVector());
	}
	
	public BoundingBox toBoundingBox() {
		return BoundingBox.of(new Vector(), new Vector(x, y, z));
	}

	public void write(Vector3f scale) {
		this.write(scale.x, scale.y, scale.z);
	}
	
	public void write(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return "EntitySize {" + this.x +", " + this.y +", " + this.z +"}";
	}
}
