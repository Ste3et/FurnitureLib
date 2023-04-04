package de.Ste3et_C0st.FurnitureLib.Crafting;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.Utilitis.MaterialConverter;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CraftingFile {
    
	public String systemID = "";
    public File filePath;
    private FileConfiguration file;
    private String name;
    private String header;
    private ShapedRecipe recipe;
    private boolean isDisable, useItemStackObject = false, enabledModel = false;
    private PlaceableSide side = null;
    
    public CraftingFile(String name, FileConfiguration fileConfiguration) {
        this.name = name;
        if (Objects.isNull(this.name)) return;
        this.filePath = new File(getPath(name));
        this.file = fileConfiguration;
        if (file == null) {
        	FurnitureLib.debug("problems to load " + name, 10);
            return;
        }
        
        header = getHeader();
        
        this.enabledModel = this.file.getBoolean(header + ".enabled", true);
        if(!this.enabledModel) {
        	return;
        }
        
        if (this.file.contains(header + ".system-ID")) {
            systemID = this.file.getString(header + ".system-ID");
        } else {
            this.file.set(header + ".system-ID", name);
            systemID = name;
            try {
                this.file.save(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
            if (Objects.nonNull(Class.forName("org.bukkit.NamespacedKey"))) {
                loadCrafting(name);
            }
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        }
    }

    public static String getPath(String modelName) {
    	if (FurnitureLib.isNewVersion()) {
            return "plugins/" + FurnitureLib.getInstance().getName() + "/models/" + modelName + ".dModel";
        } else {
        	return "plugins/" + FurnitureLib.getInstance().getName() + "/Crafting/" + modelName + ".yml";
        }
    }
    
    public ShapedRecipe getRecipe() {
        return this.recipe;
    }

    public ItemStack getItemstack() {
        return Objects.nonNull(getRecipe()) ? getRecipe().getResult() : null;
    }

    public boolean isEnable() {
        return this.isDisable;
    }
    
    public boolean isEnabledModel() {
    	return this.enabledModel;
    }

    public String getFileName() {
        return this.name;
    }

    public String getSystemID() {
        return this.systemID;
    }

    public File getFilePath() {
        return this.filePath;
    }

    public String getFileHeader() {
        return this.header;
    }
    
    public boolean useItemStackObject() {
    	return useItemStackObject;
    }

    public void setFileConfiguration(FileConfiguration file) {
        this.file = file;
    }

    public String getHeader() {
        try {
            return (String) this.file.getConfigurationSection("").getKeys(false).toArray()[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return this.name;
        }
    }

    public void rename(String name) {
        if (name == null || name.equalsIgnoreCase(""))
            return;
        ItemStack stack = getRecipe().getResult();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        stack.setItemMeta(meta);
    }


    public void loadCrafting(String s) {
        try {
            this.isDisable = file.getBoolean(header + ".crafting.disable", false);
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), this.name.toLowerCase()); // <-- Key
            String[] fragements = returnFragment();
            AtomicInteger materialCount = new AtomicInteger(0);
            this.recipe = new ShapedRecipe(key, returnResult(s)).shape(fragements[0], fragements[1], fragements[2]);
            returnMaterial(s,fragements).entrySet().stream().filter(c -> c.getValue() != Material.AIR).forEach(c -> {
                this.recipe.setIngredient(c.getKey(), c.getValue());
                materialCount.addAndGet(1);
            });
            if (!isDisable) {
                if (materialCount.get() > 0) {
                    Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
                    	if(isKeyisKeyRegistered(key) == false) {
                    		Bukkit.getServer().addRecipe(this.recipe);
                    	}
                    });
                }
            }
            getPlaceAbleSide();
            loadFunction();
        } catch (Exception e) {
        	System.err.println(this.header + " is a corrupted model File !");
            e.printStackTrace();
        }
    }

    public List<JsonObject> loadFunction() {
        List<JsonObject> jsonList = new ArrayList<>();
        if (this.file.contains(header + ".projectData.functions")) {
            List<String> stringList = this.file.getStringList(header + ".projectData.functions");
            for (String str : stringList) {
                try {
                    jsonList.add(new JsonParser().parse(str).getAsJsonObject());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonList;
    }

    private boolean isKeyisKeyRegistered(org.bukkit.NamespacedKey key) {
    	Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
    	while (it.hasNext()) {
			Recipe recipe = it.next();
			if(ShapedRecipe.class.isInstance(recipe)) {
				if(ShapedRecipe.class.cast(recipe).getKey().equals(key)) {
					return true;
				}
			}
		}
        return false;
    }

    public PlaceableSide getPlaceAbleSide() {
        this.side = PlaceableSide.valueOf(file.getString(header + ".PlaceAbleSide", "TOP").toUpperCase());
        return this.side;
    }

    public FileConfiguration getFile() {
        return this.file;
    }

    public void setName(String s) {
        ItemStack is = getRecipe().getResult();
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(s);
        is.setItemMeta(im);

        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), this.name.toLowerCase());
        ShapedRecipe recipe = new ShapedRecipe(key, is).shape(this.getRecipe().getShape());
        this.recipe.getIngredientMap().forEach((key1, value) -> recipe.setIngredient(key1, value.getData()));
        this.recipe = recipe;
        if (!isDisable)
            Bukkit.getServer().addRecipe(this.recipe);
    }

    private ItemStack returnResult(String s) {
    	if(file.contains(header + ".spawnItemStack")) {
    		try {
    			ItemStack stack = file.getItemStack(header + ".spawnItemStack");
    			if(!stack.getType().equals(Material.AIR)) {
    				ItemMeta meta = stack.getItemMeta();
        			List<String> loreText = new ArrayList<String>();
        			
        			if(meta.hasLore()) {
        				loreText.addAll(meta.getLore());
        			}
        			
        			if(FurnitureLib.getVersionInt() > 13) {
        				meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), "model"), PersistentDataType.STRING, getSystemID());
        			}else {
        				loreText.add(HiddenStringUtils.encodeString(getSystemID()));
        			}
        			
        			if(!loreText.isEmpty()) meta.setLore(loreText);
        			stack.setItemMeta(meta);
        			stack.setAmount(1);
        			useItemStackObject = true;
        			return stack;
    			}
    		}catch (Exception e) {
    			FurnitureLib.getInstance().getLogger().warning("Can't load " + header + ".spawnItemStack" + " from format, use spawnMaterial");
    			e.printStackTrace();
			}
    	}
    	
    	
        Material mat = FurnitureLib.getInstance().getDefaultSpawnMaterial();
        if (file.contains(header + ".spawnMaterial")) {
            String str = file.getString(header + ".spawnMaterial");
            if (!str.equalsIgnoreCase("383")) {
                mat = Material.getMaterial(str);
            }
        }
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        try {
            if (file.contains(header + ".unbreakable")) {
                boolean str = file.getBoolean(header + ".unbreakable", false);
                im.setUnbreakable(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String name = file.getString(header + (FurnitureLib.isNewVersion() ? ".displayName" : ".name"), header);
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> loreText = new ArrayList<String>();
        if (im.hasLore()) loreText = im.getLore();
        
        if (file.contains(header + ".custommodeldata")) {
        	try{
        		im.setCustomModelData(file.getInt(header + ".custommodeldata"));
        	}catch (Exception e) {/* Method = setCustomModelData didn't exist (ignore Exception) */}
        }

        if(FurnitureLib.getVersionInt() > 13) {
        	im.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), "model"), PersistentDataType.STRING, getSystemID());
		}else {
			loreText.add(HiddenStringUtils.encodeString(getSystemID()));
		}
        
        if (file.contains(header + ".itemLore")) {
            if (file.isList(header + ".itemLore")) {
                List<String> lore = file.getStringList(header + ".itemLore");
                if (im.getLore() != null) {
                    loreText = im.getLore();
                }
                for (String str : lore) {
                    String a = ChatColor.translateAlternateColorCodes('&', str);
                    loreText.add(a);
                }
            }
        }
        is.setAmount(1);
        if(!loreText.isEmpty()) im.setLore(loreText);

        try {
            if (file.contains(header + ".durability")) {
                int str = file.getInt(header + ".durability", 0);
                if (im instanceof Damageable) {
                    ((Damageable) im).setDamage(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(im);
        return is;
    }

    private String[] returnFragment() {
        return this.file.getString(header + ".crafting.recipe", "").split(",");
    }

    private HashMap<Character, Material> returnMaterial(String s, String[] recipe) {
        List<Character> stringList = returnCharacters(recipe);
        HashMap<Character, Material> materialHash = new HashMap<Character, Material>();
        stringList.forEach(letter -> {
            String part = this.file.getString(header + ".crafting.index." + letter, "AIR");
            Material material;
            if (!FurnitureLib.isNewVersion()) {
                try {
                    int i = Integer.parseInt(part);
                    material = MaterialConverter.convertMaterial(i, (byte) 0);
                } catch (Exception e) {
                    material = Material.getMaterial(part);
                }
            } else {
                material = Material.getMaterial(part);
            }

            materialHash.put(letter, material);
        });

        return materialHash;
    }

    private List<Character> returnCharacters(String[] recipe) {
        List<Character> stringList = new ArrayList<>();
        for (String str : returnFragment()) {
            for (String o : str.split("(?!^)")) {
            	Character character = o.charAt(0);
                if (!stringList.contains(character)) {
                    stringList.add(character);
                }
            }
        }
        return stringList;
    }

    public void removeCrafting(ItemStack stack) {
    	if(Objects.isNull(stack)) return;
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        if(UnmodifiableIterator.class.isInstance(it)) {
        	removeCraftingUnmodifiable(stack);
        	return;
        }
        Recipe recipe;
        while (it.hasNext()) {
            recipe = it.next();
            if (recipe != null && recipe.getResult().equals(stack)) {
                it.remove();
            }
        }
    }
    
    public void removeCraftingUnmodifiable(ItemStack stack) {
    	List<Recipe> backup = new ArrayList<Recipe>();
    	Recipe resultRecipe = null;
    	//Get Recipe Iterator
	    Iterator<Recipe> a = Bukkit.getServer().recipeIterator();

	    while(a.hasNext()){
	        Recipe recipe = a.next();
	        ItemStack result = recipe.getResult();
	        if(!result.isSimilar(stack)) {
	        	backup.add(recipe);
	        }else {
	        	resultRecipe = recipe;
	        }
	    }	
    	  
	    if(Objects.nonNull(resultRecipe)) {
	    	Bukkit.getServer().clearRecipes();
	    	for (Recipe r : backup) Bukkit.getServer().addRecipe(r);
	    }
    }
    
    public static YamlConfiguration loadDefaultConfig(InputStream craftingFile, YamlConfiguration configuartion, String path) {
    	File yamlPath = new File(path);
    	try (Reader inReader = new InputStreamReader(craftingFile)) {
    		if(Objects.isNull(configuartion)) configuartion = new YamlConfiguration();
    		configuartion.addDefaults(YamlConfiguration.loadConfiguration(inReader));
    		configuartion.options().copyDefaults(true);
    		if(yamlPath.exists() == false) {
    			configuartion.save(yamlPath);
    			configuartion = YamlConfiguration.loadConfiguration(yamlPath);
    		}
            return configuartion;
        } catch (IOException e) {
            e.printStackTrace();
        }
		return configuartion;
    }
}