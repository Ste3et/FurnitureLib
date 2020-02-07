package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTNumber {

    private float data;

    NBTTagFloat() {
    }

    public NBTTagFloat(float f) {
        this.data = f;
    }

    @Override
    public long asLong() {
        return (long) this.data;
    }

    @Override
    public NBTBase clone() {
        return new NBTTagFloat(this.data);
    }

    @Override
    public int asInt() {
        return MathHelper.d(this.data);
    }

    @Override
    public short asShort() {
        return (short) (MathHelper.d(this.data) & '\uffff');
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagFloat nbttagfloat = (NBTTagFloat) object;

            return this.data == nbttagfloat.data;
        } else {
            return false;
        }
    }

    @Override
    public byte asByte() {
        return (byte) (MathHelper.d(this.data) & 255);
    }

    @Override
    public double asDouble() {
        return this.data;
    }

    @Override
    public byte getTypeId() {
        return (byte) 5;
    }

    @Override
    public float asFloat() {
        return this.data;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }

    @Override
    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
        nbtreadlimiter.readBytes(32L);
        try {
            this.data = datainput.readFloat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "" + this.data + "f";
    }

    @Override
    void write(DataOutput dataoutput) {
        try {
            dataoutput.writeFloat(this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
