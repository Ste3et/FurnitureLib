package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ExecuteTimer;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackBoolean;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackObjectIDs;
import de.Ste3et_C0st.FurnitureLib.main.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SQLManager {
	
	private FurnitureLib plugin;
    private BukkitTask sqlSaveInterval = null;
    private Database database;
    private static int versionInt = FurnitureLib.getVersionInt();
    
    public SQLManager(FurnitureLib plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
    	ExecuteTimer timer = new ExecuteTimer();
        if (Objects.isNull(FurnitureLib.getInstance().getFurnitureConfig())) return;
        HikariConfig config = FurnitureConfig.getFurnitureConfig().loadDatabaseAsset();
        DataBaseType dataBaseType = FurnitureConfig.getFurnitureConfig().getDatabaseType();

        if(dataBaseType == DataBaseType.MySQL) {
        	isExist();
        	this.database = new MySQL(plugin, config);
        }else if(dataBaseType == DataBaseType.SQLite) {
        	this.database = new SQLite(plugin, config);
        }else {
        	plugin.getLogger().warning("Database Type not supported: FurnitureLib will shutdown.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        
        FurnitureLib.debug("FurnitureLib Started " + this.database.getType().name() + " database. Took " + timer.getMilliString(), 1);
    }

    public void loadALL() {
        if (Objects.nonNull(database)) database.loadAll(SQLAction.NOTHING);
        FurnitureLib.getInstance().getFurnitureManager().sendAll();
    }

    private void isExist() {
        File fileDB = null;
        if (!FurnitureConfig.getFurnitureConfig().isImportCheck()) {
            return;
        }
        File folder = new File("plugins/" + plugin.getName());
        File[] array = folder.listFiles();
        if (array == null) return;
        for (File file : array) {
            if (file == null) continue;
            if (file.getName().substring(file.getName().length() - 3).equalsIgnoreCase(".db")) {
                plugin.getLogger().info("Old Database File found: " + file.getName());
                plugin.getLogger().info("Start importing");
                fileDB = file;
            }
        }
        if (fileDB != null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:plugins/FurnitureLib/" + fileDB.getName());
            SQLite database = new SQLite(FurnitureLib.getInstance(), config);
            database.loadAll(SQLAction.SAVE);
            plugin.getLogger().info("Import finish");
            plugin.getLogger().info("Make old Database unusable.");
            fileDB.renameTo(new File("plugins/" + plugin.getName(), fileDB.getName() + ".old"));
            fileDB.delete();
        }
    }

    public void save() {
    	if(FurnitureConfig.getFurnitureConfig().shouldAutoSaveConsoleMessage()) plugin.getLogger().info("Furniture save started");
        if (!plugin.getFurnitureManager().getObjectList().isEmpty()) {
            List<ObjectID> objList = new ArrayList<>();
            int j = 0, i = 0;
            List<ObjectID> idList = new ArrayList<ObjectID>(plugin.getFurnitureManager().getObjectList());
            List<ObjectID> saveList = new ArrayList<ObjectID>();
            List<ObjectID> removeList = new ArrayList<ObjectID>();
            int stepSize = 100;

            for (ObjectID obj : idList) {
                if (!objList.contains(obj)) {
                	SQLAction sqlAction = obj.getSQLAction();
                	if(SQLAction.REMOVE == sqlAction) {
                		remove(obj);
                        removeList.add(obj);
                        //plugin.getFurnitureManager().deleteObjectID(obj);
                	}else if(SQLAction.UPDATE == sqlAction) {
                		if(versionInt > 11) {
                			saveList.add(obj);
                		}else {
                			save(obj);
                		}
                		j++;
                	}else if(SQLAction.SAVE == sqlAction){
                		if(versionInt > 11) {
                			saveList.add(obj);
                		}else {
                			save(obj);
                		}
                		i++;
                	}else {
                		continue;
                	}
                	objList.add(obj);
                }
            }
            
            if(!saveList.isEmpty()) {
            	Collection<List<ObjectID>> collection = splitListBySize(saveList, stepSize);
                if(Objects.nonNull(collection)) {
                	collection.stream().filter(Objects::nonNull).forEach(list -> {
                    	SQLStatement statement = new SQLStatement();
                    	statement.add(list);
                    	save(statement.getStatement());
                    });
                }
            }
            
            plugin.getFurnitureManager().deleteObjectID(removeList);
            
            if(FurnitureConfig.getFurnitureConfig().shouldAutoSaveConsoleMessage()) {
            	if(i != 0 || j != 0 || removeList.size() != 0) {
            		plugin.getLogger().info(i + " furniture has been saved to the database.");
                    plugin.getLogger().info(j + " furniture has been updated in the database.");
                    plugin.getLogger().info(removeList.size() + " furniture has been removed from the database.");
            	}
            }
        }
    }
    
    public void save(CallbackBoolean callBackBoolean) {
    	if(FurnitureConfig.getFurnitureConfig().shouldAutoSaveConsoleMessage()) plugin.getLogger().info("Furniture save started");
        if (!plugin.getFurnitureManager().getObjectList().isEmpty()) {
            List<ObjectID> objList = new ArrayList<>();
            int j = 0, i = 0, l = 0;
            List<ObjectID> idList = new ArrayList<ObjectID>(plugin.getFurnitureManager().getObjectList());
            List<ObjectID> saveList = new ArrayList<ObjectID>();
            
            int stepSize = 100;

            for (ObjectID obj : idList) {
                if (!objList.contains(obj)) {
                	SQLAction sqlAction = obj.getSQLAction();
                	if(SQLAction.REMOVE == sqlAction) {
                		remove(obj);
                        l++;
                        plugin.getFurnitureManager().deleteObjectID(obj);
                	}else if(SQLAction.UPDATE == sqlAction) {
                		if(versionInt > 11) {
                			saveList.add(obj);
                		}else {
                			save(obj);
                		}
                		j++;
                	}else if(SQLAction.SAVE == sqlAction){
                		if(versionInt > 11) {
                			saveList.add(obj);
                		}else {
                			save(obj);
                		}
                		i++;
                	}else {
                		continue;
                	}
                	objList.add(obj);
                }
            }
            
            if(!saveList.isEmpty()) {
            	Collection<List<ObjectID>> collection = splitListBySize(saveList, stepSize);
                if(Objects.nonNull(collection)) {
                	collection.stream().filter(Objects::nonNull).forEach(list -> {
                    	SQLStatement statement = new SQLStatement();
                    	statement.add(list);
                    	save(statement.getStatement());
                    });
                }
            }
            if(FurnitureConfig.getFurnitureConfig().shouldAutoSaveConsoleMessage()) {
            	if(i != 0 || j != 0 || l != 0) {
            		plugin.getLogger().info(i + " furniture has been saved to the database.");
                    plugin.getLogger().info(j + " furniture has been updated in the database.");
                    plugin.getLogger().info(l + " furniture has been removed from the database.");       
            	} 	
            }
        }
        
        if(Objects.nonNull(callBackBoolean)) {
        	callBackBoolean.onResult(true);
        }
    }

    
    public static Collection<List<ObjectID>> splitListBySize(List<ObjectID> intList, int size) {
        if (!intList.isEmpty() && size > 0) {
            final AtomicInteger counter = new AtomicInteger(0);
            return intList.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size)).values();
        }
        return null;
    }

    public void save(ObjectID obj) {
        if (Objects.nonNull(database)) {
            database.save(obj);
            return;
        }
        FurnitureLib.getInstance().getLogger().warning("No SQLite and MySQL instance found.");
    }
    
    public void save(String query) {
        if (Objects.nonNull(database)) {
            database.save(query);
            return;
        }
        FurnitureLib.getInstance().getLogger().warning("No SQLite and MySQL instance found.");
    }
    

    public void remove(ObjectID obj) {
        if (Objects.nonNull(database)) {
            database.delete(obj);
            return;
        }
        FurnitureLib.getInstance().getLogger().warning("No SQLite and MySQL instance found.");
    }

    public void saveInterval(int time) {
    	if(Objects.nonNull(sqlSaveInterval)) sqlSaveInterval.cancel();
    	if(time < 1) return;
        sqlSaveInterval = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> FurnitureLib.getInstance().getFurnitureManager().saveAsynchron(Bukkit.getConsoleSender()), 20 * time, 20 * time);
    }

    public void stop() {
        if (sqlSaveInterval != null) {
            sqlSaveInterval.cancel();
            sqlSaveInterval = null;
        }
    }

    public void convert(CommandSender sender, String table) {
        database.getConverter().startConvert(sender, table);
    }

    public void loadAsynchron(ChunkData data, CallbackObjectIDs callBack, World world) {
        database.loadAsynchron(data, callBack, world);
    }
    
    public Database getDatabase() {
    	return this.database;
    }
}