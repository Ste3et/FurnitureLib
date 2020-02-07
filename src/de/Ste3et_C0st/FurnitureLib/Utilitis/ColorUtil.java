package de.Ste3et_C0st.FurnitureLib.Utilitis;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.ColorType;
import de.Ste3et_C0st.FurnitureLib.main.Type.DyeColor;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorUtil {

    private FurnitureLib lib = FurnitureLib.getInstance();
    private FurnitureManager manager = lib.getFurnitureManager();

    public void color(Player p, boolean canBuild, String identifier, ObjectID obj, ColorType type, int row) {
        if (identifier.contains("BANNER")) {
            type = ColorType.BANNER;
        }
        switch (type) {
            case BLOCK:
                colorBlock(p, canBuild, identifier, obj, row);
                break;
            case BANNER:
                colorBlock(p, canBuild, identifier, obj, row);
                break;
        }
    }

    private void colorBlock(Player p, boolean canBuild, String identifier, ObjectID obj, int row) {
        if (!canBuild) {
            return;
        }
        ItemStack is = p.getInventory().getItemInMainHand();
        AtomicInteger Amount = new AtomicInteger(is.getAmount());
        List<fEntity> asp = manager.getfArmorStandByObjectID(obj);
        int j = row;

        if (FurnitureLib.isNewVersion()) {
            DyeColor start = DyeColor.getDyeColor(is.getType());
            for (fEntity packet : asp) {
                if (Objects.nonNull(packet.getInventory().getHelmet()) && packet.getInventory().getHelmet().getType().name().contains(identifier) && Amount.get() > 0) {
                    DyeColor now = DyeColor.getDyeToReplace(packet.getInventory().getHelmet().getType());
                    if (!now.equals(start)) {
                        packet.getInventory().setHelmet(start.applyToItemStack(packet.getInventory().getHelmet()));
                        if (!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()) {
                            j--;
                            if (j == 0) {
                                Amount.getAndDecrement();
                                j = row;
                            }
                        }
                    }
                }
            }
        } else {
            short color = identifier.contains("BANNER") ? is.getDurability() : getFromDey(is.getDurability());

            asp.stream().filter(entity -> Objects.nonNull(entity.getHelmet())).filter(entity -> entity.getHelmet().getType().name().contains(identifier)).forEach(entity -> {
                short color2 = entity.getInventory().getHelmet().getDurability();
                if (Amount.get() > 0) {
                    if (color2 != color) {
                        ItemStack stack = entity.getInventory().getHelmet().clone();
                        stack.setDurability(color);
                        entity.getInventory().setHelmet(stack);
                        if (!p.getGameMode().equals(GameMode.CREATIVE) || !lib.useGamemode()) {
                            Amount.getAndDecrement();
                        }
                    }
                }
            });

        }
        removeIS(obj, p, Amount.get());
    }

    public short getFromDey(short s) {
        return (short) (15 - s);
    }

    private void removeIS(ObjectID obj, Player p, int Amount) {
        manager.updateFurniture(obj);
        if (p.getGameMode().equals(GameMode.CREATIVE) && FurnitureLib.getInstance().useGamemode()) return;
        int i = p.getInventory().getHeldItemSlot();
        ItemStack item = p.getInventory().getItemInMainHand();
        item.setAmount(Amount);
        p.getInventory().setItem(i, item);
        p.updateInventory();
    }
//	
//	@SuppressWarnings("unchecked")
//	public static org.bukkit.Tag<Material> getMaterialTag(String name) throws NullPointerException{
//		if(name == null) return null;
//		Class<?> tag = org.bukkit.Tag.class;
//		try {
//			return (org.bukkit.Tag<Material>) tag.getDeclaredField(name).get(null);
//		}catch (Exception e) {
//			return null;
//		}
//	}
//	
//	public static String getTagName(org.bukkit.Tag<Material> material) throws NullPointerException {
//		if(material == null) return null;
//		Class<?> tag = org.bukkit.Tag.class;
//		try {
//			for(Field f : tag.getFields()) {
//				if(f.get(null).equals(material)) {
//					return f.getName();
//				}
//			}
//		}catch (Exception e) {
//			return null;
//		}
//		return null;
//	}
//	
//	public static org.bukkit.Tag<Material> contains(Material mat){
//		if(mat == null) return null;
//		Class<?> tag = org.bukkit.Tag.class;
//		try {
//			for(Field f : tag.getFields()) {
//				if(f.getType().equals(tag)) {
//					@SuppressWarnings("unchecked")
//					org.bukkit.Tag<Material> matTag = (org.bukkit.Tag<Material>) f.get(null);
//					if(matTag == null) continue;
//					if(matTag.isTagged(mat)) {
//						return matTag;
//					}
//				}
//			}
//		}catch (Exception e) {
//			return null;
//		}
//		return null;
//	}
}
