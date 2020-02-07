package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import de.Ste3et_C0st.FurnitureLib.Utilitis.CallbackObjectIDs;
import de.Ste3et_C0st.FurnitureLib.main.ChunkData;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class SQLManager {

    FurnitureLib plugin;
    BukkitTask sqlSaveInterval;
    private Database database;

    public SQLManager(FurnitureLib plugin) {
        this.plugin = plugin;
        initialize();
    }

    public void initialize() {
        if (plugin.getConfig() == null) return;
        if (plugin.getConfig().getString("config.Database.type") == null) return;
        if (plugin.getConfig().getString("config.Database.type").equalsIgnoreCase("SQLite")) {
            String database = plugin.getConfig().getString("config.Database.database");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:plugins/FurnitureLib/" + database + ".db");
            config.setDriverClassName("org.sqlite.JDBC");
            config.setPoolName("FurnitureLib");
            config.setConnectionTestQuery("SELECT 1");
            config.setMaxLifetime(60000);
            config.setMaximumPoolSize(50);
            this.database = new SQLite(plugin, config);
        } else if (plugin.getConfig().getString("config.Database.type").equalsIgnoreCase("Mysql")) {
            isExist();
            String database = plugin.getConfig().getString("config.Database.database");
            String user = plugin.getConfig().getString("config.Database.user");
            String password = plugin.getConfig().getString("config.Database.password");
            String port = plugin.getConfig().getString("config.Database.port", "3306");
            String host = plugin.getConfig().getString("config.Database.host");
            boolean useSSL = plugin.getConfig().getBoolean("config.Database.useSSL", true);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL);
            config.setUsername(user);
            config.setPassword(password);
            config.setPoolName("FurnitureLib");
            config.setMaxLifetime(60000);
            config.setMaximumPoolSize(50);
            this.database = new MySQL(plugin, config);
        } else {
            plugin.getLogger().warning("Database Type not supported: FurnitureLib will shutdown.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
    }

    public void loadALL() {
        if (Objects.nonNull(database)) database.loadAll(SQLAction.NOTHING);
        FurnitureLib.getInstance().getFurnitureManager().sendAll();
    }

    private void isExist() {
        File fileDB = null;
        if (!plugin.getConfig().getBoolean("config.Database.importCheck")) {
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

    @SuppressWarnings("unchecked")
    public void save() {
        plugin.getLogger().info("Furniture save started");
        if (!plugin.getFurnitureManager().getObjectList().isEmpty()) {
            List<ObjectID> objList = new ArrayList<>();
            int j = 0, i = 0, l = 0;
            HashSet<ObjectID> idList = (HashSet<ObjectID>) plugin.getFurnitureManager().getObjectList().clone();
            for (ObjectID obj : idList) {
                if (!objList.contains(obj)) {
                    switch (obj.getSQLAction()) {
                        case UPDATE:
                            save(obj);
                            j++;
                            break;
                        case SAVE:
                            save(obj);
                            i++;
                            break;
                        case REMOVE:
                            remove(obj);
                            l++;
                            plugin.getFurnitureManager().deleteObjectID(obj);
                            break;
                        case NOTHING:
                            break;
                        case PURGE:
                            break;
                    }
                    if (!obj.getSQLAction().equals(SQLAction.REMOVE)) {
                        obj.setSQLAction(SQLAction.NOTHING);
                    }
                    objList.add(obj);
                    obj.setSQLAction(SQLAction.NOTHING);
                }
            }

            plugin.getLogger().info(i + " furniture has been saved to the database.");
            plugin.getLogger().info(j + " furniture has been updated in the database.");
            plugin.getLogger().info(l + " furniture has been removed from the database.");
        } else {
            plugin.getLogger().info("the list of objects is empty.");
        }
    }


    public void save(ObjectID obj) {
        if (Objects.nonNull(database)) {
            database.save(obj);
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
        sqlSaveInterval = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> FurnitureLib.getInstance().getFurnitureManager().saveAsynchron(Bukkit.getConsoleSender()), 0, 20 * time);
    }

    public void stop() {
        if (sqlSaveInterval != null) {
            sqlSaveInterval.cancel();
            sqlSaveInterval = null;
        }
    }

    public void convert(CommandSender sender) {
        database.getConverter().startConvert(sender);
    }

    public void loadAsynchron(ChunkData data, CallbackObjectIDs callBack) {
        database.loadAsynchron(data, callBack);
    }
}
