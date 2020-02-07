package de.Ste3et_C0st.FurnitureLib.Utilitis;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class EntityID {

    public static int nextEntityIdOld() {
        try {
            Class<?> entityClass = Class.forName("net.minecraft.server." + getVersion() + "Entity");
            Field f = entityClass.getDeclaredField("entityCount");
            f.setAccessible(true);
            int id = f.getInt(null);
            f.set(null, id + 1);
            return id;
        } catch (Exception e) {
            FurnitureLib.debug(e.getMessage());
            return 0;
        }
    }

    public static int nextEntityIdNew() {
        try {
            Class<?> entityClass = Class.forName("net.minecraft.server." + getVersion() + "Entity");
            Field f = entityClass.getDeclaredField("entityCount");
            f.setAccessible(true);
            Object obj = f.get(null);
			return (int) obj.getClass().getMethod("incrementAndGet").invoke(obj);
        } catch (Exception e) {
            FurnitureLib.debug(e.getMessage());
            return 0;
        }
    }

    public static int nextEntityId() {
        if (Type.version.equalsIgnoreCase("1.14") || Type.version.equalsIgnoreCase("1.15")) {
            return nextEntityIdNew();
        } else {
            return nextEntityIdOld();
        }
    }

    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
		return name.substring(name.lastIndexOf('.') + 1) + ".";
    }
}
