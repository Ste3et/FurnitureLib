package de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class replaceFunction extends projectFunction{

	public replaceFunction() {
		super(replaceFunction.class.getSimpleName() + "{entitySelector:<name/materialPaser>,equipmentSlot:Integer.class,consume:Boolean.class}");
	}

	@Override
	public boolean parse(JsonObject jsonObject, ObjectID id, Player p) {
		ItemStack toReplace = getPlayerItemStack(p).clone();
		if(toReplace != null) {
			if((jsonObject.has("materialParser") || jsonObject.has("entityName")) && jsonObject.has("equipmentslot")) {
				AtomicBoolean bool = new AtomicBoolean(false);
				int equipmentslot = getSlot(jsonObject.get("equipmentslot").getAsString());
				List<fEntity> entitys = jsonObject.has("materialParser") ? 
										this.searchEntityByMaterialName(jsonObject.get("materialParser").getAsString(), equipmentslot, id) : 
										this.searchEntityByName(jsonObject.get("entityName").getAsString(), id);
				if(Objects.nonNull(entitys) && !entitys.isEmpty()) {
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
