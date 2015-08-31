package de.Ste3et_C0st.FurnitureLib.NBT;

public class NBTReadLimiterUnlimited extends NBTReadLimiter {

	public static NBTReadLimiterUnlimited create(){
		return new NBTReadLimiterUnlimited(0L);
	}
	
	NBTReadLimiterUnlimited(long i) {
		super(i);
	}

	@Override
	public void readBytes(long i) {
	}
}
