package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;

public class Type {
	static LanguageManager lang = FurnitureLib.getInstance().getLangManager();
	
	public enum BodyPart{
		HEAD("Head",11, new EulerAngle(0D,0D,0D)), 
		BODY("Body",12, new EulerAngle(0D,0D,0D)), 
		LEFT_ARM("Left_Arm",13, new EulerAngle((double)-10.0F, 0.0D, (double)-10.0F)), 
		RIGHT_ARM("Right_Arm",14, new EulerAngle((double)-15.0F, 0.0D, (double)10.0F)), 
		LEFT_LEG("Left_Leg",15, new EulerAngle((double)-1.0F, 0.0D, (double)-1.0F)), 
		RIGHT_LEG("Right_Leg",16, new EulerAngle((double)1.0F, 0.0D, (double)1.0F));
		
		String name;
		EulerAngle angle;
        int field;
        
        BodyPart(String name,int field,EulerAngle angle){
            this.name=name;
            this.field=field;
            this.angle = angle;
        }
        
        public String getName(){
            return name;
        }
        
        public int getField(){
            return field;
        }

        public EulerAngle getDefAngle(){
        	return angle;
        }
        
		public static List<BodyPart> getList() {
			List<BodyPart> parts = Arrays.asList(BodyPart.HEAD, BodyPart.BODY, BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM, BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG);
			return parts;
		}
	}
	
	public enum DataBaseType{
		MySQL, SQLite;
	}
	
	public enum ColorType{
		BLOCK, BANNER;
	}
	
	public enum EventType{
		PLACE(null, null, null, null),
		BREAK(lang.getName("Break"), lang.getMaterial("Break"), lang.getShort("Break"), 1),
		INTERACT(lang.getName("Interact"), lang.getMaterial("Interact"), lang.getShort("Interact"), 1),
		BREAK_INTERACT(lang.getName("Break_interact"), lang.getMaterial("Break_interact"), lang.getShort("Break_interact"), 1),
		NONE(lang.getName("None"), lang.getMaterial("None"), lang.getShort("None"), 1);
		
		String name;
		Material material;
		Short durability;
		Integer amount;
		
		public String getName(){return this.name;}
		public Material getMaterial(){return this.material;}
		public Short getDurability(){return this.durability;}
		public Integer getAmount(){return this.amount;}
		
		EventType(String name,Material material,Short durability, Integer amount){
            this.name=name;
            this.material=material;
            this.durability = durability;
            this.amount = amount;
		}
		
		public ItemStack getItemStack() {
			ItemStack is = new ItemStack(material, amount, durability);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(name);
			is.setItemMeta(im);
			return is;
		}
	}
	
	public enum LimitationType{
		PLAYER, CHUNK, WORLD, PLOT;
	}
	
	public enum SQLAction{
		SAVE, UPDATE, REMOVE, NOTHING;
	}
	
	public enum PublicMode{
		PRIVATE(lang.getName("Private"), lang.getMaterial("Private"), lang.getShort("Private"), 1, lang.getStringList("Private")), 
		MEMBERS(lang.getName("Member"), lang.getMaterial("Member"), lang.getShort("Member"), 1, lang.getStringList("Member")), 
		PUBLIC(lang.getName("Public"), lang.getMaterial("Public"), lang.getShort("Public"), 1, lang.getStringList("Public"));
		
		String name;
		Material material;
		Short durability;
		Integer amount;
		List<String> stringl;
		
		public String getName(){return this.name;}
		public Material getMaterial(){return this.material;}
		public Short getDurability(){return this.durability;}
		public Integer getAmount(){return this.amount;}
		public List<String> getStringList(){return this.stringl;}
		
		PublicMode(String name,Material material,Short durability, Integer amount, List<String> stringl){
            this.name=name;
            this.material=material;
            this.durability = durability;
            this.amount = amount;
            this.stringl = stringl;
        }
		public ItemStack getItemStack() {
			ItemStack is = new ItemStack(material, amount, durability);
			ItemMeta im = is.getItemMeta();
			im.setLore(stringl);
			im.setDisplayName(name);
			is.setItemMeta(im);
			return is;
		}
	}
}
