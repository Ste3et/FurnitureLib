package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import java.lang.reflect.Constructor;
import org.bukkit.Location;

import com.comphenix.protocol.utility.MinecraftReflection;

public class Vec3 {

	private final double x, y, z;
	
	public static final Vec3 ZERO;
	private static final Class<?> VEC3;
	private static Constructor<?> CONSTRUCTOR;
	
	static {
		ZERO = new Vec3(0.0, 0.0, 0.0);
		VEC3 = MinecraftReflection.getMinecraftClass("world.phys.Vec3", "Vec3");

		try {
			CONSTRUCTOR = VEC3.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public Vec3(Location location) {
		this(location.getX(), location.getY(), location.getZ());
	}
	
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Object build() {
		Object object = null;
		
		try {
			object = CONSTRUCTOR.newInstance(x, y, z);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	public static Class<?> getNMS() {
		return VEC3;
	}
}
