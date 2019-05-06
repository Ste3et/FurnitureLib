package de.Ste3et_C0st.FurnitureLib.Command;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.EnumWrappers;

import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.Utilitis.MaterialConverter;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class downloadCommand {

	public downloadCommand(CommandSender sender, Command cmd, String arg2, String[] args){
		if(sender instanceof Player){
			if(args.length==2){
				if(args[0].equalsIgnoreCase("download")){
					try{
						if(!command.noPermissions(sender, "furniture.download")) return;
						String name = args[1];
						final URL url = new URL("http://api.dicecraft.de/furniture/download.php");
						sender.sendMessage("§7§m+-------------------§7[§2Download§7]§m--------------------+");
						sender.sendMessage("§6Download startet from: " + name);
						downLoadData(name, url, sender, null);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else if(args.length==3){
				if(args[0].equalsIgnoreCase("download")){
					try{
						if(!command.noPermissions(sender, "furniture.download")) return;
						String name = args[1];
						final URL url = new URL("http://api.dicecraft.de/furniture/download.php");
						sender.sendMessage("§7§m+-------------------§7[§2Download§7]§m--------------------+");
						sender.sendMessage("§6Download startet from: " + name);
						downLoadData(name, url, sender, args[2]);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else{
				command.sendHelp((Player) sender);return;
			}

		}
	}
	
	private void downLoadData(final String name, final URL url, final CommandSender sender, final String s){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					boolean b = true;
					URLConnection connection = (URLConnection) url.openConnection();
					connection.setRequestProperty("User-Agent", "FurnitureMaker/" + FurnitureLib.getInstance().getDescription().getVersion());
					connection.setDoOutput(true);
					
					PrintStream stream = new PrintStream(connection.getOutputStream());
					stream.println("id=" + name);
					stream.println("&spigot=1.14");
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					stream.checkError();
					stream.flush();
					stream.close();
					
					String line = null;
					String config = "";
					String playerName = "";
					String projectName = "";
					
					int i = 0;
					while ((line = reader.readLine()) != null) {
						if(line.equalsIgnoreCase("#NOTEXIST") || line.equalsIgnoreCase("Invalid Page")){
							sender.sendMessage("§cProject Not Found");
						}else{
							if(!line.isEmpty()) {
								switch (i) {
								case 0:config = line;
								case 1:playerName = line;
								case 2:projectName = line;
								}
								i++;
							}
						}
					}
					
					if(config.equals("")){b=false;}
					if(playerName.equals("")){b=false;}
					if(projectName.equals("")){b=false;}
					
					if(b){
						sender.sendMessage("§6You have downloaded: " + projectName);
						sender.sendMessage("§6With the ID: " + name);
						sender.sendMessage("§6Createt from: " + playerName);
						if(s!=null){
							projectName=s;
							sender.sendMessage("§7Saved as Project: §e" + projectName);
						}
						add(config, playerName, projectName, sender);
					}
					connection.getInputStream().close();
					sender.sendMessage("§7§m+------------------------------------------------+");
				}catch(Exception e){
					sender.sendMessage("§cThe FurnitureMaker Downloader have an Exception");
					sender.sendMessage("§cPlease contact the Developer");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	private void add(String config, String playerName, String project, CommandSender sender){
		try{
			byte[] by = Base64.getUrlDecoder().decode(config);
			ByteArrayInputStream bin = new ByteArrayInputStream(by);
			NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
			if(compound.hasKey("lore")) {
				convert(compound, project);
			}else {
				save(compound, project);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void save(NBTTagCompound compound, String project) throws IOException{
		YamlConfiguration file = new YamlConfiguration();
		Reader inReader = new InputStreamReader(FurnitureLib.getInstance().getResource("default.dModel"));
		file.addDefaults(YamlConfiguration.loadConfiguration(inReader));
		file.options().copyDefaults(true);
		file.options().header("------------------------------------  #\n"
				+ "                                      #\n"
				+ "      never touch the system-ID !     #\n"
				+ "                                      #\n"
				+ "------------------------------------  #\n");
		file.options().copyHeader(true);
		NBTTagCompound crafting = compound.getCompound("crafting");
		NBTTagCompound index = crafting.getCompound("index");
		NBTTagCompound lore = crafting.getCompound("lore");
		String systemID = project;
		if(compound.hasKey("system-ID")) systemID = compound.getString("system-ID");
		if(!systemID.equalsIgnoreCase(project)) systemID = project;
		file.set(project + ".displayName", compound.getString("displayName"));
		file.set(project + ".system-ID", systemID);
		file.set(project + ".spawnMaterial", compound.getString("spawnMaterial"));
		file.set(project + ".itemGlowEffect", compound.getBoolean("itemGlowEffect"));
		List<String> loreText = new ArrayList<String>();
		for(Object s : lore.c()){
			loreText.add(lore.getString((String) s));
		}
		file.set(project + ".itemLore", loreText);
		
		file.set(project + ".crafting.disable", crafting.getBoolean("disable"));
		file.set(project + ".crafting.recipe", crafting.getString("recipe"));
		for(Object s : index.c()){
			Material mat = MaterialConverter.getMaterialFromOld(index.getString((String) s));
			file.set(project + ".crafting.index." + ((String) s), mat.name());
		}
		
		if(compound.hasKey("entitys")){
			NBTTagCompound armorStands = compound.getCompound("entitys");
			for(Object s : armorStands.c()){
				String str = armorStands.getString((String) s);
				file.set(project+".projectData.entitys."+ ((String) s), str);
			}
		}
		
		PlaceableSide side = PlaceableSide.TOP;
		if(compound.hasKey("placeAbleSide")){
			side = PlaceableSide.valueOf(compound.getString("placeAbleSide"));
		}
		
		file.set(project+".placeAbleSide", side.toString());
		
		if(compound.hasKey("blockList")){
			NBTTagCompound blocks = compound.getCompound("blockList");
			for(Object s : blocks.c()){
				String str = (String) s;
				if(blocks.hasKey(str)){
					NBTTagCompound block = blocks.getCompound(str);
					file.set(project+".projectData.blockList." + str + ".xOffset", block.getDouble("xOffset"));
					file.set(project+".projectData.blockList." + str + ".yOffset", block.getDouble("yOffset"));
					file.set(project+".projectData.blockList." + str + ".zOffset", block.getDouble("zOffset"));
					if(block.hasKey("material")) {
						String blockData = "minecraft:" + block.getString("material");
						if(block.hasKey("Rotation")) blockData += "[facing=" + block.getString("Rotation") + "]";
						file.set(project+".projectData.blockList." + str + ".blockData", blockData);
					}else if(block.hasKey("blockData")) {
						file.set(project+".projectData.blockList." + str + ".blockData", block.getString("blockData"));
					}
					
				}
			}
		}
		
		if(compound.hasKey("function")) {
			NBTTagCompound stringList = compound.getCompound("function");
			List<String> functions = new ArrayList<String>();
			for(int j = 0; j < stringList.c().size(); j++) {
				functions.add(stringList.getString(j + ""));
			}
			file.set(project + ".projectData.functions", functions);
		}
		file.save(new File("plugins/"+FurnitureLib.getInstance().getName()+"/models/" + project + ".dModel"));
		final PlaceableSide s = side;
		Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () ->{
			try {
				FurnitureLib.getInstance().getProjectManager().registerProeject(project, s);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void convert(NBTTagCompound compound, String project) throws IOException{
		YamlConfiguration file = new YamlConfiguration();
		Reader inReader = new InputStreamReader(FurnitureLib.getInstance().getResource("default.dModel"));
		file.addDefaults(YamlConfiguration.loadConfiguration(inReader));
		file.options().copyDefaults(true);
		file.options().header("------------------------------------  #\n"
				+ "                                      #\n"
				+ "      never touch the system-ID !     #\n"
				+ "                                      #\n"
				+ "------------------------------------  #\n");
		file.options().copyHeader(true);
		
		NBTTagCompound crafting = compound.getCompound("crafting");
		NBTTagCompound index = crafting.getCompound("index");
		NBTTagCompound lore = crafting.getCompound("lore");
		String systemID = project;
		if(compound.hasKey("systemID")) systemID = compound.getString("systemID");
		if(!systemID.equalsIgnoreCase(project)) systemID = project;
		file.set(project + ".displayName", compound.getString("name"));
		file.set(project + ".system-ID", systemID);
		file.set(project + ".spawnMaterial", MaterialConverter.getMaterialFromOld(compound.getString("material")).name());
		file.set(project + ".itemGlowEffect", compound.getBoolean("glow"));
		List<String> loreText = new ArrayList<String>();
		for(Object s : lore.c()){
			loreText.add(lore.getString((String) s));
		}
		file.set(project + ".itemLore", loreText);
		
		file.set(project + ".crafting.disable", crafting.getBoolean("disable"));
		file.set(project + ".crafting.recipe", crafting.getString("recipe"));
		for(Object s : index.c()){
			Material mat = MaterialConverter.getMaterialFromOld(index.getString((String) s));
			file.set(project + ".crafting.index." + ((String) s), mat.name());
		}
		
		if(compound.hasKey("ArmorStands")){
			NBTTagCompound armorStands = compound.getCompound("ArmorStands");
			for(Object s : armorStands.c()){
				String md5 = armorStands.getString(((String) s));
				byte[] by = Base64.getDecoder().decode(md5);
				ByteArrayInputStream bin = new ByteArrayInputStream(by);
				try {
					NBTTagCompound metadata = NBTCompressedStreamTools.read(bin);
					NBTTagCompound inventory = metadata.getCompound("Inventory");
					NBTTagCompound updatetInventory = new NBTTagCompound();
					for(Object object : EnumWrappers.ItemSlot.values()){
						if(!inventory.getString(object.toString()).equalsIgnoreCase("NONE")){
							NBTTagCompound item = MaterialConverter.convertNMSItemStack(inventory.getCompound(object.toString()+""));
							updatetInventory.set(object.toString(), item);
						}else {
							updatetInventory.setString(object.toString(), "NONE");
						}
					}
					metadata.set("Inventory", updatetInventory);
					byte[] out = NBTCompressedStreamTools.toByte(metadata);
					String str = Base64.getEncoder().encodeToString(out);
					file.set(project+".projectData.entitys."+ ((String) s), str);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		PlaceableSide side = PlaceableSide.TOP;
		if(compound.hasKey("PlaceAbleSide")){
			side = PlaceableSide.valueOf(compound.getString("PlaceAbleSide"));
		}
		
		file.set(project+".placeAbleSide", side.toString());
		
		if(compound.hasKey("Blocks")){
			NBTTagCompound blocks = compound.getCompound("Blocks");
			for(Object s : blocks.c()){
				String str = (String) s;
				if(blocks.hasKey(str)){
					NBTTagCompound block = blocks.getCompound(str);
					file.set(project+".projectData.blockList." + str + ".xOffset", block.getDouble("X-Offset"));
					file.set(project+".projectData.blockList." + str + ".yOffset", block.getDouble("Y-Offset"));
					file.set(project+".projectData.blockList." + str + ".zOffset", block.getDouble("Z-Offset"));
					Material materialBlock = MaterialConverter.getMaterialFromOld(block.getString("Type"));
					String blockData = "minecraft:" + materialBlock.name().toLowerCase();
					if(block.hasKey("Rotation")) blockData += "[facing=" + block.getString("Rotation") + "]";
					file.set(project+".projectData.blockList." + str + ".blockData", blockData);
				}
			}
		}
		file.save(new File("plugins/"+FurnitureLib.getInstance().getName()+"/models/" + project + ".dModel"));
		FurnitureLib.getInstance().getProjectManager().registerProeject(project, side);
	}
}
