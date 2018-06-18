
package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class CraftingFile {
	private config c;
	private FileConfiguration file;
	private String name;
	private String header;
	private ShapedRecipe recipe;
	private boolean isDisable;
	private PlaceableSide side = null;
	public ShapedRecipe getRecipe(){return this.recipe;}
	public ItemStack getItemstack(){return getRecipe().getResult();}
	public boolean isEnable(){return this.isDisable;}
	public String getFileName(){return this.name;}
	public String systemID = "";
	public String getSystemID(){return this.systemID;}
	public File filePath;
	public File getFilePath(){return this.filePath;}
	public String getFileHeader(){return this.header;}
	
	public CraftingFile(String name,InputStream file){
		this.c = new config(FurnitureLib.getInstance());
		this.name = name;
		this.file = c.getConfig(name, "/Crafting/");
		Reader inReader = new InputStreamReader(file);
		this.file.addDefaults(YamlConfiguration.loadConfiguration(inReader));
		this.file.options().copyDefaults(true);
		this.c.saveConfig(name, this.file, "/Crafting/");
		this.filePath = new File(new File("plugins/FurnitureLib/Crafting"), name + ".yml");
		header = getHeader();
		if(this.file.isSet(header+".system-ID")){
			systemID = this.file.getString(header+".system-ID");
		}else{
			this.file.set(header+".system-ID", name);
			systemID = name;
			this.c.saveConfig(name, this.file, "/Crafting/");
		}
		this.file = c.getConfig(name, "/Crafting/");
		
		if(Bukkit.getServer().getBukkitVersion().startsWith("1.12")){
			loadCrafting(name);
		}else {
			loadCraftingUnder1_12(name);
		}
		
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
					if(!returnMaterial(s).get(c).getItemType().equals(Material.AIR)){
						this.recipe.setIngredient(c.charValue(), returnMaterial(s).get(c));
					}
				}				
				if(!isDisable){
					if(!isKeyRegistred(key)) {
						Bukkit.getServer().addRecipe(this.recipe);
					}
				}
				getPlaceAbleSide();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void loadCraftingUnder1_12(String s){
		try{
				this.isDisable = file.getBoolean(header+".crafting.disable");
				//NamespacedKey key = new NamespacedKey(FurnitureLib.getInstance(), this.name);
				//this.recipe = new ShapedRecipe(key, returnResult(s)).shape(returnFragment(s)[0], returnFragment(s)[1], returnFragment(s)[2]);
				this.recipe = new ShapedRecipe(returnResult(s)).shape(returnFragment(s)[0], returnFragment(s)[1], returnFragment(s)[2]);
				for(Character c : returnMaterial(s).keySet()){
					if(!returnMaterial(s).get(c).getItemType().equals(Material.AIR)){
						this.recipe.setIngredient(c.charValue(), returnMaterial(s).get(c));
					}
				}				
				if(!isDisable){
					Bukkit.getServer().addRecipe(this.recipe);
				}
				getPlaceAbleSide();
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	@SuppressWarnings("deprecation")
	private ItemStack returnResult(String s){
		Material mat = Material.MONSTER_EGG;
		short durability = 0;
		if(file.contains(header + ".material")) {
			String str = file.getString(header + ".material");
			if(str.contains(":")) {
				str = str.split(":")[0];
			}else {
				try {
					Integer i = Integer.parseInt(str);
					mat = Material.getMaterial(i);
				}catch (NumberFormatException ex) {
					mat = Material.getMaterial(str);
				}
			}
		}
		
		if(file.contains(header + ".durability")) {
			String str = file.getString(header + ".durability");
			try {
				durability = (short) Integer.parseInt(str);
			}catch (NumberFormatException ex) {
				ex.printStackTrace();
				durability = 0;
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
		}
		
		String name = file.getString(header+".name", "");
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		List<String> loreText = new ArrayList<String>();
		if(im.getLore()!=null) loreText = im.getLore();
		loreText.add(HiddenStringUtils.encodeString(getSystemID()));
		
		if(file.isSet(header+".lore")){
			if(file.isList(header+".lore")){
				List<String> lore = file.getStringList(header+".lore");
				if(im.getLore()!=null) loreText = im.getLore();
				for(String str : lore){
					String a = ChatColor.translateAlternateColorCodes('&', str);
					loreText.add(a);
				}
			}
		}
		is.setAmount(1);
		is.setDurability(durability);
		im.setLore(loreText);
		is.setItemMeta(im);
		return is;
	}
	
	private String[] returnFragment(String s){
		String recipe = this.file.getString(header+".crafting.recipe", "");
		String[] fragments = recipe.split(",");
		return fragments;
	}
	
	@SuppressWarnings("deprecation")
	private HashMap<Character,MaterialData> returnMaterial(String s){
		List<String> stringList = returnCharacters(s);
		HashMap<Character, MaterialData> materialHash = new HashMap<Character, MaterialData>();
		for(String str : stringList){
			Character chars = str.charAt(0);
			String part = this.file.getString(header+".crafting.index." + str);
			Material material = null;
			byte data = 0;
			if(part.contains(":")){
				String[] array = part.split(":");
				material = Material.getMaterial(Integer.parseInt(array[0]));
				data = Byte.parseByte(array[1]);
			}else{
				material = Material.getMaterial(Integer.parseInt(part));
				
			}
			materialHash.put(chars, new MaterialData(material, data));
		}
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
	
	public void setCraftingDisabled(boolean b){
		file.set(header+".crafting.disable", b);
		isDisable = b;
		if(file.isSet(header+".ProjectModels")){
			File file = new File("plugins/FurnitureLib/plugin/DiceEditor/" + filePath.getName());
			YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
			conf.set(header+".crafting.disable", b);
			try {
				conf.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			file.save(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}