package de.Ste3et_C0st.FurnitureLib.main;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions.FunctionTypes.FunctionType;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public abstract class Furniture extends FurnitureHelper implements Listener{
	
	public Furniture(ObjectID id){super(id);}
	
	public fArmorStand spawnArmorStand(Location loc){return getManager().createArmorStand(getObjID(), loc);}
	
	public abstract void spawn(Location location);
	
	public boolean runFunction(Player p) {
		List<JsonObject> functions = getObjID().getProjectOBJ().getFunctions();
		if(!functions.isEmpty()) {
			boolean update = false;
			for(JsonObject function : functions) {
				if(function.has("function") && function.has("data")) {
					FunctionType type = FunctionType.valueOf(function.get("function").getAsString().toUpperCase());
					boolean b = type.parse(function.getAsJsonObject("data"), getObjID(), p);
					if(b) update = true;
				}
			}
			if(update) {
				return update;
			}
		}else {
			if(runOldFunctions(p)) {
				return true;
			}
		}
		
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#SITZ")){
				if(stand.getPassanger().isEmpty()){
					stand.setPassanger(p);
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean runOldFunctions(Player p) {
		if(this.canBuild(p)) {
			ItemStack stack = p.getInventory().getItemInMainHand();
			if(stack != null) {
				Material m = stack.getType();
				if(m.equals(Material.AIR) || !m.isBlock()){
					for(fEntity stand : getfAsList()){
						if(stand.getName().startsWith("#ITEM")){
							if(stand.getInventory().getItemInMainHand()!=null&&!stand.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
								ItemStack is = stand.getInventory().getItemInMainHand();
								is.setAmount(1);
								getWorld().dropItem(getLocation(), is);
							}
							if(p.getInventory().getItemInMainHand()!=null){
								ItemStack is = p.getInventory().getItemInMainHand().clone();
								if(is.getAmount()<=0){
									is.setAmount(0);
								}else{
									is.setAmount(1);
								}
								stand.setItemInMainHand(is);
								stand.update();
								consumeItem(p);	
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
