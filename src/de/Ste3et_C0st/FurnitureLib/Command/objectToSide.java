package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.List;

import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class objectToSide {

	public objectToSide(List<JsonBuilder> objList, Player p, int page){
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
		
		p.sendMessage("§7§m+-------------------------------------------------+");
	}
	
	private int getPage(int i){
        int a = (((i+9)/10)*10);
        return a;
	}
	
}
