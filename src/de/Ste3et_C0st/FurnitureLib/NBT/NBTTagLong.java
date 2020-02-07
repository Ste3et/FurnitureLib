package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTNumber {

    private long data;

    NBTTagLong() {
    }

    public NBTTagLong(long i) {
        this.data = i;
    }

    @Override
    public long asLong() {
        return this.data;
    }

    @Override
    public NBTBase clone() {
        return new NBTTagLong(this.data);
    }

    @Override
    public int asInt() {
        return (int) (this.data);
    }

    @Override
    public short asShort() {
        return (short) (int) (this.data & 65535L);
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagLong nbttaglong = (NBTTagLong) object;

            return this.data == nbttaglong.data;
        } else {
            return false;
        }
    }

    @Override
    public byte asByte() {
        return (byte) (int) (this.data & 255L);
    }

    @Override
    public double asDouble() {
        return this.data;
    }

    @Override
    public byte getTypeId() {
        return (byte) 4;
    }

    @Override
    public float asFloat() {
        return this.data;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (int) (this.data ^ this.data >>> 32);
    }

    @Override
    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
        nbtreadlimiter.readBytes(64L);
        try {
            this.data = datainput.readLong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "" + this.data + "L";
    }

    @Override
    void write(DataOutput dataoutput) {
        try {
            dataoutput.writeLong(this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
