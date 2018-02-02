package de.Ste3et_C0st.FurnitureLib.ShematicLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Database.CallBack;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.CenterType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class ProjectManager {

	public void loadProjectFiles(){
		List<String> projectList = new ArrayList<String>();
		File folder = new File("plugins/FurnitureLib/Crafting/");
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
									if(!projectList.contains(systemID)) {projectList.add(systemID);}
									int Width = 0, Height = 0, Lentgh = 0;
									boolean silent = false;

									if(configuration.isSet(header + ".Options.ProjectBreakEvent.Silent")){silent = configuration.getBoolean(header + ".Options.ProjectBreakEvent.Silent");}
									p.setSilent(silent);
									
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
				ex.printStackTrace();
				return;
			}
			FurnitureLib.getInstance().getFurnitureManager().setFinishLoading(true);
		}
		
		if(projectList.size()>1){
			String str = "";
			Collections.sort(projectList);
			for(String s : projectList) {str += s + ",";}
			str = str.substring(0, str.length() - 1);
			FurnitureLib.getInstance().send("FurnitureLib load Models("+projectList.size()+"): " + str);
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
