package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {

    private int[] data;

    NBTTagIntArray() {
    }

    public NBTTagIntArray(int[] aint) {
        this.data = aint;
    }

    public int[] getData() {
        return this.data;
    }

    @Override
    public NBTBase clone() {
        int[] aint = new int[this.data.length];

        System.arraycopy(this.data, 0, aint, 0, this.data.length);
        return new NBTTagIntArray(aint);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) && Arrays.equals(this.data, ((NBTTagIntArray) object).data);
    }

    @Override
    public byte getTypeId() {
        return (byte) 11;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.data);
    }

    @Override
    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
        int byte_length = 0;
        try {
            byte_length = datainput.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nbtreadlimiter.readBytes(32 * byte_length);
        this.data = new int[byte_length];

        for (int k = 0; k < byte_length; ++k) {
            try {
                this.data[k] = datainput.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        int[] aint = this.data;

		for (int k : aint) {
			s.append(k).append(",");
		}

        return s + "]";
    }

    @Override
    void write(DataOutput dataoutput) {
        try {
            dataoutput.writeInt(this.data.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

		for (int date : this.data) {
			try {
				dataoutput.writeInt(date);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
}
