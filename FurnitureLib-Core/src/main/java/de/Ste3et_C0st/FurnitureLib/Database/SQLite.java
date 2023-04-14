package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;

public class SQLite extends Database {

    public String query = "CREATE TABLE IF NOT EXISTS furnitureLibData (" +
            "`ObjID` STRING NOT NULL PRIMARY KEY," +
            "`Data` STRING NOT NULL," +
            "`world` STRING NOT NULL," +
            "`x` int NOT NULL," +
            "`z` int NOT NULL," +
            "`uuid` STRING NOT NULL" +
            ");";

    public SQLite(FurnitureLib instance, HikariConfig config) {
        super(instance, config);
        createTable(query);
    }

    public DataBaseType getType() {
        return DataBaseType.SQLite;
    }
}