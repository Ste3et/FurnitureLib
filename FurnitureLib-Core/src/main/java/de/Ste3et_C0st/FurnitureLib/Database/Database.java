package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ExecuteTimer;
import de.Ste3et_C0st.FurnitureLib.async.ChunkData;
import de.Ste3et_C0st.FurnitureLib.async.WorldData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Database {
    public FurnitureLib plugin;
    private final HikariConfig config;
    private final HikariDataSource dataSource;
    private final Converter converter;
    public static final String TABLE_NAME = "furnitureLibData";
    
    //Prepare debugPool to store the connection Objects
    private static HashSet<Connection> connectionDebugPool = new HashSet<Connection>();
    
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
            connectionDebugPool.add(connection);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static HashSet<Connection> getConnections(){
    	return connectionDebugPool;
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
    			this.loadWorld(action, world);
    		}
    	}
    	FurnitureManager.getInstance().sendAll();
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
    	final HashSet<ObjectID> idList = new HashSet<ObjectID>();
    	if(Objects.isNull(query)) return idList;
        final String worldName = bukkitWorld.getName();
        if(Objects.nonNull(bukkitWorld)) {
        	try (Connection con = getConnection(); ResultSet rs = con.createStatement().executeQuery(query)) {
        		if(Objects.nonNull(rs)) {
        			if (rs.next() == true) {
            			do {
            				String objectSerial = rs.getString("ObjID");
            				String data = rs.getString("Data");
                            if (!(objectSerial.isEmpty())) {
                                ObjectID obj = DeSerializer.Deserialize(objectSerial, data, action, bukkitWorld);
                                if (Objects.nonNull(obj)) {
                                	obj.setWorldName(worldName);
                                    idList.add(obj);
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
        	if (Objects.nonNull(entry.getProjectOBJ())) {
        		entry.registerBlocks();
            	entry.getProjectOBJ().applyFunction(entry);
        	}
        });
        
        FurnitureManager.getInstance().addObjectID(idList);;
		return idList;
    }

    public <T> CompletableFuture<HashSet<ObjectID>> loadAsynchron(ChunkData chunkdata, World bukkitWorld) {
    	final UUID worldUUID = bukkitWorld.getUID();
        final String worldName = bukkitWorld.getName();
        final String query = "SELECT ObjID,Data,world FROM " + TABLE_NAME + " WHERE x=" + chunkdata.getX() + " AND z=" + chunkdata.getZ() + " AND world='"+ worldName +"' OR world='" + worldUUID.toString() + "'";
        
        final CompletableFuture<HashSet<ObjectID>> future = CompletableFuture.supplyAsync(() -> {
        	return loadQuery(SQLAction.NOTHING, bukkitWorld, query);
        });
        
        return future;
    }
    
    public HashSet<ObjectID> loadWorld(SQLAction action, World bukkitWorld) {
    	HashSet<ObjectID> idList = new HashSet<ObjectID>();
    	String worldName = bukkitWorld.getName();
    	if(!FurnitureConfig.getFurnitureConfig().isWorldIgnored(worldName)) {
    		 ExecuteTimer timer = new ExecuteTimer();
    	        UUID worldUUID = bukkitWorld.getUID();
    	        AtomicInteger atomic = new AtomicInteger(0);
    	        if(Objects.nonNull(bukkitWorld)) {
    	        	FurnitureLib.debug("FurnitureLib try to load models for world (" + worldName + ")", 1);
    	        	idList.addAll(loadQuery(action, bukkitWorld, "SELECT ObjID,Data FROM " + TABLE_NAME + " WHERE world='"+ worldName +"' OR world='" + worldUUID.toString() + "'"));
    	        	double difference = timer.difference();
    	            double size = idList.size();
    	            
    	            idList.stream().forEach(entry -> atomic.addAndGet(entry.getPacketList().size()));
    	            
    	            if(size > 0) {
    	            	FurnitureLib.debug("FurnitureLib load " + idList.size() + " models with " + atomic.get() +" entities", 1);
    	            	double avgSpeed = Math.round((difference / size) * 100d) / 100d;
    	            	FurnitureLib.debug("With avg speed of " + avgSpeed + " FurnitureModel/ms", 1);
    	            }else {
    	            	FurnitureLib.debug("No Models are found in world: " + worldName, 1);
    	            	return idList;
    	            }
    	            
    	            FurnitureLib.debug("It takes: " + timer.getDifference(), 1);
    	            //FurnitureManager.getInstance().addObjectID(idList);
    	        }
    	}
        return idList;
    }
    
    public <T> CompletableFuture<WorldData> loadWorldAsync(World bukkitWorld){
    	final String worldName = bukkitWorld.getName();
    	final String worldUUID = bukkitWorld.getUID().toString();
    	final WorldData worldData = new WorldData(worldName);
    	if(!FurnitureConfig.getFurnitureConfig().isWorldIgnored(worldName)) {
    		ExecuteTimer timer = new ExecuteTimer();
    		final String query = ("SELECT x,z FROM <table> WHERE world='<worldName>' or world='<worldUUID>'").replace("<table>", TABLE_NAME).replace("<worldName>", worldName).replace("<worldUUID>", worldUUID);
    		
    		final CompletableFuture<WorldData> future = CompletableFuture.supplyAsync(() -> {
    			try (Connection con = getConnection(); ResultSet rs = con.createStatement().executeQuery(query)) {
            		if(Objects.nonNull(rs)) {
            			if (rs.next() == true) {
                			do {
                				final int chunkX = rs.getInt("x");
                				final int chunkZ = rs.getInt("z");
                				worldData.addPoint(chunkX, chunkZ);
                			} while (rs.next());
                		}
            		}
        		}catch (Exception e) {
        			e.printStackTrace();
    			}
    			
            	return worldData;
            });
    		FurnitureLib.debug("It takes: " + timer.getDifference(), 1);
    		return future;
    	}
    	return null;
    }

    public void delete(ObjectID objID) {
    	String query = "DELETE FROM " + TABLE_NAME + " WHERE ObjID = '" + objID.getID() + "'";
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Converter getConverter() {
        return this.converter;
    }
    
    public void createTable(final String query) {
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
            FurnitureLib.debug(getType().name() + " createTable -> " + query, 0);
        } catch (SQLException e) {
            FurnitureLib.debug(getType().name() + " createTable: Fail", 10);
            e.printStackTrace();
        }
    }
}