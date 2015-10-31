package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;

public class EntityID {

	public static int nextEntityId(){
		try{
			Field f = Reflection.getNMSClass("Entity").getDeclaredField("entityCount");
			f.setAccessible(true);
			int id = f.getInt(null);
			f.set(null, id+1);
			return id;
		}catch(Exception e){
			return 0;
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
