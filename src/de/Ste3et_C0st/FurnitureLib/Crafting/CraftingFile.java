
package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class CraftingFile {
	private config c;
	private FileConfiguration file;
	private String name;
	private String header;
	private ShapedRecipe recipe;
	private boolean isDisable;
	public ShapedRecipe getRecipe(){return this.recipe;}
	public ItemStack getItemstack(){return this.recipe.getResult();}
	public boolean isEnable(){return this.isDisable;}
	public String getFileName(){return this.name;}
	public String systemID = "";
	public String getSystemID(){return this.systemID;}
	public File filePath;
	public File getFilePath(){return this.filePath;}
	public String getFileHeader(){return this.header;}
	@SuppressWarnings("deprecation")
	
	public CraftingFile(String name,InputStream file){
		this.c = new config(FurnitureLib.getInstance());
		this.name = name;
		this.file = c.getConfig(name, "/Crafting/");
		this.file.addDefaults(YamlConfiguration.loadConfiguration(file));
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
				this.recipe = new ShapedRecipe(returnResult(s)).shape(returnFragment(s)[0], returnFragment(s)[1], returnFragment(s)[2]);
				for(Character c : returnMaterial(s).keySet()){
					if(!returnMaterial(s).get(c).getItemType().equals(Material.AIR)){
						this.recipe.setIngredient(c.charValue(), returnMaterial(s).get(c));
					}
				}				
				if(!isDisable){
					Bukkit.getServer().addRecipe(this.recipe);
				}
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setName(String s){
		ItemStack is = getRecipe().getResult();
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(s);
		is.setItemMeta(im);
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
		String MaterialSubID = "0";
		try{
			MaterialSubID = file.getString(header+".material", "0");
		}catch(Exception e){
			MaterialSubID = "0";
		}
		Material mat = Material.AIR;
		short durability = 0;
		if(MaterialSubID.contains(":")){
			String[] str = MaterialSubID.split(":");
			mat = Material.getMaterial(Integer.parseInt(str[0]));
			try{
				durability = (short) Integer.parseInt(str[1]);
			}catch(Exception e){
				durability = (short) 0;
			}
			
		}else{
			mat = Material.getMaterial(Integer.parseInt(MaterialSubID));
		}

		ItemStack is = new ItemStack(mat);
		ItemMeta im = is.getItemMeta();
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
	
	public void removeCrafting(){
		Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
		Recipe recipe;
		while(it.hasNext())
		{
		recipe = it.next();
		if (recipe != null && recipe.getResult().equals(getItemstack())){it.remove();}
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