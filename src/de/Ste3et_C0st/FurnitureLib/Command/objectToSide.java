package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.List;

import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder.ClickAction;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class objectToSide {

	public objectToSide(List<JsonBuilder> objList, Player p, Integer page, String command){
		if(page==0)page=1;
		int objects = 10;
		int min = page*objects-objects;
		int max = page*objects;
		int maxPage = getPage(objList.size())/objects;
		
		String a = "";
		String b = "";
		if(maxPage<10){a+="0"+maxPage;}else{a=maxPage+"";}
		if(page<10){b+="0"+page;}else{b=page+"";}
		
		if(page>maxPage){
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("SideNotFound"));
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("SideNavigation").replaceAll("#MAX#", maxPage + ""));
			return;
		}
		
		p.sendMessage("§7§m+--------------------------------------------+§8[§e" + b + "§8/§a" + a + "§8]");
		
		int j = 0;
		for(Object obj : objList){
			if(j>=min&&j<max){
				if(obj instanceof String){p.sendMessage((String) obj);}
				else if(obj instanceof JsonBuilder){((JsonBuilder) obj).sendJson(p);}
			}
			j++;
		}
		
		String prevCommand = null;
		String nextCommand = null;
		String prevColor = "§c";
		String nextColor = "§a";
		
		if(page>=2){
			prevCommand = command + " " + (page - 1);
		}else{
			prevColor = "§7";
		}
		
		if(page + 1 > maxPage){
			nextColor = "§7";
		}else{
			nextCommand = command + " " + (page + 1);
		}
		
		new JsonBuilder("§7§m+--------------------------------------------+§8[§e")
						.withText(prevColor + "«").withClickEvent(ClickAction.RUN_COMMAND, prevCommand)
						.withText("§8/§a")
						.withText(nextColor + "»").withClickEvent(ClickAction.RUN_COMMAND, nextCommand)
						.withText("§8]").sendJson(p);
	}
	
	private int getPage(int i){
        int a = (((i+9)/10)*10);
        return a;
	}
	
}
