
package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import com.google.common.base.Charsets;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class CraftingFile {
	private FileConfiguration file;
	private String name;
	private String header;
	private ShapedRecipe recipe;
	private boolean isDisable;
	private PlaceableSide side = null;
	private HashSet<NBTTagCompound> functionList = new HashSet<NBTTagCompound>(); 
	public ShapedRecipe getRecipe(){return this.recipe;}
	public ItemStack getItemstack(){return getRecipe().getResult();}
	public boolean isEnable(){return this.isDisable;}
	public String getFileName(){return this.name;}
	public String systemID = "";
	public String getSystemID(){return this.systemID;}
	public File filePath;
	public File getFilePath(){return this.filePath;}
	public String getFileHeader(){return this.header;}
	public HashSet<NBTTagCompound> getFunctionList(){return this.functionList;}
	
	public CraftingFile(String name,InputStream file){
		this.name = name;
		this.filePath = new File("plugins/" + FurnitureLib.getInstance().getName() + "/models/" + name + ".dModel");
		this.file = YamlConfiguration.loadConfiguration(filePath);
		if(file == null) {System.out.println("problems to load " + name);return;}
		Reader inReader = new InputStreamReader(file);
		this.file.addDefaults(YamlConfiguration.loadConfiguration(inReader));
		this.file.options().copyDefaults(true);
		try {
			this.file.save(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		header = getHeader();
		if(!this.file.contains(header + ".displayName")) {
			System.out.println("Cannot load " + name + " (Old format)");
			return;
		}
		
		if(this.file.isSet(header+".system-ID")){
			systemID = this.file.getString(header+".system-ID");
		}else{
			this.file.set(header+".system-ID", name);
			systemID = name;
			try {
				this.file.save(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.file = YamlConfiguration.loadConfiguration(filePath);
		loadCrafting(name);
	}
	
	public void setFileConfiguration(FileConfiguration file){
		this.file = file;
	}
	
	public String getHeader(){
		try{
			return (String) this.file.getConfigurationSection("").getKeys(false).toArray()[0];
		}catch(ArrayIndexOutOfBoundsException ex){
			return this.name;
		}
	}
	
	public void rename(String name){
		if(name==null||name.equalsIgnoreCase("")) return;
		ItemStack stack = getRecipe().getResult();
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		stack.setItemMeta(meta);
	}

	public void loadCrafting(String s){
		try{
				this.isDisable = file.getBoolean(header+".crafting.disable");
				NamespacedKey key = new NamespacedKey(FurnitureLib.getInstance(), this.name);
				this.recipe = new ShapedRecipe(key, returnResult(s)).shape(returnFragment(s)[0], returnFragment(s)[1], returnFragment(s)[2]);
				for(Character c : returnMaterial(s).keySet()){
					if(!returnMaterial(s).get(c).equals(Material.AIR)){
						this.recipe.setIngredient(c.charValue(), returnMaterial(s).get(c));
					}
				}				
				if(!isDisable){
					if(!isKeyRegistred(key)) {
						Bukkit.getServer().addRecipe(this.recipe);
					}
				}
				getPlaceAbleSide();
				loadFunction();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadFunction() {
		if(this.file.contains(header+".projectData.functions")) {
//			NBTTagCompound compoundTest = new NBTTagCompound();
//			compoundTest.setString("function", "replace");
//			
//			NBTTagCompound dataTest = new NBTTagCompound();
//			
//			dataTest.setString("type", "*_STAINED_GLASS_PANE");
//			dataTest.setBoolean("consume", true);
//			
//			compoundTest.set("data", dataTest);
//			
//			System.out.println(compoundTest.toString());
//			
//			functionList.clear();
//			for(String str : this.file.getStringList(header+".projectData.functions")) {
//				try {
//					
//					ByteArrayInputStream bin = new ByteArrayInputStream(encStage1(str));
//					NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
//					NBTCompressedStreamTools.read(inputstream)
//					functionList.add(compound);
//				}catch (Exception e) {
//					e.printStackTrace();
//					continue;
//				}
//			}
			
//			String testString = "{ChestedHorse:true,Items:[{ Slot:2b, id:stone, Count:1 }],SaddleItem:{ id:saddle, Count:1 },Tame:true}";
//			
//			try{
//				byte[] b = compress(testString);
//				ByteArrayInputStream bin = new ByteArrayInputStream(b);
//				NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
//				bin.close();
//				System.out.println(compound.toString());
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
		}
	}
	
	public static byte[] compress(String str) throws Exception {
	    if (str == null || str.length() == 0) {
	        return null;
	    }
	    System.out.println("String length : " + str.length());
	    ByteArrayOutputStream obj=new ByteArrayOutputStream();
	    GZIPOutputStream gzip = new GZIPOutputStream(obj);
	    gzip.write(str.getBytes("UTF-8"));
	    gzip.close();
	    
	    return obj.toByteArray();
	 }

	
	private boolean isKeyRegistred(NamespacedKey key) {
		Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();
		while(recipes.hasNext()) {
			Recipe recipe = recipes.next();
			if(recipe instanceof ShapedRecipe) {
				ShapedRecipe r = (ShapedRecipe) recipe;
				if(r.getKey().equals(key)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public PlaceableSide getPlaceAbleSide(){
		if(this.side != null) return this.side;
		if(file.isSet(header+".PlaceAbleSide")) {
			String str = file.getString(header+".PlaceAbleSide", "TOP");
			PlaceableSide side = PlaceableSide.valueOf(str);
			this.side = side;
			return side;
		}
		this.side = PlaceableSide.TOP;
		return this.side;
	}
	
	public void setName(String s){
		ItemStack is = getRecipe().getResult();
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(s);
		is.setItemMeta(im);
		@SuppressWarnings("deprecation")
		ShapedRecipe recipe = new ShapedRecipe(is).shape(this.getRecipe().getShape());
		for(Character c : recipe.getIngredientMap().keySet()){
			recipe.setIngredient(c, this.recipe.getIngredientMap().get(c).getData());
		}
		this.recipe = recipe;
		if(!isDisable){
			Bukkit.getServer().addRecipe(this.recipe);
		}
	}
	
	private ItemStack returnResult(String s){
		Material mat = FurnitureLib.getInstance().getDefaultSpawnMaterial();
		if(file.contains(header + ".spawnMaterial")) {
			String str = file.getString(header + ".spawnMaterial");
			if(!str.equalsIgnoreCase("383")) {
				mat = Material.getMaterial(str);
			}
		}
		ItemStack is = new ItemStack(mat);
		ItemMeta im = is.getItemMeta();
		try{
			if(file.contains(header + ".unbreakable")) {
				boolean str = file.getBoolean(header + ".unbreakable", false);
				im.setUnbreakable(str);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		String name = file.getString(header+".displayName", "");
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		List<String> loreText = new ArrayList<String>();
		if(im.getLore()!=null) loreText = im.getLore();
		loreText.add(HiddenStringUtils.encodeString(getSystemID()));

		if(file.contains(header+".itemLore")){
			if(file.isList(header+".itemLore")){
				List<String> lore = file.getStringList(header+".itemLore");
				if(im.getLore()!=null) {loreText = im.getLore();}
				for(String str : lore){
					String a = ChatColor.translateAlternateColorCodes('&', str);
					loreText.add(a);
				}
			}
		}
		is.setAmount(1);
		im.setLore(loreText);
		
		try{
			if(file.contains(header + ".durability")) {
				int str = file.getInt(header + ".durability", 0);
				if(im instanceof Damageable) {
					((Damageable) im).setDamage(str);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		
		is.setItemMeta(im);
		return is;
	}
	
	private String[] returnFragment(String s){
		String recipe = this.file.getString(header+".crafting.recipe", "");
		String[] fragments = recipe.split(",");
		return fragments;
	}
	
	private HashMap<Character,Material> returnMaterial(String s){
		List<String> stringList = returnCharacters(s);
		HashMap<Character, Material> materialHash = new HashMap<Character, Material>();
		stringList.stream().forEach(letter -> {
			Character chars = letter.charAt(0);
			String part = this.file.getString(header+".crafting.index." + letter, "AIR");
			materialHash.put(chars, Material.getMaterial(part));
		});

		return materialHash;
	}
	
	private List<String> returnCharacters(String s){
		List<String> stringList = new ArrayList<String>();
		for(String str: returnFragment(s)){
			String[] sl = str.split("(?!^)");
			for(String o : sl){
				if(!stringList.contains(o)){
					stringList.add(o);
				}
			}
		}
		return stringList;
	}
	
	public void removeCrafting(ItemStack stack){
		Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
		Recipe recipe;
		while(it.hasNext())
		{
		recipe = it.next();
		if (recipe != null && recipe.getResult().equals(stack)){it.remove();}
		}
	}
}