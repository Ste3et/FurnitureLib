package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper;

import java.lang.reflect.Constructor;

import org.bukkit.Location;

import com.comphenix.protocol.utility.MinecraftReflection;

public class WrappedPostionMoveRotation{

	private final Vec3 position, deltaMovement;
	private final float yRot, xRot;
	private static final Class<?> NMSCLASS;
	private static Constructor<?> CONSTRUCTOR;
	
	static {
		NMSCLASS = MinecraftReflection.getMinecraftClass("world.entity.PositionMoveRotation", "PositionMoveRotation");
		
		try {
			CONSTRUCTOR = NMSCLASS.getConstructor(Vec3.getNMS(), Vec3.getNMS(), Float.TYPE, Float.TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	public WrappedPostionMoveRotation(Location location) {
		this(new Vec3(location), Vec3.ZERO, location.getYaw(), location.getPitch());
	}

	public WrappedPostionMoveRotation(Vec3 position, Vec3 deltaMovement, float yRot, float xRot) {
		this.position = position;
		this.deltaMovement = deltaMovement;
		this.yRot = yRot;
		this.xRot = xRot;
	}
	
	public Object build() {
		Object object = null;
		
		try {
			object = CONSTRUCTOR.newInstance(position.build(), deltaMovement.build(), yRot, xRot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	public static Class<?> getNMS() {
		return NMSCLASS;
	}
}
