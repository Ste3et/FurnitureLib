package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.Objects;
import java.util.function.Consumer;

import com.google.common.base.Supplier;

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
    
    public boolean equalsCurrent(K object) {
        if (this == object) return true;
        if (object == null || getOrDefault().getClass() != object.getClass()) return false;
        return getOrDefault().equals(object);
    }

    @Override
    public int hashCode() {
        return 31 * (key1 != null ? key1.hashCode() : 0) + (key2 != null ? key2.hashCode() : 0);
    }

    @Override
    public String toString() {
    	String key1String = this.isEmptyKey1() ? "null" : this.key1.toString();
    	String key2String = this.isEmptyKey2() ? "null" : this.key2.toString();
    	String className = this.isEmptyKey1() == false ? this.key1.getClass().getSimpleName() : this.isEmptyKey2() == false ? this.key2.getClass().getSimpleName() : "";
    	
        return className + "@ [" + key1String + ", " + key2String + "]";
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
    
    public DefaultKey<K> setValue(K key2) {
    	this.key2 = key2;
    	return this;
    }
    
    public DefaultKey<K> setValue(Supplier<K> key) {
    	this.key2 = key.get();
    	return this;
    }
    
    public boolean isEmptyKey1() {
    	return Objects.isNull(this.key1);
    }
    
    public boolean isEmptyKey2() {
    	return Objects.isNull(this.key2);
    }
    
    public boolean isDefault() {
    	if(Objects.isNull(this.key1) && Objects.nonNull(this.key2)) return false;
    	if(Objects.nonNull(this.key2)) return key1.equals(key2);
    	return true;
    }
}
