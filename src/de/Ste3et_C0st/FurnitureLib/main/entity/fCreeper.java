package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
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
	private Creeper entity = null;
	
	public fCreeper(Location loc, ObjectID obj) {
		super(loc, EntityType.CREEPER, obj);
		this.armorstandID = FurnitureLib.getInstance().getFurnitureManager().getLastID();
		this.setObjID(obj);
		this.pro = obj.getProjectOBJ();
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
	
	public Creeper toRealEntity() {
		if(entity!=null){if(!entity.isDead()){return entity;}}
		entity = (Creeper) getWorld().spawnEntity(getLocation(), getEntityType());
		entity.setPowered(charged);
		
		return entity;
	}
	
	public boolean isRealEntity(){
		if(entity==null) return false;
		return true;
	}
	
	public void setEntity(Entity entity){
		if(entity instanceof Creeper) this.entity = (Creeper) entity;
	}
	
	public NBTTagCompound getMetadata() {
		return getMetaData(this);
	}
}
