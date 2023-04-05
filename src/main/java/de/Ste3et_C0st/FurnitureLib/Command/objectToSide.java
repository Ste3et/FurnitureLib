package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.function.BiFunction;

public class objectToSide {

	private static final BiFunction<List<Component>, Integer, Integer> maxPage = (list, objects) -> {
		if(list.isEmpty() == false && objects > 0) {
			return (int) Math.ceil((double) list.size() / (double) objects);
		}
		return 0;
	};
	
	public objectToSide(List<Component> objList, CommandSender sender, Integer page, String command) {
		this(objList, sender, page, command, 10);
	}
	
    public objectToSide(List<Component> objList, CommandSender sender, Integer currentPage, String command, int objects) {
        this(objList, sender, currentPage, command, objects, maxPage.apply(objList, objects));
    }
	
    public objectToSide(List<Component> objList, CommandSender sender, Integer page, String command, int objects, int maxPage) {
        if (page == 0) page = 1;
        final int skip = page * objects - objects;

        if (page > maxPage) {
        	LanguageManager.send(sender, "message.SideNotFound");
        	LanguageManager.send(sender, "message.SideNavigation", new StringTranslator("max", maxPage + ""));
            return;
        }
        
        final String a = maxPage < 10 ? "0" + maxPage : String.valueOf(maxPage);
        final String b = page < 10 ? "" + page : String.valueOf(page);

        sender.sendMessage("§7§m+--------------------------------------------+§8[§e" + b + "§8/§a" + a + "§8]");
        
        objList.stream().skip(skip).limit(objects).forEach(component -> {
        	LanguageManager.sendChatMessage(sender, component);
        });

        final String prevCommand = page > 1 ? command + " " + (page - 1) : "";
        final String nextCommand = page < maxPage ? command + " " + (page + 1) : "";
        final NamedTextColor prevColor = page > 1 ? NamedTextColor.RED : NamedTextColor.GRAY;
        final NamedTextColor nextColor = page < maxPage ? NamedTextColor.GREEN : NamedTextColor.GRAY;
        final Component navigation = Component.text("+--------------------------------------------+").color(NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH)
        			.decoration(TextDecoration.STRIKETHROUGH, false)
        			.append(Component.text("[").color(NamedTextColor.GRAY))
        			.append(Component.text("«").color(prevColor).clickEvent(ClickEvent.runCommand(prevCommand)))
        			.append(Component.text("|").color(NamedTextColor.GRAY)
        			.append(Component.text("»")).color(nextColor).clickEvent(ClickEvent.runCommand(nextCommand)))
        			.append(Component.text("]").color(NamedTextColor.GRAY));
        LanguageManager.sendChatMessage(sender, navigation);
    }
}
