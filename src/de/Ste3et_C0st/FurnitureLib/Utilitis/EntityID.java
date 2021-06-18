package de.Ste3et_C0st.FurnitureLib.Utilitis;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityID {

	private static Class<?> entityClass;
	private static Field entityCountField;
	public static String version;
	
	static {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		version = name.substring(name.lastIndexOf('.') + 1) + ".";
		try {
			entityClass = Class.forName(FurnitureLib.getVersionInt() == 17 ? "net.minecraft.world.entity.Entity" : "net.minecraft.server." + getVersion() + "Entity");
			entityCountField = entityClass.getDeclaredField(FurnitureLib.getVersionInt() == 17 ? "b" : "entityCount");
			entityCountField.setAccessible(true);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
    public static int nextEntityIdOld() {
        try {
            int id = entityCountField.getInt(null);
            entityCountField.set(null, id + 1);
            return id;
        } catch (Exception e) {
            FurnitureLib.debug(e.getMessage());
            return 0;
        }
    }

    public static int nextEntityIdNew() {
        try {
			return AtomicInteger.class.cast(entityCountField.get(null)).incrementAndGet();
        } catch (Exception e) {
            FurnitureLib.debug(e.getMessage());
            return 0;
        }
    }

    public static int nextEntityId() {
        if (FurnitureLib.getVersionInt() > 13) {
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
		return version;
    }
}
