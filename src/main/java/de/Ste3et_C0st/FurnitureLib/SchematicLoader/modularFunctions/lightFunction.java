package de.Ste3et_C0st.FurnitureLib.SchematicLoader.modularFunctions;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.bukkit.entity.Player;


import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class lightFunction extends modularFunction{

	private final Predicate<fEntity> entityPredicate = entity -> entity.hasCustomName() && entity.getCustomName().toUpperCase().contains("#LIGHT:");
	
	private final BiFunction<Collection<fEntity>, Player, AtomicBoolean> function = (collection, player) -> {
		AtomicBoolean returnValue = new AtomicBoolean(false);
		collection.stream().forEach(entity -> {
			if(entity.getName().contains(":")) {
	    		String[] str = entity.getName().split(":");
	            String lightBool = str[2];
	            if (lightBool.equalsIgnoreCase("off#")) {
	            	entity.setName(entity.getName().replace("off#", "on#"));
	                if (!entity.isFire()) {
	                	entity.setFire(true);
	                	returnValue.set(true);
	                }
	            } else if (lightBool.equalsIgnoreCase("on#")) {
	            	entity.setName(entity.getName().replace("on#", "off#"));
	                if (entity.isFire()) {
	                	entity.setFire(false);
	                	returnValue.set(true);
	                }
	            }
	    	}
		});
		return returnValue;
	};
	
	public lightFunction(){
		this.setPriotity(2);
	}

	@Override
	public AtomicBoolean run(Player player, Collection<fEntity> collection) {
		return function.apply(collection, player);
	}

	@Override
	public void update(Player player) {}

	@Override
	public Predicate<fEntity> getPredicate() {
		return entityPredicate;
	}
	
	

}
