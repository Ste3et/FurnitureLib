package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.ColorType;
import de.Ste3et_C0st.FurnitureLib.main.Type.DyeColor;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class ColorUtil {

	private FurnitureLib lib = FurnitureLib.getInstance();
	private FurnitureManager manager = lib.getFurnitureManager();
	
	public void color(Player p, boolean canBuild, String identifier, ObjectID obj, ColorType type, int row){
		if(identifier.contains("_BANNER")){type=ColorType.BANNER;}
		switch (type) {
		case BLOCK:
			colorBlock(p, canBuild, identifier, obj, row);
			break;
		case BANNER:
			colorBlock(p, canBuild, identifier, obj, row);
			break;
		}
	}
	
	private void colorBlock(Player p, boolean canBuild, String identifier, ObjectID obj, int row){
		if(!canBuild){return;}
		ItemStack is = p.getInventory().getItemInMainHand();
		Integer Amount = is.getAmount();
		List<fEntity> asp = manager.getfArmorStandByObjectID(obj);
		DyeColor start = DyeColor.getDyeColor(is.getType());
		int j = row;
		for(fEntity packet : asp){
			if(packet.getInventory().getHelmet()!=null&&packet.getInventory().getHelmet().getType().name().contains(identifier)&&Amount>0){
				DyeColor now = DyeColor.getDyeToReplace(packet.getInventory().getHelmet().getType());
				if(!now.equals(start)){
					packet.getInventory().setHelmet(start.applyToItemStack(packet.getInventory().getHelmet()));
					if(!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()){j--;if(j==0){Amount--;j=row;}}
				}
			}
		}
		removeIS(obj, p, Amount);
	}
	
	private void removeIS(ObjectID obj, Player p, int Amount){
		manager.updateFurniture(obj);
		if(p.getGameMode().equals(GameMode.CREATIVE) && FurnitureLib.getInstance().useGamemode()) return;
		Integer i = p.getInventory().getHeldItemSlot();
		ItemStack item = p.getInventory().getItemInMainHand();
		item.setAmount(Amount);
		p.getInventory().setItem(i, item);
		p.updateInventory();
	}
	
	@SuppressWarnings("unchecked")
	public static Tag<Material> getMaterialTag(String name) throws NullPointerException{
		if(name == null) return null;
		Class<?> tag = Tag.class;
		try {
			return (Tag<Material>) tag.getDeclaredField(name).get(null);
		}catch (Exception e) {
			return null;
		}
	}
	
	public static String getTagName(Tag<Material> material) throws NullPointerException {
		if(material == null) return null;
		Class<?> tag = Tag.class;
		try {
			for(Field f : tag.getFields()) {
				if(f.get(null).equals(material)) {
					return f.getName();
				}
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public static Tag<Material> contains(Material mat){
		if(mat == null) return null;
		Class<?> tag = Tag.class;
		try {
			for(Field f : tag.getFields()) {
				if(f.getType().equals(tag)) {
					@SuppressWarnings("unchecked")
					Tag<Material> matTag = (Tag<Material>) f.get(null);
					if(matTag == null) continue;
					if(matTag.isTagged(mat)) {
						return matTag;
					}
				}
			}
		}catch (Exception e) {
			return null;
		}
		return null;
	}
 	
	@Deprecated
	private void colorBanner(Player p, boolean canBuild, Material m, ObjectID obj, int row){
//		if(!canBuild){return;}
//		ItemStack is = p.getInventory().getItemInMainHand();
//		Integer Amount = is.getAmount();
//		List<fEntity> asp = manager.getfArmorStandByObjectID(obj);
//		BannerMeta meta = (BannerMeta) is.getItemMeta();
//		DyeColor color = meta.getBaseColor();
//		int j = row;
//		for(fEntity packet : asp){
//			if(packet.getInventory().getHelmet()!=null&&packet.getInventory().getHelmet().getType().name().toLowerCase().contains("_banner")&&Amount>0){
//				DyeColor color2 = ((BannerMeta) packet.getInventory().getHelmet().getItemMeta()).getBaseColor();
//				if(color2 != color){
//					packet.getInventory().setHelmet(getNewIS(packet.getInventory().getHelmet(), color));
//					if(!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()){j--;if(j==0){Amount--;j=row;}}
//				}
//			}
//		}
//		removeIS(obj, p, Amount);
	}
	
//	@SuppressWarnings("deprecation")
//	private ItemStack getNewIS(ItemStack helmet, DyeColor color) {
////		//ItemStack is = new ItemStack(helmet.getType(), 1, helmet.getDurability());
////		ItemStack is = helmet.clone();
////		is.setAmount(1);
////		BannerMeta im = (BannerMeta) helmet.getItemMeta();
////		im.setBaseColor(color);
////		is.setItemMeta(im);
//		return is;
//	}
}
