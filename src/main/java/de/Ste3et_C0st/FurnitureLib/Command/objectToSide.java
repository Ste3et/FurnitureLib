package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.ChatComponentWrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class objectToSide {

	public objectToSide(List<ComponentBuilder> objList, CommandSender sender, Integer page, String command) {
		this(objList, sender, page, command, 10);
	}
	
    public objectToSide(List<ComponentBuilder> objList, CommandSender sender, Integer page, String command, int objects) {
        if (page == 0) page = 1;
        int min = page * objects - objects;
        int max = page * objects;
        double d = objList.size();
        double k = Math.ceil(d / objects);
        int maxPage = (int) k;

        String a = "";
        String b = "";
        if (maxPage < 10) {
            a += "0" + maxPage;
        } else {
            a = maxPage + "";
        }
        if (page < 10) {
            b += "0" + page;
        } else {
            b = page + "";
        }

        if (page > maxPage) {
        	LanguageManager.send(sender, "message.SideNotFound");
        	LanguageManager.send(sender, "message.SideNavigation", new StringTranslator("max", maxPage + ""));
            return;
        }

        sender.sendMessage("§7§m+--------------------------------------------+§8[§e" + b + "§8/§a" + a + "§8]");

        int j = 0;
        for (Object obj : objList) {
            if (j >= min && j < max) {
                if (obj instanceof String) {
                	sender.sendMessage((String) obj);
                } else if (obj instanceof ComponentBuilder) {
                	if(sender instanceof Player) {
                		ChatComponentWrapper.sendChatComponent(Player.class.cast(sender), ((ComponentBuilder) obj).create());
                	}else {
                		sender.sendMessage(TextComponent.toLegacyText(((ComponentBuilder) obj).create()));
                	}
                }
            }
            j++;
        }

        String prevCommand = null;
        String nextCommand = null;
        String prevColor = "§c";
        String nextColor = "§a";

        if (page >= 2) {
            prevCommand = command + " " + (page - 1);
        } else {
            prevColor = "§7";
        }

        if (page + 1 > maxPage) {
            nextColor = "§7";
        } else {
            nextCommand = command + " " + (page + 1);
        }

        ComponentBuilder builder = new ComponentBuilder("§7§m+--------------------------------------------+§8[§e");
        if (Objects.nonNull(prevCommand)) {
            builder.append(prevColor + "«").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, prevCommand));
        } else {
            builder.reset().append("§7«");
        }

        builder.append("§8/§a");

        if (Objects.nonNull(nextCommand)) {
            builder.append(nextColor + "»").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, nextCommand));
        } else {
            builder.reset().append("§7»");
        }
        
        if (sender instanceof Player) {
            ChatComponentWrapper.sendChatComponent(Player.class.cast(sender), builder.append("§8]").create());
        } else {
            sender.sendMessage(TextComponent.toLegacyText(builder.create()));
        }
    }
    
    public objectToSide(List<BaseComponent[]> objList, CommandSender sender, Integer currentPage, String command, int objects, int maxPage) {
        String a = "";
        String b = "";
        if(maxPage == 0) maxPage = 1;
        if (maxPage < 10) {
            a += "0" + maxPage;
        } else {
            a = maxPage + "";
        }
        if (currentPage < 10) {
            b += "0" + (currentPage + 1);
        } else {
            b = (currentPage + 1) + "";
        }

        if (currentPage > maxPage) {
        	LanguageManager.send(sender, "message.SideNotFound");
        	LanguageManager.send(sender, "message.SideNavigation", new StringTranslator("max", maxPage + ""));
            return;
        }

        sender.sendMessage("§7§m+--------------------------------------------+§8[§e" + b + "§8/§a" + a + "§8]");

        for (BaseComponent[] obj : objList) {
        	if(Objects.nonNull(obj)) {
        		if (sender instanceof Player) {
            		ChatComponentWrapper.sendChatComponent(Player.class.cast(sender), obj);
            	}else {
                    sender.sendMessage(TextComponent.toLegacyText(obj));
                }
        	}
        }

        String prevCommand = null;
        String nextCommand = null;
        String prevColor = "§c";
        String nextColor = "§a";

        if (currentPage > 0) {
            prevCommand = command + " " + (currentPage - 1);
        } else {
            prevColor = "§7";
        }

        if (currentPage + 1 < maxPage) {
            nextCommand = command + " " + (currentPage + 1);
        } else {
        	nextColor = "§7";
        }

        ComponentBuilder builder = new ComponentBuilder("§7§m+--------------------------------------------+§8[§e");
        if (Objects.nonNull(prevCommand)) {
            builder.append(prevColor + "«").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, prevCommand));
        } else {
            builder.reset().append("§7«");
        }

        builder.append("§8/§a");

        if (Objects.nonNull(nextCommand)) {
            builder.append(nextColor + "»").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, nextCommand));
        } else {
            builder.reset().append("§7»");
        }
       
        if (sender instanceof Player) {
            ChatComponentWrapper.sendChatComponent(Player.class.cast(sender), builder.append("§8]").create());
        } else {
            sender.sendMessage(TextComponent.toLegacyText(builder.create()));
        }
    }

}
