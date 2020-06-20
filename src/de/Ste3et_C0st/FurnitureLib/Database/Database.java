package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ExecuteTimer;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackObjectIDs;
import de.Ste3et_C0st.FurnitureLib.main.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Database {
    public FurnitureLib plugin;
    private HikariConfig config;
    private HikariDataSource dataSource;
    private Converter converter;
    public static final String TABLE_NAME = "furnitureLibData";
    
    public Database(FurnitureLib instance, HikariConfig config) {
        this.plugin = instance;
        this.config = config;
        this.dataSource = new HikariDataSource(config); //load 1
        this.converter = new Converter(this);
    }

    public abstract DataBaseType getType();

    public HikariConfig getConfig() {
        return this.config;
    }

    public Connection getConnection() {
        try {
            Connection connection = this.dataSource.getConnection();
            if (connection == null) {
                throw new SQLException("Unable to get a connection from the pool.");
            }
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean save(String query) {
    	 try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
             stmt.executeUpdate(query);
             return true;
         } catch (Exception e) {
             e.printStackTrace();
         }
         return false;
    }

    public void loadAll(SQLAction action) {
    	for (World world : Bukkit.getWorlds()) {
    		if(Objects.nonNull(world)) {
    			System.out.print("try to load furniture in world: " + world.getName());
    			this.loadWorld(action, world);
    		}
    	}
    }
    
    public boolean save(ObjectID id) {
    	String base64NBT = Serializer.SerializeObjectID(id);
        int x = id.getStartLocation().getBlockX() >> 4;
        int z = id.getStartLocation().getBlockZ() >> 4;
        String query = "REPLACE INTO " + TABLE_NAME + " (ObjID, Data, world, `x`, `z`, `uuid`) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query)) {
        	stmt.setString(1, id.getID());
        	stmt.setString(2, base64NBT);
        	stmt.setString(3, id.getWorldName());
        	stmt.setInt(4, x);
        	stmt.setInt(5, z);
        	stmt.setString(6, id.getUUID().toString());
        	stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public HashSet<ObjectID> loadQuery(SQLAction action, World bukkitWorld, String query){
    	HashSet<ObjectID> idList = new HashSet<ObjectID>();
    	if(Objects.isNull(query)) return idList;
        String worldName = bukkitWorld.getName();
        AtomicInteger atomic = new AtomicInteger(0);
        if(Objects.nonNull(bukkitWorld)) {
        	try (Connection con = getConnection(); ResultSet rs = con.createStatement().executeQuery(query)) {
        		if(Objects.nonNull(rs)) {
        			if (rs.next() == true) {
            			do {
            				String objectSerial = rs.getString(1);
            				String data = rs.getString(2);
                            if (!(objectSerial.isEmpty())) {
                                ObjectID obj = DeSerializer.Deserialize(objectSerial, data, action, bukkitWorld);
                                if (Objects.nonNull(obj)) {
                                	obj.setWorldName(worldName);
                                    idList.add(obj);
                                    atomic.addAndGet(obj.getPacketList().size());
                                }
                            }
            			} while (rs.next());
            		}
        		}
        	}catch (Exception e) {
                e.printStackTrace();
        	}
        }
        idList.stream().filter(Objects::nonNull).forEach(entry -> {
        	if(Objects.nonNull(entry.getProjectOBJ())) {
        		entry.registerBlocks();
            	entry.getProjectOBJ().applyFunction(entry);
        	}else {
        		System.out.println("Project: [" + entry.getProject() + "] not found.");
        	}
        });
        
        FurnitureManager.getInstance().getObjectList().addAll(idList);
		return idList;
    }

    public void loadAsynchron(ChunkData chunkdata, CallbackObjectIDs callBack, World bukkitWorld) {
    	UUID worldUUID = bukkitWorld.getUID();
        String worldName = bukkitWorld.getName();
        Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), () -> {
            String query = "SELECT ObjID,Data,world FROM " + TABLE_NAME + " WHERE x=" + chunkdata.getX() + " AND z=" + chunkdata.getZ() + " AND world='"+ worldName +"' OR world='" + worldUUID.toString() + "'";
            callBack.onResult(loadQuery(SQLAction.NOTHING, bukkitWorld, query));
        });
    }
    
    public HashSet<ObjectID> loadWorld(SQLAction action, World bukkitWorld) {
    	HashSet<ObjectID> idList = new HashSet<ObjectID>();
    	String worldName = bukkitWorld.getName();
    	if(!FurnitureLib.getInstance().isWorldIgnored(worldName)) {
    		 ExecuteTimer timer = new ExecuteTimer();
    	        UUID worldUUID = bukkitWorld.getUID();
    	        AtomicInteger atomic = new AtomicInteger(0);
    	        if(Objects.nonNull(bukkitWorld)) {
    	        	plugin.getLogger().info("FurnitureLib load models from world -> " + worldName);
    	        	idList.addAll(loadQuery(action, bukkitWorld, "SELECT ObjID,Data FROM " + TABLE_NAME + " WHERE world='"+ worldName +"' OR world='" + worldUUID.toString() + "'"));
    	        	double difference = timer.difference();
    	            double size = idList.size();
    	            
    	            if(size > 0) {
    	            	plugin.getLogger().info("Models: " + idList.size() + " with " + atomic.get() +" entities");
    	            	double avgSpeed = Math.round((difference / size) * 100d) / 100d;
    	                plugin.getLogger().info("With avg speed of " + avgSpeed + " FurnitureModel/ms");
    	            }else {
    	            	plugin.getLogger().info("No Models are found in world: " + worldName);
    	            	return idList;
    	            }
    	            
    	            plugin.getLogger().info("It takes: " + timer.getDifference() + " from Database: " + this.getType().name());
    	        }
    	}
        return idList;
    }

    public void delete(ObjectID objID) {
    	String query = "DELETE FROM " + TABLE_NAME + " WHERE ObjID = '" + objID.getID() + "'";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
        	System.out.println("remove statement -> " + query);
            stmt.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Converter getConverter() {
        return this.converter;
    }
}