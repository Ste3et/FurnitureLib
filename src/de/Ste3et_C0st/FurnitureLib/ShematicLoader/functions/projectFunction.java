package de.Ste3et_C0st.FurnitureLib.ShematicLoader.functions;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fInventory.EquipmentSlot;

public abstract class projectFunction{
	
	private String constructor = "";
	
	public abstract boolean parse(JsonObject jsonObject, ObjectID id, Player p);
	
	public projectFunction(String constructor) {
		this.constructor = constructor;
	}
	
	public String getConstructor() {
		return this.constructor;
	}
	
	public void consumeItem(Player p){
		if(p.getGameMode().equals(GameMode.CREATIVE) && FurnitureLib.getInstance().useGamemode()) return;
		ItemStack is = p.getInventory().getItemInMainHand();
		if((is.getAmount()-1)<=0){
			is.setType(Material.AIR);
		}else{
			is.setAmount(is.getAmount()-1);
		}

		p.getInventory().setItem(p.getInventory().getHeldItemSlot(), is);
		p.updateInventory();
	}
	
	public List<fEntity> searchEntityByName(String str, ObjectID obj){
		return obj.getPacketList().stream().filter(entity -> entity.getName().equalsIgnoreCase(str)).collect(Collectors.toList());
	}
	
	public List<fEntity> searchEntityByMaterialName(final String str,EquipmentSlot slot,ObjectID obj){
		return searchEntityByMaterialName(str, slot.getSlot(), obj);
	}
	
	public List<fEntity> searchEntityByMaterial(Material mat, int slot, ObjectID obj){
		return searchEntityByMaterialName(mat.name(), slot, obj);
	}
	
	public List<fEntity> searchEntityByMaterial(Material mat, EquipmentSlot slot, ObjectID obj){
		return searchEntityByMaterialName(mat.name(), slot.getSlot(), obj);
	}
	
	public List<fEntity> searchEntityByMaterialName(final String str,int slot,ObjectID obj){
		final String query = str.replace("*", "").toUpperCase();
		if(str.startsWith("*")) {
			return obj.getPacketList().stream().filter(entity -> entity.getInventory().getSlot(slot) != null).filter(entity -> entity.getInventory().getSlot(slot).getType().name().endsWith(query)).collect(Collectors.toList());
		}else if(str.endsWith("*")) {
			return obj.getPacketList().stream().filter(entity -> entity.getInventory().getSlot(slot) != null).filter(entity -> entity.getInventory().getSlot(slot).getType().name().startsWith(query)).collect(Collectors.toList());
		}else {
			return obj.getPacketList().stream().filter(entity -> entity.getInventory().getSlot(slot) != null).filter(entity -> entity.getInventory().getSlot(slot).getType().name().equalsIgnoreCase(query)).collect(Collectors.toList());
		}
	}
	
	public ItemStack getPlayerItemStack(Player p) {
		return p.getInventory().getItemInMainHand();
	}
	
	public int getSlot(String st) {
		int k = 0;
		try {
			k = Integer.parseInt(st);
		}catch (NumberFormatException e) {
			try {
				k = EquipmentSlot.valueOf(st.toUpperCase()).getSlot();
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return k;
	}
}
