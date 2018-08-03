package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.sqlite.SQLiteException;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.MaterialConverter;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import net.minecraft.server.v1_13_R1.MinecraftServer;

public abstract class Database {
	public FurnitureLib plugin;
	public Connection connection;
    public int stepSize = 250, offset = 0, dataFiles = 0, step = 1, stepComplete = 0;
    
    public Database(FurnitureLib instance){
        this.plugin = instance;
    }
    
    public abstract Connection getSQLConnection();

    public abstract void load();
    public abstract DataBaseType getType();
    
    public void initialize(){
        connection = getSQLConnection();
        try(PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM furnitureLibData")){
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public boolean save(ObjectID id){
    	String binary = FurnitureLib.getInstance().getSerializer().SerializeObjectID(id);
    	int x = id.getStartLocation().getBlockX() >> 4;
    	int z = id.getStartLocation().getBlockZ() >> 4;
    	String sql = "REPLACE INTO furnitureLibData (ObjID, Data, world, `x`, `z`, `uuid`) " + 
    			"VALUES (" + 
    			"'"+id.getID()+"'," +
    			"'"+binary+"'," +
    			"'"+id.getWorldName()+"'," +
    			+x+"," +
    			+z+"," +
    			"'"+id.getUUID().toString()+"');";
    	try{
    		connection.createStatement().executeUpdate(sql);
    		return true;
    	}catch(Exception e){
    		if(e instanceof SocketException || e instanceof EOFException){
    			initialize();
    			try{
    				connection.createStatement().executeUpdate(sql);
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    			return false;
    		}
    		e.printStackTrace();
    	}
    	return false;
    }
    
    public void startConvert(CommandSender sender) {
    	String sql = "SELECT COUNT(*) FROM `FurnitureLib_Objects`";
    	try (ResultSet rs = connection.createStatement().executeQuery(sql)) {
			while (rs.next()){
				dataFiles = rs.getInt(1);
				if(dataFiles != 0) {
					stepComplete = (int) Math.ceil(((double) dataFiles) / ((double) stepSize));
					sender.sendMessage("Convert of " + dataFiles + " from "  + getType().name());
					sender.sendMessage("It takes a while " + stepComplete + " Steps");
					convert(sender);
				}else {
					rs.close();
					sender.sendMessage("Nothing to convert");
					FurnitureLib.getInstance().send("==========================================");
					return;
				}
			}
		} catch (MySQLSyntaxErrorException ex) {
			sender.sendMessage("§2Database is already converted !");
			FurnitureLib.getInstance().getSQLManager().loadALL();
		} catch (SQLiteException sqlex) {
			sender.sendMessage("§2Database is already converted !");
			FurnitureLib.getInstance().getSQLManager().loadALL();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			FurnitureLib.getInstance().getConfig().set("config.autoFileUpdater", false);
			FurnitureLib.getInstance().saveConfig();
		}
    }

    @SuppressWarnings("unchecked")
	private void convert(CommandSender sender) {
    	Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				try (ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM FurnitureLib_Objects LIMIT " + stepSize + " OFFSET " + offset)) {
					sender.sendMessage("§7Convert Models Step §e" + step + "/" + stepComplete + " start ! §7[§e"+MinecraftServer.TPS+"§7]");
					while (rs.next()){
						if(rs != null){
							offset++;
							String a = rs.getString(1), c = rs.getString(2);
							if(!(a.isEmpty() || c.isEmpty())) {
								ByteArrayInputStream bin = new ByteArrayInputStream(Base64.decodeBase64(c));
								NBTTagCompound compound = NBTCompressedStreamTools.read(bin);
								bin.close();
								
								//Location Part
								NBTTagCompound location = compound.getCompound("Location");
								String world = location.getString("World");
								int chunkX = location.getInt("X") >> 4;
								int chunkZ = location.getInt("Z") >> 4;
								
								//UUID Part
								UUID uuidObj = DeSerializer.uuidFetcher(compound.getString("Owner-UUID"));
								String uuid = "";
								if(uuidObj != null) uuid = uuidObj.toString();
								
								NBTTagCompound armorStands = compound.getCompound("ArmorStands");
								
								//Convert ItemStacks
								armorStands.c().stream().filter(entity -> entity != null).forEach(entity -> {
									NBTTagCompound metadata = armorStands.getCompound((String) entity);
									NBTTagCompound inventory = metadata.getCompound("Inventory");
									NBTTagCompound updatetInventory = new NBTTagCompound();
									EnumSet.allOf(EnumWrappers.ItemSlot.class).stream().forEach(slot ->{
										if(!inventory.getString(slot.name()).equalsIgnoreCase("NONE")){
											NBTTagCompound item = MaterialConverter.convertNMSItemStack(inventory.getCompound(slot.name()));
											updatetInventory.set(slot.name(), item);
										}else {
											updatetInventory.setString(slot.name(), "NONE");
										}
									});
									metadata.set("Inventory", updatetInventory);
									armorStands.set((String) entity, metadata);
								});
								compound.set("entitys", armorStands);
								compound.remove("ArmorStands");
								String g = Base64.encodeBase64String(Serializer.armorStandtoBytes(compound));
						    	String sql = "REPLACE INTO furnitureLibData (ObjID, Data, world, `x`, `z`, `uuid`) " + 
						    			"VALUES (" + 
						    			"'"+a+"'," +
						    			"'"+g+"'," +
						    			"'"+world+"'," +
						    			+chunkX+"," +
						    			+chunkZ+"," +
						    			"'"+uuid+"');";
						    	connection.createStatement().executeUpdate(sql);
							}
						}
					}
					sender.sendMessage("§7Convert Models Step §e" + step + "/" + stepComplete + " Finish ! §7[§e"+MinecraftServer.TPS+"§7]");
					step++;
					rs.close();
					if(offset != dataFiles) {
						convert(sender);
					}else{
						sender.sendMessage("§2Database Convert Finished :D");
						connection.createStatement().execute("ALTER TABLE `FurnitureLib_Objects` RENAME TO `FurnitureLib_ObjectsOLD`;");
						FurnitureLib.getInstance().getSQLManager().loadALL();
						FurnitureLib.getInstance().send("==========================================");
					}
				}catch (Exception e) {
					e.printStackTrace();
					FurnitureLib.getInstance().send("==========================================");
				}
		});
    }
    
    public void loadAll(SQLAction action){
    	long time1 = System.currentTimeMillis();
    	boolean b = FurnitureLib.getInstance().isAutoPurge();
    	try (ResultSet rs = connection.createStatement().executeQuery("SELECT ObjID,Data FROM furnitureLibData")){    		
    		while (rs.next()){
    			if(rs != null){
    				String a = rs.getString(1), c = rs.getString(2);
    				if(!(a.isEmpty() || c.isEmpty())) FurnitureLib.getInstance().getDeSerializer().Deserialze(a, c, action, b);
    			}
    		}
    		rs.close();
    		plugin.getLogger().info("FurnitureLib load " + FurnitureLib.getInstance().getFurnitureManager().getObjectList().size()  +  " Objects from: " + getType().name() + " Database");
    		long time2 = System.currentTimeMillis();
	    	SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
	    	String timeStr = time.format(time2-time1);
	    	int ArmorStands = FurnitureLib.getInstance().getDeSerializer().armorStands;
	    	int purged = FurnitureLib.getInstance().getDeSerializer().purged;
	    	plugin.getLogger().info("FurnitureLib have loadet " + ArmorStands + " in " +timeStr);
	    	plugin.getLogger().info("FurnitureLib have purged " + purged + " Objects");
	    	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
	    		FurnitureLib.getInstance().getProjectManager().loadProjectFiles();
	    	});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void delete(ObjectID objID){
    	try {
    		connection.createStatement().execute("DELETE FROM furnitureLibData WHERE ObjID = '" + objID.getID() + "'");
		} catch (Exception e) {
    		if(e instanceof SocketException || e instanceof EOFException){
    			initialize();
    			try{
    				connection.createStatement().execute("DELETE FROM furnitureLibData WHERE ObjID = '" + objID.getID() + "'");
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    			return;
    		}
    		e.printStackTrace();
		}
    }
 

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void close(){
    	try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}