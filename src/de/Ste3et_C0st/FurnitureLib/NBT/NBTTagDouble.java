package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagDouble extends NBTNumber {

    private double data;

    NBTTagDouble() {
    }

    public NBTTagDouble(double d0) {
        this.data = d0;
    }

    @Override
    public long asLong() {
        return (long) Math.floor(this.data);
    }

    @Override
    public NBTBase clone() {
        return new NBTTagDouble(this.data);
    }

    @Override
    public int asInt() {
        return MathHelper.floor(this.data);
    }

    @Override
    public short asShort() {
        return (short) (MathHelper.floor(this.data) & '\uffff');
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagDouble nbttagdouble = (NBTTagDouble) object;

            return this.data == nbttagdouble.data;
        } else {
            return false;
        }
    }

    @Override
    public byte asByte() {
        return (byte) (MathHelper.floor(this.data) & 255);
    }

    @Override
    public double asDouble() {
        return this.data;
    }

    @Override
    public byte getTypeId() {
        return (byte) 6;
    }

    @Override
    public float asFloat() {
        return (float) this.data;
    }

    @Override
    public int hashCode() {
        long i = Double.doubleToLongBits(this.data);

        return super.hashCode() ^ (int) (i ^ i >>> 32);
    }

    @Override
    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
        nbtreadlimiter.readBytes(64L);
        try {
            this.data = datainput.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "" + this.data + "d";
    }

    @Override
    void write(DataOutput dataoutput) {
        try {
            dataoutput.writeDouble(this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
