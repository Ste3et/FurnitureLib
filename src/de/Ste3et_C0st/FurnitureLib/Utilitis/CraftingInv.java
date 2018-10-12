package de.Ste3et_C0st.FurnitureLib.Utilitis;

import de.Ste3et_C0st.FurnitureLib.Crafting.CraftingFile;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

public class CraftingInv implements Listener
{
  public Plugin plugin;
  public List<Player> playerList = new ArrayList<Player>();
  public boolean editable = false;
  public Project project = null;
  
  public CraftingInv(Plugin pl) {
	  Bukkit.getPluginManager().registerEvents(this, pl);
	  plugin=pl;
  }
 
  private List<Integer> slots = new ArrayList<Integer>();
  
public void openCrafting(final Player p, Project project, boolean editable)
  {
	if(!editable && project.getCraftingFile().isEnable()){ p.sendMessage("This Furniture has not recipe"); return;}
	this.project = project;
	this.editable = editable;
    Inventory inv = Bukkit.createInventory(null, 54, project.getName());

    ShapedRecipe recipe = project.getCraftingFile().getRecipe();
    final ItemStack is = recipe.getResult();
    is.setAmount(1);
    
    ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE);
    stack.setDurability((short) 8);
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName("§c");
    stack.setItemMeta(meta);
    for(int i = 0; i<inv.getSize();i++) {
    	inv.setItem(i, stack);
    }
    
    inv.setItem(25, is);
    slots.add(25);
    
    final String[] recipeShape = recipe.getShape();
	final Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();
	for (int j = 0; j < recipeShape.length; j++)
	{
		for (int k = 0; k < recipeShape[j].length(); k++)
		{
			final ItemStack item = ingredientMap.get(recipeShape[j].toCharArray()[k]);
			int i = (9 + ((9 * j) + k)) + 1;
			slots.add(i);
			if (item == null)
			{
				inv.setItem(i, new ItemStack(Material.AIR));
				continue;
			}
			item.setAmount(1);
			
			p.sendMessage(i + "");
			inv.setItem(i, item);
		}
	}
	p.openInventory(inv);
    playerList.add(p);
  }
  
  @EventHandler
  private void onClick(InventoryClickEvent e){
	  if(playerList.contains((Player) e.getWhoClicked())){
		  if(!editable){e.setCancelled(true);return;}
		  e.getWhoClicked().sendMessage(e.getSlot() + "");
	  		if(!this.slots.contains(e.getRawSlot())) {
	  			InventoryView view = e.getView();
	  			if(e.getClickedInventory() == null) {
	  				e.setCancelled(true);
	  				return;
	  			}
	  			if(e.getClickedInventory().equals(view.getTopInventory())) {
	  				e.setCancelled(true);
	  			}
	  		}
	  }
  }
  
  
List<String> stringList = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "X");
@EventHandler
  private void onClose(InventoryCloseEvent e){
	  if(playerList.contains(e.getPlayer())){
		  if(editable){
			  String s = "";
			  HashMap<String, Material> materialList = new HashMap<String, Material>();
			  ItemStack result = e.getInventory().getItem(25);
			  int l = 0;
			  for(int j = 0; j<3;j++) {
				  for(int k = 0; k<3;k++) {
					  int i = (9 + ((9 * j) + k)) + 1;
					  ItemStack stack = e.getInventory().getItem(i);
					  if(stack!=null){
						  s+=stringList.get(l);
						  materialList.put(stringList.get(l), stack.getType());
					  }else{
						  s+=stringList.get(10);
						  materialList.put(stringList.get(10), Material.AIR);
					  }
					  l++;
				  }
				  s+=",";
			  }
			  CraftingFile file = this.project.getCraftingFile();
			  if(s.equalsIgnoreCase("xxx,xxx,xxx")){
				  //file.setCraftingDisabled(true);
				  file.removeCrafting(file.getItemstack());
				  YamlConfiguration conf = YamlConfiguration.loadConfiguration(file.getFilePath());
				  setItem(result, conf, file.getFileHeader());
				  save(conf, file.getFilePath());
			  }else{
				  file.removeCrafting(file.getItemstack());
				  //file.setCraftingDisabled(false);
				  YamlConfiguration conf = YamlConfiguration.loadConfiguration(file.getFilePath());
				  conf.set(file.getFileHeader() + ".crafting.recipe", "");
				  conf.set(file.getFileHeader() + ".crafting.index", "");
				  save(conf, file.getFilePath());
				  setItem(result, conf, file.getFileHeader());
				  conf.set(file.getFileHeader() + ".crafting.recipe", s);
				  for(String str : materialList.keySet()){
					  conf.set(file.getFileHeader() + ".crafting.index." + str, materialList.get(str).name());
				  }
				  save(conf, file.getFilePath());
				  file.setFileConfiguration(conf);
				  file.loadCrafting(file.getFileName());
			  }
			  e.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("CraftingEdit"));
		  }
		  e.getInventory().clear();
		  playerList.remove(e.getPlayer());}
	 }
  
  public void save(YamlConfiguration yml, File file){
	  try {
		  yml.save(file);
	  } catch (IOException e1) {e1.printStackTrace();}
  }
  
  @SuppressWarnings("deprecation")
public void setItem(ItemStack stack, YamlConfiguration conf, String header){
	  if(stack!=null){
		  if(!stack.getType().equals(Material.AIR)){
			  String name = "";
			  Material material = Material.MONSTER_EGG;
			  byte data = 0;
			  List<String> lore = new ArrayList<String>();
			  material = stack.getType();
			  data = (byte) stack.getDurability();
			  if(stack.hasItemMeta()){
				  ItemMeta meta = stack.getItemMeta();
				  name = meta.getDisplayName();
				  if(meta.hasLore()){
					  for(String s : lore){
						  if(!HiddenStringUtils.hasHiddenString(s)){
							  lore.add(s);
						  }
					  }
				  }
			  }
			  conf.set(header + ".name", name);
			  conf.set(header + ".material", material.getId() + ":" + data);
			  conf.set(header + ".lore", lore);
		  }
	  }
  }
}


