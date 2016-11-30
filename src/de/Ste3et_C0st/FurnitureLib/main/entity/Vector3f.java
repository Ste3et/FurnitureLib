package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.lang.reflect.Constructor;

import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class Vector3f {
	@Deprecated
	public Object a(EulerAngle angle){
		try{
			Class<?> Vector3f = Class.forName("net.minecraft.server." + FurnitureLib.getInstance().getBukkitVersion() + ".Vector3f");
			Constructor<?> ctor = Vector3f.getConstructors()[0];
			return ctor.newInstance((float) angle.getX(), (float) angle.getY(), (float) angle.getZ());
		}catch(Exception e){
			System.out.println("Your Server Version are not Supportet the class 'Vector3f' is missing");
			return null;
		}
	}
	@Deprecated
	public Object[] b(){
		try{
			Class<?> EnumItemSlot = Class.forName("net.minecraft.server." + FurnitureLib.getInstance().getBukkitVersion() + ".EnumItemSlot");
			return EnumItemSlot.getEnumConstants();
		}catch(Exception e){
			System.out.println("Your Server Version are not Supportet the class 'EnumItemSlot' is missing");
			return null;
		}
	}
}
