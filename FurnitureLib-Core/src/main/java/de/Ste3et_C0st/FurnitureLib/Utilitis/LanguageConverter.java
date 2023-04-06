package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class LanguageConverter {
	
	private final LanguageManager manager;
	private final String lang;
    private static final char COLOR_CHAR = '\u00A7';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile( "(?i)" + String.valueOf( COLOR_CHAR ) + "[0-9A-FK-ORX]" );
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("#[a-zA-Z0-9_]+#", Pattern.CASE_INSENSITIVE);
    
	public LanguageConverter(LanguageManager manager, Plugin plugin) {
		this.lang = manager.getLanguage();
		this.manager = manager;
		this.convertLangFile();
	}
	
    public File getLegacyFolder() {
    	final File folder = new File(FurnitureLib.getInstance().getDataFolder(), "/lang/");
    	if(folder.exists() == Boolean.FALSE) folder.mkdirs();
    	return folder;
    }

    public void convertLangFile() {
        try {
            final File languageFile = new File(this.getLegacyFolder(), this.lang + ".yml");
            if(languageFile.exists() == false) return;
            final YamlConfiguration config = YamlConfiguration.loadConfiguration(languageFile);
            final HashMap<String, String> hash = new HashMap<String, String>();
            config.getConfigurationSection("").getKeys(true).forEach(key -> {
                if (key.startsWith(".")) key = key.replaceFirst(".", "");
                if (config.isString(key)) {
                    String value = config.getString(key);
                    hash.put(key, StringTranslator.transfareVariable(value));
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
                    hash.put(key, StringTranslator.transfareVariable(value.toString()));
                } else {
                    hash.put(key, key.toLowerCase() + " is Missing");
                }
            });
            
            this.saveConvertedFile(this.lang, hash);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
	}
	
	public void saveConvertedFile(String fileName, HashMap<String, String> language) {
		final File file = new File(manager.getLangFolder(), fileName + ".yml");
		final YamlConfiguration configuration = new YamlConfiguration();
		language.entrySet().forEach(entry -> {
			final String key = entry.getKey();
			final String value = entry.getValue();
			if(value.contains("is Missing") == false) {
				if(value.contains("\n")) {
					String[] lines = value.split("\n");
					List<String> exportList = new ArrayList<String>();
					for(String str : lines) {
						exportList.add(serializeLegacyColors(str));
					}
					configuration.set(key, exportList);
				}else {
					configuration.set(key, serializeLegacyColors(value));
					System.out.println(key + ":" + serializeLegacyColors(value));
				}
			}
		});
		
		try {
			configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String transfareVariable(String name) {
		String copyString = name;
		Matcher matcher = VARIABLE_PATTERN.matcher(name);
		while (matcher.find()) {
		      String match = matcher.group();
		      String replace = match.toLowerCase().replaceFirst("#", "<").replace("#", ">").toLowerCase();
		      copyString = copyString.replace(match, replace);
		}
		return copyString;
	}
	
	public static String serializeLegacyColors(String input) {
		if(Objects.isNull(input)) return "";
		if(input.isEmpty()) return "";
		String output = ChatColor.translateAlternateColorCodes('&', input).replaceAll("§m", "<st>").replaceAll("§o", "<i>").replaceAll("§n", "<u>").replaceAll("§l", "<b>").replaceAll("§k", "<obf>");
		Matcher matcher = STRIP_COLOR_PATTERN.matcher(output);
		
		while (matcher.find()) {
            String color = output.substring(matcher.start(), matcher.end());
            ChatColor chatColor = ChatColor.getByChar(color.charAt(1));
            if(Objects.isNull(chatColor)) continue;
            String colorCode = "<" + chatColor.name().toLowerCase() + ">";
            output = output.replaceAll(color, colorCode + "");
            matcher = STRIP_COLOR_PATTERN.matcher(output);
        }
		
		return transfareVariable(output);
	}
	
}
