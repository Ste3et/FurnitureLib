package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.CallbackObjectIDs;
import de.Ste3et_C0st.FurnitureLib.Utilitis.MaterialConverter;
import de.Ste3et_C0st.FurnitureLib.main.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.FurniturePlugin;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public abstract class Database {
	public FurnitureLib plugin;
    public int stepSize = 250, offset = 0, dataFiles = 0, step = 1, stepComplete = 0;
    private HikariConfig config;
    private HikariDataSource dataSource;
    private Connection currentConnection;
    
    public Database(FurnitureLib instance, HikariConfig config){
        this.plugin = instance;
        this.config = config;
        this.dataSource = new HikariDataSource(config);
    }
    
    public abstract DataBaseType getType();
    
    public HikariConfig getConfig() {
    	return this.config;
    }

    public void closeConnection() {
    	try {
    		currentConnection.close();
    		this.currentConnection = null;
		} catch(SQLException throwable) {
    		throwable.printStackTrace();
		}
	}
    
	public Connection getConnection() {
		 try{
			 this.currentConnection = dataSource.getConnection();
		     if (currentConnection == null) {
		          throw new SQLException("Unable to get a connection from the pool.");
		     }
		     return currentConnection;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
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
    	try(Connection con = getConnection(); Statement stmt = con.createStatement()){
    		stmt.executeUpdate(sql);
    		return true;
    	}catch(Exception e){
    		e.printStackTrace();
    	} finally {
    		closeConnection();
		}
    	return false;
    }
    
    public void startConvert(CommandSender sender) {
    	String sql = "SELECT COUNT(*) FROM `FurnitureLib_Objects`";
    	try (Connection con = getConnection();ResultSet rs = con.createStatement().executeQuery(sql)) {
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
		} catch (Exception ex) {
			sender.sendMessage("§2Database is already converted !");
			FurnitureLib.getInstance().getSQLManager().loadALL();
		} finally {
			FurnitureLib.getInstance().getConfig().set("config.autoFileUpdater", false);
			FurnitureLib.getInstance().saveConfig();
			closeConnection();
		}
    }

    @SuppressWarnings("unchecked")
	private void convert(CommandSender sender) {
    	Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				try (Connection con = getConnection();ResultSet rs = con.createStatement().executeQuery("SELECT * FROM FurnitureLib_Objects LIMIT " + stepSize + " OFFSET " + offset)) {
					sender.sendMessage("§7Convert Models Step §e" + step + "/" + stepComplete + " start ! §7[§e"+getTPS()+"§7]");
					while (rs.next()){
						if(rs != null){
							offset++;
							String a = rs.getString(1), c = rs.getString(2);
							if(!(a.isEmpty() || c.isEmpty())) {
								ByteArrayInputStream bin = new ByteArrayInputStream(Base64.getDecoder().decode(c));
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
								String g = Base64.getEncoder().encodeToString(Serializer.armorStandtoBytes(compound));
						    	String sql = "REPLACE INTO furnitureLibData (ObjID, Data, world, `x`, `z`, `uuid`) " + 
						    			"VALUES (" + 
						    			"'"+a+"'," +
						    			"'"+g+"'," +
						    			"'"+world+"'," +
						    			+chunkX+"," +
						    			+chunkZ+"," +
						    			"'"+uuid+"');";
						    	con.createStatement().executeUpdate(sql);
							}
						}
					}
					
					sender.sendMessage("§7Convert Models Step §e" + step + "/" + stepComplete + " Finish ! §7[§e"+getTPS()+"§7]");
					step++;
					rs.close();
					if(offset != dataFiles) {
						convert(sender);
					}else{
						sender.sendMessage("§2Database Convert Finished :D");
						con.createStatement().execute("ALTER TABLE `FurnitureLib_Objects` RENAME TO `FurnitureLib_ObjectsOLD`;");
						con.close();
						FurnitureLib.getInstance().getSQLManager().loadALL();
						FurnitureLib.getInstance().send("==========================================");
					}
				}catch (Exception e) {
					e.printStackTrace();
					FurnitureLib.getInstance().send("==========================================");
				} finally {
					closeConnection();
				}
		});
    }
    
    public int getTPS() {
    	try {
			return Class.forName("net.minecraft.server." + plugin.getBukkitVersion() + ".MinecraftServer").getField("TPS").getInt(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
    }
    
	public void loadAsynchron(ChunkData chunkdata, CallbackObjectIDs callBack) {
		Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), () -> {
			String query = "SELECT ObjID,Data,world FROM furnitureLibData WHERE x=" + chunkdata.getX() + " AND z=" + chunkdata.getZ() + " AND world='"+chunkdata.getWorld()+"'";
			try (Connection con = getConnection();ResultSet rs = con.createStatement().executeQuery(query)){
				HashSet<ObjectID> idList = new HashSet<ObjectID>();
				if(rs.next()) {
					do {
						String a = rs.getString(1), c = rs.getString(2), d = rs.getString(3);
	    				if(Objects.nonNull(a) && Objects.nonNull(c)) {
	    					ObjectID obj = FurnitureLib.getInstance().getDeSerializer().Deserialize(a, c, SQLAction.NOTHING, d);
	    					if(Objects.nonNull(obj)) {
	    						idList.add(obj);
	    					}
	    				}
					}while(rs.next());
					FurnitureLib.debug("FurnitureLib load " + idList.size() + " Models for chunk " + " x:" + chunkdata.getX() + " z:" + chunkdata.getZ() + " TPS: " + getTPS());
				}
	    		callBack.onResult(idList);
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		});
	}
    
    public void loadAll(SQLAction action){
    	long time1 = System.currentTimeMillis();
    	FurnitureLib.getInstance().getProjectManager().loadProjectFiles();
    	try (Connection con = getConnection();ResultSet rs = con.createStatement().executeQuery("SELECT ObjID,Data,world FROM furnitureLibData")){    		
    		HashSet<ObjectID> idList = new HashSet<ObjectID>();
    		if(rs.next() == true) {
    			long time2 = System.currentTimeMillis();
    	    	SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
    	    	String timeStr = time.format(time2-time1);
    			System.out.println("FurnitureLib load data from Source Finish Start deserialize ["+timeStr+"]");
				do {
					String a = rs.getString(1), c = rs.getString(2), d = rs.getString(3);
					if(!(a.isEmpty() || c.isEmpty())) {
						ObjectID obj = FurnitureLib.getInstance().getDeSerializer().Deserialize(a, c, action, d);
						if(Objects.nonNull(obj)) {
    						idList.add(obj);
    					}
					};
				}while(rs.next());
			}
    		FurnitureManager.getInstance().addObjectID(idList);
    		plugin.getLogger().info("FurnitureLib load " + idList.size()  +  " Objects from: " + getType().name() + " Database");
    		long time2 = System.currentTimeMillis();
	    	SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
	    	String timeStr = time.format(time2-time1);
	    	int ArmorStands = FurnitureLib.getInstance().getDeSerializer().armorStands.get();
	    	int purged = FurnitureLib.getInstance().getDeSerializer().purged;
	    	plugin.getLogger().info("FurnitureLib have loadet " + ArmorStands + " in " +timeStr);
	    	plugin.getLogger().info("FurnitureLib have purged " + purged + " Objects");
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
			FurnitureManager.getInstance().getProjects().forEach(Project::applyFunction);
			closeConnection();
		}
    }

    public void delete(ObjectID objID){
    	try(Connection con = getConnection(); Statement stmt = con.createStatement()){
    		stmt.execute("DELETE FROM furnitureLibData WHERE ObjID = '" + objID.getID() + "'");
		} catch (Exception e) {
    		e.printStackTrace();
		} finally {
    		closeConnection();
		}
    }
}