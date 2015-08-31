package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
	private String data;

	public NBTTagString() {
		this.data = "";
	}

	public NBTTagString(String paramString) {
		this.data = paramString;
		if(paramString == null){
			throw new IllegalArgumentException("Empty string not allowed");
		}
	}

	void write(DataOutput paramDataOutput) {
		try{
			paramDataOutput.writeUTF(this.data);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	void load(DataInput paramDataInput, int paramInt, NBTReadLimiter paramNBTReadLimiter) {
		try{
			this.data = paramDataInput.readUTF();
		}catch (IOException e){
			e.printStackTrace();
		}
		paramNBTReadLimiter.readBytes(16 * this.data.length());
	}

	public byte getTypeId() {
		return 8;
	}

	public String toString() {
		return "\"" + this.data + "\"";
	}

	public NBTBase clone() {
		return new NBTTagString(this.data);
	}

	public boolean equals(Object paramObject) {
		if(super.equals(paramObject)){
			NBTTagString localNBTTagString = (NBTTagString) paramObject;
			return ((this.data == null) && (localNBTTagString.data == null)) || ((this.data != null) && (this.data.equals(localNBTTagString.data)));
		}
		return false;
	}

	public int hashCode() {
		return super.hashCode() ^ this.data.hashCode();
	}

	public String a_() {
		return this.data;
	}
}
