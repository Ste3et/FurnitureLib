package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {

	private byte[] data;

	NBTTagByteArray() {
	}

	public NBTTagByteArray(byte[] abyte) {
		this.data = abyte;
	}

	public byte[] getData() {
		return this.data;
	}

	@Override
	public NBTBase clone() {
		byte[] abyte = new byte[this.data.length];

		System.arraycopy(this.data, 0, abyte, 0, this.data.length);
		return new NBTTagByteArray(abyte);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) ? Arrays.equals(this.data, ((NBTTagByteArray) object).data) : false;
	}

	@Override
	public byte getTypeId() {
		return (byte) 7;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.data);
	}

	@Override
	void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) {
		int length = 0;
		try{
			length = datainput.readInt();
		}catch (IOException e){
			e.printStackTrace();
		}

		nbtreadlimiter.readBytes(8 * length);
		this.data = new byte[length];
		try{
			datainput.readFully(this.data);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "[" + this.data.length + " bytes]";
	}

	@Override
	void write(DataOutput dataoutput) {
		try{
			dataoutput.writeInt(this.data.length);
		}catch (IOException e){
			e.printStackTrace();
		}
		try{
			dataoutput.write(this.data);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
