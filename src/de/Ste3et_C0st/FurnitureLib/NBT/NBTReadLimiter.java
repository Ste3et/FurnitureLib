package de.Ste3et_C0st.FurnitureLib.NBT;

public class NBTReadLimiter {

	public static final NBTReadLimiter unlimited = new NBTReadLimiterUnlimited(0L);
	private final long limit;
	private long readed;

	public NBTReadLimiter(long limit) {
		this.limit = limit;
	}

	public void readBytes(long bytes) {
		this.readed += bytes / 8L;
		if(this.readed > this.limit){
			throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.readed + "bytes where max allowed: " + this.limit);
		}
	}
}
