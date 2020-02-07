package de.Ste3et_C0st.FurnitureLib.NBT;

public class NBTReadLimiterUnlimited extends NBTReadLimiter {

    NBTReadLimiterUnlimited(long i) {
        super(i);
    }

    public static NBTReadLimiterUnlimited create() {
        return new NBTReadLimiterUnlimited(0L);
    }

    @Override
    public void readBytes(long i) {
    }
}
