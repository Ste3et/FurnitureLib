package de.Ste3et_C0st.FurnitureLib.Database;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class SQLStatement {

	private StringBuilder sqlStatement = new StringBuilder("REPLACE INTO furnitureLibData (ObjID, Data, world, `x`, `z`, `uuid`) VALUES ");
	private int counter = 0;
	
	public SQLStatement(ObjectID ... obj) {
		if(obj.length > 0) {
			add(obj);
		}
	}
	
	public SQLStatement() {}
	
	public String getStatement() {
		return this.sqlStatement.toString();
	}
	
	public void add(ObjectID ... obj) {
		add(Arrays.asList(obj));
	}
	
	public void add(List<ObjectID> obj) {
		Iterator<ObjectID> iterator = obj.iterator();
		while (iterator.hasNext()) {
			ObjectID object = iterator.next();
			String binary = FurnitureLib.getInstance().getSerializer().SerializeObjectID(object);
			int x = object.getStartLocation().getBlockX() >> 4;
	        int z = object.getStartLocation().getBlockZ() >> 4;
			StringBuilder singleStatement = new StringBuilder(
					"(" + "'" + object.getID() + "'," + "'" + binary + "'," + "'" + object.getWorldName() + "'," +  + x + "," + + z + "," + "'" + object.getUUID().toString() + "')"
			);
			if (!iterator.hasNext()) {
				singleStatement.append(";");
			}else {
				singleStatement.append(",");
			}
			sqlStatement.append(singleStatement);
			counter++;
			object.setSQLAction(SQLAction.NOTHING);
		}
	}
	
	public int getCounter() {
		return this.counter;
	}
}
