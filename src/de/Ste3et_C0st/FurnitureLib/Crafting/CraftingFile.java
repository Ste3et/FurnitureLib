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

import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class CraftingFile {
	private config c;
	private FileConfiguration file;
	private String name;
	private ShapedRecipe recipe;
	public ShapedRecipe getRecipe(){return this.recipe;}
	
	@SuppressWarnings("deprecation")
	public CraftingFile(String name,InputStream file){
		this.c = new config(FurnitureLib.getInstance());
		this.name = name;
		this.file = c.getConfig(name, "/Crafting/");
		this.file.addDefaults(YamlConfiguration.loadConfiguration(file));
		this.file.options().copyDefaults(true);
		this.c.saveConfig(name, this.file, "/Crafting/");
		loadCrafting(name);
	}
	
	private void loadCrafting(String s){
		try{
				this.recipe = new ShapedRecipe(returnResult(s)).shape(returnFragment(s)[0], returnFragment(s)[1], returnFragment(s)[2]);
				for(Character c : returnMaterial(s).keySet()){
					if(!returnMaterial(s).get(c).equals(Material.AIR)){
						this.recipe.setIngredient(c.charValue(), returnMaterial(s).get(c));
					}
				}
				Bukkit.getServer().addRecipe(this.recipe);
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack returnResult(String s){
		String path = name;
		String MaterialSubID = path+".material";
		short durability = 0;
		if(MaterialSubID.contains(":")){
			String[] split = MaterialSubID.split(":");
			durability = (short) Integer.parseInt(split[1]);
		}
		Integer MaterialID = file.getInt(path+".material");
		ItemStack is = new ItemStack(Material.getMaterial(MaterialID));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', file.getString(path+".name")));
		is.setItemMeta(im);
		is.setDurability(durability);
		is.setAmount(1);
		return is;
	}
	
	private String[] returnFragment(String s){
		String path = name;
		String recipe = this.file.getString(path+".crafting.recipe");
		String[] fragments = recipe.split(",");
		return fragments;
	}
	
	@SuppressWarnings("deprecation")
	private HashMap<Character,Material> returnMaterial(String s){
		String path = name;
		List<String> stringList = returnCharacters(s);
		HashMap<Character, Material> materialHash = new HashMap<Character, Material>();
		for(String str : stringList){
			Character chars = str.charAt(0);
			Integer MaterialID = this.file.getInt(path+".crafting.index." + str);
			Material material = Material.getMaterial(MaterialID);
			materialHash.put(chars, material);
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
