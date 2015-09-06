package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class JsonBuilder {
 
    public enum ClickAction {
        RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
    }
    public enum HoverAction {
        SHOW_TEXT
    }
 
    private List<String> extras = new ArrayList<String>();
 
    public JsonBuilder(String... text) {
        for(String extra : text)
            parse(extra);
    }
 
    public JsonBuilder parse(String text) {
           String regex = "[&§]{1}([a-fA-Fl-oL-O0-9]){1}";
           text = text.replaceAll(regex, "§$1");
           if(!Pattern.compile(regex).matcher(text).find()) {
              withText(text);
              return this;
           }
           String[] words = text.split(regex);
 
           int index = words[0].length();
           for(String word : words) {
               try {
                   if(index != words[0].length())
               withText(word).withColor("§"+text.charAt(index - 1));
               } catch(Exception e){}
               index += word.length() + 2;
           }
           return this;
       }
 
    public JsonBuilder withText(String text) {
        extras.add("{text:\"" + text + "\"}");
        return this;
    }
 
    public JsonBuilder withColor(ChatColor color) {
        String c = color.name().toLowerCase();
        addSegment(color.isColor() ? "color:" + c : c + ":true");
        return this;
    }
 
    public JsonBuilder withColor(String color) {
        while(color.length() != 1) color = color.substring(1).trim();
        withColor(ChatColor.getByChar(color));
        return this;
    }
 
    public JsonBuilder withClickEvent(ClickAction action, String value) {
        addSegment("clickEvent:{action:" + action.toString().toLowerCase()
                + ",value:\"" + value + "\"}");
        return this;
    }
 
    public JsonBuilder withHoverEvent(HoverAction action, String value) {
        addSegment("hoverEvent:{action:" + action.toString().toLowerCase()
                + ",value:\"" + value + "\"}");
        return this;
    }
 
    private void addSegment(String segment) {
        String lastText = extras.get(extras.size() - 1);
        lastText = lastText.substring(0, lastText.length() - 1)
                + ","+segment+"}";
        extras.remove(extras.size() - 1);
        extras.add(lastText);
    }
 
    public String toString() {
        if(extras.size() <= 1) return extras.size() == 0 ? "{text:\"\"}" : extras.get(0);
        String text = extras.get(0).substring(0, extras.get(0).length() - 1) + ",extra:[";
        extras.remove(0);;
        for (String extra : extras)
            text = text + extra + ",";
        text = text.substring(0, text.length() - 1) + "]}";
        return text;
    }
 
    public void sendJson(Player p) {
    	try {
			PacketContainer container = new PacketContainer(PacketType.Play.Server.CHAT);
			container.getChatComponents().write(0, WrappedChatComponent.fromJson(toString()));
			ProtocolLibrary.getProtocolManager().sendServerPacket(p, container);
		} catch (Exception e) {e.printStackTrace();}
    }
}