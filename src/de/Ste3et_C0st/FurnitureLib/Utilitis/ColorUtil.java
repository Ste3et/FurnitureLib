package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import org.bukkit.GameMode;
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
		int j = row;

		if(FurnitureLib.isNewVersion()) {
			DyeColor start = DyeColor.getDyeColor(is.getType());
			for(fEntity packet : asp){
				if(Objects.nonNull(packet.getInventory().getHelmet())&&packet.getInventory().getHelmet().getType().name().contains(identifier)&&Amount>0){
					DyeColor now = DyeColor.getDyeToReplace(packet.getInventory().getHelmet().getType());
					if(!now.equals(start)){
						packet.getInventory().setHelmet(start.applyToItemStack(packet.getInventory().getHelmet()));
						if(!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()){j--;if(j==0){Amount--;j=row;}}
					}
				}
			}
		}else {
			short color = getFromDey(is.getDurability());
			for(fEntity packet : asp){
				if(Objects.nonNull(packet.getInventory().getHelmet())&&packet.getInventory().getHelmet().getType().name().contains(identifier)&&Amount>0){
					short color2 = packet.getInventory().getHelmet().getDurability();
					if(color2 != color){
						ItemStack stack = packet.getInventory().getHelmet().clone();
						stack.setDurability(color);
						packet.getInventory().setHelmet(stack);
						if(!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()){j--;if(j==0){Amount--;j=row;}}
					}
				}
			}

		}
		removeIS(obj, p, Amount);
	}
	
	public short getFromDey(short s){return (short) (15-s);}
	
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
}
