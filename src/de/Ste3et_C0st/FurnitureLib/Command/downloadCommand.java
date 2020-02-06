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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.EnumWrappers;

import de.Ste3et_C0st.FurnitureLib.Command.command;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelFileLoader;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTBase;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagInt;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagString;
import de.Ste3et_C0st.FurnitureLib.Utilitis.MaterialConverter;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;

public class downloadCommand extends iCommand{

	public downloadCommand(String subCommand, String ...args) {
		super(subCommand);
	}


	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof Player){
			if(args.length==2){
				try{
					if(!hasCommandPermission(sender)) return;
					String name = args[1];
					final URL url = new URL("http://api.dicecraft.de/furniture/download.php");
					sender.sendMessage("§7§m+-------------------§7[§2Download§7]§m--------------------+");
					sender.sendMessage("§6Download started from: " + name);
					downLoadData(name, url, sender, null);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if(args.length==3){
				try{
					if(!hasCommandPermission(sender)) return;
					String name = args[1];
					final URL url = new URL("http://api.dicecraft.de/furniture/download.php");
					sender.sendMessage("§7§m+-------------------§7[§2Download§7]§m--------------------+");
					sender.sendMessage("§6Download started from: " + name);
					downLoadData(name, url, sender, args[2]);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				command.sendHelp((Player) sender);return;
			}

		}
	}
	
	private String normalize(String input) {
		String output = input;
		output = output.replaceAll("ä", "ae");
		output = output.replaceAll("ö", "oe");
		output = output.replaceAll("ü", "ue");
		output = output.replaceAll("ß", "sz");
		output = output.replaceAll("Ä", "Ae");
		output = output.replaceAll("Ö", "Oe");
		output = output.replaceAll("Ü", "Ue");
		output = output.replaceAll("ẞ", "Sz");
		return output;
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
					stream.println("&spigot=1." + FurnitureLib.getVersionInt());
					
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
								case 2:projectName = normalize(line);
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
						sender.sendMessage("§6Created from: " + playerName);
						if(s!=null){
							projectName=s;
							sender.sendMessage("§7Saved as Project: §e" + projectName);
						}
						add(config, playerName, projectName, sender);
					}
					connection.getInputStream().close();
					sender.sendMessage("§7§m+------------------------------------------------+");
				}catch(Exception e){
					sender.sendMessage("§cThe FurnitureMaker Downloader has an exception");
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
				if(FurnitureLib.isNewVersion()) {
					convert(compound, project);
				}else{
					addOld(compound, project);
				}
			}else {
				save(compound, project);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void addOld(NBTTagCompound compound, String project){
		try{
			YamlConfiguration file = new YamlConfiguration();
			NBTTagCompound crafting = compound.getCompound("crafting");
			NBTTagCompound index = crafting.getCompound("index");
			NBTTagCompound lore = compound.getCompound("lore");
			String systemID = project;
			String fileHeader = project.replace(".", "");
			if(compound.hasKey("systemID")) systemID = compound.getString("systemID");
			if(!systemID.equalsIgnoreCase(project)) systemID = project;
			file.set(fileHeader + ".name", compound.getString("name"));
			file.set(fileHeader + ".system-ID", systemID);
			
			
			
			NBTBase base = compound.get("material");
			if(NBTTagString.class.isInstance(base)) {
				file.set(fileHeader + ".material", compound.getString("material"));
			}else if(NBTTagInt.class.isInstance(base)) {
				file.set(fileHeader + ".material", compound.getInt("material"));
			}else {
				file.set(fileHeader + ".material", FurnitureLib.getInstance().getDefaultSpawnMaterial());
			}
			
			file.set(fileHeader + ".glow", compound.getBoolean("glow"));
			List<String> loreText = new ArrayList<String>();
			for(Object s : lore.c()) loreText.add(lore.getString((String) s));
			file.set(fileHeader + ".lore", loreText);
			
			file.set(fileHeader + ".crafting.disable", crafting.getBoolean("disable"));
			file.set(fileHeader + ".crafting.recipe", crafting.getString("recipe"));
			for(Object s : index.c()){
				file.set(fileHeader + ".crafting.index." + ((String) s), index.getString((String) s));
			}
			if(compound.hasKey("ArmorStands")){
				NBTTagCompound armorStands = compound.getCompound("ArmorStands");
				for(Object s : armorStands.c()){
					file.set(fileHeader+".ProjectModels.ArmorStands."+ ((String) s), armorStands.getString((String) s) + "");
				}
			}
			
			PlaceableSide side = PlaceableSide.TOP;
			if(compound.hasKey("PlaceAbleSide")){
				side = PlaceableSide.valueOf(compound.getString("PlaceAbleSide"));
			}
			
			file.set(fileHeader+".PlaceAbleSide", side.toString());
			
			if(compound.hasKey("Blocks")){
				NBTTagCompound blocks = compound.getCompound("Blocks");
				for(Object s : blocks.c()){
					String str = (String) s;
					if(blocks.hasKey(str)){
						NBTTagCompound block = blocks.getCompound(str);
						file.set(fileHeader+".ProjectModels.Block." + str + ".X-Offset", block.getDouble("X-Offset"));
						file.set(fileHeader+".ProjectModels.Block." + str + ".Y-Offset", block.getDouble("Y-Offset"));
						file.set(fileHeader+".ProjectModels.Block." + str + ".Z-Offset", block.getDouble("Z-Offset"));
						file.set(fileHeader+".ProjectModels.Block." + str + ".Type", block.getString("Type"));
						file.set(fileHeader+".ProjectModels.Block." + str + ".Data", block.getInt("Data"));
					}
				}
			}
			
			file.save(new File("plugins/"+FurnitureLib.getInstance().getName()+"/Crafting/" + project + ".yml"));
			Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () ->{
				ModelFileLoader.loadModelFile(new File("plugins/"+FurnitureLib.getInstance().getName()+"/Crafting/" + project + ".yml"));
			});
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
		String header = project.replace(".", "");
		file.set(header + ".displayName", compound.getString("displayName"));
		file.set(header + ".system-ID", systemID);
		file.set(header + ".spawnMaterial", compound.getString("spawnMaterial"));
		file.set(header + ".itemGlowEffect", compound.getBoolean("itemGlowEffect"));
		List<String> loreText = new ArrayList<String>();
		for(Object s : lore.c()){
			loreText.add(lore.getString((String) s));
		}
		file.set(header + ".itemLore", loreText);
		
		file.set(header + ".crafting.disable", crafting.getBoolean("disable"));
		file.set(header + ".crafting.recipe", crafting.getString("recipe"));
		for(Object s : index.c()){
			Material mat = MaterialConverter.getMaterialFromOld(index.getString((String) s));
			file.set(header + ".crafting.index." + ((String) s), mat.name());
		}
		
		if(compound.hasKey("entities")){
			NBTTagCompound armorStands = compound.getCompound("entities");
			for(Object s : armorStands.c()){
				String str = armorStands.getString((String) s);
				file.set(header+".projectData.entities."+ ((String) s), str);
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
					String blockData = block.hasKey("material") ? "minecraft:" + block.getString("material") : block.getString("blockData");
					if(block.hasKey("material")) {
						if(block.hasKey("Rotation")) blockData += "[facing=" + block.getString("Rotation") + "]";
					}
					if(!blockData.isEmpty() && !blockData.contains("air")) {
						file.set(header+".projectData.blockList." + str + ".blockData", blockData);
						file.set(header+".projectData.blockList." + str + ".xOffset", block.getDouble("xOffset"));
						file.set(header+".projectData.blockList." + str + ".yOffset", block.getDouble("yOffset"));
						file.set(header+".projectData.blockList." + str + ".zOffset", block.getDouble("zOffset"));
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
			file.set(header + ".projectData.functions", functions);
		}
		file.save(new File("plugins/"+FurnitureLib.getInstance().getName()+"/models/" + project + ".dModel"));
		Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () ->{
			ModelFileLoader.loadModelFile(new File("plugins/"+FurnitureLib.getInstance().getName()+"/models/" + project + ".dModel"));
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
		String header = project.replace(".", "");
		NBTTagCompound crafting = compound.getCompound("crafting");
		NBTTagCompound index = crafting.getCompound("index");
		NBTTagCompound lore = crafting.getCompound("lore");
		String systemID = project;
		if(compound.hasKey("systemID")) systemID = compound.getString("systemID");
		if(!systemID.equalsIgnoreCase(project)) systemID = project;
		file.set(header + ".displayName", compound.getString("name"));
		file.set(header + ".system-ID", systemID);
		
		NBTBase base = compound.get("material");
		if(NBTTagString.class.isInstance(base)) {
			file.set(header + ".spawnMaterial", MaterialConverter.getMaterialFromOld(compound.getString("material")).name());
		}else if(NBTTagInt.class.isInstance(base)) {
			file.set(header + ".spawnMaterial", MaterialConverter.getMaterialFromOld(compound.getInt("material") + "").name());
		}else {
			file.set(header + ".spawnMaterial", FurnitureLib.getInstance().getDefaultSpawnMaterial().name());
		}
		
		
		file.set(header + ".itemGlowEffect", compound.getBoolean("glow"));
		List<String> loreText = new ArrayList<String>();
		for(Object s : lore.c()){
			loreText.add(lore.getString((String) s));
		}
		file.set(header + ".itemLore", loreText);
		
		file.set(header + ".crafting.disable", crafting.getBoolean("disable"));
		file.set(header + ".crafting.recipe", crafting.getString("recipe"));
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
					NBTTagCompound updatedInventory = new NBTTagCompound();
					for(Object object : EnumWrappers.ItemSlot.values()){
						if(!inventory.getString(object.toString()).equalsIgnoreCase("NONE")){
							NBTTagCompound item = MaterialConverter.convertNMSItemStack(inventory.getCompound(object.toString()+""));
							updatedInventory.set(object.toString(), item);
						}else {
							updatedInventory.setString(object.toString(), "NONE");
						}
					}
					metadata.set("Inventory", updatedInventory);
					byte[] out = NBTCompressedStreamTools.toByte(metadata);
					String str = Base64.getEncoder().encodeToString(out);
					file.set(project+".projectData.entities."+ ((String) s), str);
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
					file.set(header+".projectData.blockList." + str + ".xOffset", block.getDouble("X-Offset"));
					file.set(header+".projectData.blockList." + str + ".yOffset", block.getDouble("Y-Offset"));
					file.set(header+".projectData.blockList." + str + ".zOffset", block.getDouble("Z-Offset"));
					Material materialBlock = MaterialConverter.getMaterialFromOld(block.getString("Type"));
					String blockData = "minecraft:" + materialBlock.name().toLowerCase();
					if(block.hasKey("Rotation")) blockData += "[facing=" + block.getString("Rotation") + "]";
					file.set(header+".projectData.blockList." + str + ".blockData", blockData);
				}
			}
		}
		file.save(new File("plugins/"+FurnitureLib.getInstance().getName()+"/models/" + project + ".dModel"));
		ModelFileLoader.loadModelFile(new File("plugins/"+FurnitureLib.getInstance().getName()+"/models/" + project + ".dModel"));
	}
}
