package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTNumber {

    private int data;

    NBTTagInt() {
    }

    public NBTTagInt(int i) {
        this.data = i;
    }

    @Override
    public long asLong() {
        return this.data;
    }

    @Override
    public NBTBase clone() {
        return new NBTTagInt(this.data);
    }

    @Override
    public int asInt() {
        return this.data;
    }

    @Override
    public short asShort() {
        return (short) (this.data & '\uffff');
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagInt nbttagint = (NBTTagInt) object;

            return this.data == nbttagint.data;
        } else {
            return false;
        }
    }

    @Override
    public byte asByte() {
        return (byte) (this.data & 255);
    }

    @Override
    public double asDouble() {
        return this.data;
    }

    @Override
    public byte getTypeId() {
        return (byte) 3;
    }

    @Override
    public float asFloat() {
        return this.data;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    @Override
    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
        nbtreadlimiter.readBytes(32L);
        try {
            this.data = datainput.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "" + this.data;
    }

    @Override
    void write(DataOutput dataoutput) {
        try {
            dataoutput.writeInt(this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
