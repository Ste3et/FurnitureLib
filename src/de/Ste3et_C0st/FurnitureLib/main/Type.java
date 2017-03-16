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
	static List<Material> swords = Arrays.asList(Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD);
	static List<Material> spades = Arrays.asList(Material.WOOD_SPADE, Material.STONE_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.DIAMOND_SPADE);
	static List<Material> axt = Arrays.asList(Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE);
	static List<Material> pickaxe = Arrays.asList(Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE);
	static List<Material> hoes = Arrays.asList(Material.WOOD_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLD_HOE, Material.DIAMOND_HOE);
	static List<Material> weapons = Arrays.asList(Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD,
												  Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE);
	static List<Material> tools = Arrays.asList(Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE,
												Material.WOOD_SPADE, Material.STONE_SPADE, Material.IRON_SPADE, Material.GOLD_SPADE, Material.DIAMOND_SPADE,
												Material.WOOD_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE,
												Material.WOOD_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLD_HOE, Material.DIAMOND_HOE);
	public enum DataBaseType{MySQL, SQLite;}
	public enum ColorType{BLOCK, BANNER;}
	public enum LimitationType{PLAYER, CHUNK, WORLD;}
	public enum SQLAction{SAVE, UPDATE, REMOVE, PURGE,NOTHING;}
	public enum CenterType{LEFT, RIGHT, CENTER, FRONT}
	public enum PlaceableSide{TOP,BOTTOM,SIDE, WATER}
	public enum BodyPart{
		HEAD("Head",12, new EulerAngle(0D,0D,0D)), 
		BODY("Body",13, new EulerAngle(0D,0D,0D)), 
		LEFT_ARM("Left_Arm",14, new EulerAngle(-0.174533, 0.0D, -0.174533)), 
		RIGHT_ARM("Right_Arm",15, new EulerAngle(-0.261799, 0.0D, 0.174533)), 
		LEFT_LEG("Left_Leg",16, new EulerAngle(-0.0174533, 0.0D, -0.0174533)), 
		RIGHT_LEG("Right_Leg",17, new EulerAngle(0.0174533, 0.0D, 0.0174533));
		
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
        
        @Deprecated
        public int getField(){
            return field;
        }

        public EulerAngle getDefAngle(){
        	return angle;
        }
	}
	
	public enum ProtocolFields{
		Spigot19(10,11,12,13,14,15,16),
		Spigot110(11,12,13,14,15,16,17);
		
		int bitMask, HeadRotation, BodyRotation, LeftArmRotation, RightArmRotation, LeftLegRotation, RightLegRotation;
		ProtocolFields(int a,int b, int c, int d, int e, int f, int g){
			this.bitMask = a;
			this.HeadRotation = b;
			this.BodyRotation = c;
			this.LeftArmRotation = d;
			this.RightArmRotation = e;
			this.LeftLegRotation = f;
			this.RightLegRotation = g;
		}
		
		public int getBitMask(){return this.bitMask;}
		public int getHeadRotation(){return this.HeadRotation;}
		public int getBodyRotation(){return this.BodyRotation;}
		public int getLeftArmRotation(){return this.LeftArmRotation;}
		public int getRightArmRotation(){return this.RightArmRotation;}
		public int getLeftLegRotation(){return this.LeftLegRotation;}
		public int getRightLegRotation(){return this.RightLegRotation;}
		
		public static ProtocolFields getField(String s){
			if(s.startsWith("1.9")) return Spigot19;
			if(s.startsWith("1.10")) return Spigot110;
			return Spigot110;
		}
		
		public int getFieldFromPose(BodyPart part){
			switch (part) {
			case HEAD:return getHeadRotation();
			case BODY:return getBodyRotation();
			case LEFT_ARM:return getLeftArmRotation();
			case RIGHT_ARM:return getRightArmRotation();
			case LEFT_LEG:return getLeftLegRotation();
			case RIGHT_LEG:return getRightLegRotation();
			}
			return BodyRotation;
		}
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
	
	public enum ToolType{
		SWORD(swords),
		SPADE(spades),
		AXE(axt),
		PICKAXE(pickaxe),
		HOE(hoes),
		WEAPON(weapons),
		TOOLS(tools);
		
		List<Material> matList;
		ToolType(List<Material> matList){this.matList=matList;}
		
		public List<Material> getMaterliaList(){return this.matList;}
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
	
	public enum EntityMoving{
		LEFT(1,0,false),
		RIGHT(-1,0,false),
		FORWARD(0,1,false),
		BACKWARD(0,-1,false),
		JUMPING(0,0,true),
		SNEEKING(0,0,false),
		LEFT_FORWARD(1,1,false),
		RIGHT_FORWARD(-1,1,false),
		LEFT_BACKWARD(1,-1,false),
		RIGHT_BACKWARD(-1,-1,false);

		public void setValues(float a, float b, boolean c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
		
		private float a = 0;
		private float b = 0;
		private boolean c = false;
		
		EntityMoving(float a, float b, boolean c){
			this.a = a;
			this.b = b;
			this.c = c;
		}
		
		public float getFieldA(){return this.a;}
		public float getFieldB(){return this.b;}
		public boolean getFieldC(){return this.c;}
	}
	
	public enum MoveType{
		FORWARD,BACKWAR,NOTHING
	}
}
