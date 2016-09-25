package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class config
{
  public JavaPlugin plugin;
  public String fileName;
  public String path = "plugins/";
  
  public config(Plugin plugin)
  {
    this.path = "plugins/" + plugin.getName();
  }
  
  public FileConfiguration createConfig(String name, String Folder)
  {
    if (!name.endsWith(".yml")) {
      name = name + ".yml";
    }
    File f = new File(path + Folder + "/");
    if (!f.exists()) {
      f.mkdirs();
    }
    File arena = new File(path + Folder, name);
    if (!arena.exists()) {
      try
      {
        arena.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    return YamlConfiguration.loadConfiguration(arena);
  }
  
  public void saveConfig(String name, FileConfiguration config, String Folder)
  {
    if (!name.endsWith(".yml")) {
      name = name + ".yml";
    }
    File arena = new File(path + Folder, name);
    try
    {
      config.save(arena);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public FileConfiguration getConfig(String name, String Folder)
  {
    if (!name.endsWith(".yml")) {
      name = name + ".yml";
    }
    createConfig(name, Folder);
    File arena = new File(path + Folder, name);
    return YamlConfiguration.loadConfiguration(arena);
  }
  
  public boolean ExistMaps(String folder){
	  if(new File(path + folder).exists()){
		  return true; 
	  }
	  return false;
  }
  
  public boolean ExistConfig(String folder, String name){
	  if(new File(path + folder, name).exists()){
		  return true; 
	  }
	  return false;
  }
  
  public void deleteFolder(File folder)
  {

    
    File[] files = folder.listFiles();
    if(files!=null) { //some JVMs return null for empty dirs
        for(File f: files) {
            if(f.isDirectory()) {
                deleteFolder(f);
            } else {
                f.delete();
            }
        }
    }
    folder.delete();
  }
}
