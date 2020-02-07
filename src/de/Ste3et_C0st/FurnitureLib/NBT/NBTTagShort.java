package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTNumber {

    private short data;

    public NBTTagShort() {
    }

    public NBTTagShort(short short1) {
        this.data = short1;
    }

    @Override
    public long asLong() {
        return this.data;
    }

    @Override
    public NBTBase clone() {
        return new NBTTagShort(this.data);
    }

    @Override
    public int asInt() {
        return this.data;
    }

    @Override
    public short asShort() {
        return this.data;
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagShort nbttagshort = (NBTTagShort) object;

            return this.data == nbttagshort.data;
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
        return (byte) 2;
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
        nbtreadlimiter.readBytes(16L);
        try {
            this.data = datainput.readShort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "" + this.data + "s";
    }

    @Override
    void write(DataOutput dataoutput) {
        try {
            dataoutput.writeShort(this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
