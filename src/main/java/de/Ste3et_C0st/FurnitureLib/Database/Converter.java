package de.Ste3et_C0st.FurnitureLib.Database;

import com.comphenix.protocol.wrappers.EnumWrappers;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.MaterialConverter;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Task;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Converter {

    private Database database;
    private int stepSize = 250, offset = 0, dataFiles = 0, step = 1, stepComplete = 0;
    private List<String> objID = new ArrayList<String>();
    
    public Converter(Database database) {
        this.database = database;
    }

    @SuppressWarnings("unchecked")
    public static NBTTagCompound convertPacketItemStack(NBTTagCompound compound) {
        compound.c().stream().filter(entity -> entity != null).forEach(entity -> {
            NBTTagCompound metadata = compound.getCompound((String) entity);
            NBTTagCompound inventory = metadata.getCompound("Inventory");
            NBTTagCompound updatedInventory = new NBTTagCompound();
            EnumSet.allOf(EnumWrappers.ItemSlot.class).stream().forEach(slot -> {
                if (!inventory.getString(slot.name()).equalsIgnoreCase("NONE")) {
                    NBTTagCompound item = MaterialConverter.convertNMSItemStack(inventory.getCompound(slot.name()));
                    updatedInventory.set(slot.name(), item);
                } else {
                    updatedInventory.setString(slot.name(), "NONE");
                }
            });
            metadata.set("Inventory", updatedInventory);
            compound.set((String) entity, metadata);
        });
        return compound;
    }

    private boolean checkIfTableExist(String str) {
    	if(str.isEmpty()) return false;
        boolean b = false;
        try (Connection con = database.getConnection(); ResultSet rs = database.getConnection().getMetaData().getTables(null, null, str, null)) {
            b = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public void startConvert(CommandSender sender, String tableName) {
        String sql = "SELECT COUNT(*) FROM `"+ tableName +"`";
        FurnitureLib.debug("Try to convert database Table: " + tableName, 10);
        if (this.checkIfTableExist(tableName)) {
        	FurnitureLib.debug("FurnitureLib: Found table to convert (" + tableName +")", 10);
            try (Connection con = database.getConnection(); ResultSet rs = con.createStatement().executeQuery(sql)) {
                if (rs.next()) {
                    do {
                        if (Objects.nonNull(rs)) {
                            this.dataFiles = rs.getInt(1);
                            if (dataFiles != 0) {
                                stepComplete = (int) Math.ceil(((double) dataFiles) / ((double) stepSize));
                                sender.sendMessage("Convert of " + dataFiles + " from " + database.getType().name());
                                sender.sendMessage("It takes a while " + stepComplete + " Steps");
                                convert(sender, tableName);
                            }
                        }
                    } while (rs.next());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        	FurnitureLib.debug("FurnitureLib: Found no able to convert", 1);
        	FurnitureConfig.getFurnitureConfig().setFileUpdater(false);
            FurnitureLib.getInstance().saveConfig();
            FurnitureLib.getInstance().getSQLManager().loadALL();
        }
    }

    private void convert(CommandSender sender, String tableName) {
        Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), () -> {
            try (Connection con = database.getConnection(); ResultSet rs = con.createStatement().executeQuery("SELECT * FROM " + tableName + " LIMIT " + stepSize + " OFFSET " + offset)) {
                sender.sendMessage("§7Convert Models Step §e" + step + "/" + stepComplete + " start !");
                if (rs.next()) {
                    do {
                        this.offset++;
                        if (Objects.nonNull(rs)) {
                            String a = rs.getString(1), c = rs.getString(2);
                            if (!(a.isEmpty() || c.isEmpty())) {
                            	if(!objID.contains(a)) {
                            		ByteArrayInputStream bin = new ByteArrayInputStream(Base64.getDecoder().decode(c));
                                    NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
                                    bin.close();

                                    NBTTagCompound location = compound.getCompound("Location");
                                    String world = location.getString("World");
                                    int chunkX = location.getInt("X") >> 4;
                                    int chunkZ = location.getInt("Z") >> 4;

                                    UUID uuidObj = DeSerializer.uuidFetcher(compound.getString("Owner-UUID"));
                                    String uuid = "";
                                    if (uuidObj != null) uuid = uuidObj.toString();

                                    if (!FurnitureLib.isNewVersion()) {
                                        String sql = "REPLACE INTO furnitureLibData (ObjID, Data, world, `x`, `z`, `uuid`) " +
                                                "VALUES (" +
                                                "'" + a + "'," +
                                                "'" + c + "'," +
                                                "'" + world + "'," +
                                                +chunkX + "," +
                                                +chunkZ + "," +
                                                "'" + uuid + "');";
                                        con.createStatement().executeUpdate(sql);
                                    } else {
                                    	if(compound.hasKey("entities")) {
                                    		NBTTagCompound armorStands = compound.getCompound("entities");
                                    		compound.set("entities", convertPacketItemStack(armorStands));
                                    	}else if(compound.hasKey("ArmorStands")) {
                                    		NBTTagCompound armorStands = compound.getCompound("ArmorStands");
                                            compound.set("entities", convertPacketItemStack(armorStands));
                                            compound.remove("ArmorStands");
                                    	}
                                        
                                        String g = Base64.getEncoder().encodeToString(Serializer.armorStandtoBytes(compound));
                                        String sql = "REPLACE INTO furnitureLibData (ObjID, Data, world, `x`, `z`, `uuid`) " +
                                                "VALUES (" +
                                                "'" + a + "'," +
                                                "'" + g + "'," +
                                                "'" + world + "'," +
                                                +chunkX + "," +
                                                +chunkZ + "," +
                                                "'" + uuid + "');";
                                        con.createStatement().executeUpdate(sql);
                                    }
                                    objID.add(a);
                            	}
                            }
                        }
                    } while (rs.next());
                }
                sender.sendMessage("§7Convert Models Step §e" + step + "/" + stepComplete + " Finish !");
                this.step++;
                rs.close();
                if (offset < dataFiles) {
                    this.convert(sender, tableName);
                } else {
                    sender.sendMessage("§2Database Convert Finished :D");
                    FurnitureConfig.getFurnitureConfig().setFileUpdater(false);
                    FurnitureLib.getInstance().saveConfig();
//                    con.createStatement().execute("ALTER TABLE `FurnitureLib_Objects` RENAME TO `FurnitureLib_ObjectsOLD`;");
//                    con.close();
                    FurnitureLib.getInstance().getSQLManager().loadALL();
                    FurnitureLib.getInstance().send("==========================================");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
