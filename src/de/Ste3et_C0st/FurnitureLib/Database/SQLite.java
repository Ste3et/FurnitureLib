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
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(Objects);
            System.out.println(Objects);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataBaseType getType() {
        return this.type;
    }
}