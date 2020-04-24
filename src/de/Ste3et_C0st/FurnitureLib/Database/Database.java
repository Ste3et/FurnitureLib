package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
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
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Database {
    public FurnitureLib plugin;
    private HikariConfig config;
    private HikariDataSource dataSource;
    private Converter converter;
    private static final Pattern URN_UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
    
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

    public boolean save(ObjectID id) {
        String binary = FurnitureLib.getInstance().getSerializer().SerializeObjectID(id);
        int x = id.getStartLocation().getBlockX() >> 4;
        int z = id.getStartLocation().getBlockZ() >> 4;
        String sql = "REPLACE INTO furnitureLibData (ObjID, Data, world, `x`, `z`, `uuid`) " +
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

    public void loadAsynchron(ChunkData chunkdata, CallbackObjectIDs callBack, World world) {
        Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), () -> {
            String query = "SELECT ObjID,Data,world FROM furnitureLibData WHERE x=" + chunkdata.getX() + " AND z=" + chunkdata.getZ() + " AND world='" + chunkdata.getWorld() + "'";
            try (Connection con = getConnection(); ResultSet rs = con.createStatement().executeQuery(query)) {
                HashSet<ObjectID> idList = new HashSet<ObjectID>();
                if (rs.next()) {
                    do {
                        String a = rs.getString(1), c = rs.getString(2);
                        if (Objects.nonNull(a) && Objects.nonNull(c)) {
                            ObjectID obj = FurnitureLib.getInstance().getDeSerializer().Deserialize(a, c, SQLAction.NOTHING, world);
                            if (Objects.nonNull(obj)) {
                                idList.add(obj);
                            }
                        }
                    } while (rs.next());
                    FurnitureLib.debug("FurnitureLib load " + idList.size() + " Models for chunk " + " x:" + chunkdata.getX() + " z:" + chunkdata.getZ());
                }
                callBack.onResult(idList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadAll(SQLAction action) {
    	for (World world : Bukkit.getWorlds()) {
    		if(Objects.nonNull(world)) {
    			this.loadWorld(action, world.getName(), world.getUID().toString());
    		}
    	}
    }
    
    public HashSet<ObjectID> loadWorld(SQLAction action, String world, String uuid) {
        long time1 = System.currentTimeMillis();
        HashSet<ObjectID> idList = new HashSet<ObjectID>();
        SimpleDateFormat time = new SimpleDateFormat("mm:ss.SSS");
        //FurnitureLib.getInstance().getProjectManager().loadProjectFiles();
        World bukkitWorld = Bukkit.getWorld(world);
        if(Objects.nonNull(bukkitWorld)) {
        	try (Connection con = getConnection(); ResultSet rs = con.createStatement().executeQuery("SELECT ObjID,Data,world FROM furnitureLibData WHERE world='"+ world +"' OR world='" + uuid + "'")) {
                if (rs.next() == true) {
                    do {
                        String a = rs.getString(1), c = rs.getString(2), d = rs.getString(3);
                        if (!(a.isEmpty() || c.isEmpty())) {
                            ObjectID obj = FurnitureLib.getInstance().getDeSerializer().Deserialize(a, c, action, bukkitWorld);
                            if (Objects.nonNull(obj)) {
                                obj.setWorldName(world);
                                Matcher matcher = URN_UUID_PATTERN.matcher(d);
                                if(matcher.matches()) {
                                	obj.setSQLAction(SQLAction.UPDATE);
                                }
                                
                                idList.add(obj);
                            }
                        }
    				} while (rs.next());
                    
                    FurnitureManager.getInstance().addObjectID(idList);
                    /* Load Blocks */
                    idList.forEach(ObjectID::registerBlocks);
                    
                    int ArmorStands = FurnitureLib.getInstance().getDeSerializer().armorStands.get();
                    int purged = FurnitureLib.getInstance().getDeSerializer().purged;
                    long time2 = System.currentTimeMillis();
                    long timedef = time2 - time1;
                    //double avg = Math.ceil(timedef / idList.size());
                    String timeStr = time.format(timedef);
                    
                    plugin.getLogger().info("FurnitureLib load models from world -> " + world);
                    plugin.getLogger().info("Models: " + idList.size() + " with " + ArmorStands +" entities");
                    //plugin.getLogger().info("With avg speed of " + avg + " Model/ms");
                    plugin.getLogger().info("Purged: " + purged + " models");
                    plugin.getLogger().info("It takes: " + timeStr + " from Database: " + this.getType().name());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                FurnitureManager.getInstance().getProjects().forEach(Project::applyFunction);
            }
        }
        return idList;
    }

    public void delete(ObjectID objID) {
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute("DELETE FROM furnitureLibData WHERE ObjID = '" + objID.getID() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Converter getConverter() {
        return this.converter;
    }
}