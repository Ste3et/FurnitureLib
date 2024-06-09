package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.Optional;

import com.comphenix.protocol.utility.MinecraftReflection;

public class InternalClassReader {

	private final String className, rawClassName;
	private final Optional<Class<?>> clazzOptional;
	
	public static final String packetVersion = MinecraftReflection.getPackageVersion() == null ? "" : MinecraftReflection.getPackageVersion();
	
	public static final String NMS = "net.minecraft.server";
	public static final String NBT = "net.minecraft.nbt";
	public static final String OBC = String.format("org.bukkit.craftbukkit%s", getPacketVersion().isEmpty() ? "" : "." + InternalClassReader.getPacketVersion());
	
	public InternalClassReader(String className) {
		System.out.println(getPacketVersion());
		this.className = className.contains("%s") ? String.format(className, getPacketVersion()) : className;
		this.rawClassName = className;
		
		Optional<Class<?>> classOptional = Optional.empty();
		
		try {
			classOptional = Optional.ofNullable(Class.forName(this.className));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		this.clazzOptional = classOptional;
	}
	
	public Optional<Class<?>> getOptional(){
		return this.clazzOptional;
	}
	
	public boolean isPresent() {
		return this.clazzOptional.isPresent();
	}
	
	public Class<?> getClazz() throws ClassNotFoundException{
		if(clazzOptional.isPresent() == false) {
			
			return null;
		}else {
			return this.clazzOptional.get();
		}
	}
	
	public String getFormated() {
		return this.className;
	}
	
	public String getRawString() {
		return this.rawClassName;
	}
	
	public static String getPacketVersion() {
		return packetVersion;
	}
	
}
