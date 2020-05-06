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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sound.midi.SysexMessage;

public abstract class Database {
    public FurnitureLib plugin;
    private HikariConfig config;
    private HikariDataSource dataSource;
    private Converter converter;
    public static final String TABLE_NAME = "furnitureLibData";
    
    public Database(FurnitureLib instance, HikariConfig config) {
        this.plugin = instance;
        this.config = config;
        this.dataSource = new HikariDataSource(config);
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
        String binary = FurnitureLib.getInstance().getSerializer().SerializeObjectID(id);
        int x = id.getStartLocation().getBlockX() >> 4;
        int z = id.getStartLocation().getBlockZ() >> 4;
        String sql = "REPLACE INTO " + TABLE_NAME + " (ObjID, Data, world, `x`, `z`, `uuid`) " +
                "VALUES (" +
                "'" + id.getID() + "'," +
                "'" + binary + "'," +
                "'" + id.getWorldName() + "'," +
                +x + "," +
                +z + "," +
                "'" + id.getUUID().toString() + "');";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
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
            				String objectSerial = rs.getString(1), base64Storage = rs.getString(2);
                            if (!(objectSerial.isEmpty() || base64Storage.isEmpty())) {
                                ObjectID obj = DeSerializer.Deserialize(objectSerial, base64Storage, action, bukkitWorld);
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
        ExecuteTimer timer = new ExecuteTimer();
        UUID worldUUID = bukkitWorld.getUID();
        String worldName = bukkitWorld.getName();
        AtomicInteger atomic = new AtomicInteger(0);
        if(Objects.nonNull(bukkitWorld)) {
        	idList.addAll(loadQuery(action, bukkitWorld, "SELECT ObjID,Data FROM " + TABLE_NAME + " WHERE world='"+ worldName +"' OR world='" + worldUUID.toString() + "'"));
        	double difference = timer.difference();
            double size = idList.size();
            
            plugin.getLogger().info("FurnitureLib load models from world -> " + worldName);
            plugin.getLogger().info("Models: " + idList.size() + " with " + atomic.get() +" entities");
            
            if(size > 0) {
            	double avgSpeed = Math.round((difference / size) * 100d) / 100d;
                plugin.getLogger().info("With avg speed of " + avgSpeed + " FurnitureModel/ms");
            }
            
            plugin.getLogger().info("It takes: " + timer.getDifference() + " from Database: " + this.getType().name());
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