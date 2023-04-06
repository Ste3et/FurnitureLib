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
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            stmt.executeUpdate(Objects);
            FurnitureLib.debug("MySQL createTable -> " + Objects, 0);
        } catch (SQLException e) {
            FurnitureLib.debug("MySQL createTable: Fail", 10);
            e.printStackTrace();
        }
    }

    public DataBaseType getType() {
        return this.type;
    }
}