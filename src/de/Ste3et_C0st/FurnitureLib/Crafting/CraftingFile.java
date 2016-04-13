
package de.Ste3et_C0st.FurnitureLib.Crafting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

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
	public ItemStack getItemstack(){return this.getRecipe().getResult();}
	public boolean isEnable(){return this.isDisable;}
	public String getFileName(){return this.name;}
	public String systemID = "";
	public String getSystemID(){return this.systemID;}
	@SuppressWarnings("deprecation")
	public CraftingFile(String name,InputStream file){
		this.c = new config(FurnitureLib.getInstance());
		this.name = name;
		this.file = c.getConfig(name, "/Crafting/");
		this.file.addDefaults(YamlConfiguration.loadConfiguration(file));
		this.file.options().copyDefaults(true);
		this.c.saveConfig(name, this.file, "/Crafting/");
		header = getHeader();
		if(this.file.isSet(header+".system-ID")){
			systemID = this.file.getString(header+".system-ID");
		}else{
			this.file.set(header+".system-ID", name);
			systemID = name;
			this.c.saveConfig(name, this.file, "/Crafting/");
		}
		loadCrafting(name);
	}
	
	public String getHeader(){
		return (String) this.file.getConfigurationSection("").getKeys(false).toArray()[0];
	}
	
	public void rename(String name){
		if(name==null||name.equalsIgnoreCase("")) return;
		ItemStack stack = getRecipe().getResult();
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		stack.setItemMeta(meta);
	}
	
	private void loadCrafting(String s){
		try{
				this.isDisable = file.getBoolean(header+".crafting.disable");
				this.recipe = new ShapedRecipe(returnResult(s)).shape(returnFragment(s)[0], returnFragment(s)[1], returnFragment(s)[2]);
				for(Character c : returnMaterial(s).keySet()){
					if(!returnMaterial(s).get(c).equals(Material.AIR)){
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
		String MaterialSubID = header+".material";
		short durability = 0;
		if(MaterialSubID.contains(":")){
			String[] split = MaterialSubID.split(":");
			durability = (short) Integer.parseInt(split[1]);
		}
		Integer MaterialID = file.getInt(header+".material");
		ItemStack is = new ItemStack(Material.getMaterial(MaterialID));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', file.getString(header+".name")));
		
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
		im.setLore(loreText);
		is.setItemMeta(im);
		is.setDurability(durability);
		is.setAmount(1);
		return is;
	}
	
	private String[] returnFragment(String s){
		String recipe = this.file.getString(header+".crafting.recipe");
		String[] fragments = recipe.split(",");
		return fragments;
	}
	
	@SuppressWarnings("deprecation")
	private HashMap<Character,Material> returnMaterial(String s){
		List<String> stringList = returnCharacters(s);
		HashMap<Character, Material> materialHash = new HashMap<Character, Material>();
		for(String str : stringList){
			Character chars = str.charAt(0);
			Material mat = Material.getMaterial(this.file.getInt(header+".crafting.index." + str));
			materialHash.put(chars, mat);
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
}