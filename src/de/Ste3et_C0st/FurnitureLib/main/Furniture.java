package de.Ste3et_C0st.FurnitureLib.main;

import java.util.List;

import org.bukkit.Bukkit;
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
			return runOldFunctions(p);
		}
		return false;
	}
	
	public boolean passangerFunction(Player p) {
		if(!p.isSneaking()) {
			for(fEntity stand : getfAsList()){
				if(stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#SITZ")){
					if(stand.getPassanger().isEmpty()){
						stand.setPassanger(p);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean runOldFunctions(Player p) {
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
						if(stand.getName().startsWith("/")){
							if(!stand.getName().startsWith("/op")){
								String str = stand.getName();
								str = str.replaceAll("@player", p.getName());
								str = str.replaceAll("@uuid", p.getUniqueId().toString());
								str = str.replaceAll("@world", p.getWorld().getName());
								str = str.replaceAll("@x", p.getLocation().getX() + "");
								str = str.replaceAll("@y", p.getLocation().getY() + "");
								str = str.replaceAll("@z", p.getLocation().getZ() + "");
								if(str.endsWith("!console!")) {
									str = str.replaceAll("!console!", "");
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str);
								}else {
									p.chat(str);
								}
							}
						}
					}
			}
		}
		return false;
	}
}
