package de.Ste3et_C0st.FurnitureLib.Utilitis;

public class DoubleKey<K extends Comparable<K>> implements Comparable<DoubleKey<K>> {
    private K key1;
    private K key2;

    public DoubleKey(K key1, K key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DoubleKey key = (DoubleKey) object;

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

    public K getKey1() {
        return key1;
    }

    public K getKey2() {
        return key2;
    }

    @Override
    public int compareTo(DoubleKey<K> key) {
        if (key1.compareTo(key.key1) < 0) return -1;
        else if (key1.compareTo(key.key1) == 0) {
            return Integer.compare(key2.compareTo(key.key2), 0);
        }
        return 1;
    }
}
