package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.util.Vector;

public class EntitySize {

	private final double width, height;
	
	public EntitySize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	public BoundingBox getBoundingBox(Vector vector) {
		return BoundingBox.of(vector, height, width / 2, height);
	}
}
