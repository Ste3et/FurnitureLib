package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLite extends Database {

    public String Objects = "CREATE TABLE IF NOT EXISTS furnitureLibData (" +
            "`ObjID` STRING NOT NULL PRIMARY KEY," +
            "`Data` STRING NOT NULL," +
            "`world` STRING NOT NULL," +
            "`x` int NOT NULL," +
            "`z` int NOT NULL," +
            "`uuid` STRING NOT NULL" +
            ");";
    private DataBaseType type = DataBaseType.SQLite;

    public SQLite(FurnitureLib instance, HikariConfig config) {
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
        } catch (SQLException e) {
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