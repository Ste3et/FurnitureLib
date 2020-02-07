package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;

import java.util.List;

public class objectToSide {

    public objectToSide(List<ComponentBuilder> objList, Player p, Integer page, String command) {
        if (page == 0) page = 1;
        int objects = 10;
        int min = page * objects - objects;
        int max = page * objects;
        int maxPage = getPage(objList.size()) / objects;

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
            p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.SideNotFound"));
            p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.SideNavigation").replaceAll("#MAX#", maxPage + ""));
            return;
        }

        p.sendMessage("§7§m+--------------------------------------------+§8[§e" + b + "§8/§a" + a + "§8]");

        int j = 0;
        for (Object obj : objList) {
            if (j >= min && j < max) {
                if (obj instanceof String) {
                    p.sendMessage((String) obj);
                } else if (obj instanceof ComponentBuilder) {
                    p.spigot().sendMessage(((ComponentBuilder) obj).create());
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
        if (prevCommand != null) {
            builder.append(prevColor + "«").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, prevCommand));
        } else {
            builder.append("§7«");
        }

        builder.append("§8/§a");

        if (nextCommand != null) {
            builder.append(nextColor + "»").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, nextCommand));
        } else {
            builder.append("§7«");
        }
        p.spigot().sendMessage(builder.append("§8]").create());
    }

    private int getPage(int i) {
		return (((i + 9) / 10) * 10);
    }

}
