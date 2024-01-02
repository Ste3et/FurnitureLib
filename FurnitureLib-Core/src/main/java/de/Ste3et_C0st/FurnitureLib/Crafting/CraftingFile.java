package de.Ste3et_C0st.FurnitureLib.Crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.MaterialConverter;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SchedularHelper;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    
	public  final String systemID;
    public  final File filePath;
    private final String name;
    private final String header;
    private ShapedRecipe recipe;
    private boolean isDisable, useItemStackObject = false, enabledModel = false;
    private PlaceableSide side = null;
    
    public CraftingFile(final String name,final YamlConfiguration fileConfiguration) {
        this.name = name;
        if (Objects.isNull(this.name) || Objects.isNull(fileConfiguration)) {
        	FurnitureLib.debug("problems to load " + name, 10);
        	this.filePath = null;
        	this.header = null;
        	this.enabledModel = false;
        	this.systemID = null;
            return;
        }
        
        this.filePath = new File(getPath(name));
        this.header = getHeader(fileConfiguration);
        this.enabledModel = fileConfiguration.getBoolean(header + ".enabled", true);
        this.systemID = fileConfiguration.getString(header + ".system-ID", name);
        
        if(!this.enabledModel) {return;}
        
        try {
            if (Objects.nonNull(Class.forName("org.bukkit.NamespacedKey"))) {
                loadCrafting(name, fileConfiguration);
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

    public String getHeader(final YamlConfiguration configuration) {
        try {
            return (String) configuration.getConfigurationSection("").getKeys(false).toArray()[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return this.name;
        }
    }

    public void rename(Component component) {
        if (Objects.isNull(component)) return;
        ItemStack stack = getRecipe().getResult();
        FurnitureLib.getInstance().getServerFunction().displayName(stack, BungeeComponentSerializer.get().serialize(component));
    }


    public void loadCrafting(final String name, final YamlConfiguration configuration) {
        try {
            this.isDisable = configuration.getBoolean(header + ".crafting.disable", false);
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), this.name.toLowerCase()); // <-- Key
            String[] fragements = returnFragment(configuration);
            AtomicInteger materialCount = new AtomicInteger(0);
            this.recipe = new ShapedRecipe(key, returnResult(name, configuration)).shape(fragements[0], fragements[1], fragements[2]);
            
            returnMaterial(name, fragements, configuration).entrySet().stream().filter(c -> c.getValue() != Material.AIR).forEach(c -> {
                this.recipe.setIngredient(c.getKey(), c.getValue());
                materialCount.addAndGet(1);
            });
            
            if (!isDisable) {
                if (materialCount.get() > 0) {
                	SchedularHelper.runTask(() -> {
                		if(isKeyisKeyRegistered(key) == false) {
                    		Bukkit.getServer().addRecipe(this.recipe);
                    	}
                	}, true);
                }
            }
            getPlaceAbleSide(configuration);
            loadFunction(configuration);
        } catch (Exception e) {
        	System.err.println(this.header + " is a corrupted model File !");
            e.printStackTrace();
        }
    }

    public List<JsonObject> loadFunction(final YamlConfiguration configuration) {
        List<JsonObject> jsonList = new ArrayList<>();
        if (configuration.contains(header + ".projectData.functions")) {
            List<String> stringList = configuration.getStringList(header + ".projectData.functions");
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

    public PlaceableSide getPlaceAbleSide(YamlConfiguration configuration) {
        this.side = PlaceableSide.valueOf(configuration.getString(header + ".PlaceAbleSide", "TOP").toUpperCase());
        return this.side;
    }
    
    public void saveName(Component component) {
    	YamlConfiguration configuration = YamlConfiguration.loadConfiguration(this.getFilePath());
    	configuration.set(header + ".displayName", MiniMessage.miniMessage().serialize(component));
    	try {
			configuration.save(this.getFilePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void setName(Component component) {
        ItemStack stack = getRecipe().getResult();
        FurnitureLib.getInstance().getServerFunction().displayName(stack, BungeeComponentSerializer.get().serialize(component));
        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), this.name.toLowerCase());
        ShapedRecipe recipe = new ShapedRecipe(key, stack).shape(this.getRecipe().getShape());
        this.recipe.getIngredientMap().entrySet().stream().filter(entry -> Objects.nonNull(entry.getValue())).forEach(entry -> recipe.setIngredient(entry.getKey(), entry.getValue().getData()));
        this.recipe = recipe;
        this.saveName(component);
        if (!isDisable) Bukkit.getServer().addRecipe(this.recipe);
    }

    private ItemStack returnResult(final String name,final YamlConfiguration configuration) {
    	if(configuration.contains(header + ".spawnItemStack")) {
    		try {
    			ItemStack stack = configuration.getItemStack(header + ".spawnItemStack");
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
    	
    	
        Material material = FurnitureLib.getInstance().getDefaultSpawnMaterial();
        if (configuration.contains(header + ".spawnMaterial")) {
            String str = configuration.getString(header + ".spawnMaterial");
            if (!str.equalsIgnoreCase("383")) {
            	material = Material.getMaterial(str);
            }
        }
        
        final String displayName = configuration.getString(header + (FurnitureLib.isNewVersion() ? ".displayName" : ".name"), header);
        final ItemStack itemStack = new ItemStack(material);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final List<String> loreText = new ArrayList<String>();
        
        FurnitureLib.getInstance().getServerFunction().setDisplayName(itemMeta, BungeeComponentSerializer.get().serialize(LanguageManager.getInstance().stringConvert("<i:false>" + displayName)));
        
        if (itemMeta.hasLore()) loreText.addAll(itemMeta.getLore());
        
        if (configuration.contains(header + ".custommodeldata")) {
        	try{
        		itemMeta.setCustomModelData(configuration.getInt(header + ".custommodeldata"));
        	}catch (Exception e) {/* Method = setCustomModelData didn't exist (ignore Exception) */}
        }

        if(FurnitureLib.getVersionInt() > 13) {
        	itemMeta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), "model"), PersistentDataType.STRING, getSystemID());
		}else {
			loreText.add(HiddenStringUtils.encodeString(getSystemID()));
		}
        
        if(configuration.contains(header + ".itemLore") && configuration.isList(header + ".itemLore")) {
        	final List<BaseComponent[]> componentList = Lists.newArrayList();
        	configuration.getStringList(header + ".itemLore").stream().forEach(loreString -> loreText.add("<i:false>" + loreString));
        	loreText.stream().map(LanguageManager.getInstance()::stringConvert).map(BungeeComponentSerializer.get()::serialize).forEach(componentList::add);
        	FurnitureLib.getInstance().getServerFunction().setLore(itemMeta, componentList);
        }
        
        if (configuration.getBoolean(header + ".unbreakable", false)) itemMeta.setUnbreakable(true);
        if (configuration.contains(header + ".durability") && itemMeta instanceof Damageable) ((Damageable) itemMeta).setDamage(configuration.getInt(header + ".durability", 0));

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(1);
        return itemStack;
    }

    private String[] returnFragment(final YamlConfiguration configuartion) {
        return configuartion.getString(header + ".crafting.recipe", "").split(",");
    }

    private HashMap<Character, Material> returnMaterial(String s, String[] recipe, final YamlConfiguration configuration) {
        List<Character> stringList = returnCharacters(recipe, configuration);
        HashMap<Character, Material> materialHash = new HashMap<Character, Material>();
        stringList.forEach(letter -> {
            String part = configuration.getString(header + ".crafting.index." + letter, "AIR");
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

    private List<Character> returnCharacters(final String[] recipe, final YamlConfiguration configuartion) {
        List<Character> stringList = new ArrayList<>();
        for (final String str : returnFragment(configuartion)) {
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
    	final File yamlPath = new File(path);
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