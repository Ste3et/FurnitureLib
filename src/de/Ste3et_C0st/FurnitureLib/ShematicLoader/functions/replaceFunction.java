package de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class replaceFunction extends projectFunction{

	@Override
	public boolean parse(JsonObject jsonObject, ObjectID id, Player p) {
		ItemStack toReplace = getPlayerItemStack(p).clone();
		if(toReplace != null) {
			if((jsonObject.has("materialParser") || jsonObject.has("entityName")) && jsonObject.has("equipmentslot")) {
				AtomicBoolean bool = new AtomicBoolean(false);
				int equipmentslot = getSlot(jsonObject.get("equipmentslot").getAsString());
				List<fEntity> entitys = new ArrayList<fEntity>();
				if(jsonObject.has("materialParser")) {
					entitys = this.searchEntityByMaterialName(jsonObject.get("materialParser").getAsString(), equipmentslot, id);
				}else {
					entitys = this.searchEntityByName(jsonObject.get("entityName").getAsString(), id);
				}
				if(!entitys.isEmpty()) {
					toReplace.setAmount(1);
					entitys.stream().forEach(entity -> {
						ItemStack stack = entity.getInventory().getSlot(equipmentslot);
						if(!stack.equals(toReplace)){
							bool.set(true);
							entity.getInventory().setSlot(equipmentslot, toReplace);
						}
					});
				}
				return bool.get();
			}
		}
		return false;
	}

}
