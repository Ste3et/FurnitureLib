package de.Ste3et_C0st.FurnitureLib.SchematicLoader.modularFunctions;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class modularFunction {

	public abstract AtomicBoolean run(Player player, Collection<fEntity> collection);
	public abstract void update(Player player);
	public abstract Predicate<fEntity> getPredicate();
	
	private int priority = 1;
	private boolean functionIspublic = false;
	
	public boolean testPredicate(fEntity entity) {
		return getPredicate().test(entity);
	};
	
	public int getPriority() {
		return this.priority;
	}
	
	protected void setPriotity(int priority) {
		this.priority = priority;
	}
	
	public boolean isPublic() {
		return this.functionIspublic;
	}
	
	protected void setPublic(boolean runPublic) {
		this.functionIspublic = runPublic;
	}
	
}
