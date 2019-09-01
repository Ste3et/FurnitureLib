package de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DyeColor;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class recolorFunction extends projectFunction{
	
	public recolorFunction() {
		super("{}");
	}

	@Override
	public boolean parse(JsonObject jsonObject, ObjectID id, Player p) {
		DyeColor color = getColor(getPlayerItemStack(p));
		if(color != null) {
			if((jsonObject.has("materialParser") || jsonObject.has("entityName")) && jsonObject.has("equipmentslot")) {
				int equipmentslot = getSlot(jsonObject.get("equipmentslot").getAsString());
				List<fEntity> entitys = new ArrayList<fEntity>();
				if(jsonObject.has("materialParser")) {
					entitys = this.searchEntityByMaterialName(jsonObject.get("materialParser").getAsString(), equipmentslot, id);
				}else {
					entitys = this.searchEntityByName(jsonObject.get("entityName").getAsString(), id);
				}
				
				if(!entitys.isEmpty()) {
					AtomicBoolean bool = new AtomicBoolean(false);
					entitys.stream().forEach(entity -> {
						ItemStack stack = entity.getInventory().getSlot(equipmentslot);
						DyeColor now = DyeColor.getDyeToReplace(entity.getInventory().getHelmet().getType());
						if(!now.equals(color)){
							bool.set(true);
							entity.getInventory().setSlot(equipmentslot, color.applyToItemStack(stack));
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
	
	private DyeColor getColor(ItemStack stack) {
		if(stack == null) return null;
		return DyeColor.getDyeColor(stack.getType());
	}

}
