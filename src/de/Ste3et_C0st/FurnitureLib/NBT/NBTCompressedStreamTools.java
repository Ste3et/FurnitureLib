package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTCompressedStreamTools {

	public static NBTTagCompound read(byte[] abyte, NBTReadLimiter nbtreadlimiter) throws Exception {
		DataInputStream datainputstream = null;
		try{
			datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte))));
		}catch (Exception e1){
			e1.printStackTrace();
		}

		NBTTagCompound nbttagcompound;

		try{
			nbttagcompound = read(datainputstream, nbtreadlimiter);
		}finally{
			try{
				datainputstream.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}

		return nbttagcompound;
	}

	public static NBTTagCompound read(DataInput datainput, NBTReadLimiter nbtreadlimiter) throws Exception {
		NBTBase nbtbase;
		byte b0 = 0;
		try{
			b0 = datainput.readByte();
		}catch (java.io.IOException e1){
			e1.printStackTrace();
		}

		if(b0 == 0){
			nbtbase = new NBTTagEnd();
		}else{
			try{
				datainput.readUTF();
			}catch (java.io.IOException e){
				e.printStackTrace();
			}
			nbtbase = NBTBase.createTag(b0);

			try{
				nbtbase.load(datainput, 0, nbtreadlimiter);
			}catch (IOException ioexception){
				throw new RuntimeException();
			}
		}

		if(nbtbase instanceof NBTTagCompound){
			return (NBTTagCompound) nbtbase;
		}else{
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static NBTTagCompound read(DataInputStream datainputstream) throws Exception {
		return read(datainputstream, NBTReadLimiter.unlimited);
	}

	public static NBTTagCompound read(InputStream inputstream) throws Exception {
		DataInputStream datainputstream = createCompressedInput(inputstream);
		NBTTagCompound nbttagcompound;
		try{
			nbttagcompound = read(datainputstream, NBTReadLimiter.unlimited);
		}finally{
			try{
				try{
					datainputstream.close();
				}catch (java.io.IOException e){
					e.printStackTrace();
				}
			}catch (IOException e){
				e.printStackTrace();
			}
		}

		return nbttagcompound;
	}

	public static byte[] toByte(NBTTagCompound nbttagcompound) throws Exception {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		DataOutputStream dataoutputstream = createCompressedOutput(bytearrayoutputstream);
		try{
			write(nbttagcompound, (DataOutput)dataoutputstream);
		}finally{
			try{
				dataoutputstream.close();
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		return bytearrayoutputstream.toByteArray();
	}

	public static void write(NBTTagCompound nbttagcompound, DataOutput dataoutput) throws Exception {
		try{
			dataoutput.writeByte(nbttagcompound.getTypeId());
			if(nbttagcompound.getTypeId() != 0){
				try{
					dataoutput.writeUTF("");
				}catch (java.io.IOException e){
					e.printStackTrace();
				}
				nbttagcompound.write(dataoutput);
			}
		}catch (java.io.IOException e){
			e.printStackTrace();
		}finally{
		}
	}

	public static void write(NBTTagCompound nbttagcompound, OutputStream outputstream) throws Exception {
		DataOutputStream dataoutputstream = createCompressedOutput(outputstream);
		try{
			write(nbttagcompound,(DataOutput) dataoutputstream);
		}finally{
			try{
				dataoutputstream.close();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private static DataOutputStream createCompressedOutput(OutputStream out){
		try{
			return new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(out)));
		}catch (java.io.IOException e){
			e.printStackTrace();
			return null;
		}
	}
	private static DataInputStream createCompressedInput(InputStream in){
		try{
			return new DataInputStream(new BufferedInputStream(new GZIPInputStream(in)));
		}catch (java.io.IOException e){
			e.printStackTrace();
			return null;
		}
	}
}
