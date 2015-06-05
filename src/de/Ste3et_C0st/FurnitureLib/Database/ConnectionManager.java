package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;

public class ConnectionManager {
  private static List<Connection> connections = new ArrayList<Connection>();
  private static Database sql;
  
  public static boolean createConnection(Database database) {
    if (connections.contains(database.getConnection())) {
      return false;
  }
    Connection con;
    try {
    	database.open();
    	con = database.getConnection();
    }
    catch (Exception ex) {
      return false;
    }
  
    connections.add(con);
    return true;
  }

  public static Connection getConnection(Database database) {
    if (!connections.contains(database.getConnection())) {
      if (!createConnection(database)) {
        return null;
      }
    }
    return connections.get(connections.indexOf(database.getConnection()));
  }
  
  public static void createDatabase(Plugin plugin){
	  sql = new SQLite(
				Logger.getLogger("Minecraft"),
				"[FurnitureLib]", 
				plugin.getDataFolder().getAbsolutePath(), 
				"Furniture", 
				".sqlite");
  }
  
  public static Database getDataBase(){return sql;}
}