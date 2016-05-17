package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fCreeper extends fEntity{

	private boolean charged = false, ignited = false;
	private int armorstandID;
	public fCreeper setObjID(ObjectID objID) {setObjectID(objID);return this;}
	public fCreeper setArmorID(int i){this.armorstandID = i;return this;}
	public int getArmorID(){return this.armorstandID;}
	public Project getProject(){return this.pro;}
	private Project pro;
	
	public fCreeper(Location loc, ObjectID obj) {
		super(loc, EntityType.CREEPER, obj);
		this.armorstandID = FurnitureLib.getInstance().getFurnitureManager().getLastID();
		this.setObjID(obj);
		this.pro = obj.getProjectOBJ();
	}

	public NBTTagCompound getMetadata() {
		return getMetaData(this);
	}
	public boolean isCharged() {
		return charged;
	}

	public boolean isIgnited() {
		return ignited;
	}
	
	public fCreeper setCharged(boolean charged) {
		setObject(getWatcher(), charged, 12);
		this.charged = charged;
		return this;
	}
	
	public fCreeper setIgnited(boolean ignited) {
		setObject(getWatcher(), ignited, 13);
		this.ignited = ignited;
		return this;
	}
	
	
}
