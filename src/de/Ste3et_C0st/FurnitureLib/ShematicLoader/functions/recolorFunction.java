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
				List<fEntity> entities = new ArrayList<fEntity>();
				if(jsonObject.has("materialParser")) {
					entities = this.searchEntityByMaterialName(jsonObject.get("materialParser").getAsString(), equipmentslot, id);
				}else {
					entities = this.searchEntityByName(jsonObject.get("entityName").getAsString(), id);
				}
				
				if(!entities.isEmpty()) {
					AtomicBoolean bool = new AtomicBoolean(false);
					entities.stream().forEach(entity -> {
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
