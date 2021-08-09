package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class MySQL extends Database {
	
    public String Objects = "CREATE TABLE IF NOT EXISTS furnitureLibData (" +
            "`ObjID` varchar(255) NOT NULL," +
            "`Data` TEXT NOT NULL," +
            "`world` TEXT NOT NULL," +
            "`x` int NOT NULL," +
            "`z` int NOT NULL," +
            "`uuid` TEXT NOT NULL, PRIMARY KEY (ObjID)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
    private DataBaseType type = DataBaseType.MySQL;

    public MySQL(FurnitureLib instance, HikariConfig config) {
        super(instance, config);
        createTable();
    }

    public void createTable() {
        Connection con = null;
        Statement stmt = null;

        try {
            con = getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(Objects);
            FurnitureLib.debug("MySQL createTable -> " + Objects);
        } catch (SQLException e) {
            FurnitureLib.debug("MySQL createTable: Fail");
            e.printStackTrace();
        } finally {
            try { if(stmt != null) stmt.close(); } catch (Exception e) {}
            try { if(con != null) con.close(); } catch (Exception e) {}
        }
    }

    public DataBaseType getType() {
        return this.type;
    }
}