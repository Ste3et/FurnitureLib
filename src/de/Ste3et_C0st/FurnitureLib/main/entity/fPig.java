package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fPig extends fEntity{

	private boolean saddle = false;
	private int armorstandID;
	public fPig setObjID(ObjectID objID) {setObjectID(objID);return this;}
	public fPig setArmorID(int i){this.armorstandID = i;return this;}
	public int getArmorID(){return this.armorstandID;}
	public Project getProject(){return this.pro;}
	private Project pro;
	private Pig entity;
	
	public fPig(Location loc, ObjectID obj) {
		super(loc, EntityType.PIG, obj);
		this.armorstandID = FurnitureLib.getInstance().getFurnitureManager().getLastID();
		this.setObjID(obj);
		this.pro = obj.getProjectOBJ();
	}

	@Override
	public NBTTagCompound getMetadata() {
		return getMetaData(this);
	}
	
	public fPig setSaddle(boolean b){
		setObject(getWatcher(), b, 12);
		this.saddle = b;
		return this;
	}
	
	public boolean haseSaddle(){return this.saddle;}

	public Pig toRealEntity() {
		if(entity!=null){if(!entity.isDead()){return entity;}}
		entity = (Pig) getWorld().spawnEntity(getLocation(), getEntityType());
		entity.setSaddle(saddle);
		return entity;
	}
	
	public boolean isRealEntity(){
		if(entity==null) return false;
		return true;
	}
	
	public void setEntity(Entity entity){
		if(entity instanceof Pig) this.entity = (Pig) entity;
	}
	
}
