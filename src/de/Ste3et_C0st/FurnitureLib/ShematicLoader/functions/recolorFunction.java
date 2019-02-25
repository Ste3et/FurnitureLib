package de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DyeColor;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class recolorFunction extends projectFunction{

	@Override
	public boolean parse(JsonObject jsonObject, ObjectID id, Player p) {
		DyeColor color = getColor(getPlayerItemStack(p));
		if(color != null) {
			if(jsonObject.has("materialParser") && jsonObject.has("equipmentslot")) {
				String materialParser = jsonObject.get("materialParser").getAsString();
				int equipmentslot = getSlot(jsonObject.get("equipmentslot").getAsString());
				List<fEntity> entitys = this.searchEntityByMaterialName(materialParser, equipmentslot, id);
				if(!entitys.isEmpty()) {
					AtomicBoolean bool = new AtomicBoolean(false);
					entitys.stream().forEach(entity -> {
						ItemStack stack = entity.getInventory().getSlot(equipmentslot);
						DyeColor now = DyeColor.getDyeToReplace(entity.getInventory().getHelmet().getType());
						if(!now.equals(color)){
							bool.set(true);
							entity.getInventory().setHelmet(color.applyToItemStack(stack));
						}
					});
					if(bool.get()) {
						if(jsonObject.has("consume")) if(jsonObject.get("consume").getAsBoolean()) consumeItem(p);
					}
					return bool.get();
				}
			}
		}
		return false;
	}
	
	private boolean isRecolorMaterial(ItemStack stack) {
		if(stack == null) return false;
		return DyeColor.getDyeColor(stack.getType()) != null;
	}
	
	private DyeColor getColor(ItemStack stack) {
		if(stack == null) return null;
		return DyeColor.getDyeColor(stack.getType());
	}

}
