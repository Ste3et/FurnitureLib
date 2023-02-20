package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;

public class LimitationInforamtion { 
	
	private final String type;
	private final int max, amount;
	
	public LimitationInforamtion(String type, int max, int amount) {
		this.type = type;
		this.max = max;
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public int getMax() {
		return max;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isCanceld() {
		if(isInfinite()) return false;
		return amount + 1 > max;
	}
	
	public boolean isInfinite() {
		return max < 1;
	}
	
	public void sendMessage(Player player, Project project, int amount) {
		String name = isCanceld() ? ".reached" : ".info";
		String messageName = "message.limit." + type + name;
		player.sendMessage(LanguageManager.getInstance().getString(messageName,
				new StringTranslator("amount", Integer.toString(amount)), 
				new StringTranslator("size", Integer.toString(getMax())),
				new StringTranslator("project", project.getDisplayName()),
				new StringTranslator("world", player.getWorld().getName())
		));
	}
	
}
