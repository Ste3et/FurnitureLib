package de.Ste3et_C0st.FurnitureLib.main.entity;

import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;

public interface SizeableEntity {

	public BoundingBox getBoundingBox();
	public EntitySize getEntitySize();
	
}
