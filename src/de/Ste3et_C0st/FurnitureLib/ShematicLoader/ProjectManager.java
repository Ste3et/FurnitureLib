package de.Ste3et_C0st.FurnitureLib.ShematicLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class ProjectManager {

	public void loadProjectFiles(){
		String s = "";
		File folder = new File("plugins/FurnitureLib/Crafting/");
		File folder2 = new File("plugins/FurnitureLib/plugin/DiceEditor");
		List<File> deleteList = new ArrayList<File>();
		if(folder2.exists()){
			if(!folder.exists()) folder.mkdir();
			File[] array = folder2.listFiles();
			if(array!=null){
				for(File file : array){
					if(!file.exists()) continue;
					if(!file.isFile()) continue;
	 				String str = file.getName();
					if(!str.endsWith(".yml")) str += ".yml";
					System.out.println("Old Project: " + str + " found");
					try {
						Files.copy(file, new File(folder, str));
						deleteList.add(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				for(File file : deleteList){
					file.deleteOnExit();
				}
			}
		}
		
		if(folder.exists()){
			try{
			for(File file : folder.listFiles()){
				if(file!=null){
					if(file.exists()){
						if(file.isFile()){
							try {
								YamlConfiguration configuration = new YamlConfiguration();
								configuration.load(file);
								String name = file.getName().replaceAll(".yml", "");
								String header = getHeader(configuration, name);
								if(configuration.contains(header + ".ProjectModels") || configuration.contains(header + ".ProjectModels.Block")){
									PlaceableSide side = PlaceableSide.TOP;
									String systemID = configuration.getString(header + ".system-ID");
									if(configuration.isSet(header + ".PlaceAbleSide")){side = PlaceableSide.valueOf(configuration.getString(header + ".PlaceAbleSide"));}
									Project p = new Project(systemID, FurnitureLib.getInstance(), new FileInputStream(file), side, ProjectLoader.class).setEditorProject(true);
									s += systemID + ",";
									int Width = 0, Height = 0, Lentgh = 0;
									
									if(configuration.isConfigurationSection(header+".ProjectModels.Block")){
										
										int minWitdh = 0, maxWidth = 0, maxHeight = 0, minHeight = 0, maxLentgh = 0, minLentgh = 0;
										for(String str : configuration.getConfigurationSection(header+".ProjectModels.Block").getKeys(false)){
											double x = configuration.getDouble(header+".ProjectModels.Block." + str + ".X-Offset");
											double y = configuration.getDouble(header+".ProjectModels.Block." + str + ".Y-Offset");
											double z = configuration.getDouble(header+".ProjectModels.Block." + str + ".Z-Offset");
											if(x > maxWidth) maxWidth = (int) x;
											if(y > maxHeight) maxHeight = (int) y;
											if(z > maxLentgh) maxLentgh = (int) z;
											if(x < minWitdh) minWitdh = (int) x;
											if(y < minHeight) minHeight = (int) y;
											if(z < minLentgh) minLentgh = (int) z;
										}
										minWitdh = Math.abs(minWitdh);
										minHeight = Math.abs(minHeight);
										minLentgh = Math.abs(minLentgh);
										
										Lentgh = minWitdh + maxWidth + 1;
										Height = minHeight + maxHeight + 1;
										Width = minLentgh + maxLentgh + 1;
									}
									p.setSize(Width, Height, Lentgh, CenterType.RIGHT);
								}
							} catch (Exception e) {e.printStackTrace();}
						}
					}
				}
			}}catch(NullPointerException ex){
				return;
			}
		}
		FurnitureLib.getInstance().registerPluginFurnitures(FurnitureLib.getInstance());
		if(s.length()>1){
			String str = s.substring(0, s.length()-1);
			FurnitureLib.getInstance().send("FurnitureLib load Proejcts("+StringUtils.countMatches(s, ",")+"): " + str);
		}else{
			FurnitureLib.getInstance().send("If you want to install more models look at here: http://dicecraft.de/furniture/models.php");
		}
	}
	
	public String getHeader(YamlConfiguration file, String fileName){
		try{
			return (String) file.getConfigurationSection("").getKeys(false).toArray()[0];
		}catch(ArrayIndexOutOfBoundsException ex){
			return fileName;
		}
	}
	
	public void registerProeject(String name, PlaceableSide side) throws FileNotFoundException{
		File file = new File("plugins/FurnitureLib/Crafting/", name+".yml");
		InputStream stream = new FileInputStream(file);
		Project pro = new Project(name, FurnitureLib.getInstance(), stream, side, ProjectLoader.class);
		pro.setEditorProject(true);
		pro.setModel(stream);
	}
}
