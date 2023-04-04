package de.Ste3et_C0st.FurnitureLib.Utilitis;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.utility.MinecraftReflection;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageManager {

    private static LanguageManager instance;
    private String lang;
    private Plugin plugin;
    private HashMap<String, String> hash = new HashMap<>();
    private HashMap<String, List<String>> invHashList = new HashMap<>();
    private HashMap<String, Material> invMatList = new HashMap<>();
    private HashMap<String, String> invStringList = new HashMap<>();
    private HashMap<String, Short> invShortList = new HashMap<>();
    private AdventureHandling handling = null;
    
    public LanguageManager(Plugin plugin, String lang) {
        instance = this;
        this.lang = lang;
        this.plugin = plugin;
        this.loadLanguageConfig();
        if (FurnitureLib.isNewVersion()) {
            loadNewManageInv();
        } else {
            oldManageInv();
        }
	}
    
    private void initHandling() {
    	if(FurnitureLib.isPaper() == false) {
    		handling = new AdventureHandling(plugin);
    	}
    }
    
    public File getLangFolder() {
    	final File folder = new File(FurnitureLib.getInstance().getDataFolder(), "/language/");
    	if(folder.exists() == Boolean.FALSE) folder.mkdirs();
    	return folder;
    }

    private void loadLanguageConfig() {
        try {
            if (this.lang == null || this.lang.isEmpty()) lang = "EN_en";
            
            final String selectetLanguage = Objects.nonNull(plugin.getResource("language/" + lang + ".yml")) ? lang : "EN_en";
            final File languageFile = new File(this.getLangFolder(), selectetLanguage + ".yml");
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(languageFile);
            
            config.addDefaults(YamlConfiguration.loadConfiguration(FurnitureLib.getInstance().loadStream("language/" + selectetLanguage + ".yml")));
            config.options().copyDefaults(true);
            config.options().copyHeader(true);
            config.getConfigurationSection("").getKeys(true).forEach(key -> {
                if (key.startsWith(".")) key = key.replaceFirst(".", "");
                if (config.isString(key)) {
                    String value = config.getString(key);
                    hash.put(key.toLowerCase(), StringTranslator.transfareVariable(value));
                } else if (config.isList(key)) {
                    StringBuilder value = new StringBuilder();
                    List<String> stringList = config.getStringList(key);
                    int end = (stringList.size() - 1);
                    for (String a : stringList) {
                        if (stringList.indexOf(a) != end) {
                            value.append(StringTranslator.transfareVariable(a)).append("\n");
                        } else {
                            value.append(StringTranslator.transfareVariable(a));
                        }
                    }
                    hash.put(key.toLowerCase(), StringTranslator.transfareVariable(value.toString()));
                } else {
                    hash.put(key.toLowerCase(), key.toLowerCase() + " is Missing");
                }
            });
            
            config.save(languageFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
	}

    private void loadNewManageInv() {
    	try {
    		final File inventoryFile = new File(FurnitureLib.getInstance().getDataFolder(), "manageInv.yml");
        	final YamlConfiguration inventory = YamlConfiguration.loadConfiguration(inventoryFile);
        	inventory.addDefaults(YamlConfiguration.loadConfiguration(FurnitureLib.getInstance().loadStream("manageInv.yml")));
        	inventory.options().copyDefaults(true);
        	inventory.save(inventoryFile);
        	
            for (String str : inventory.getConfigurationSection("inv.mode").getKeys(false)) {
                invHashList.put(str, inventory.getStringList("inv.mode." + str + ".Text"));
                invMatList.put(str, Material.valueOf(inventory.getString("inv.mode." + str + ".Material").toUpperCase()));
                invStringList.put(str, inventory.getString("inv.mode." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.mode." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.event").getKeys(false)) {
                invMatList.put(str, Material.valueOf(inventory.getString("inv.event." + str + ".Material").toUpperCase()));
                invStringList.put(str, inventory.getString("inv.event." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.event." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.player").getKeys(false)) {
                invMatList.put(str, Material.valueOf(inventory.getString("inv.player." + str + ".Material").toUpperCase()));
                invStringList.put(str, inventory.getString("inv.player." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.player." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.controller").getKeys(false)) {
                invMatList.put(str, Material.valueOf(inventory.getString("inv.controller." + str + ".Material").toUpperCase()));
                invStringList.put(str, inventory.getString("inv.controller." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.controller." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.admin").getKeys(false)) {
                invMatList.put(str, Material.valueOf(inventory.getString("inv.admin." + str + ".Material").toUpperCase()));
                invStringList.put(str, inventory.getString("inv.admin." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.admin." + str + ".SubID"));
                invHashList.put(str, inventory.getStringList("inv.admin." + str + ".Text"));
            }
            
            invStringList.put("manageInvName", inventory.getString("inv.manageInvName"));
            invStringList.put("playerAddInvName", inventory.getString("inv.playerAddInvName"));
            invStringList.put("playerRemoveInvName", inventory.getString("inv.playerRemoveInvName"));
            invStringList.put("playerSetInvName", inventory.getString("inv.playerSetInvName"));
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    }

    private void oldManageInv() {
    	try {
    		final File inventoryFile = new File(FurnitureLib.getInstance().getDataFolder(), "manageInv.yml");
        	final YamlConfiguration inventory = YamlConfiguration.loadConfiguration(inventoryFile);
        	inventory.addDefaults(YamlConfiguration.loadConfiguration(FurnitureLib.getInstance().loadStream("manageInvOld.yml")));
        	inventory.options().copyDefaults(true);
        	inventory.save(inventoryFile);

            for (String str : inventory.getConfigurationSection("inv.mode").getKeys(false)) {
                invHashList.put(str, inventory.getStringList("inv.mode." + str + ".Text"));
                invMatList.put(str, MaterialConverter.convertMaterial(inventory.getInt("inv.mode." + str + ".Material"), (byte) 0));
                invStringList.put(str, inventory.getString("inv.mode." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.mode." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.event").getKeys(false)) {
                invMatList.put(str, MaterialConverter.convertMaterial(inventory.getInt("inv.event." + str + ".Material"), (byte) 0));
                invStringList.put(str, inventory.getString("inv.event." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.event." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.player").getKeys(false)) {
                invMatList.put(str, MaterialConverter.convertMaterial(inventory.getInt("inv.player." + str + ".Material"), (byte) 0));
                invStringList.put(str, inventory.getString("inv.player." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.player." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.controller").getKeys(false)) {
                invMatList.put(str, MaterialConverter.convertMaterial(inventory.getInt("inv.controller." + str + ".Material"), (byte) 0));
                invStringList.put(str, inventory.getString("inv.controller." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.controller." + str + ".SubID"));
            }
            for (String str : inventory.getConfigurationSection("inv.admin").getKeys(false)) {
                invMatList.put(str, MaterialConverter.convertMaterial(inventory.getInt("inv.admin." + str + ".Material"), (byte) 0));
                invStringList.put(str, inventory.getString("inv.admin." + str + ".String"));
                invShortList.put(str, (short) inventory.getInt("inv.admin." + str + ".SubID"));
                invHashList.put(str, inventory.getStringList("inv.admin." + str + ".Text"));
            }
            invStringList.put("manageInvName", inventory.getString("inv.manageInvName"));
            invStringList.put("playerAddInvName", inventory.getString("inv.playerAddInvName"));
            invStringList.put("playerRemoveInvName", inventory.getString("inv.playerRemoveInvName"));
            invStringList.put("playerSetInvName", inventory.getString("inv.playerSetInvName"));
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }

    public String getString(String a) {
        a = a.toLowerCase();
        if (hash.isEmpty()) return "§cHash is empty";
        if (!hash.containsKey(a)) return "§fkey not found: §5" + a;
        String b = hash.get(a);
        return FurnitureLib.getVersionInt() > 15 ? applyHexColors(hash.get(a)) : ChatColor.translateAlternateColorCodes('&', b);
    }
    
    private final Pattern hexPattern = Pattern.compile("#([a-fA-F0-9]){6}");
    
    public String applyHexColors(String message){
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
        	String color = message.substring(matcher.start(), matcher.end());
        	message = message.replace(color, ChatColor.of(color) + "");
            matcher = hexPattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Deprecated
    public String getString(String key, StringTranslator... stringTranslators) {
        String a = getString(key);
        if (stringTranslators != null) {
            for (StringTranslator trans : stringTranslators) {
                if (trans.getKey() != null && trans.getValue() != null) {
                    a = a.replaceAll(trans.oldKey(), trans.getValue());
                    a = a.replaceAll(trans.newKey(), trans.getValue());
                }
            }
        }
        return a;
    }

    public List<String> getStringList(String a) {
        if (!invHashList.containsKey(a)) {
            return null;
        }
        List<String> b = invHashList.get(a);
        int i = 0;
        for (String str : b) {
            b.set(i, ChatColor.translateAlternateColorCodes('&', str));
            i++;
        }
        return b;
    }

    public void addText(YamlConfiguration configuration) {
        try {
        	final File lang = new File(getLangFolder(), this.lang);
            final YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
            conf.addDefaults(configuration);
            conf.options().copyDefaults(true);
            conf.save(lang);
            this.loadLanguageConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendString(CommandSender sender, String key, StringTranslator ... stringTranslators) {
    	sender.sendMessage(getString(key, stringTranslators));
    }
    
    public static void send(CommandSender sender, String key, StringTranslator ... stringTranslators) {
    	LanguageManager.getInstance().sendMessage(sender, key, stringTranslators);
    }
    
    
    public void sendMessage(CommandSender sender, String key, StringTranslator ... stringTranslators) {
    	final String rawString = LanguageConverter.serializeLegacyColors(this.getString(key, stringTranslators));
    	final TagResolver[] tags = getTagsArray(Arrays.asList(stringTranslators));
		final Component returnMessage = MiniMessage.miniMessage().deserialize(rawString, tags);
		
		if(FurnitureLib.getVersionInt() < 16) {
			final String legacyString = LegacyComponentSerializer.legacySection().serialize(returnMessage);
			sender.sendMessage(legacyString);
			return;
		}
		
		if(Objects.isNull(handling)) {
			sender.sendMessage(returnMessage);
		}else {
			handling.sendMessage(sender, returnMessage);
		}
    }
    
    public TagResolver[] getTagsArray(List<StringTranslator> stringTranslaters){
		HashSet<TagResolver> tags = getTags(stringTranslaters);
		return tags.toArray(new TagResolver[tags.size()]);
	}
    
	public HashSet<TagResolver> getTags(List<StringTranslator> stringTranslaters){
		HashSet<TagResolver> hashSet = new HashSet<TagResolver>();
		stringTranslaters.stream().filter(Objects::nonNull).forEach(entry -> {
			hashSet.add(entry.getPlaceHolder());
		});
		return hashSet;
	}
    
    public String getName(String a) {
        String b = invStringList.get(a);
        return ChatColor.translateAlternateColorCodes('&', b);
    }

    public Short getShort(String a) {
        return invShortList.get(a);
    }

    public Material getMaterial(String a) {
        return invMatList.get(a);
    }
    
    public String getLanguage() {
    	return this.lang;
    }
    
    public static LanguageManager getInstance() {
        return instance;
    }
    
    public void close() {
    	if(this.handling != null) this.handling.close();
    }
}