package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fPig extends fEntity{

	private boolean saddle = false;
	private Pig entity;
	
	public fPig(Location loc, ObjectID obj) {
		super(loc, EntityType.PIG, obj);
	}
	
	public fPig setSaddle(boolean b){
		getWatcher().setObject(new WrappedDataWatcherObject(12, Registry.get(Boolean.class)), b);
		this.saddle = b;
		return this;
	}
	
	public boolean hasSaddle(){return this.saddle;}

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

	public NBTTagCompound getMetaData(){
		getDefNBT(this);
		setMetadata("Saddle", this.hasSaddle());
		return getNBTField();
	}
	
	@Override
	public void loadMetadata(NBTTagCompound metadata) {
		loadDefMetadata(metadata);
		boolean s = (metadata.getInt("Saddle")==1);
		this.setSaddle(s);
	}
}
