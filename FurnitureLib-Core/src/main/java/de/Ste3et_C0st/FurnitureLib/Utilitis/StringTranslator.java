package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class StringTranslator {

    private String key, value;
    private Component component;
    
    private static final Pattern VARIABLE_PATTERN_OLD = Pattern.compile("#[a-zA-Z0-9_]+#", Pattern.CASE_INSENSITIVE);
    private static final Pattern VARIABLE_PATTERN_NEW = Pattern.compile("<[a-zA-Z0-9_]+>", Pattern.CASE_INSENSITIVE);
    
    public StringTranslator(String key, String value) {
        this.key = key.toLowerCase().replaceFirst("#", "").replace("#", "");
        this.value = LanguageManager.serializeLegacyColors(value);
    }
    
	public StringTranslator(String key, Component component) {
		this.key = key.toLowerCase().replaceFirst("#", "").replace("#", "");
		this.component = component;
	}

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
    
    public static Pattern getOldPattern() {
    	return VARIABLE_PATTERN_OLD;
    }
    
    public static Pattern getNewPattern() {
    	return VARIABLE_PATTERN_NEW;
    }
    
    public String oldKey() {
    	return "#" + this.key + "#";
    }
    
    public String newKey() {
    	return "<" + this.key + ">";
    }
    
	public static String transfareVariable(String name) {
		String copyString = name;
		Matcher matcher = VARIABLE_PATTERN_OLD.matcher(name);
		while (matcher.find()) {
		      String match = matcher.group();
		      String replace = match.toLowerCase().replaceFirst("#", "<").replace("#", ">").toLowerCase();
		      copyString = copyString.replace(match, replace);
		}
		return copyString;
	}

	public TagResolver getPlaceHolder() {
		return Placeholder.component(key, getComponent());
	}
	
	public Component getComponent() {
		return Objects.nonNull(component) ? component : MiniMessage.miniMessage().deserialize(value);
	}
}
