package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.util.Vector;

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
	
	public EntitySize(Vector vector3f) {
		this(vector3f.getX(), vector3f.getY(), vector3f.getZ());
	}
	
	public static EntitySize of(double x, double y, double z) {
		return new EntitySize(x, y, z);
	}
	
	public static EntitySize of(Vector vector3f) {
		return new EntitySize(vector3f.getX(), vector3f.getY(), vector3f.getZ());
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
	
	public Vector toVector() {
		return new Vector((float) this.x,(float) this.y,(float) this.z);
	}
	
    public BoundingBox getBoundingBox(Vector vector) {
		return BoundingBox.of(vector, toVector());
	}
	
	public BoundingBox toBoundingBox() {
		return BoundingBox.of(new Vector(), new Vector(x, y, z));
	}

	public void write(Vector scale) {
		this.write(scale.getX(), scale.getY(), scale.getZ());
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