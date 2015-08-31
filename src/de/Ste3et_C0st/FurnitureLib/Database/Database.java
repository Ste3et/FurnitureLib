package de.Ste3et_C0st.FurnitureLib.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public abstract class Database {
    FurnitureLib plugin;
    Connection connection;
    Statement statement;
    public Database(FurnitureLib instance){
        plugin = instance;
    }
    
    public abstract Connection getSQLConnection();

    public abstract void load();
    public abstract DataBaseType getType();
    
    public boolean isExist(String s){
    	try{
    		boolean query = statement.execute("SELECT * FROM `"+s+"`");
    		if(query){return true;}
    	}catch(Exception e){
    		return false;
    	}
    	return false;
    }
    
    public void initialize(){
        connection = getSQLConnection();
        try{
        	statement = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM FurnitureLib_Objects");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
    public void save(ObjectID id){
    	String objid = id.getID();
    	String binary = FurnitureLib.getInstance().getSerializer().SerializeObjectID(id);
    	String query = "REPLACE INTO FurnitureLib_Objects (`ObjID`,`Data`) VALUES('"+ objid +"', '" + binary + "');";
    	try{
    		statement.executeUpdate(query);
    		if(isExist("FurnitureLib_ArmorStand")) statement.execute("DROP TABLE `FurnitureLib_ArmorStand`");
    		if(isExist("FurnitureLib_ObjectID")) statement.execute("DROP TABLE `FurnitureLib_ObjectID`");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void loadAll(SQLAction action){
    	try{
    		if(isExist("FurnitureLib_ArmorStand")) loadaltArmorStands(SQLAction.SAVE);
    		if(isExist("FurnitureLib_ObjectID")) loadAltObjIDs();
    		ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_Objects");
   		    List<String[]> asList = new ArrayList<String[]>();
    		while (rs.next()) {FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString(1), rs.getString(2));}
    		plugin.getLogger().info("FurnitureLib load " + asList.size()  +  " Objects from: " + getType().name() + " Database");
    		rs.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void loadaltArmorStands(SQLAction action)
    {
      try
      {
        ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_ArmorStand");
        List<String[]> asList = new ArrayList<String[]>();
        int i;
        while (rs.next())
        {
          String[] a = new String[17];
          for (i = 0; i <= 16; i++) {
            a[i] = rs.getString(i + 1);
          }
          if (!asList.contains(a)) {
            asList.add(a);
          }
        }
        rs.close();
        for (String[] l : asList) {
          FurnitureLib.getInstance().getSerialize().fromArmorStandString(l, action);
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    
    public void loadAltObjIDs()
    {
      try
      {
        ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_ObjectID");
        List<String[]> objList = new ArrayList<String[]>();
        int i;
        while (rs.next())
        {
          String[] a = new String[5];
          for (i = 0; i <= 4; i++) {
            a[i] = rs.getString(i + 1);
          }
          if (!objList.contains(a)) {
            objList.add(a);
          }
        }
        rs.close();
        for (String[] l : objList) {
          FurnitureLib.getInstance().getSerialize().fromArmorObjectString(l);
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    public void delete(ObjectID objID){
    	try {
			statement.execute("DELETE FROM FurnitureLib_Objects WHERE ObjID = '" + objID.getID() + "'");
		} catch (SQLException e) {
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