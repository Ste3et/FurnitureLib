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
    	String query = "REPLACE INTO FurnitureLib_Objects (`ObjID`,`Data`) VALUES('"+id.getID()+"', '" + FurnitureLib.getInstance().getSerializer().SerializeObjectID(id) + "');";
    	try{
    		statement.executeUpdate(query);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public void loadAll(SQLAction action){
    	try{
    		ResultSet rs = statement.executeQuery("SELECT * FROM FurnitureLib_Objects");
    		List<String[]> asList = new ArrayList<String[]>();
    		while (rs.next()) {
				FurnitureLib.getInstance().getDeSerializer().Deserialze(rs.getString(1), rs.getString(2));
			}
    		
    		plugin.getLogger().info("FurnitureLib load " + asList.size()  +  " Objects from: " + getType().name() + " Database");
    		rs.close();
    	}catch(Exception e){
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
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
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