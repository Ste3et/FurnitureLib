package de.Ste3et_C0st.FurnitureLib.NBT;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class NBTTagCompound extends NBTBase implements Cloneable {

    private static final Logger logger = FurnitureLib.getInstance() == null ? null : FurnitureLib.getInstance().getLogger();
    private Map map = new HashMap();

    public NBTTagCompound() {
    }

    static NBTBase createNBTBase(byte type, String s, DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws Exception {
        NBTBase nbtbase = NBTBase.createTag(type);
        nbtbase.load(datainput, i, nbtreadlimiter);
        return nbtbase;
    }

    static Map getDataAsMap(NBTTagCompound nbttagcompound) {
        return nbttagcompound.map;
    }

    private static void writeTag(String s, NBTBase nbtbase, DataOutput dataoutput) throws Exception {
        try {
            dataoutput.writeByte(nbtbase.getTypeId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (nbtbase.getTypeId() != 0) {
            try {
                dataoutput.writeUTF(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            nbtbase.write(dataoutput);
        }
    }

    private static String readString(DataInput datainput, NBTReadLimiter nbtreadlimiter) {
        try {
            return datainput.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte readByte(DataInput datainput, NBTReadLimiter nbtreadlimiter) {
        try {
            return datainput.readByte();
        } catch (IOException e) {
            e.printStackTrace();
            return 0x00;
        }
    }

    public byte getNBTBaseType(String s) {
        NBTBase nbtbase = (NBTBase) this.map.get(s);
        return nbtbase != null ? nbtbase.getTypeId() : 0;
    }

    public Set c() {
        return this.map.keySet();
    }

    @Override
    public NBTBase clone() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.map.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            nbttagcompound.set(s, ((NBTBase) this.map.get(s)).clone());
        }

        return nbttagcompound;
    }
    
    public void cloneFrom(NBTTagCompound source) {
    	Iterator iterator = source.map.keySet().iterator();
    	while (iterator.hasNext()) {
          String s = (String) iterator.next();
          this.set(s, ((NBTBase) this.map.get(s)).clone());
        }
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) object;

            return this.map.entrySet().equals(nbttagcompound.map.entrySet());
        } else {
            return false;
        }
    }

    public NBTBase get(String s) {
        return (NBTBase) this.map.get(s);
    }

    public boolean getBoolean(String s) {
        return this.getByte(s) != 0;
    }

    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }
    
    public byte getByte(String key, byte defaultVaule) {
        try {
            return !this.map.containsKey(key) ? defaultVaule : ((NBTNumber) this.map.get(key)).asByte();
        } catch (ClassCastException classcastexception) {
            return (byte) defaultVaule;
        }
    }

    public byte[] getByteArray(String key) {
        try {
            return !this.map.containsKey(key) ? new byte[0] : ((NBTTagByteArray) this.map.get(key)).getData();
        } catch (ClassCastException classcastexception) {
            throw new RuntimeException();
        }
    }

    public NBTTagCompound getCompound(String key) {
        try {
            return !this.map.containsKey(key) ? new NBTTagCompound() : (NBTTagCompound) this.map.get(key);
        } catch (ClassCastException classcastexception) {
            throw new RuntimeException();
        }
    }

    public double getDouble(String key) {
        return getDouble(key, 0D);
    }
    
    public double getDouble(String key, double defaultValue) {
        try {
            return !this.map.containsKey(key) ? defaultValue : ((NBTNumber) this.map.get(key)).asDouble();
        } catch (ClassCastException classcastexception) {
            return defaultValue;
        }
    }

    public float getFloat(String key) {
    	return getFloat(key, 0F);
    }
    
    public float getFloat(String key, float defaultValue) {
        try {
            return !this.map.containsKey(key) ? defaultValue : ((NBTNumber) this.map.get(key)).asFloat();
        } catch (ClassCastException classcastexception) {
            return defaultValue;
        }
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }
    
    public int getInt(String key, int defaultValue) {
        try {
            return !this.map.containsKey(key) ? defaultValue : ((NBTNumber) this.map.get(key)).asInt();
        } catch (ClassCastException classcastexception) {
            return defaultValue;
        }
    }

    public int[] getIntArray(String key) {
        return getIntArray(key, new int[0]);
    }
    
    public int[] getIntArray(String key, int[] defaultValue) {
        try {
            return !this.map.containsKey(key) ? defaultValue : ((NBTTagIntArray) this.map.get(key)).getData();
        } catch (ClassCastException classcastexception) {
            return defaultValue;
        }
    }

    public NBTTagList getList(String key) {
        try {
            if (this.getNBTBaseType(key) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList nbttaglist = (NBTTagList) this.map.get(key);

                return nbttaglist;
            }
        } catch (ClassCastException classcastexception) {
            throw new RuntimeException();
        }
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }
    
    public long getLong(String key, long defaultValue) {
        try {
            return !this.map.containsKey(key) ? defaultValue : ((NBTNumber) this.map.get(key)).asLong();
        } catch (ClassCastException classcastexception) {
            return defaultValue;
        }
    }
    
    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    public short getShort(String key, short defaultValue) {
        try {
            return !this.map.containsKey(key) ? defaultValue : ((NBTNumber) this.map.get(key)).asShort();
        } catch (ClassCastException classcastexception) {
            return (short) defaultValue;
        }
    }

    public String getString(String key) {
        return getString(key, "");
    }
    
    public String getString(String key, String defaultValue) {
        String str = "";
        try {
            str = !this.map.containsKey(key) ? defaultValue : this.map.get(key).toString();
        } catch (ClassCastException classcastexception) {
            return defaultValue;
        }

        return str != null ? str.replaceAll("\"", "") : str;
    }

    @Override
    public byte getTypeId() {
        return (byte) 10;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.map.hashCode();
    }

    public boolean hasKey(String key) {
        return this.map.containsKey(key);
    }

    public boolean hasKeyOfType(String s, int i) {
        byte b0 = this.getNBTBaseType(s);

        return b0 == i || i == 99 && (b0 == 1 || b0 == 2 || b0 == 3 || b0 == 4 || b0 == 5 || b0 == 6);
    }
    
    public <M extends NBTBase> boolean getCompound(String key, final Class<M> clazz, Consumer<? super M> consumer){
    	try {
    		NBTBase base = this.get(key);
    		if(base != null && clazz.isInstance(this.get(key))) {
    			consumer.accept((M) base);
    			return true;
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}
    	return false;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws Exception {
        if (i > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.map.clear();

            byte b0;

            while ((b0 = readByte(datainput, nbtreadlimiter)) != 0) {
                String s = readString(datainput, nbtreadlimiter);

                nbtreadlimiter.readBytes(16 * s.length());
                NBTBase nbtbase = createNBTBase(b0, s, datainput, i + 1, nbtreadlimiter);

                this.map.put(s, nbtbase);
            }
        }
    }

    public void remove(String s) {
        this.map.remove(s);
    }

    public void set(String s, NBTBase nbtbase) {
        this.map.put(s, nbtbase);
    }

    public void setBoolean(String s, boolean flag) {
        this.setByte(s, (byte) (flag ? 1 : 0));
    }

    public void setByte(String s, byte b0) {
        this.map.put(s, new NBTTagByte(b0));
    }

    public void setByteArray(String s, byte[] abyte) {
        this.map.put(s, new NBTTagByteArray(abyte));
    }

    public void setDouble(String s, double d0) {
        this.map.put(s, new NBTTagDouble(d0));
    }

    public void setFloat(String s, float f) {
        this.map.put(s, new NBTTagFloat(f));
    }

    public void setInt(String s, int i) {
        this.map.put(s, new NBTTagInt(i));
    }

    public void setIntArray(String s, int[] aint) {
        this.map.put(s, new NBTTagIntArray(aint));
    }

    public void setLong(String s, long i) {
        this.map.put(s, new NBTTagLong(i));
    }

    public void setShort(String s, short short1) {
        this.map.put(s, new NBTTagShort(short1));
    }

    public void setString(String s, String s1) {
        this.map.put(s, new NBTTagString(s1));
    }
    
    public void setEnum(String s, Enum value) {
    	this.setString(s, value == null ? "" : value.name());
    }
    

    @Override
    public String toString() {
        String s = "{";

        String s1;

        for (Iterator iterator = this.map.keySet().iterator(); iterator.hasNext(); s = s + s1 + ':' + this.map.get(s1) + ',') {
            s1 = (String) iterator.next();
        }

        return s + "}";
    }

    @Override
    void write(DataOutput dataoutput) throws Exception {

		for (Object o : this.map.keySet()) {
			String s = (String) o;
			NBTBase nbtbase = (NBTBase) this.map.get(s);

			writeTag(s, nbtbase, dataoutput);
		}

        try {
            dataoutput.writeByte(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
