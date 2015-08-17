package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class SQLManager {

	MySQL mysql;
	SQLite sqlite;
	FurnitureLib plugin;
	Integer sqlSaveIntervall;
	Connection con;
	
	public SQLManager(FurnitureLib plugin){
		this.plugin = plugin;initialize();
		initialize();
	}
	
	public void initialize(){
		if(plugin.getConfig().getString("config.Database.type").equalsIgnoreCase("SQLite")){
			String database = plugin.getConfig().getString("config.Database.database");
			this.sqlite = new SQLite(plugin, database);
			this.sqlite.load();
			this.con = this.sqlite.getSQLConnection();
		}else if(plugin.getConfig().getString("config.Database.type").equalsIgnoreCase("Mysql")){
			isExist();
			String database = plugin.getConfig().getString("config.Database.database");
			String user = plugin.getConfig().getString("config.Database.user");
			String password = plugin.getConfig().getString("config.Database.password");
			String port = plugin.getConfig().getString("config.Database.port");
			String host = plugin.getConfig().getString("config.Database.host");
			this.mysql = new MySQL(plugin, host, database, password, user, port);
			this.mysql.load();
			this.con = this.mysql.getSQLConnection();
		}else{
			plugin.getLogger().warning("Database Type not supported: Plugin shutdown");
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
	}
	
	public void loadALL(){
		if(this.sqlite!=null){
			this.sqlite.loadAll(false);
			this.sqlite.loadAllObjIDs();
		}else if(this.mysql!=null){
			this.mysql.loadAll(false);
			this.mysql.loadAllObjIDs();
		}
		FurnitureLib.getInstance().getFurnitureManager().sendAll();
	}
	
	private void isExist(){
		File fileDB = null;
		if(!plugin.getConfig().getBoolean("config.Database.importCheck")){return;}
		for(File file : new File("plugins/" + plugin.getName()).listFiles()){
			if(file.getName().substring(file.getName().length() - 3, file.getName().length()).equalsIgnoreCase(".db")){
				plugin.getLogger().info("Old Database File found: " + file.getName());
				plugin.getLogger().info("Start importing");
				fileDB = file;
				
			}
		}
		if(fileDB!=null){
			this.sqlite = new SQLite(plugin, fileDB.getName().replace(".db", ""));
			this.sqlite.load();
			this.sqlite.loadAll(true);
			plugin.getLogger().info("Import finish");
			this.sqlite.close();
			this.sqlite = null;
			plugin.getLogger().info("Make old Database unusable.");
			fileDB.renameTo(new File("plugins/" + plugin.getName(), fileDB.getName() + ".old"));
			fileDB.delete();
		}
	}
	
	public void save(){
		if(!plugin.getFurnitureManager().getObjectList().isEmpty()){
			List<ObjectID> objList = new ArrayList<ObjectID>();
			for(ObjectID obj : plugin.getFurnitureManager().getUpdateList()){
				if(!plugin.getFurnitureManager().getPreLoadetList().contains(obj)){
					if(plugin.getFurnitureManager().getUpdateList().contains(obj)){
						objList.add(obj);
					}
				}
			}
			for(ObjectID obj : plugin.getFurnitureManager().getObjectList()){
				if(!objList.contains(obj)){
					if(!plugin.getFurnitureManager().getPreLoadetList().contains(obj)){
							objList.add(obj);
					}
				}
			}

			
			for(ObjectID obj : objList){
				save(obj);
			}
			
			for(ObjectID obj : plugin.getFurnitureManager().getRemoveList()){
				remove(obj);
			}
		}else{
			for(ObjectID obj : plugin.getFurnitureManager().getRemoveList()){
				remove(obj);
			}
			plugin.getLogger().info("ObjectList Empty");
		}
	}
	
	public void save(ObjectID obj){
		if(this.sqlite!=null){
			this.sqlite.save(obj);
		}else if(this.mysql!=null){
			this.mysql.save(obj);
		}
	}
	
	public void remove(ObjectID obj){
		if(this.sqlite!=null){
			this.sqlite.delete(obj);
		}else if(this.mysql!=null){
			this.mysql.delete(obj);
		}
	}
	
	public void saveIntervall(int time){
		sqlSaveIntervall=Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				save();
				plugin.getLogger().info("Furniture Saved");
				plugin.getFurnitureManager().getRemoveList().clear();
				plugin.getFurnitureManager().getPreLoadetList().clear();
				plugin.getFurnitureManager().getPreLoadetList().addAll(plugin.getFurnitureManager().getObjectList());
			}
		}, 0, 20*time);
	}

	public void stop() {
		if(sqlSaveIntervall!=null){
			plugin.getServer().getScheduler().cancelTask(sqlSaveIntervall);
			sqlSaveIntervall = null;
		}
	}

	public void close() {
		if(this.sqlite!=null){
			this.sqlite.close();
			this.sqlite=null;
		}else if(this.mysql!=null){
			this.mysql.close();
			this.mysql=null;
		}
	}
	
}
