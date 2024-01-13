package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class EntitySize {

	private final double x, y, z;
	
	
	public EntitySize(double width, double height) {
		this.x = width;
		this.z = width;
		this.y = height;
	}
	
	public EntitySize(Vector3f vector3f) {
		this.x = vector3f.x;
		this.y = vector3f.y;
		this.z = vector3f.z;
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
}
