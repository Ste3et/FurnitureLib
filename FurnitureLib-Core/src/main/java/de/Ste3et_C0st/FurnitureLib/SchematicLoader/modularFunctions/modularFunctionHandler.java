package de.Ste3et_C0st.FurnitureLib.SchematicLoader.modularFunctions;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureHelper;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class modularFunctionHandler {

	private static HashSet<modularFunction> modularFunctions = new HashSet<modularFunction>();
	
	static {
		modularFunctions.add(new lightFunction());
	}
	
	public static boolean performEntityActions(FurnitureHelper helper, Player player) {
		AtomicBoolean returnValue = new AtomicBoolean(false);
		AtomicInteger lastPriority = new AtomicInteger(0);
		modularFunctions.stream().forEach(entry -> {
			helper.getfAsList().stream().filter(fEntity::hasCustomName).filter(entry::testPredicate).forEach(entity -> {
				if(lastPriority.get() < entry.getPriority()) {
					AtomicBoolean atomicBoolean = entry.run(player, helper.getEntitySet());
					if(atomicBoolean.get()) {
						returnValue.set(true);
						lastPriority.set(entry.getPriority());
					}
				}
			});
		});
		return returnValue.get();
	}
	
	public static HashSet<modularFunction> getEntityActions(FurnitureHelper helper) {
		HashSet<modularFunction> functionSet = new HashSet<modularFunction>();
		modularFunctions.stream().forEach(entry -> {
			Optional<fEntity> function = helper.getfAsList().stream().filter(fEntity::hasCustomName).filter(entry::testPredicate).findFirst();
			if(function.isPresent()) {
				functionSet.add(entry);
			}
		});
		return functionSet;
	}
	
	public static boolean hasEntityActions(FurnitureHelper helper) {
		return getEntityActions(helper).size() > 0;
	}
	
    public static void consumeItem(Player p) {
        if (p.getGameMode().equals(GameMode.CREATIVE) && FurnitureConfig.getFurnitureConfig().useGamemode())
            return;
        ItemStack is = p.getInventory().getItemInMainHand();
        if ((is.getAmount() - 1) <= 0) {
            is.setType(Material.AIR);
        } else {
            is.setAmount(is.getAmount() - 1);
        }

        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), is);
        p.updateInventory();
    }
	
}
