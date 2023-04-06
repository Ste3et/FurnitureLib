package de.Ste3et_C0st.FurnitureLib.Utilitis;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.async.ChunkData;
import de.Ste3et_C0st.FurnitureLib.async.WorldData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class autoConverter {

    public static void modelConverter(CommandSender sender) {
        if (!FurnitureLib.getInstance().isAutoFileUpdater()) return;
        if (!FurnitureLib.isNewVersion()) return;
        FurnitureLib.getInstance().send("==========================================");
        FurnitureLib.getInstance().send("Auto-Converter: ");

        File folder = new File("plugins/" + FurnitureLib.getInstance().getName() + "/Crafting");
        if (folder.exists()) {
            for (File f : folder.listFiles()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
                YamlConfiguration newConfig = new YamlConfiguration();
                Reader inReader = new InputStreamReader(FurnitureLib.getInstance().getResource("default.dModel"));
                newConfig.addDefaults(YamlConfiguration.loadConfiguration(inReader));
                newConfig.options().copyDefaults(true);
                
                final List<String> headerConfig = Arrays.asList(
                    	"------------------------------------  #",
                    	"                                      #",
                    	"      never touch the system-ID !     #",
                    	"                                      #",
                    	"------------------------------------  #"
                    );
        				
        		if(FurnitureLib.getVersionInt() > 15) {	
        			newConfig.options().setHeader(headerConfig);
        		}else {
        			final String headerString = String.join("\n", headerConfig.toArray(String[]::new));
        			newConfig.options().copyHeader(true);
        			newConfig.options().header(headerString);
        		}
                
                String header = getHeader(f.getName().replace(".yml", ""), config);
                String systemID = config.getString(header + ".system-ID");
                String name = config.getString(header + ".name");
                try {
                    String material = config.getString(header + ".material");
                    boolean glow = config.getBoolean(header + ".glow");
                    List<String> stringList = new ArrayList<String>();
                    if (config.isList(header + ".lore")) stringList = config.getStringList(header + ".lore");
                    Material mat = MaterialConverter.getMaterialFromOld(material);
                    if (mat == null) return;

                    newConfig.set(header + ".system-ID", systemID);
                    newConfig.set(header + ".displayName", name);
                    newConfig.set(header + ".itemGlowEffect", glow);
                    newConfig.set(header + ".spawnMaterial", mat.name());
                    newConfig.set(header + ".itemLore", stringList);

                    if (config.contains(header + ".creator")) {
                        newConfig.set(header + ".creator", UUID.fromString(config.getString(header + ".creator")).toString());
                    }

                    if (config.contains(header + ".PlaceAbleSide")) {
                        newConfig.set(header + ".placeAbleSide", PlaceableSide.valueOf(config.getString(header + ".PlaceAbleSide")).name());
                    }

                    if (config.contains(header + ".crafting.recipe")) {
                        newConfig.set(header + ".crafting.recipe", config.getString(header + ".crafting.recipe"));
                        newConfig.set(header + ".crafting.disable", config.getBoolean(header + ".crafting.disable"));
                        config.getConfigurationSection(header + ".crafting.index").getKeys(false).forEach(letter -> {
                            Material cM = MaterialConverter.getMaterialFromOld(config.getString(header + ".crafting.index." + letter));
                            newConfig.set(header + ".crafting.index." + letter, cM.name());
                        });
                    }

                    if (config.contains(header + ".ProjectModels.ArmorStands")) {
                        config.getConfigurationSection(header + ".ProjectModels.ArmorStands").getKeys(false).forEach(letter -> {
                            String md5 = config.getString(header + ".ProjectModels.ArmorStands." + letter);
                            byte[] by = Base64.getDecoder().decode(md5);
                            ByteArrayInputStream bin = new ByteArrayInputStream(by);
                            try {
                                NBTTagCompound metadata = NBTCompressedStreamTools.read(bin);
                                NBTTagCompound inventory = metadata.getCompound("Inventory");
                                NBTTagCompound updatedInventory = new NBTTagCompound();
                                for (Object object : EnumWrappers.ItemSlot.values()) {
                                    if (!inventory.getString(object.toString()).equalsIgnoreCase("NONE")) {
                                        NBTTagCompound item = MaterialConverter.convertNMSItemStack(inventory.getCompound(object.toString()));
                                        updatedInventory.set(object.toString(), item);
                                    } else {
                                        updatedInventory.setString(object.toString(), "NONE");
                                    }
                                }
                                metadata.set("Inventory", updatedInventory);
                                byte[] out = NBTCompressedStreamTools.toByte(metadata);
                                newConfig.set(header + ".projectData.entities." + letter, Base64.getEncoder().encodeToString(out));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    if (config.contains(header + ".ProjectModels.Block")) {
                        if (config.isConfigurationSection(header + ".ProjectModels.Block")) {
                            config.getConfigurationSection(header + ".ProjectModels.Block").getKeys(false).forEach(letter -> {
                                double x = config.getDouble(header + ".ProjectModels.Block." + letter + ".X-Offset");
                                double y = config.getDouble(header + ".ProjectModels.Block." + letter + ".Y-Offset");
                                double z = config.getDouble(header + ".ProjectModels.Block." + letter + ".Z-Offset");
                                Material materialBlock = MaterialConverter.getMaterialFromOld(config.getString(header + ".ProjectModels.Block." + letter + ".Type"));
                                newConfig.set(header + ".projectData.blockList." + letter + ".xOffset", x);
                                newConfig.set(header + ".projectData.blockList." + letter + ".yOffset", y);
                                newConfig.set(header + ".projectData.blockList." + letter + ".zOffset", z);
                                newConfig.set(header + ".projectData.blockList." + letter + ".material", materialBlock.name());

                                String str = "minecraft:" + materialBlock.name().toLowerCase();
                                if (config.contains(header + ".ProjectModels.Block." + letter + ".Rotation"))
                                    str += "[facing=" + config.get(header + ".ProjectModels.Block." + letter + ".Rotation") + "]";
                                newConfig.set(header + ".projectData.blockList." + letter + ".blockData", str);
                            });
                        }
                    }
                    newConfig.save(new File("plugins/" + FurnitureLib.getInstance().getName() + "/models/" + f.getName().replace(".yml", ".dModel")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sender.sendMessage("convert finish");
            folder.renameTo(new File("plugins/" + FurnitureLib.getInstance().getName() + "/CraftingOld"));
            return;
        }
        sender.sendMessage("§2Model Files already converted");
        return;
    }

    public static void databaseConverter(CommandSender sender, String table) {
        if (!FurnitureLib.getInstance().isAutoFileUpdater()) {
            if (FurnitureConfig.getFurnitureConfig().isSync()) {
                FurnitureLib.getInstance().getSQLManager().loadALL();
            } else {
            	//asnyc here
            	SchedularHelper.runLater(() -> {
            		Bukkit.getWorlds().forEach(world -> {
            			FurnitureLib.getInstance().getSQLManager().getDatabase().loadWorldAsync(world).thenAccept(worldData -> {
            				worldData.loadData(world, world.getLoadedChunks());
            				FurnitureManager.getInstance().getAsyncWorldFiles().add(worldData);
            			});
                    });
            	}, 20 * 10, false);
            }
            return;
        }
        FurnitureLib.getInstance().getSQLManager().convert(sender, table);
    }

    public static String getHeader(String fileName, YamlConfiguration config) {
        try {
            return (String) config.getConfigurationSection("").getKeys(false).toArray()[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return fileName;
        }
    }
}
