package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type;

public class EntityID {

	public static int nextEntityIdOld(){
		try{
			Class<?> entityClass = Class.forName("net.minecraft.server." + getVersion() + "Entity");
			Field f = entityClass.getDeclaredField("entityCount");
			f.setAccessible(true);
			int id = f.getInt(null);
			f.set(null, id+1);
			return id;
		}catch(Exception e){
			FurnitureLib.debug(e.getMessage());
			return 0;
		}
	}
	
	public static int nextEntityIdNew(){
		try {
			Class<?> entityClass = Class.forName("net.minecraft.server." + getVersion() + "Entity");
			Field f = entityClass.getDeclaredField("entityCount"); 
			f.setAccessible(true);
			Object obj = f.get(null);
			int id = (int) obj.getClass().getMethod("incrementAndGet").invoke(obj);
			return id;
		}catch (Exception e) {
			FurnitureLib.debug(e.getMessage());
			return 0;
		}
	}
	
	public static int nextEntityId() {
		if(Type.version.equalsIgnoreCase("1.14")) {
			return nextEntityIdNew();
		}else {
			return nextEntityIdOld();
		}
	}
	
	public static Class<?> getNMSClass(String className) {
		String fullName = "net.minecraft.server." + getVersion() + className;
		Class<?> clazz = null;
		try{
			clazz = Class.forName(fullName);
		}catch (Exception e){
			e.printStackTrace();
		}
		return clazz;
	}
	public static String getVersion() {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		String version = name.substring(name.lastIndexOf('.') + 1) + ".";
		return version;
	}
}
