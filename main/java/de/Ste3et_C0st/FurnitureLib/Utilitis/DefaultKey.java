package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.Objects;

public class DefaultKey<K> {
    private K key1;
    private K key2;

    public DefaultKey(K key1) {
        this.key1 = key1;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DefaultKey key = (DefaultKey) object;

        if (key1 != null ? !key1.equals(key.key1) : key.key1 != null) return false;
        return key2 != null ? key2.equals(key.key2) : key.key2 == null;
    }

    @Override
    public int hashCode() {
        return 31 * (key1 != null ? key1.hashCode() : 0) + (key2 != null ? key2.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "[" + key1.toString() + ", " + key2.toString() + "]";
    }

    public K getDefault() {
        return key1;
    }

    public K getValue() {
        return key2;
    }
    
    public K getOrDefault() {
    	if(this.isDefault()) {
    		return key1;
    	}else {
    		return key2;
    	}
    }
    
    public void setValue(K key2) {
    	this.key2 = key2;
    }
    
    public boolean isDefault() {
    	if(Objects.nonNull(this.key2)) return key1.equals(key2);
    	return true;
    }
}
