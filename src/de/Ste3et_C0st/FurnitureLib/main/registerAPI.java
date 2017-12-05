package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Database.DataBaseCallBack;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class registerAPI {

	DataBaseCallBack callback = null;
	Plugin plugin = null;
	
	public registerAPI(Plugin plugin) {
		this.plugin = plugin;
		callback = new DataBaseCallBack() {
			@Override
			public void onResult(boolean paramBoolean) {
				if(paramBoolean) {
					registerPluginFurnitures(getPlugin());
				}
			}
		};
	}
	
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public void trigger() {
		if(callback != null) {
			callback.onResult(true);
		}
	}
	
	public void registerPluginFurnitures(Plugin plugin){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : getInstance().getFurnitureManager().getObjectList()){
			if(obj==null) continue;
			if(objList.contains(obj)) continue;
			if(!objList.contains(obj)) objList.add(obj);
			if(obj.getPlugin()==null) continue;
			if(obj.getSQLAction().equals(SQLAction.REMOVE)) continue;
			if(obj.getPlugin().equalsIgnoreCase(plugin.getName())){
				getInstance().spawn(obj.getProjectOBJ(), obj);
			}
		}
	}
	
	public FurnitureLib getInstance() {
		return FurnitureLib.getInstance();
	}
	
	
}
