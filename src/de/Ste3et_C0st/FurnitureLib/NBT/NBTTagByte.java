package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTNumber {

	private byte data;

	NBTTagByte() {
	}

	public NBTTagByte(byte b0) {
		this.data = b0;
	}

	@Override
	public long asLong() {
		return this.data;
	}

	@Override
	public NBTBase clone() {
		return new NBTTagByte(this.data);
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
		if(super.equals(object)){
			NBTTagByte nbttagbyte = (NBTTagByte) object;

			return this.data == nbttagbyte.data;
		}else{
			return false;
		}
	}

	@Override
	public byte asByte() {
		return this.data;
	}

	@Override
	public double asDouble() {
		return this.data;
	}

	@Override
	public byte getTypeId() {
		return (byte) 1;
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
		nbtreadlimiter.readBytes(8L);
		try{
			this.data = datainput.readByte();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "" + this.data + "b";
	}

	@Override
	void write(DataOutput dataoutput) {
		try{
			dataoutput.writeByte(this.data);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
