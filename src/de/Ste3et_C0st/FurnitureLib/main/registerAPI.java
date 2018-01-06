package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Database.CallBack;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class registerAPI {

	CallBack callback = null;
	Plugin plugin = null;
	
	public registerAPI(Plugin plugin, CallBack callback) {
		this.plugin = plugin;
		this.callback = callback;
	}
	
	public Plugin getPlugin() {return this.plugin;}
	public CallBack getCallback() {return this.callback;}
	
	public void trigger() {
		if(callback != null) {
			callback.onResult(true);
			registerPluginFurnitures(getPlugin());
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
	
	public FurnitureLib getInstance() {return FurnitureLib.getInstance();}
	
	
}
