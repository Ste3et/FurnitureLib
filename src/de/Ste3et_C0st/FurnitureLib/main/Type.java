package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;

public class Type {
	static LanguageManager lang = FurnitureLib.getInstance().getLangManager();
	static List<Material> swords = Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD);
	static List<Material> spades = Arrays.asList(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL);
	static List<Material> axt = Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE);
	static List<Material> pickaxe = Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE);
	static List<Material> hoes = Arrays.asList(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE);
	static List<Material> weapons = Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD,
												  Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE);
	static List<Material> tools = Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE,
												Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL,
												Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE,
												Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE);
	public enum DataBaseType{MySQL, SQLite;}
	public enum ColorType{BLOCK, BANNER;}
	public enum LimitationType{PLAYER, CHUNK, WORLD;}
	public enum SQLAction{SAVE, UPDATE, REMOVE, PURGE,NOTHING;}
	public enum CenterType{LEFT, RIGHT, CENTER, FRONT}
	public enum PlaceableSide{TOP,BOTTOM,SIDE, WATER}
	public enum Reason{BLOCK,ENTITY}
	
	public enum DyeColor
	{
	  WHITE(Material.BONE_MEAL),  
	  ORANGE(Material.ORANGE_DYE),  
	  MAGENTA(Material.MAGENTA_DYE),  
	  LIGHT_BLUE(Material.LIGHT_BLUE_DYE),  
	  YELLOW(Material.DANDELION_YELLOW),  
	  LIME(Material.LIME_DYE),  
	  PINK(Material.PINK_DYE),  
	  LIGHT_GRAY(Material.LIGHT_GRAY_DYE),
	  GRAY(Material.GRAY_DYE),  
	  CYAN(Material.CYAN_DYE),  
	  PURPLE(Material.PURPLE_DYE),  
	  BLUE(Material.LAPIS_LAZULI),  
	  BROWN(Material.COCOA_BEANS),  
	  GREEN(Material.CACTUS_GREEN),  
	  RED(Material.ROSE_RED),  
	  BLACK(Material.INK_SAC);

	  private Material mat;
	  
	  private DyeColor(Material mat)
	  {
	    this.mat = mat;
	  }

	  public Material getMaterial()
	  {
	    return this.mat;
	  }
	  
	  public org.bukkit.DyeColor getDyeColor() {
		  return EnumSet.allOf(org.bukkit.DyeColor.class).stream().filter(color -> color.name().equalsIgnoreCase(this.name())).findFirst().orElse(org.bukkit.DyeColor.WHITE);
	  }
	  
	  public Material replaceMaterial(Material startMaterial) {
		  String str = startMaterial.name().toUpperCase();
		  DyeColor toReplace = EnumSet.allOf(DyeColor.class).stream().filter(color -> startMaterial.name().startsWith(color.name())).findFirst().orElse(DyeColor.WHITE);
		  str = str.replace(toReplace.name(), this.name());
		  return Material.valueOf(str);
	  }
	  
	  public ItemStack applyToBannerBase(ItemStack banner) {
		  ItemMeta meta = banner.getItemMeta();
		  if(meta instanceof BannerMeta){
			  BannerMeta bannerMeta = (BannerMeta) meta;
			  banner.setType(replaceMaterial(banner.getType()));
			  banner.setItemMeta(bannerMeta);
			  return banner;
		  }
		  banner.setItemMeta(meta);
		  return banner;
	  }
	  
	  public static DyeColor getDyeColor(Material mat) {
		  return EnumSet.allOf(DyeColor.class).stream().filter(color -> color.getMaterial().equals(mat)).findFirst().orElse(null);
	  }
	  
	  public static DyeColor getDyeToReplace(Material mat) {
		  return EnumSet.allOf(DyeColor.class).stream().filter(color -> mat.name().contains(color.name())).findFirst().orElse(null);
	  }
	  
	  public ItemStack applyToItemStack(ItemStack stack) {
		  ItemMeta meta = stack.getItemMeta();
		  stack.setType(replaceMaterial(stack.getType()));
		  stack.setItemMeta(meta);
		  return stack;
	  }
	}
	
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
        
        public int getField(){
            return field;
        }

        public EulerAngle getDefAngle(){
        	return angle;
        }
	}
	

	public enum ProtocolFields{
		Spigot19(10,11,12,13,14,15,16,9),
		Spigot110(11,12,13,14,15,16,17,10);

		int bitMask, HeadRotation, BodyRotation, LeftArmRotation, RightArmRotation, LeftLegRotation, RightLegRotation, wrapperBit;
		ProtocolFields(int a,int b, int c, int d, int e, int f, int g,int h){
			this.bitMask = a;
			this.HeadRotation = b;
			this.BodyRotation = c;
			this.LeftArmRotation = d;
			this.RightArmRotation = e;
			this.LeftLegRotation = f;
			this.RightLegRotation = g;
			this.wrapperBit = h;
		}
		
		public int getBitMask(){return this.bitMask;}
		public int getHeadRotation(){return this.HeadRotation;}
		public int getBodyRotation(){return this.BodyRotation;}
		public int getLeftArmRotation(){return this.LeftArmRotation;}
		public int getRightArmRotation(){return this.RightArmRotation;}
		public int getLeftLegRotation(){return this.LeftLegRotation;}
		public int getRightLegRotation(){return this.RightLegRotation;}
		public int getWrapperBit(){return this.wrapperBit;}
		
		public static ProtocolFields getField(String s){
			if(s.startsWith("1.9")){return Spigot19;}
			if(s.startsWith("1.10")){return Spigot110;}
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
		PLACE(null, null, null),
		BREAK(lang.getName("Break"), lang.getMaterial("Break"), 1),
		INTERACT(lang.getName("Interact"), lang.getMaterial("Interact"), 1),
		BREAK_INTERACT(lang.getName("Break_interact"), lang.getMaterial("Break_interact"), 1),
		NONE(lang.getName("None"), lang.getMaterial("None"), 1);
		
		String name;
		Material material;
		Integer amount;
		
		public String getName(){return this.name;}
		public Material getMaterial(){return this.material;}
		public Integer getAmount(){return this.amount;}
		
		EventType(String name,Material material, Integer amount){
            this.name=name;
            this.material=material;
            this.amount = amount;
		}
		
		public ItemStack getItemStack() {
			ItemStack is = new ItemStack(material, amount);
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
		PRIVATE(lang.getName("Private"), lang.getMaterial("Private"), 1, lang.getStringList("Private")), 
		MEMBERS(lang.getName("Member"), lang.getMaterial("Member"), 1, lang.getStringList("Member")), 
		PUBLIC(lang.getName("Public"), lang.getMaterial("Public"), 1, lang.getStringList("Public"));
		
		String name;
		Material material;
		Integer amount;
		List<String> stringl;
		
		public String getName(){return this.name;}
		public Material getMaterial(){return this.material;}
		public Integer getAmount(){return this.amount;}
		public List<String> getStringList(){return this.stringl;}
		
		PublicMode(String name,Material material, Integer amount, List<String> stringl){
            this.name=name;
            this.material=material;
            this.amount = amount;
            this.stringl = stringl;
        }
		
		public ItemStack getItemStack() {
			ItemStack is = new ItemStack(material, amount);
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
}
