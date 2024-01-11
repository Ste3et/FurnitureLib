package de.Ste3et_C0st.FurnitureLib.NBT;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NBTTagList extends NBTBase implements Cloneable {
    private List<NBTBase> list = new ArrayList<NBTBase>();
    private byte type = 0;

    void write(DataOutput paramDataOutput) throws Exception {
        if (!this.list.isEmpty()) {
            this.type = this.list.get(0).getTypeId();
        } else {
            this.type = 0;
        }
        paramDataOutput.writeByte(this.type);
        paramDataOutput.writeInt(this.list.size());
        for (NBTBase nbtBase : this.list) {
            nbtBase.write(paramDataOutput);
        }
    }

    void load(DataInput paramDataInput, int paramInt, NBTReadLimiter paramNBTReadLimiter) throws Exception {
        if (paramInt > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        paramNBTReadLimiter.readBytes(8L);
        this.type = paramDataInput.readByte();
        int i = paramDataInput.readInt();

        this.list = new ArrayList<NBTBase>();
        for (int j = 0; j < i; j++) {
            NBTBase localNBTBase = NBTBase.createTag(this.type);
            localNBTBase.load(paramDataInput, paramInt + 1, paramNBTReadLimiter);
            this.list.add(localNBTBase);
        }
    }

    public byte getTypeId() {
        return 9;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("[");
        int i = 0;
        for (NBTBase localNBTBase : this.list) {
            str.append(i).append(':').append(localNBTBase).append(',');
            i++;
        }
        return str + "]";
    }

    public void add(NBTBase paramNBTBase) {
        if (this.type == 0) {
            this.type = paramNBTBase.getTypeId();
        } else if (this.type != paramNBTBase.getTypeId()) {
            System.err.println("WARNING: Adding mismatching tag types to tag list");
            return;
        }
        this.list.add(paramNBTBase);
    }

    public NBTTagCompound get(int paramInt) {
        if ((paramInt < 0) || (paramInt >= this.list.size())) {
            return new NBTTagCompound();
        }
        NBTBase localNBTBase = this.list.get(paramInt);
        if (localNBTBase.getTypeId() == 10) {
            return (NBTTagCompound) localNBTBase;
        }
        return new NBTTagCompound();
    }

    public int[] getIntArray(int paramInt) {
        if ((paramInt < 0) || (paramInt >= this.list.size())) {
            return new int[0];
        }
        NBTBase localNBTBase = this.list.get(paramInt);
        if (localNBTBase.getTypeId() == 11) {
            return ((NBTTagIntArray) localNBTBase).getData();
        }
        return new int[0];
    }

    public double getDouble(int paramInt) {
        if ((paramInt < 0) || (paramInt >= this.list.size())) {
            return 0.0D;
        }
        NBTBase localNBTBase = this.list.get(paramInt);
        if (localNBTBase.getTypeId() == 6) {
            return ((NBTTagDouble) localNBTBase).asDouble();
        }
        return 0.0D;
    }

    public float getFloat(int paramInt) {
        if ((paramInt < 0) || (paramInt >= this.list.size())) {
            return 0.0F;
        }
        NBTBase localNBTBase = this.list.get(paramInt);
        if (localNBTBase.getTypeId() == 5) {
            return ((NBTTagFloat) localNBTBase).asFloat();
        }
        return 0.0F;
    }
    
    public byte getType(int paramInt) {
    	if ((paramInt < 0) || (paramInt >= this.list.size())) {
            return -1;
        }
    	NBTBase localNBTBase = this.list.get(paramInt);
    	return localNBTBase.getTypeId();
    }
    

    public String getString(int paramInt) {
        if ((paramInt < 0) || (paramInt >= this.list.size())) {
            return "";
        }
        NBTBase localNBTBase = this.list.get(paramInt);
        if (localNBTBase.getTypeId() == 8) {
            return localNBTBase.toString();
        }
        return localNBTBase.toString();
    }

    public int size() {
        return this.list.size();
    }

    public NBTBase clone() {
        NBTTagList localNBTTagList = new NBTTagList();
        localNBTTagList.type = this.type;
        for (NBTBase localNBTBase1 : this.list) {
            NBTBase localNBTBase2 = localNBTBase1.clone();
            localNBTTagList.list.add(localNBTBase2);
        }
        return localNBTTagList;
    }

    public boolean equals(Object paramObject) {
        if (super.equals(paramObject)) {
            NBTTagList localNBTTagList = (NBTTagList) paramObject;
            if (this.type == localNBTTagList.type) {
                return this.list.equals(localNBTTagList.list);
            }
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode() ^ this.list.hashCode();
    }

    public int getType() {
        return this.type;
    }
    
    public Stream<NBTBase> stream() {
    	return this.list.stream();
    }
}
