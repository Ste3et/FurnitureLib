package de.Ste3et_C0st.FurnitureLib.Database;

import com.zaxxer.hikari.HikariConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type.DataBaseType;

public class MySQL extends Database {
	
    public String query = "CREATE TABLE IF NOT EXISTS furnitureLibData (" +
            "`ObjID` varchar(255) NOT NULL," +
            "`Data` TEXT NOT NULL," +
            "`world` TEXT NOT NULL," +
            "`x` int NOT NULL," +
            "`z` int NOT NULL," +
            "`uuid` TEXT NOT NULL, PRIMARY KEY (ObjID)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    public MySQL(FurnitureLib instance, HikariConfig config) {
        super(instance, config);
        createTable(query);
    }

    public DataBaseType getType() {
        return DataBaseType.MySQL;
    }
}