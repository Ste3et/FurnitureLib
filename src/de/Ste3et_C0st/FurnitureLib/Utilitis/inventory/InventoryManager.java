package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Objects;
import de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage.ManageInventoryAqua;
import de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage.ManageInventoryCombat;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class InventoryManager {
	
	private HashMap<String, Class<? extends InventoryHandler>> inventoryMap = new HashMap<String, Class<? extends InventoryHandler>>();
	
	public InventoryManager() {
		this.inventoryMap.put("manage", FurnitureLib.isNewVersion() ? ManageInventoryAqua.class : ManageInventoryCombat.class);
	}
	
	public Class<? extends InventoryHandler> getInventory(String key) {
		if(Objects.isNull(key)) return null;
		return inventoryMap.getOrDefault(key.toLowerCase(), null);
	}
	
	public boolean isInventoryPresent(String key) {
		return getInventory(key) != null;
	}
	
	public InventoryHandler openInventory(String key, Object ... objects){
		Class<? extends InventoryHandler> inventoryClazz = getInventory(key);
		try {
			if(Objects.nonNull(inventoryClazz)){
				Constructor<?> constructor = inventoryClazz.getConstructors()[0];
				if(Objects.nonNull(constructor)) {
					Object returnValue = constructor.newInstance(objects);
					return Objects.nonNull(returnValue) ? InventoryHandler.class.cast(returnValue) : null;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static enum InventoryMode{
		ADDFRIEND("playerAddInvName"),
		REMOVEFRIEND("playerRemoveInvName"),
		SETOWNER("playerSetInvName");
		
		String titleName;
		
		InventoryMode(String titleName) {
			this.titleName = titleName;
		}
		
		public String getInvTitle() {
			return titleName;
		};
	}
}
