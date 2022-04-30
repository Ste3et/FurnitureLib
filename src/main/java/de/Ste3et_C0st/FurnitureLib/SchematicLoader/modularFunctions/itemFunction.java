package de.Ste3et_C0st.FurnitureLib.SchematicLoader.modularFunctions;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class itemFunction extends modularFunction{

	private final Predicate<fEntity> entityPredicate = entity -> entity.hasCustomName() && entity.getCustomName().toUpperCase().contains("#ITEM:");
	
	private final BiFunction<Collection<fEntity>, Player, AtomicBoolean> function = (collection, player) -> {
		AtomicBoolean returnValue = new AtomicBoolean(false);
		AtomicBoolean consumeItem = new AtomicBoolean(false);
		ItemStack stack = player.getInventory().getItemInMainHand().clone();
		stack.setAmount(stack.getAmount() <= 0 ? 0 : 1);
		
		collection.stream().forEach(entity -> {
			if (entity.getInventory().getItemInMainHand() != null && entity.getInventory().getItemInMainHand().getType() != Material.AIR) {
				ItemStack is = entity.getInventory().getItemInMainHand();
                is.setAmount(1);
                player.getWorld().dropItem(entity.getObjID().getStartLocation(), is);
                returnValue.set(true);
			}
			if (player.getInventory().getItemInMainHand() != null) {
				entity.setItemInMainHand(stack);
				entity.update();
				returnValue.set(true);
				consumeItem.set(true);
			}
		});
		if(consumeItem.get()) modularFunctionHandler.consumeItem(player);
		return returnValue;
	};
	
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
