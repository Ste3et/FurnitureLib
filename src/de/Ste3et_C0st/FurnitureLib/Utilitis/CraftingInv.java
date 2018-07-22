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

 
public void openCrafting(final Player p, Project project, boolean editable)
  {
	if(!editable && project.getCraftingFile().isEnable()){ p.sendMessage("This Furniture has not recipe"); return;}
	this.project = project;
	this.editable = editable;
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
			item.setAmount(1);
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
  private void onClick(InventoryClickEvent e){
	  if(playerList.contains((Player) e.getWhoClicked())){
		  if(!editable){e.setCancelled(true);return;}
	  		if(e.getSlotType().equals(SlotType.RESULT)){
	  			if(e.getCurrentItem()==null){
		  			e.setCancelled(true);
		  			ItemStack stack = e.getCurrentItem();
		  			stack.setAmount(1);
		  			e.getInventory().setItem(e.getSlot(), stack);
		  			return;
	  			}else{
		  			e.setCancelled(true);
		  			ItemStack stack = e.getCursor();
		  			stack.setAmount(1);
		  			e.getInventory().setItem(e.getSlot(), stack);
		  			return;
	  			}
	  		}
	  }
  }
  
  
  List<String> stringList = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "X");
  @SuppressWarnings("deprecation")
@EventHandler
  private void onClose(InventoryCloseEvent e){
	  if(playerList.contains(e.getPlayer())){
		  if(editable){
			  String s = "";
			  HashMap<String, MaterialData> materialList = new HashMap<String, MaterialData>();
			  ItemStack result = null;
			  for(int i = 0; i<10;i++){
				  if(i!=0){
					  ItemStack stack = e.getInventory().getItem(i);
					  if(stack!=null){
						  s+=stringList.get(i);
						  materialList.put(stringList.get(i), stack.getData());
					  }else{
						  s+=stringList.get(10);
						  materialList.put(stringList.get(10), new MaterialData(Material.AIR));
					  }
					switch (i) {
						case 3: s+=",";break;
						case 6: s+=",";break;
						default:break;
					}
				  }else{
					  result = e.getInventory().getItem(i);
				  }
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
					  MaterialData data = materialList.get(str);
					  String l = "";
					  if(data.getItemType().equals(Material.AIR)){
						  l = "0";
					  }else{
						  l = data.getItemType().getId() + ":" + data.getData();
					  }
					  conf.set(file.getFileHeader() + ".crafting.index." + str, l);
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
		  if(project.isEditorProject()){
			  Files.copy(file.toPath(), new File("plugins/FurnitureLib/plugin/DiceEditor/" + file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
		  }
	  } catch (IOException e1) {e1.printStackTrace();}
  }
  
  @SuppressWarnings("deprecation")
public void setItem(ItemStack stack, YamlConfiguration conf, String header){
	  if(stack!=null){
		  if(!stack.getType().equals(Material.AIR)){
			  String name = "";
			  Material material = FurnitureLib.getInstance().getDefaultSpawnMaterial();
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
