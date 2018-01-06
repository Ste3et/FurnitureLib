package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.ColorType;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ColorUtil {

	private FurnitureLib lib = FurnitureLib.getInstance();
	private LocationUtil lutil = lib.getLocationUtil();
	private FurnitureManager manager = lib.getFurnitureManager();
	
	public void color(Player p, boolean canBuild, Material m, ObjectID obj, ColorType type, int row){
		if(m.equals(Material.BANNER)){type=ColorType.BANNER;}
		switch (type) {
		case BLOCK:
			colorBlock(p, canBuild, m, obj, row);
			break;
		case BANNER:
			colorBanner(p, canBuild, m, obj, row);
			break;
		}
	}
	
	private void colorBlock(Player p, boolean canBuild, Material m, ObjectID obj, int row){
		if(!canBuild){return;}
		ItemStack is = p.getInventory().getItemInMainHand();
		Integer Amount = is.getAmount();
		List<fEntity> asp = manager.getfArmorStandByObjectID(obj);
		short color = lutil.getFromDey(is.getDurability());
		int j = row;
		for(fEntity packet : asp){
			if(packet.getInventory().getHelmet()!=null&&packet.getInventory().getHelmet().getType().equals(m)&&Amount>0){
				short color2 = packet.getInventory().getHelmet().getDurability();
				if(color2 != color){
					packet.getInventory().setHelmet(getNewIS(m, color));
					if(!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()){j--;if(j==0){Amount--;j=row;}}
				}
			}
		}
		removeIS(obj, p, Amount);
	}
	
	private ItemStack getNewIS(Material m, Short durability){return new ItemStack(m, 1, durability);}
	
	private void removeIS(ObjectID obj, Player p, int Amount){
		manager.updateFurniture(obj);
		if(p.getGameMode().equals(GameMode.CREATIVE) && FurnitureLib.getInstance().useGamemode()) return;
		Integer i = p.getInventory().getHeldItemSlot();
		ItemStack item = p.getInventory().getItemInMainHand();
		item.setAmount(Amount);
		p.getInventory().setItem(i, item);
		p.updateInventory();
	}
	
	@SuppressWarnings("deprecation")
	private void colorBanner(Player p, boolean canBuild, Material m, ObjectID obj, int row){
		if(!canBuild){return;}
		ItemStack is = p.getInventory().getItemInMainHand();
		Integer Amount = is.getAmount();
		List<fEntity> asp = manager.getfArmorStandByObjectID(obj);
		DyeColor color = DyeColor.getByColor(lutil.getDyeFromDurability(is.getDurability()));
		int j = row;
		for(fEntity packet : asp){
			if(packet.getInventory().getHelmet()!=null&&packet.getInventory().getHelmet().getType().equals(m)&&Amount>0){
				DyeColor color2 = ((BannerMeta) packet.getInventory().getHelmet().getItemMeta()).getBaseColor();
				if(color2 != color){
					packet.getInventory().setHelmet(getNewIS(packet.getInventory().getHelmet(), color));
					if(!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()){j--;if(j==0){Amount--;j=row;}}
				}
			}
		}
		removeIS(obj, p, Amount);
	}
	@SuppressWarnings("deprecation")
	private ItemStack getNewIS(ItemStack helmet, DyeColor color) {
		//ItemStack is = new ItemStack(helmet.getType(), 1, helmet.getDurability());
		ItemStack is = helmet.clone();
		is.setAmount(1);
		BannerMeta im = (BannerMeta) helmet.getItemMeta();
		im.setBaseColor(color);
		is.setItemMeta(im);
		return is;
	}
}
