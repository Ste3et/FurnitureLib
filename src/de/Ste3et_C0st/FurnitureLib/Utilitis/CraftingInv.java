package de.Ste3et_C0st.FurnitureLib.Utilitis;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class CraftingInv implements Listener
{
  static Plugin plugin;
  public static List<Player> playerList = new ArrayList<Player>();
  
  public CraftingInv(Plugin pl) {
	  Bukkit.getPluginManager().registerEvents(this, pl);
	  plugin=pl;
  }

 
public void openCrafting(final Player p, Project project)
  {
	if(project.getCraftingFile().isEnable()){ p.sendMessage("This Furniture has not recipe"); return;}
	
    HumanEntity clicker = p;
    InventoryView invView = clicker.openWorkbench(null, true);
    final CraftingInventory inv = (CraftingInventory)invView.getTopInventory();
    ShapedRecipe recipe = project.getCraftingFile().getRecipe();
    final ItemStack is = recipe.getResult();
    is.setAmount(1);
    inv.setResult(is);
    
    final String[] recipeShape = recipe.getShape();
	final Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();
	for (int j = 0; j < recipeShape.length; j++)
	{
		for (int k = 0; k < recipeShape[j].length(); k++)
		{
			final ItemStack item = ingredientMap.get(recipeShape[j].toCharArray()[k]);
			if (item == null)
			{
				continue;
			}
			item.setAmount(0);
			invView.getTopInventory().setItem(j * 3 + k + 1, item);
		}
	}
	
	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
    {
      public void run()
      {
        inv.setResult(is);
        p.updateInventory();
      }
    }, 2L);
	
      playerList.add(p);
  }
  
  @EventHandler
  private void onClick(InventoryClickEvent e){if(playerList.contains((Player) e.getWhoClicked())){e.setCancelled(true);}}
  
  @EventHandler
  private void onClose(InventoryCloseEvent e){if(playerList.contains(e.getPlayer())){playerList.remove(e.getPlayer());}}
}
